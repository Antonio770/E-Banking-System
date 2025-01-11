package org.poo.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.visitors.Visitable;
import org.poo.visitors.Visitor;

@Getter
@Setter
public final class SavingsAccount extends Account implements Visitable {
    @JsonIgnore
    private double interestRate;

    public SavingsAccount(final CommandInput input) {
        super(input);
        this.interestRate = input.getInterestRate();
    }

    /**
     * Adds the interest to the account's total balance
     */
    public void addInterest() {
        setBalance(getBalance() + getBalance() * interestRate);
    }

    @JsonIgnore
    public double getInterestAmount() {
        return getBalance() * interestRate;
    }

    @Override
    public double accept(final Visitor visitor) {
        return visitor.visit(this);
    }
}
