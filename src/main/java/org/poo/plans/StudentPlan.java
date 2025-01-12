package org.poo.plans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class StudentPlan implements Plan {
    @Override
    public double addFee(final double amount, final String currency) {
        return amount;
    }

    @Override
    public String getType() {
        return "student";
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
