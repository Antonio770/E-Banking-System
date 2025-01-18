package org.poo.cashbackStrategies;

import org.poo.accounts.Account;
import org.poo.commerciant.Commerciant;

public interface CashbackStrategy {
    /**
     * Adds cashback to an account
     * @param account the account that will receive the cashback
     * @param amount the amount of money spent in the last transaction
     * @param commerciant the commerciant to which the transaction was made
     */
    void cashback(Account account, double amount, Commerciant commerciant);

    int TWO_TRANSACTION_MADE = 2;
    int FIVE_TRANSACTION_MADE = 5;
    int TEN_TRANSACTION_MADE = 10;

    int FIRST_THRESHOLD = 100;
    int SECOND_THRESHOLD = 300;
    int THIRD_THRESHOLD = 500;

    double TWO_PERCENT = 0.02;
    double FIVE_PERCENT = 0.05;
    double TEN_PERCENT = 0.10;

    double POINT_ONE_PERCENT = 0.001;
    double POINT_TWO_PERCENT = 0.002;
    double POINT_TWO_FIVE_PERCENT = 0.0025;
    double POINT_THREE_PERCENT = 0.003;
    double POINT_FOUR_PERCENT = 0.004;
    double POINT_FIVE_PERCENT = 0.005;
    double POINT_FIVE_FIVE_PERCENT = 0.0055;
    double POINT_SEVEN_PERCENT = 0.007;
}
