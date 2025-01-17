package org.poo.cashbackStrategies;

import org.poo.accounts.Account;
import org.poo.commerciant.Commerciant;
import org.poo.managers.ExchangeManager;
import org.poo.user.User;

public final class SpendingThreshold implements CashbackStrategy {
    @Override
    public double cashback(final Account account, final double amount,
                         final Commerciant commerciant) {
        User user = account.ownerOfAccount();

        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        double ronAmount = exchangeManager.getAmount(account.getCurrency(), "RON", amount);
        account.addTotalSpent(ronAmount);

        double totalCashback = 0;

        System.out.println("totalSpent: " + account.getTotalSpent() + ", commerciant: " + commerciant.getName());

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
                    totalCashback = amount * 0.007;
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
                    totalCashback = amount * 0.004;
                    break;
                case "gold":
                    totalCashback = amount * 0.0055;
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

        boolean isDiscountUsed = account.getDiscounts().getOrDefault(commerciant.getType(), true);

        if (!isDiscountUsed) {
            switch (commerciant.getType()) {
                case "Food":
                    totalCashback += amount * 0.02;
                    break;
                case "Clothes":
                    totalCashback += amount * 0.05;
                    break;
                case "Tech":
                    totalCashback += amount * 0.10;
                    break;
                default:
                    break;
            }

            // Mark the discount as used
            account.getDiscounts().put(commerciant.getType(), true);
        }


        account.addFunds(totalCashback);
        return totalCashback;
    }
}
