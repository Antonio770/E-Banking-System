package org.poo.cashbackStrategies;

import org.poo.accounts.Account;
import org.poo.commerciant.Commerciant;

public final class NrOfTransactions implements CashbackStrategy {
    @Override
    public double cashback(final Account account, final double amount,
                         final Commerciant commerciant) {
        boolean isDiscountUsed = account.getDiscounts().getOrDefault(commerciant.getType(), true);
            double totalCashback = 0;

        if (!isDiscountUsed) {

            switch (commerciant.getType()) {
                case "Food":
                    totalCashback = amount * 0.02;
                    break;
                case "Clothes":
                    totalCashback = amount * 0.05;
                    break;
                case "Tech":
                    totalCashback = amount * 0.10;
                    break;
                default:
                    break;
            }

            // Get the cashback and mark the discount as used
            account.addFunds(totalCashback);
            account.getDiscounts().put(commerciant.getType(), true);
        }

        // Increment the number of transactions made
        account.addTransactionToCommerciant(commerciant);

        // Check if the account should get a cashback
        switch (account.getTransactionsMade().get(commerciant)) {
            case 2:
                account.getDiscounts().putIfAbsent("Food", false);
                break;
            case 5:
                account.getDiscounts().putIfAbsent("Clothes", false);
                break;
            case 10:
                account.getDiscounts().putIfAbsent("Tech", false);
                break;
            default:
                break;
        }

        return totalCashback;
    }
}
