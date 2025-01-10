package org.poo.paymentStrategies;

import org.poo.accounts.Account;
import org.poo.commerciant.Commerciant;
import org.poo.managers.BankManager;
import org.poo.managers.ExchangeManager;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public final class BankTransferStrategy implements PaymentStrategy {
    private final BankManager bankManager;
    private final ExchangeManager exchangeManager;

    public BankTransferStrategy() {
        bankManager = BankManager.getInstance();
        exchangeManager = ExchangeManager.getInstance();
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
            User senderUser = bankManager.getUserByAccount(sender);
            Commerciant commerciant = bankManager.getCommerciantByIban(input.getReceiver());

            double totalSenderAmount = senderUser.getPlan().addFee(input.getAmount(),
                                                                   sender.getCurrency());
            double conversionRate = exchangeManager.getConversionRate(sender.getCurrency(),
                                                                      receiver.getCurrency());
            double receiverAmount = input.getAmount() * conversionRate;

            // The sender cannot be an alias
            if (!sender.getIban().equals(input.getAccount())) {
                return false;
            }

            // If there is enough money in the account, pay the amount
            if (sender.getBalance() >= totalSenderAmount) {
                sender.spendFunds(totalSenderAmount);

                if (commerciant != null) {
                    commerciant.getCashbackStrategy().cashback(sender, input.getAmount(),
                                                               commerciant);
                } else {
                    receiver.addFunds(receiverAmount);
                }

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
