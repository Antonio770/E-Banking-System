package org.poo.paymentStrategies;

import org.poo.accounts.Account;
import org.poo.accounts.business.BusinessAccount;
import org.poo.accounts.business.BusinessRoles;
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
        if (input.getAmount() <= 0) {
            return false;
        }

        try {
            User user = bankManager.getUserByEmail(input.getEmail());
            Card card = bankManager.getCardByNumber(input.getCardNumber());
            Account account = bankManager.getAccountOfCard(card);
            Commerciant commerciant = bankManager.getCommerciantByName(input.getCommerciant());

            // Get the conversion rate from the exchange manager
            // and calculate the converted price to be paid
            double convertedPrice = exchangeManager.getAmount(input.getCurrency(),
                                                              account.getCurrency(),
                                                              input.getAmount());

            // Owner's plan
            double totalPrice = account.ownerOfAccount().getPlan()
                                       .addFee(convertedPrice, account.getCurrency());

            // User's plan
//            double totalPrice = user.getPlan().addFee(convertedPrice, account.getCurrency());


            // If the card is frozen, it cannot be used to pay
            if (card.getStatus().equals("frozen")) {
                Transaction transaction = new Transaction.Builder()
                                              .timestamp(input.getTimestamp())
                                              .custom("description", "The card is frozen")
                                              .build();

                user.addTransaction(transaction);

                return false;
            }

            // Spending limit for employees
            if (account.getType().equals("business")) {
                BusinessAccount businessAccount = (BusinessAccount) account;

                if (totalPrice > businessAccount.getSpendingLimit()
                    && businessAccount.getRole(user) == BusinessRoles.EMPLOYEE) {
                    return false;
                }
            }

            // If there is enough money to pay, subtract the amount from the balance
            if (account.canPay(totalPrice, account.getCurrency())) {
                account.spendFunds(totalPrice);

                commerciant.getCashbackStrategy().cashback(account, convertedPrice, commerciant);

                // Add the transaction to the user's list of transactions
                Transaction transaction = new Transaction.Builder()
                                              .timestamp(input.getTimestamp())
                                              .amount(convertedPrice)
                                              .custom("description", "Card payment")
                                              .custom("commerciant", input.getCommerciant())
                                              .custom("email", input.getEmail())
                                              .build();

                user.addTransaction(transaction);
                account.addTransaction(transaction);

                // If it's a one-time card, generate another card after using it
                if (card.getType().equals("OneTime")) {
                    generateNewOneTimeCard(input, user, account, card);
                }

                // If 5 transactions of over 300 RON were made, upgrade from silver to gold
                if (account.checkUpgradeTransaction(convertedPrice)) {
                    account.ownerOfAccount()
                           .addTransaction(getUpgradePlanTransaction(input, account));
                }

                return true;
            }

            // If the transaction was not possible, add a failed transaction to
            // the user's list of transactions
            Transaction failedTransaction = new Transaction.Builder()
                                                .timestamp(input.getTimestamp())
                                                .custom("description", "Insufficient funds")
                                                .build();

            user.addTransaction(failedTransaction);
            account.addTransaction(failedTransaction);
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private Transaction getUpgradePlanTransaction(final CommandInput input,
                                                  final Account account) {
        return new Transaction.Builder()
                .timestamp(input.getTimestamp())
                .custom("description", "Upgrade plan")
                .custom("accountIBAN", account.getIban())
                .custom("newPlanType", "gold")
                .build();
    }

    private void generateNewOneTimeCard(final CommandInput input, final User user,
                                        final Account account, final Card card) {
        Transaction destroyTransaction = new Transaction.Builder()
                .timestamp(input.getTimestamp())
                .custom("description",
                        "The card has been destroyed")
                .custom("card", card.getCardNumber())
                .custom("cardHolder", user.getEmail())
                .custom("account", account.getIban())
                .build();

        user.addTransaction(destroyTransaction);
        account.addTransaction(destroyTransaction);

        card.setCardNumber(Utils.generateCardNumber());

        Transaction createTransaction = new Transaction.Builder()
                .timestamp(input.getTimestamp())
                .custom("description", "New card created")
                .custom("card", card.getCardNumber())
                .custom("cardHolder", user.getEmail())
                .custom("account", account.getIban())
                .build();

        user.addTransaction(createTransaction);
        account.addTransaction(createTransaction);
    }
}
