package org.poo.cashbackStrategies;

import org.poo.accounts.Account;
import org.poo.commerciant.Commerciant;
import org.poo.managers.BankManager;
import org.poo.managers.ExchangeManager;
import org.poo.user.User;

public final class SpendingTreshold implements CashbackStrategy {
    @Override
    public void cashback(final Account account, final double amount,
                         final Commerciant commerciant) {
        BankManager bankManager = BankManager.getInstance();
        User user = bankManager.getUserByAccount(account);

        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        double convRate = exchangeManager.getConversionRate(account.getCurrency(), "RON");
        double ronAmount = amount * convRate;
        account.addTotalSpent(ronAmount);

        double totalCashback = 0;

        if (account.getTotalSpent() >= 500) {
            switch (user.getPlan().getType()) {
                case "student":
                case "standard":
                    totalCashback = amount * 0.0025;
                    break;
                case "silver":
                    totalCashback = amount * 0.005;
                    break;
                case "gold":
                    totalCashback = amount * 0.0075;
                    break;
                default:
                    break;
            }
        } else if (account.getTotalSpent() >= 300) {
            switch (user.getPlan().getType()) {
                case "student":
                case "standard":
                    totalCashback = amount * 0.002;
                    break;
                case "silver":
                    totalCashback = amount * 0.003;
                    break;
                case "gold":
                    totalCashback = amount * 0.055;
                    break;
                default:
                    break;
            }
        } else if (account.getTotalSpent() >= 100) {
            switch (user.getPlan().getType()) {
                case "student":
                case "standard":
                    totalCashback = amount * 0.001;
                    break;
                case "silver":
                    totalCashback = amount * 0.003;
                    break;
                case "gold":
                    totalCashback = amount * 0.005;
                    break;
                default:
                    break;
            }
        }

        account.addFunds(totalCashback);
    }
}
