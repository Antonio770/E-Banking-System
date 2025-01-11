package org.poo.visitors;

import org.poo.accounts.ClassicAccount;
import org.poo.accounts.SavingsAccount;

public final class ChangeInterestVisitor implements Visitor {
    private double interestRate;

    public ChangeInterestVisitor(final double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public double visit(final SavingsAccount savingsAccount) {
        savingsAccount.setInterestRate(interestRate);
        return 1;
    }

    @Override
    public double visit(final ClassicAccount classicAccount) {
        return 0;
    }
}
