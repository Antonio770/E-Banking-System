package org.poo.paymentStrategies;

import org.poo.accounts.Account;
import org.poo.commerciant.Commerciant;
import org.poo.managers.BankManager;
import org.poo.managers.ExchangeManager;
import org.poo.cards.Card;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;
import org.poo.utils.Utils;

public final class PayOnlineStrategy implements PaymentStrategy {
    private final BankManager bankManager;
    private final ExchangeManager exchangeManager;

    public PayOnlineStrategy() {
        bankManager = BankManager.getInstance();
        exchangeManager = ExchangeManager.getInstance();
    }

    /**
     * Pays for an online purchase
     * @param input the payment information
     * @return true if the payment is successful, false if not
     */
    @Override
    public boolean pay(final CommandInput input) {
        try {
            User user = bankManager.getUserByEmail(input.getEmail());
            Card card = bankManager.getCardByNumber(input.getCardNumber());
            Account account = user.getAccountOfCard(card);
            Commerciant commerciant = bankManager.getCommerciantByName(input.getCommerciant());


            // Get the conversion rate from the exchange manager
            // and calculate the converted price to be paid
            double convertedPrice = exchangeManager.getAmount(input.getCurrency(),
                                                              account.getCurrency(),
                                                              input.getAmount());
            double totalPrice = user.getPlan().addFee(convertedPrice, input.getCurrency());

            // If the card is frozen, it cannot be used to pay
            if (card.getStatus().equals("frozen")) {
                Transaction transaction = new Transaction.Builder()
                                              .timestamp(input.getTimestamp())
                                              .description("The card is frozen")
                                              .build();

                user.addTransaction(transaction);

                return false;
            }

            // If there is enough money to pay, subtract the amount from the balance
            if (account.getBalance() >= totalPrice) {
                account.spendFunds(totalPrice);

                commerciant.getCashbackStrategy().cashback(account, convertedPrice, commerciant);

                // Add the transaction to the user's list of transactions
                Transaction transaction = new Transaction.Builder()
                                              .timestamp(input.getTimestamp())
                                              .description("Card payment")
                                              .amount(convertedPrice)
                                              .commerciant(input.getCommerciant())
                                              .build();

                user.addTransaction(transaction);
                account.addTransaction(transaction);

                // If it's a one-time card, generate another card after using it
                if (card.getType().equals("OneTime")) {
                    Transaction destroyTransaction = new Transaction.Builder()
                                                        .timestamp(input.getTimestamp())
                                                        .description("The card has been destroyed")
                                                        .card(input.getCardNumber())
                                                        .cardHolder(user.getEmail())
                                                        .account(account.getIban())
                                                        .build();

                    user.addTransaction(destroyTransaction);
                    account.addTransaction(destroyTransaction);

                    card.setCardNumber(Utils.generateCardNumber());

                    Transaction createTransaction = new Transaction.Builder()
                                                        .timestamp(input.getTimestamp())
                                                        .description("New card created")
                                                        .card(card.getCardNumber())
                                                        .cardHolder(user.getEmail())
                                                        .account(account.getIban())
                                                        .build();

                    user.addTransaction(createTransaction);
                    account.addTransaction(createTransaction);
                }

                return true;
            }

            // If the transaction was not possible, add a failed transaction to
            // the user's list of transactions
            Transaction failedTransaction = new Transaction.Builder()
                                                .timestamp(input.getTimestamp())
                                                .description("Insufficient funds")
                                                .build();

            user.addTransaction(failedTransaction);
            account.addTransaction(failedTransaction);
            return false;
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
