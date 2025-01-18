package org.poo.visitors;

import org.poo.accounts.business.BusinessAccount;
import org.poo.accounts.ClassicAccount;
import org.poo.accounts.SavingsAccount;

public interface Visitor {
    /**
     * Visits a savings account
     * @param savingsAccount the account to be visited
     */
    double visit(SavingsAccount savingsAccount);

    /**
     * Visits a classic account
     * @param classicAccount the account to be visited
     */
    double visit(ClassicAccount classicAccount);

    /**
     * Visits a business account
     * @param businessAccount the account to be visited
     */
    double visit(BusinessAccount businessAccount);
}
