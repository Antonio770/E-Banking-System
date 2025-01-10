package org.poo.cashbackStrategies;

import org.poo.accounts.Account;
import org.poo.commerciant.Commerciant;
import org.poo.user.User;

public interface CashbackStrategy {
    void cashback(final Account account, final double amount, final Commerciant commerciant);
}
