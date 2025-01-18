package org.poo.plans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class StandardPlan implements Plan {
    @Override
    public double addFee(final double amount, final String currency) {
        return amount + amount * STANDARD_FEE;
    }

    @Override
    public String getType() {
        return "standard";
    }

    @Override
    public double getUpgradePrice(final String newType) {
        if (newType.equals("silver")) {
            return STANDARD_TO_SILVER;
        }

        if (newType.equals("gold")) {
            return STANDARD_TO_GOLD;
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
