package org.poo.plans;

public interface Plan {
    /**
     * Adds the bank fee to an amount of money
     * @param amount the amount of money to be paid
     * @param currency the currency is which the payment is done
     * @return the total amount of money to be paid after the bank fee is added
     */
    double addFee(double amount, String currency);

    /**
     * @return the type of plan
     */
    String getType();

    /**
     * Calculates the price that needs to be paid in order to upgrade to a new plan
     * @param newType the plan to which the user wants to upgrade
     * @return the price of the upgrade
     */
    double getUpgradePrice(String newType);

    /**
     * Upgrades the plan to the new plan
     * @param newType the plan to which the user upgrades
     * @return the new plan
     */
    Plan upgradeTo(String newType);

    int STANDARD_TO_SILVER = 100;
    int SILVER_TO_GOLD = 250;
    int STANDARD_TO_GOLD = 350;

    int SILVER_FEE_AMOUNT = 500;
    double SILVER_FEE = 0.001;
    double STANDARD_FEE = 0.002;
}
