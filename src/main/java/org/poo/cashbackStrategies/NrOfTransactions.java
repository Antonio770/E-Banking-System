package org.poo.cashbackStrategies;

import org.poo.accounts.Account;
import org.poo.commerciant.Commerciant;

public final class NrOfTransactions implements CashbackStrategy {
    @Override
    public void cashback(final Account account, final double amount,
                         final Commerciant commerciant) {
        // If there is a discount to be applied, apply it
        if (account.getDiscounts().contains(commerciant.getType())) {
            double totalCashback = 0;

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

            account.addFunds(totalCashback);
        }

        // Increment the number of transactions made
        account.setTransactionsMade(account.getTransactionsMade() + 1);

        // Check if the account should get a cashback
        switch (account.getTransactionsMade()) {
            case 2:
                account.getDiscounts().add("Food");
                break;
            case 5:
                account.getDiscounts().add("Clothes");
                break;
            case 10:
                account.getDiscounts().add("Tech");
                break;
            default:
                break;
        }
    }
}
