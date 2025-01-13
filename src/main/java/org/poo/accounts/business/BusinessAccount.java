package org.poo.accounts.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.fileio.CommandInput;
import org.poo.managers.BankManager;
import org.poo.user.User;
import org.poo.visitors.Visitor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonIgnoreProperties({"users", "roles", "cardOwners", "spendingLimit", "depositLimit"})
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
        User user = bankManager.getUserByEmail(input.getEmail());
        roles.put(user, BusinessRoles.OWNER);

        this.spendingLimit = INITIAL_LIMIT;
        this.depositLimit = INITIAL_LIMIT;
    }

    @Override
    public double accept(final Visitor visitor) {
        return visitor.visit(this);
    }

    public BusinessRoles getRole(User user) {
        return roles.get(user);
    }

    @JsonIgnore
    public User getOwner(Card card) {
        return cardOwners.get(card);
    }

    @JsonIgnore
    private ArrayList<User> getManagers() {
        return users.stream()
                .filter(user -> roles.get(user) == BusinessRoles.MANAGER)
                .sorted(Comparator.comparing(User::getFirstName)
                        .thenComparing(User::getLastName))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @JsonIgnore
    private ArrayList<User> getEmployees() {
        return users.stream()
                .filter(user -> roles.get(user) == BusinessRoles.EMPLOYEE)
                .sorted(Comparator.comparing(User::getFirstName)
                        .thenComparing(User::getLastName))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @JsonIgnore
    public ArrayList<User> getAllUsersSorted() {
        // Get the lists of managers and employees
        ArrayList<User> managers = getManagers();
        ArrayList<User> employees = getEmployees();

        // Sort the managers and employees alphabetically
        managers.sort(Comparator.comparing(User::getLastName).thenComparing(User::getFirstName));
        employees.sort(Comparator.comparing(User::getLastName).thenComparing(User::getFirstName));

        // Concatenate the 2 lists to get a list of all users sorted by role and then by name
        ArrayList<User> users = new ArrayList<>(managers);
        users.addAll(employees);

        return users;
    }

    public void addAssociate(User user, String role) {
        users.add(user);
        roles.put(user, getRoleFromString(role));
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
