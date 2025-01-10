package org.poo.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import org.poo.accounts.Account;
import org.poo.accounts.AccountFactory;
import org.poo.cards.Card;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.paymentStrategies.PaymentFactory;
import org.poo.paymentStrategies.PaymentStrategy;
import org.poo.planStrategies.Plan;
import org.poo.planStrategies.StandardPlan;
import org.poo.planStrategies.StudentPlan;
import org.poo.transaction.Transaction;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties({"transactions", "paymentStrategy", "aliasMap", "plan",
                        "birthDate", "occupation", "age"})
public final class User {
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String occupation;

    private ArrayList<Account> accounts;
    private Plan plan;

    private ArrayList<Transaction> transactions;
    private PaymentStrategy paymentStrategy;
    private Map<String, String> aliasMap;

    public User(final UserInput input) {
        this.firstName = input.getFirstName();
        this.lastName = input.getLastName();
        this.email = input.getEmail();
        this.birthDate = input.getBirthDate();
        this.occupation = input.getOccupation();
        this.accounts = new ArrayList<Account>();
        this.transactions = new ArrayList<Transaction>();
        this.aliasMap = new HashMap<String, String>();

        if (this.occupation.equals("student")) {
            this.plan = new StudentPlan();
        } else {
            this.plan = new StandardPlan();
        }
    }

    /**
     * Adds an account to the user's account list
     * @param input the information of the account
     */
    public void addAccount(final CommandInput input) {
        AccountFactory accountFactory = AccountFactory.getInstance();
        Account newAccount = accountFactory.create(input);

        this.accounts.add(newAccount);
    }

    /**
     * Adds a transaction to the user's transaction list
     * @param transaction the transaction to be added
     */
    public void addTransaction(final Transaction transaction) {
        this.transactions.add(transaction);
    }

    /**
     * Searches for the account that contains a card
     * @param card the card contained by the account
     * @return the account if it was found, null if not
     */
    public Account getAccountOfCard(final Card card) {
        for (final Account account : this.accounts) {
            if (account.getCards().contains(card)) {
                return account;
            }
        }

        return null;
    }

    /**
     * Sets an alias to an account
     * @param alias the alias to be set
     * @param iban the IBAN of the account
     */
    public void setAlias(final String alias, final String iban) {
        this.aliasMap.put(alias, iban);
    }

    /**
     * Checks if a user has a certain card
     * @param card the card to be searched for
     * @return true if the user has the card, false if not
     */
    public boolean hasCard(final Card card) {
        for (final Account account : this.accounts) {
            if (account.getCards().contains(card)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a user has a certain account
     * @param account the account to be searched for
     * @return true if the user has the account, false if not
     */
    public boolean hasAccount(final Account account) {
        return this.accounts.contains(account);
    }

    /**
     * Sends money to pay for something
     * @param input the payment information
     * @return true if the payment was successful, false if not
     */
    public boolean sendMoney(final CommandInput input) {
        PaymentFactory paymentFactory = PaymentFactory.getInstance();
        paymentStrategy = paymentFactory.create(input.getCommand());
        return paymentStrategy.pay(input);
    }

    /**
     * Calculates the age of a user
     * @return the age of the user
     */
    public int getAge() {
        LocalDate birthDate = LocalDate.parse(this.birthDate);
        LocalDate now = LocalDate.now();
        return Period.between(birthDate, now).getYears();
    }
}
