package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.managers.BankManager;
import org.poo.managers.ExchangeManager;
import org.poo.splitPayments.SplitPayment;
import org.poo.transaction.Transaction;
import org.poo.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class SplitPaymentCommand extends Command {
    public SplitPaymentCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        // If the split payment type is "equal", initialize the amountForUser list
        // to contain equal amounts for each user
        if (getInput().getSplitPaymentType().equals("equal")) {
            getInput().setAmountForUsers(initAmountPerUser());
        }

        boolean canPay = true;
        Account failedAccount = null;
        Iterator<String> accountsIterator = getInput().getAccounts().iterator();
        Iterator<Double> amountsIterator = getInput().getAmountForUsers().iterator();

        while (accountsIterator.hasNext() && amountsIterator.hasNext()) {
            String iban = accountsIterator.next();
            Double amount = amountsIterator.next();

            Account account = getBankManager().getAccount(iban);

            if (account == null) {
                // TODO: error
                return null;
            }

            if (!account.canPay(amount, getInput().getCurrency())) {
                canPay = false;
                failedAccount = account;
                break;
            }
        }

        // If everyone can pay, split the payment and update every account's balance.
        // Also add the transaction to every involved account and user
        if (canPay) {
            getBankManager().getSplitPayments().add(new SplitPayment(getInput()));
            return null;
        }

        // If the split payment failed, add the transaction
        // error to every involved account
        // TODO: add failed transaction for each account and user involved
//        String error = "Account " + failedAccount.getIban()
//                       + " has insufficient funds for a split payment.";
//        Transaction transaction = getTransaction(amountPerPerson, error);
//
//        for (Account account : accounts) {
//            User user = getBankManager().getUserByAccount(account);
//            account.addTransaction(transaction);
//            user.addTransaction(transaction);
//        }

        return null;
    }

    private List<Double> initAmountPerUser() {
        int nrAccounts = getInput().getAccounts().size();
        double amountPerUser = getInput().getAmount() / nrAccounts;
        return new ArrayList<>(Collections.nCopies(nrAccounts, amountPerUser));
    }

    /**
     * Builds a transaction for the split payment method
     * @param amountPerPerson the amount of money to be paid by every involved account
     * @param error the error message, if there is an error
     * @return the build transaction
     */
    private Transaction getTransaction(final double amountPerPerson, final String error) {
        String description = "Split payment of " + String.format("%.2f", getInput().getAmount())
                             + " " + getInput().getCurrency();

        return new Transaction.Builder()
                              .timestamp(getInput().getTimestamp())
                              .description(description)
                              .currency(getInput().getCurrency())
                              .amount(amountPerPerson)
                              .involvedAccounts(getInput().getAccounts())
                              .error(error)
                              .build();
    }
}
