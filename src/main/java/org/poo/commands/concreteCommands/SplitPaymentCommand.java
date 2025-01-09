package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.managers.ExchangeRateManager;
import org.poo.transaction.Transaction;
import org.poo.user.User;

import java.util.ArrayList;

public final class SplitPaymentCommand extends Command {
    public SplitPaymentCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        double amountPerPerson = getInput().getAmount() / getInput().getAccounts().size();
        ArrayList<Account> accounts = new ArrayList<Account>();

        ExchangeRateManager exchangeManager = ExchangeRateManager.getInstance();
        boolean canPay = true;
        Account failedAccount = null;

        // Check if all accounts have enough money to pay
        // If there is an account that cannot pay, the split payment will not happen
        // If there are multiple accounts that cannot pay, only keep the last one
        for (String iban : getInput().getAccounts()) {
            Account account = getBankManager().getAccount(iban);
            accounts.add(account);

            if (!account.canPay(amountPerPerson, getInput().getCurrency())) {
                canPay = false;
                failedAccount = account;
            }
        }

        // If everyone can pay, split the payment and update every account's balance.
        // Also add the transaction to every involved account and user
        if (canPay) {
            for (Account account : accounts) {
                double conversionRate = exchangeManager.getConversionRate(getInput().getCurrency(),
                                                                          account.getCurrency());
                double convertedAmount = amountPerPerson * conversionRate;

                account.setBalance(account.getBalance() - convertedAmount);
                Transaction transaction = getTransaction(amountPerPerson, null);

                User user = getBankManager().getUserByAccount(account);
                user.addTransaction(transaction);
                account.addTransaction(transaction);
            }

            return null;
        }

        // If the split payment failed, add the transaction
        // error to every involved account
        String error = "Account " + failedAccount.getIban()
                       + " has insufficient funds for a split payment.";
        Transaction transaction = getTransaction(amountPerPerson, error);

        for (Account account : accounts) {
            User user = getBankManager().getUserByAccount(account);
            account.addTransaction(transaction);
            user.addTransaction(transaction);
        }

        return null;
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
