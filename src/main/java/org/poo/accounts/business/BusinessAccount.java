package org.poo.accounts.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.fileio.CommandInput;
import org.poo.managers.BankManager;
import org.poo.managers.ExchangeManager;
import org.poo.user.User;
import org.poo.visitors.Visitor;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@JsonIgnoreProperties({"users", "roles", "cardOwners", "spendingLimit", "depositLimit",
                       "transactions", "minBalance", "WARNING_BALANCE", "discounts",
                       "transactionsMade", "totalSpent"})
public final class BusinessAccount extends Account {
    private ArrayList<User> users;
    private HashMap<User, BusinessRoles> roles;
    private HashMap<Card, User> cardOwners;

    private double spendingLimit;
    private double depositLimit;
    private static final double INITIAL_LIMIT = 500;

    public BusinessAccount(CommandInput input) {
        super(input);
        users = new ArrayList<>();
        roles = new HashMap<>();
        cardOwners = new HashMap<>();

        // The user that creates the account is the owner
        BankManager bankManager = BankManager.getInstance();
        User owner = bankManager.getUserByEmail(input.getEmail());
        users.add(owner);
        roles.put(owner, BusinessRoles.OWNER);

        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        double convertedLimit = exchangeManager.getAmount("RON", getCurrency(), INITIAL_LIMIT);
        this.spendingLimit = convertedLimit;
        this.depositLimit = convertedLimit;
    }

    @Override
    public double accept(final Visitor visitor) {
        return visitor.visit(this);
    }

    public void addAssociate(User user, String role) {
        users.add(user);
        roles.put(user, getRoleFromString(role));
    }

    public BusinessRoles getRole(User user) {
        return roles.get(user);
    }

    @Override
    public User ownerOfAccount() {
        for (User user : users) {
            if (roles.get(user) == BusinessRoles.OWNER) {
                return user;
            }
        }

        return null;
    }

    @JsonIgnore
    private BusinessRoles getRoleFromString(String role) {
        return switch (role) {
            case "owner" -> BusinessRoles.OWNER;
            case "manager" -> BusinessRoles.MANAGER;
            case "employee" -> BusinessRoles.EMPLOYEE;
            default -> null;
        };
    }
}
