package org.poo.commerciant;

import lombok.Getter;
import lombok.Setter;
import org.poo.cashbackStrategies.CashbackStrategy;
import org.poo.cashbackStrategies.NrOfTransactions;
import org.poo.cashbackStrategies.SpendingThreshold;
import org.poo.fileio.CommerciantInput;

@Getter
@Setter
public final class Commerciant {
    private String name;
    private int id;
    private String account;
    private String type;
    private CashbackStrategy cashbackStrategy;

    public Commerciant(final CommerciantInput input) {
        name = input.getCommerciant();
        id = input.getId();
        account = input.getAccount();
        type = input.getType();

        if (input.getCashbackStrategy().equals("nrOfTransactions")) {
            cashbackStrategy = new NrOfTransactions();
        } else if (input.getCashbackStrategy().equals("spendingThreshold")) {
            cashbackStrategy = new SpendingThreshold();
        }
    }
}
