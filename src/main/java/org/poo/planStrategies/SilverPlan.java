package org.poo.planStrategies;

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

        if (ronAmount <= 500) {
            return amount;
        }

        return amount + amount * 0.001;
    }

    @Override
    public String getType() {
        return "silver";
    }

    @Override
    public double getUpgradePrice(final String newType) {
        if (newType.equals("gold")) {
            return 250;
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
