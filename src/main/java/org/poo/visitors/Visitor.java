package org.poo.visitors;

import org.poo.accounts.ClassicAccount;
import org.poo.accounts.SavingsAccount;

public interface Visitor {
    /**
     * Visits a savings account
     * @param savingsAccount the account to be visited
     * @return true if the operation was successfully applied to the account,
     *         false if not
     */
    double visit(SavingsAccount savingsAccount);

    /**
     * Visits a classic account
     * @param classicAccount the account to be visited
     * @return true if the operation was successfully applied to the account,
     *         false if not
     */
    double visit(ClassicAccount classicAccount);
}
