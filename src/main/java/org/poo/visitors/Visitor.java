package org.poo.visitors;

import org.poo.accounts.BusinessAccount;
import org.poo.accounts.ClassicAccount;
import org.poo.accounts.SavingsAccount;

public interface Visitor {
    double visit(SavingsAccount savingsAccount);
    double visit(ClassicAccount classicAccount);
    double visit(BusinessAccount businessAccount);
}
