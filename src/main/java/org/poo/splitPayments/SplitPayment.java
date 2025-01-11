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

    private void makeSplitPayment() {
        Iterator<Account> accountIterator = accounts.iterator();
        Iterator<Double> amountIterator = amountForUsers.iterator();

        while (accountIterator.hasNext() && amountIterator.hasNext()) {
            Account account = accountIterator.next();
            double amount = amountIterator.next();

            account.spendFunds(amount);

            Transaction transaction = new Transaction.Builder()
                    .timestamp(command.getTimestamp())
                    .custom("description", "Split payment of " + command.getAmount()
                            + " " + command.getCurrency())
                    .custom("splitPaymentType", type)
                    .custom("currency", command.getCurrency())
                    .amountForUsers(command.getAmountForUsers())
                    .involvedAccounts(command.getAccounts())
                    .build();

            User user = bankManager.getUserByAccount(account);
            user.addTransaction(transaction);
        }
    }
}
