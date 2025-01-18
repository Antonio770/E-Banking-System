package org.poo.accounts.business;

import lombok.Getter;
import lombok.Setter;
import org.poo.commerciant.Commerciant;
import org.poo.user.User;

import java.util.ArrayList;

@Getter
@Setter
public final class BusinessCommerciantStats {
    private Commerciant commerciant;
    private double totalReceived;
    private ArrayList<User> users;

    public BusinessCommerciantStats(final Commerciant commerciant) {
        this.commerciant = commerciant;
        totalReceived = 0;
        users = new ArrayList<>();
    }

    /**
     * Adds a user to this commerciant's list of users
     * @param user the user that sent money to this commerciant
     */
    public void addUser(final User user) {
        users.add(user);
    }

    /**
     * Adds an amount of money to the total amount received by this commerciant
     * @param amount the amount of money received from the current transaction
     */
    public void addTotalReceived(final double amount) {
        this.totalReceived += amount;
    }
}
