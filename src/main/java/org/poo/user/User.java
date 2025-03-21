package org.poo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import org.poo.plans.Plan;
import org.poo.plans.StandardPlan;
import org.poo.plans.StudentPlan;
import org.poo.transaction.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties({"transactions", "paymentStrategy", "aliasMap", "plan",
                        "birthDate", "occupation", "age", "upgradeTransactions"})
public final class User {
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String occupation;

    private ArrayList<Account> accounts;
    private Plan plan;
    private int upgradeTransactions;

    private ArrayList<Transaction> transactions;
    private PaymentStrategy paymentStrategy;
    private Map<String, String> aliasMap;

    private static final int ERROR = 8;

    public User(final UserInput input) {
        this.firstName = input.getFirstName();
        this.lastName = input.getLastName();
        this.email = input.getEmail();
        this.birthDate = input.getBirthDate();
        this.occupation = input.getOccupation();
        this.accounts = new ArrayList<Account>();
        this.transactions = new ArrayList<Transaction>();
        this.aliasMap = new HashMap<String, String>();
        this.upgradeTransactions = 0;

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
     * Sorts this user's list of transactions by timestamp
     */
    public void sortTransactionsByTimestamp() {
        transactions.sort(Comparator.comparingInt(Transaction::getTimestamp));
    }

    /**
     * Increments the number of transactions done to "nrOfTransactions" type commerciants
     */
    public void incrementUpgradeTransactions() {
        this.upgradeTransactions++;
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
        LocalDate userBirthDate = LocalDate.parse(this.birthDate);
        LocalDate now = LocalDate.now();
        return Period.between(userBirthDate, now).getYears();
    }

    /**
     * Searches an account of type "classic" in this user's list of accounts
     * @param currency the currency of the account
     * @return the first classic account that uses the specified currency
     */
    @JsonIgnore
    public Account getFirstClassicAccount(final String currency) {
        return accounts.stream()
                       .filter(account -> account.getType().equals("classic")
                                           && account.getCurrency().equals(currency))
                       .findFirst()
                       .orElse(null);
    }

    /**
     * Rounds the balances of this user's accounts using a 10^(-8) error
     */
    public void roundBalances() {
        for (Account account : accounts) {
            BigDecimal bigDecimal = BigDecimal.valueOf(account.getBalance());
            BigDecimal roundedValue = bigDecimal.setScale(ERROR, RoundingMode.HALF_UP);

            account.setBalance(roundedValue.doubleValue());
        }
    }
}
