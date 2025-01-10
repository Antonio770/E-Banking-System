package org.poo.planStrategies;

public interface Plan {
    /**
     * Adds the bank fee to the amount, depending on the plan
     * @param amount the base amount
     * @return the base amount plus the fee
     */
    double addFee(double amount, String currency);
    String getType();
    double getUpgradePrice(String newType);
    Plan upgradeTo(String newType);
}
