package org.poo.splitPayments;

import lombok.Getter;
import lombok.Setter;
import org.poo.accounts.Account;
import org.poo.fileio.CommandInput;
import org.poo.managers.BankManager;
import org.poo.transaction.Transaction;
import org.poo.user.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        this.pendingAccounts = new ArrayList<>(this.accounts);
        this.users = initUsers();
    }

    private ArrayList<User> initUsers() {
        ArrayList<User> userList = new ArrayList<User>();

        for (Account account : accounts) {
            User user = bankManager.getUserByAccount(account);
            if (user != null) {
                userList.add(user);
            }
        }

        return userList;
    }

    private ArrayList<Account> initAccounts(List<String> accounts) {
        ArrayList<Account> accountsList = new ArrayList<Account>();

        for (String account : accounts) {
            Account acc = bankManager.getAccount(account);
            if (acc != null) {
                accountsList.add(acc);
            }
        }

        return accountsList;
    }

    public void acceptSplitPayment(User user) {
        Account accountConfirmed = null;

        for (Account account : accounts) {
            if (user.getAccounts().contains(account)) {
                accountConfirmed = account;
                break;
            }
        }

        pendingAccounts.remove(accountConfirmed);

        if (pendingAccounts.isEmpty()) {
            makeSplitPayment();
        }
    }

    public void rejectSplitPayment(User user) {
        // TODO: reject the payment
    }

    private void makeSplitPayment() {
        Account failedAccount = canEveryonePay();

        if (failedAccount != null) {
            addErrorTransactions(failedAccount);
            return;
        }

        Iterator<Account> accountIterator = accounts.iterator();
        Iterator<Double> amountIterator = amountForUsers.iterator();

        while (accountIterator.hasNext() && amountIterator.hasNext()) {
            Account account = accountIterator.next();
            double amount = amountIterator.next();
            account.spendFunds(amount);

            Transaction transaction = getSuccessfullTransaction();
            User user = bankManager.getUserByAccount(account);
            user.addTransaction(transaction);
        }
    }

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

    private void addErrorTransactions(Account failedAccount) {
        String error = "Account " + failedAccount.getIban()
                        + " has insufficient funds for a split payment.";

        for (String s : command.getAccounts()) {
            Account account = getBankManager().getAccount(s);
            User user = getBankManager().getUserByAccount(account);

            Transaction transaction = getErrorTransaction(error, type);
            user.addTransaction(transaction);
        }
    }

    private Transaction getSuccessfullTransaction() {
        return new Transaction.Builder().timestamp(command.getTimestamp())
                                        .custom("description", "Split payment of "
                                                + String.format("%.2f", command.getAmount())
                                                + " " + command.getCurrency())
                                        .custom("splitPaymentType", type)
                                        .custom("currency", command.getCurrency())
                                        .amountForUsers(command.getAmountForUsers())
                                        .involvedAccounts(command.getAccounts())
                                        .build();
    }

    private Transaction getErrorTransaction(final String error, final String type) {
        String description = "Split payment of " + String.format("%.2f", command.getAmount())
                             + " " + command.getCurrency();

        Transaction.Builder builder = new Transaction.Builder()
                                    .timestamp(command.getTimestamp())
                                    .custom("description", description)
                                    .custom("error", error)
                                    .custom("splitPaymentType", command.getSplitPaymentType())
                                    .custom("currency", command.getCurrency())
                                    .involvedAccounts(command.getAccounts());

        if (type.equals("equal")) {
            builder.amount(amountForUsers.getFirst());
        } else {
            builder.amountForUsers(command.getAmountForUsers());
        }

        return builder.build();
    }
}
