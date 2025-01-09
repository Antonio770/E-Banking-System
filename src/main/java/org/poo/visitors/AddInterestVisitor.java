package org.poo.visitors;

import org.poo.accounts.ClassicAccount;
import org.poo.accounts.SavingsAccount;

public final class AddInterestVisitor implements Visitor {
    @Override
    public boolean visit(final SavingsAccount savingsAccount) {
        savingsAccount.addInterest();
        return true;
    }

    @Override
    public boolean visit(final ClassicAccount classicAccount) {
        return false;
    }
}
