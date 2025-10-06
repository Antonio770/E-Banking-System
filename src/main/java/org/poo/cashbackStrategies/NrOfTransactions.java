package org.poo.cashbackStrategies;

import org.poo.accounts.Account;
import org.poo.merchant.Merchant;

public final class NrOfTransactions implements CashbackStrategy {
    @Override
    public void cashback(final Account account, final double amount,
                         final Merchant merchant) {
        boolean isDiscountUsed = account.getDiscounts().getOrDefault(merchant.getType(), true);
            double totalCashback = 0;

        if (!isDiscountUsed) {

            switch (merchant.getType()) {
                case "Food":
                    totalCashback = amount * TWO_PERCENT;
                    break;
                case "Clothes":
                    totalCashback = amount * FIVE_PERCENT;
                    break;
                case "Tech":
                    totalCashback = amount * TEN_PERCENT;
                    break;
                default:
                    break;
            }

            // Get the cashback and mark the discount as used
            account.addFunds(totalCashback);
            account.getDiscounts().put(merchant.getType(), true);
        }

        // Increment the number of transactions made
        account.addTransactionToCommerciant(merchant);

        // Check if the account should get a cashback
        switch (account.getTransactionsMade().get(merchant)) {
            case TWO_TRANSACTION_MADE:
                account.getDiscounts().putIfAbsent("Food", false);
                break;
            case FIVE_TRANSACTION_MADE:
                account.getDiscounts().putIfAbsent("Clothes", false);
                break;
            case TEN_TRANSACTION_MADE:
                account.getDiscounts().putIfAbsent("Tech", false);
                break;
            default:
                break;
        }
    }
}
