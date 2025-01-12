package org.poo.accounts;

import org.poo.fileio.CommandInput;
import org.poo.user.User;
import org.poo.visitors.Visitor;

public final class ClassicAccount extends Account {
    public ClassicAccount(final CommandInput input) {
        super(input);
    }

    @Override
    public double accept(final Visitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean canPay(double amount, String from, User user) {
        return super.canPay(amount, from);
    }
}
