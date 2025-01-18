package org.poo.plans;

import lombok.Getter;
import lombok.Setter;
import org.poo.managers.ExchangeManager;

@Getter
@Setter
public final class SilverPlan implements Plan {
    @Override
    public double addFee(final double amount, final String currency) {
        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        double ronAmount = exchangeManager.getAmount(currency, "RON", amount);

        if (ronAmount <= SILVER_FEE_AMOUNT) {
            return amount;
        }

        return amount + amount * SILVER_FEE;
    }

    @Override
    public String getType() {
        return "silver";
    }

    @Override
    public double getUpgradePrice(final String newType) {
        if (newType.equals("gold")) {
            return SILVER_TO_GOLD;
        }

        return -1;
    }

    @Override
    public Plan upgradeTo(final String newType) {
        if (newType.equals("gold")) {
            return new GoldPlan();
        }

        return null;
    }
}
