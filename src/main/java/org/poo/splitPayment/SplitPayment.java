package org.poo.splitPayment;

import lombok.Getter;
import lombok.Setter;
import org.poo.accounts.Account;
import org.poo.fileio.CommandInput;
import org.poo.managers.BankManager;
import org.poo.managers.ExchangeManager;
import org.poo.transaction.Transaction;
import org.poo.user.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
public final class SplitPayment {
    private String type;
    private String currency;
    private ArrayList<User> users;
    private ArrayList<Account> accounts;
    private ArrayList<Account> pendingAccounts;
    private ArrayList<Double> amountForUsers;

    private BankManager bankManager;
    private CommandInput command;

    public SplitPayment(final CommandInput input) {
        this.bankManager = BankManager.getInstance();
        this.command = input;

        this.type = input.getSplitPaymentType();
        this.currency = input.getCurrency();
        this.amountForUsers = new ArrayList<Double>(input.getAmountForUsers());
        this.accounts = initAccounts(input.getAccounts());
        this.pendingAccounts = new ArrayList<Account>(this.accounts);
        this.users = initUsers();
    }

    /**
     * Initializes the list of users that are part of the split payment
     * @return the initialized list
     */
    private ArrayList<User> initUsers() {
        return accounts.stream()
                       .map(Account::ownerOfAccount)
                       .filter(Objects::nonNull)
                       .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Initializes the list of accounts that are part of the split payment
     * @param accountsList the list of accounts listed in the input command
     * @return the initialized list
     */
    private ArrayList<Account> initAccounts(final List<String> accountsList) {
        return accountsList.stream()
                           .map(bankManager::getAccount)
                           .filter(Objects::nonNull)
                           .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Accepts a split payment. If all users accepted it,
     * make the split payment if all users have enough money to pay
     * @param user the user that accepted the split payment
     */
    public void acceptSplitPayment(final User user) {
        ArrayList<Account> accountsConfirmed = new ArrayList<Account>();

        for (Account account : accounts) {
            if (user.getAccounts().contains(account)) {
                accountsConfirmed.add(account);
            }
        }

        pendingAccounts.removeAll(accountsConfirmed);

        if (pendingAccounts.isEmpty()) {
            makeSplitPayment();
        }
    }

    /**
     * Reject a split payment and add an error transaction
     * for every user that was part of the payment
     */
    public void rejectSplitPayment() {
        addErrorTransactions("One user rejected the payment.");
        pendingAccounts.clear();
    }

    /**
     * Make the split payment if everyone can pay for it
     */
    private void makeSplitPayment() {
        // Check if everyone can pay
        Account failedAccount = canEveryonePay();

        // If one user doesn't have enough money, add the error transaction
        // to every user that is part of the split payment
        if (failedAccount != null) {
            addErrorTransactions("Account " + failedAccount.getIban()
                                 + " has insufficient funds for a split payment.");
            pendingAccounts.clear();
            return;
        }

        // Iterate through the list of accounts and amounts to be paid.
        // Convert every amount to the currency of the account
        // and add the transaction to the user and the account
        Iterator<Account> accountIterator = accounts.iterator();
        Iterator<Double> amountIterator = amountForUsers.iterator();

        while (accountIterator.hasNext() && amountIterator.hasNext()) {
            Account account = accountIterator.next();
            double amount = amountIterator.next();

            ExchangeManager exchangeManager = ExchangeManager.getInstance();
            double convAmount = exchangeManager.getAmount(currency, account.getCurrency(), amount);
            account.spendFunds(convAmount);

            Transaction transaction = getSuccessfullTransaction();
            User user = account.ownerOfAccount();
            user.addTransaction(transaction);
            account.addTransaction(transaction);
        }

        // Clear the list of pendingAccounts, meaning that the payment was done
        pendingAccounts.clear();
    }

    /**
     * Checks if every account involved in a split payment can pay
     * @return null, if everyone can pay, or the account that doesn't have enough money
     */
    private Account canEveryonePay() {
        Iterator<String> accountsIterator = command.getAccounts().iterator();
        Iterator<Double> amountsIterator = command.getAmountForUsers().iterator();

        while (accountsIterator.hasNext() && amountsIterator.hasNext()) {
            String iban = accountsIterator.next();
            Double amount = amountsIterator.next();

            Account account = getBankManager().getAccount(iban);

            if (!account.canPay(amount, command.getCurrency())) {
                return account;
            }
        }

        return null;
    }

    /**
     * @return a transaction containing information
     * about the split payment that was done successfully
     */
    private Transaction getSuccessfullTransaction() {
        Transaction.Builder builder = new Transaction.Builder().timestamp(command.getTimestamp())
                                        .custom("description", "Split payment of "
                                                + String.format("%.2f", command.getAmount())
                                                + " " + command.getCurrency())
                                        .custom("splitPaymentType", type)
                                        .custom("currency", command.getCurrency())
                                        .involvedAccounts(command.getAccounts());

        if (type.equals("equal")) {
            builder.amount(amountForUsers.getFirst());
        } else {
            builder.amountForUsers(command.getAmountForUsers());
        }

        return builder.build();
    }

    /**
     * Adds an error transaction to every account involved in the split payment
     * @param error the error that describes why the transaction couldn't be made
     */
    private void addErrorTransactions(final String error) {
        for (String s : command.getAccounts()) {
            Account account = getBankManager().getAccount(s);
            User user = getBankManager().getUserByAccount(account);

            Transaction transaction = getErrorTransaction(error, type);
            user.addTransaction(transaction);
        }
    }

    /**
     * Creates an error transaction
     * @param error the error that describes why the transaction couldn't be made
     * @param paymentType the type of the split payment ("equal" or "custom")
     * @return the created transaction
     */
    private Transaction getErrorTransaction(final String error, final String paymentType) {
        String description = "Split payment of " + String.format("%.2f", command.getAmount())
                             + " " + command.getCurrency();

        Transaction.Builder builder = new Transaction.Builder()
                                    .timestamp(command.getTimestamp())
                                    .custom("description", description)
                                    .custom("error", error)
                                    .custom("splitPaymentType", command.getSplitPaymentType())
                                    .custom("currency", command.getCurrency())
                                    .involvedAccounts(command.getAccounts());

        if (paymentType.equals("equal")) {
            builder.amount(amountForUsers.getFirst());
        } else {
            builder.amountForUsers(command.getAmountForUsers());
        }

        return builder.build();
    }
}
