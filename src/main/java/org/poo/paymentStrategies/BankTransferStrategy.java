package org.poo.paymentStrategies;

import org.poo.accounts.Account;
import org.poo.managers.BankManager;
import org.poo.managers.ExchangeRateManager;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public final class BankTransferStrategy implements PaymentStrategy {
    private final BankManager bankManager;
    private final ExchangeRateManager exchangeRateManager;

    public BankTransferStrategy() {
        bankManager = BankManager.getInstance();
        exchangeRateManager = ExchangeRateManager.getInstance();
    }

    /**
     * Sends money from a bank account to another
     * @param input the transaction information
     * @return true if the transaction is successful, false if not
     */
    @Override
    public boolean pay(final CommandInput input) {
        try {
            Account sender = bankManager.getAccount(input.getAccount());
            Account receiver = bankManager.getAccount(input.getReceiver());

            double conversionRate = exchangeRateManager.getConversionRate(sender.getCurrency(),
                                                                          receiver.getCurrency());

            // The sender cannot be an alias
            if (!sender.getIban().equals(input.getAccount())) {
                return false;
            }

            // If there is enough money in the account, pay the amount
            if (sender.getBalance() >= input.getAmount()) {
                sender.setBalance(sender.getBalance() - input.getAmount());
                receiver.setBalance(receiver.getBalance() + input.getAmount() * conversionRate);
                return true;
            }

            // There is not enough money to pay the required amount
            Transaction transaction = new Transaction.Builder()
                                          .timestamp(input.getTimestamp())
                                          .description("Insufficient funds")
                                          .build();

            User user = bankManager.getUserByEmail(input.getEmail());
            user.addTransaction(transaction);
            sender.addTransaction(transaction);
            return false;

        } catch (NullPointerException e) {
            return false;
        }
    }
}
