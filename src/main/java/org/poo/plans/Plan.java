package org.poo.plans;

public interface Plan {
    double addFee(double amount, String currency);
    String getType();
    double getUpgradePrice(String newType);
    Plan upgradeTo(String newType);
}
