package org.poo.cashbackStrategies;

import org.poo.accounts.Account;
import org.poo.commerciant.Commerciant;
import org.poo.managers.ExchangeManager;
import org.poo.user.User;

import java.util.ArrayList;

public final class SpendingThreshold implements CashbackStrategy {
    private final ArrayList<Double> firstThresholdCashback = new ArrayList<>();
    private final ArrayList<Double> secondThresholdCashback = new ArrayList<>();
    private final ArrayList<Double> thirdThresholdCashback = new ArrayList<>();

    public SpendingThreshold() {
        firstThresholdCashback.add(POINT_ONE_PERCENT);
        firstThresholdCashback.add(POINT_THREE_PERCENT);
        firstThresholdCashback.add(POINT_FIVE_PERCENT);

        secondThresholdCashback.add(POINT_TWO_PERCENT);
        secondThresholdCashback.add(POINT_FOUR_PERCENT);
        secondThresholdCashback.add(POINT_FIVE_FIVE_PERCENT);

        thirdThresholdCashback.add(POINT_TWO_FIVE_PERCENT);
        thirdThresholdCashback.add(POINT_FIVE_PERCENT);
        thirdThresholdCashback.add(POINT_SEVEN_PERCENT);
    }

    @Override
    public void cashback(final Account account, final double amount,
                         final Commerciant commerciant) {
        User user = account.ownerOfAccount();

        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        double ronAmount = exchangeManager.getAmount(account.getCurrency(), "RON", amount);
        account.addTotalSpent(ronAmount);

        double totalCashback = 0;

        if (account.getTotalSpent() >= THIRD_THRESHOLD) {
            totalCashback = getTotalCashback(amount, user, thirdThresholdCashback);
        } else if (account.getTotalSpent() >= SECOND_THRESHOLD) {
            totalCashback = getTotalCashback(amount, user, secondThresholdCashback);
        } else if (account.getTotalSpent() >= FIRST_THRESHOLD) {
            totalCashback = getTotalCashback(amount, user, firstThresholdCashback);
        }

        boolean isDiscountUsed = account.getDiscounts().getOrDefault(commerciant.getType(), true);

        if (!isDiscountUsed) {
            switch (commerciant.getType()) {
                case "Food":
                    totalCashback += amount * TWO_PERCENT;
                    break;
                case "Clothes":
                    totalCashback += amount * FIVE_PERCENT;
                    break;
                case "Tech":
                    totalCashback += amount * TEN_PERCENT;
                    break;
                default:
                    break;
            }

            // Mark the discount as used
            account.getDiscounts().put(commerciant.getType(), true);
        }

        account.addFunds(totalCashback);
    }

    private double getTotalCashback(final double amount, final User user,
                                    final ArrayList<Double> cashback) {
        return switch (user.getPlan().getType()) {
            case "student", "standard" -> amount * cashback.get(0);
            case "silver" -> amount * cashback.get(1);
            case "gold" -> amount * cashback.get(2);
            default -> 0;
        };
    }
}
