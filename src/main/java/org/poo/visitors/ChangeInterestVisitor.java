package org.poo.visitors;

import org.poo.accounts.ClassicAccount;
import org.poo.accounts.SavingsAccount;

public final class ChangeInterestVisitor implements Visitor {
    private double interestRate;

    public ChangeInterestVisitor(final double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public boolean visit(final SavingsAccount savingsAccount) {
        savingsAccount.setInterestRate(interestRate);
        return true;
    }

    @Override
    public boolean visit(final ClassicAccount classicAccount) {
        return false;
    }
}
