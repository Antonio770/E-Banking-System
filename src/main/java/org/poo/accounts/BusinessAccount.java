package org.poo.accounts;

import org.poo.fileio.CommandInput;
import org.poo.managers.BankManager;
import org.poo.managers.ExchangeManager;
import org.poo.user.User;
import org.poo.visitors.Visitor;

import java.util.ArrayList;
import java.util.HashMap;

public final class BusinessAccount extends Account {
    private ArrayList<User> users;
    private HashMap<User, BusinessRoles> roles;

    private double spendingLimit;
    private static final double INITIAL_SPENDING_LIMIT = 500;

    public BusinessAccount(CommandInput input) {
        super(input);
        roles = new HashMap<>();

        // The user that creates the account is the owner
        BankManager bankManager = BankManager.getInstance();
        User user = bankManager.getUserByEmail(input.getEmail());
        roles.put(user, BusinessRoles.OWNER);

        this.spendingLimit = INITIAL_SPENDING_LIMIT;
    }

    public BusinessRoles getRole(User user) {
        return roles.get(user);
    }

    @Override
    public double accept(final Visitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean canPay(final double amount, final String from, final User user) {
        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        double convertedAmount = exchangeManager.getAmount(from, getCurrency(), amount);

        if (roles.get(user) == BusinessRoles.EMPLOYEE && convertedAmount >= spendingLimit) {
            return false;
        }

        return super.canPay(amount, from);
    }
}
