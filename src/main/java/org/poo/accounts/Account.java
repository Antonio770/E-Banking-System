package org.poo.accounts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

import org.poo.cards.Card;
import org.poo.cards.CardFactory;
import org.poo.fileio.CommandInput;
import org.poo.managers.ExchangeRateManager;
import org.poo.transaction.Transaction;
import org.poo.utils.Utils;
import org.poo.visitors.Visitable;
import org.poo.visitors.Visitor;

import java.util.ArrayList;

@Getter
@Setter
@JsonIgnoreProperties({"transactions", "minBalance", "WARNING_BALANCE"})
public abstract class Account implements Visitable {
    @JsonProperty("IBAN")
    private String iban;
    private double balance;
    private String currency;
    private String type;
    private ArrayList<Card> cards;

    private ArrayList<Transaction> transactions;
    private double minBalance;
    private static final double WARNING_BALANCE = 30;

    public Account(final CommandInput input) {
        this.iban = Utils.generateIBAN();
        this.balance = 0;
        this.currency = input.getCurrency();
        this.type = input.getAccountType();
        this.cards = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.minBalance = 0;
    }

    /**
     * Accepts a visitor
     * @param visitor the visitor that implements a behaviour
     * @return true if the visitor can do the operations, false otherwise
     */
    public abstract boolean accept(Visitor visitor);

    /**
     * Adds money to the account
     * @param amount the amount of money added
     */
    public void addFunds(final double amount) {
        this.balance += amount;
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
        } else if (balance - minBalance <= WARNING_BALANCE) {
            card.setStatus("warning");
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
        ExchangeRateManager exchangeRateManager = ExchangeRateManager.getInstance();
        double conversionRate = exchangeRateManager.getConversionRate(from, this.currency);
        double convertedAmount = amount * conversionRate;

        return this.balance >= convertedAmount;
    }
}
