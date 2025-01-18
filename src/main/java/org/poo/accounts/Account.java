package org.poo.accounts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

import org.poo.cards.Card;
import org.poo.cards.CardFactory;
import org.poo.commerciant.Commerciant;
import org.poo.fileio.CommandInput;
import org.poo.managers.BankManager;
import org.poo.managers.ExchangeManager;
import org.poo.transaction.Transaction;
import org.poo.user.User;
import org.poo.utils.Utils;
import org.poo.visitors.Visitable;
import org.poo.visitors.Visitor;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@JsonIgnoreProperties({"transactions", "minBalance", "WARNING_BALANCE",
                       "discounts", "transactionsMade", "totalSpent"})
public abstract class Account implements Visitable {
    @JsonProperty("IBAN")
    private String iban;
    private double balance;
    private String currency;
    private String type;
    private ArrayList<Card> cards;

    private ArrayList<Transaction> transactions;
    private double minBalance;
    private static final double UPGRADE_TRANSACTION_AMOUNT = 300;
    private static final int TRANSACTIONS_REQUIRED = 5;

    private HashMap<String, Boolean> discounts;
    private HashMap<Commerciant, Integer> transactionsMade;
    private double totalSpent;

    public Account(final CommandInput input) {
        this.iban = Utils.generateIBAN();
        this.balance = 0;
        this.currency = input.getCurrency();
        this.type = input.getAccountType();
        this.cards = new ArrayList<Card>();
        this.transactions = new ArrayList<Transaction>();
        this.minBalance = 0;
        this.discounts = new HashMap<String, Boolean>();
        this.transactionsMade = new HashMap<Commerciant, Integer>();
        this.totalSpent = 0;
    }

    /**
     * Accepts a visitor
     * @param visitor the visitor to be accepted
     * @return 0 if the account is not a savings account
     * or the amount of interest if the account is a savings one
     */
    public abstract double accept(Visitor visitor);

    /**
     * Adds money to the account
     * @param amount the amount of money added
     */
    public void addFunds(final double amount) {
        this.balance += amount;
    }

    /**
     * Subtracts money from the account
     * @param amount the amount of money spent
     */
    public void spendFunds(final double amount) {
        this.balance -= amount;
    }

    /**
     * Adds money to the total amount spent on "spendigsThreshold" commerciants
     * @param amount the amount of money spent
     */
    public void addTotalSpent(final double amount) {
        this.totalSpent += amount;
    }

    /**
     * Increments the number of transactions made to a specific commerciant
     * @param commerciant the commerciant to which the transaction was made
     */
    public void addTransactionToCommerciant(final Commerciant commerciant) {
        int current = transactionsMade.getOrDefault(commerciant, 0);
        transactionsMade.put(commerciant, ++current);
    }

    /**
     * Checks if a user made enough transactions to automatically upgrade from silver plan to gold
     * @param amount the amount of money spent in the current transaction
     * @return true if the plan was upgraded to gold, false otherwise
     */
    public boolean checkUpgradeTransaction(final double amount) {
        User user = this.ownerOfAccount();

        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        double ronAmount = exchangeManager.getAmount(currency, "RON", amount);

        // Only start to count the upgrade transactions if the plan is "silver"
        if (ronAmount >= UPGRADE_TRANSACTION_AMOUNT && user != null
            && user.getPlan().getType().equals("silver")) {
            user.incrementUpgradeTransactions();

            if (user.getUpgradeTransactions() == TRANSACTIONS_REQUIRED) {
                user.setPlan(user.getPlan().upgradeTo("gold"));
                return true;
            }
        }

        return false;
    }

    /**
     * Adds a transaction to the account's history
     * @param transaction the transaction to be added
     */
    public void addTransaction(final Transaction transaction) {
        this.transactions.add(transaction);
    }

    /**
     * Adds a card to the account
     * @param input the information of the card
     */
    public void addCard(final CommandInput input) {
        CardFactory cardFactory = CardFactory.getInstance();
        this.cards.add(cardFactory.create(input));
    }

    /**
     * Removes a card from the account
     * @param card the card to be removed
     */
    public void removeCard(final Card card) {
        cards.remove(card);
    }

    /**
     * Updates the status of a card
     * @param card the card that needs to be checked
     */
    public void updateStatus(final Card card) {
        if (balance <= minBalance) {
            card.setStatus("frozen");
        } else {
            card.setStatus("active");
        }
    }

    /**
     * Checks if the account has enough money to pay a certain amount of money
     * @param amount the amount of money to be paid
     * @param from the currency of the initial amount
     * @return true if the account can pay, false otherwise
     */
    public boolean canPay(final double amount, final String from) {
        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        double convertedAmount = exchangeManager.getAmount(from, currency, amount);
        return this.balance >= convertedAmount;
    }

    /**
     * @return the owner of the account.
     */
    public User ownerOfAccount() {
        BankManager bankManager = BankManager.getInstance();
        return bankManager.getUserByAccount(this);
    }
}
