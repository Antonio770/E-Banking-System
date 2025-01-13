package org.poo.visitors;

import org.poo.accounts.business.BusinessAccount;
import org.poo.accounts.ClassicAccount;
import org.poo.accounts.SavingsAccount;

public final class AddInterestVisitor implements Visitor {
    @Override
    public double visit(final SavingsAccount savingsAccount) {
        return savingsAccount.getInterestAmount();
    }

    @Override
    public double visit(final ClassicAccount classicAccount) {
        return 0;
    }

    @Override
    public double visit(final BusinessAccount businessAccount) {
        return 0;
    }
}
