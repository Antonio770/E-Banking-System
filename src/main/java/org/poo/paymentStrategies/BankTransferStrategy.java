package org.poo.paymentStrategies;

import org.poo.accounts.Account;
import org.poo.accounts.business.BusinessAccount;
import org.poo.accounts.business.BusinessRoles;
import org.poo.merchant.Merchant;
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
            User senderUser = sender.ownerOfAccount();
            Merchant merchant = bankManager.getCommerciantByIban(input.getReceiver());

            double totalSenderAmount = senderUser.getPlan().addFee(input.getAmount(),
                                                                   sender.getCurrency());

            // The sender cannot be an alias
            if (!sender.getIban().equals(input.getAccount())) {
                return false;
            }

            // Employees have a spending limit
            if (sender.getType().equals("business")) {
                BusinessAccount businessAccount = (BusinessAccount) sender;

                if (totalSenderAmount > businessAccount.getSpendingLimit()
                        && businessAccount.getRole(senderUser) == BusinessRoles.EMPLOYEE) {
                    return false;
                }
            }

            // If there is enough money in the account, pay the amount
            if (sender.canPay(totalSenderAmount, sender.getCurrency())) {
                sender.spendFunds(totalSenderAmount);

                if (merchant != null) {
                    merchant.getCashbackStrategy().cashback(sender, input.getAmount(),
                            merchant);
                } else {
                    double receiverAmount = exchangeManager.getAmount(sender.getCurrency(),
                                                                      receiver.getCurrency(),
                                                                      input.getAmount());
                    receiver.addFunds(receiverAmount);
                }

                // If 5 transactions of over 300 RON were made, upgrade from silver to gold
                if (sender.checkUpgradeTransaction(input.getAmount())) {
                    senderUser.addTransaction(getUpgradePlanTransaction(input, sender));
                }

                return true;
            }

            // There is not enough money to pay the required amount
            Transaction transaction = new Transaction.Builder()
                                          .timestamp(input.getTimestamp())
                                          .custom("description", "Insufficient funds")
                                          .build();

            User user = bankManager.getUserByEmail(input.getEmail());
            user.addTransaction(transaction);
            sender.addTransaction(transaction);
            return false;

        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Creates a transaction that contains information about the upgraded user plan
     * @param input the command input
     * @param account the account that made the transaction
     * @return the upgrade plan transaction
     */
    private Transaction getUpgradePlanTransaction(final CommandInput input,
                                                  final Account account) {
        return new Transaction.Builder()
                .timestamp(input.getTimestamp())
                .custom("description", "Upgrade plan")
                .custom("accountIBAN", account.getIban())
                .custom("newPlanType", "gold")
                .build();
    }
}
