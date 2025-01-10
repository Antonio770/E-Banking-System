package org.poo.planStrategies;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class StandardPlan implements Plan {
    @Override
    public double addFee(final double amount, final String currency) {
        return amount + amount * 0.002;
    }

    @Override
    public String getType() {
        return "standard";
    }

    @Override
    public double getUpgradePrice(final String newType) {
        if (newType.equals("silver")) {
            return 100;
        }

        if (newType.equals("gold")) {
            return 350;
        }

        return -1;
    }

    @Override
    public Plan upgradeTo(final String newType) {
        return switch (newType) {
            case "silver" -> new SilverPlan();
            case "gold" -> new GoldPlan();
            default -> null;
        };
    }
}
