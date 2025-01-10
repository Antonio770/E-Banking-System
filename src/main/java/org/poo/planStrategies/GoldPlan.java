package org.poo.planStrategies;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class GoldPlan implements Plan {
    @Override
    public double addFee(final double amount, final String currency) {
        return amount;
    }

    @Override
    public String getType() {
        return "gold";
    }

    @Override
    public double getUpgradePrice(final String newType) {
        return -1;
    }

    @Override
    public Plan upgradeTo(final String newType) {
        return null;
    }
}
