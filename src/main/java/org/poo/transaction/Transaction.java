package org.poo.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public final class Transaction {
    private int timestamp;
    private String description;

    private String card;
    private String cardHolder;

    private String account;
    private String accountIban;
    private String email;

    private Double amount;
    private String stringAmount;
    private String commerciant;

    private String senderIban;
    private String receiverIban;
    private String transferType;

    private String currency;
    private List<String> involvedAccounts;
    private List<Double> amountForUsers;
    private String error;

    private String newPlanType;

    private HashMap<String, String> stringMap;

    public static final class Builder {
        // Mandatory fields
        private int timestamp;
        private String description;

        // Optional fields
        private String card = null;
        private String cardHolder = null;

        private String account = null;
        private String accountIban = null;
        private String email = null;

        private Double amount = null;
        private String stringAmount = null;
        private String commerciant = null;

        private String senderIban = null;
        private String receiverIban = null;
        private String transferType = null;

        private String currency = null;
        private List<String> involvedAccounts = null;
        private List<Double> amountForUsers = null;
        private String error = null;

        private String newPlanType = null;

        private HashMap<String, String> stringMap = new HashMap<>();

        public Builder custom(final String key, final String value) {
            stringMap.put(key, value);
            return this;
        }

        /**
         * Sets the timestamp of the transaction
         * @param ts the timestamp to be set
         * @return the Builder instance
         */
        public Builder timestamp(final int ts) {
            this.timestamp = ts;
            return this;
        }

        /**
         * Sets the description of the transaction
         * @param des the description to be set
         * @return the Builder instance
         */
        public Builder description(final String des) {
            this.description = des;
            return this;
        }

        /**
         * Sets the card used for the transaction
         * @param c the card to be set
         * @return the Builder instance
         */
        public Builder card(final String c) {
            this.card = c;
            return this;
        }

        /**
         * Sets the cardholder that did the transaction
         * @param holder the cardholder to be set
         * @return the Builder instance
         */
        public Builder cardHolder(final String holder) {
            this.cardHolder = holder;
            return this;
        }

        /**
         * Sets the account used to do the transaction
         * @param acc the account to be set
         * @return the Builder instance
         */
        public Builder account(final String acc) {
            this.account = acc;
            return this;
        }

        /**
         * Sets the account used to do the transaction
         * @param acc the account to be set
         * @return the Builder instance
         */
        public Builder accountIban(final String acc) {
            this.accountIban = acc;
            return this;
        }

        /**
         * Sets the email of the person that did the transaction
         * @param e the email to be set
         * @return the Builder instance
         */
        public Builder email(final String e) {
            this.email = e;
            return this;
        }

        /**
         * Sets the amount of money sent in the transaction
         * @param amnt the amount to be set
         * @return the Builder instance
         */
        public Builder amount(final double amnt) {
            this.amount = amnt;
            return this;
        }

        /**
         * Sets the amount of money sent in the transaction
         * also containing the currency
         * @param strAmount the amount to be set
         * @return the Builder instance
         */
        public Builder stringAmount(final String strAmount) {
            this.stringAmount = strAmount;
            return this;
        }

        /**
         * Sets the commerciant involved in the transaction
         * @param trader the commerciant to be set
         * @return the Builder instance
         */
        public Builder commerciant(final String trader) {
            this.commerciant = trader;
            return this;
        }

        /**
         * Sets the IBAN of the sender
         * @param iban the senderIBAN to be set
         * @return the Builder instance
         */
        public Builder senderIban(final String iban) {
            this.senderIban = iban;
            return this;
        }

        /**
         * Sets the IBAN of the receiver
         * @param iban the receiverIBAN to be set
         * @return the Builder instance
         */
        public Builder receiverIban(final String iban) {
            this.receiverIban = iban;
            return this;
        }

        /**
         * Sets the type of transfer
         * @param type the transferType to be set
         * @return the Builder instance
         */
        public Builder transferType(final String type) {
            this.transferType = type;
            return this;
        }

        /**
         * Sets the currency used in the transaction
         * @param curr the currency to be set
         * @return the Builder instance
         */
        public Builder currency(final String curr) {
            this.currency = curr;
            return this;
        }

        /**
         * Sets the involved accounts in the transactions
         * @param accounts list of the involved accounts to be set
         * @return the Builder instance
         */
        public Builder involvedAccounts(final List<String> accounts) {
            this.involvedAccounts = accounts;
            return this;
        }

        public Builder amountForUsers(final List<Double> amounts) {
            this.amountForUsers = amounts;
            return this;
        }

        /**
         * Sets the error message of the transaction
         * @param err the error message to be set
         * @return the Builder instance
         */
        public Builder error(final String err) {
            this.error = err;
            return this;
        }

        /**
         * Sets the new plan type of the upgrade transaction
         * @param type the type of the new plan
         * @return the Builder instance
         */
        public Builder newPlanType(final String type) {
            this.newPlanType = type;
            return this;
        }

        /**
         * Builds the transaction
         * @return the transaction built
         */
        public Transaction build() {
            return new Transaction(this);
        }
    }

    private Transaction(final Builder builder) {
        this.timestamp = builder.timestamp;
        this.description = builder.description;
        this.card = builder.card;
        this.cardHolder = builder.cardHolder;
        this.account = builder.account;
        this.accountIban = builder.accountIban;
        this.email = builder.email;
        this.amount = builder.amount;
        this.stringAmount = builder.stringAmount;
        this.commerciant = builder.commerciant;
        this.senderIban = builder.senderIban;
        this.receiverIban = builder.receiverIban;
        this.transferType = builder.transferType;
        this.currency = builder.currency;
        this.involvedAccounts = builder.involvedAccounts;
        this.amountForUsers = builder.amountForUsers;
        this.error = builder.error;
        this.newPlanType = builder.newPlanType;
        this.stringMap = builder.stringMap;
    }

    /**
     * Creates an object node containing the fields of the
     * transaction that have been initialized
     * @return the object node to be written to the output JSON file
     */
    public ObjectNode getObjectNode() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode output = objectMapper.createObjectNode();

        output.put("timestamp", this.timestamp);
        output.put("description", this.description);

        if (stringMap != null && !stringMap.isEmpty()) {
            for (var entry : stringMap.entrySet()) {
                output.put(entry.getKey(), entry.getValue());
            }
        }

        if (card != null) {
            output.put("card", this.card);
        }

        if (cardHolder != null) {
            output.put("cardHolder", this.cardHolder);
        }

        if (account != null) {
            output.put("account", this.account);
        }

        if (accountIban != null) {
            output.put("accountIBAN", this.accountIban);
        }

        if (email != null) {
            output.put("email", this.email);
        }

        if (amount != null) {
            output.put("amount", this.amount);
        }

        if (stringAmount != null) {
            output.put("amount", this.stringAmount);
        }

        if (commerciant != null) {
            output.put("commerciant", this.commerciant);
        }

        if (senderIban != null) {
            output.put("senderIBAN", this.senderIban);
        }

        if (receiverIban != null) {
            output.put("receiverIBAN", this.receiverIban);
        }

        if (transferType != null) {
            output.put("transferType", this.transferType);
        }

        if (currency != null) {
            output.put("currency", this.currency);
        }

        if (involvedAccounts != null) {
            ArrayNode accountsNode = objectMapper.createArrayNode();

            for (String acc : this.involvedAccounts) {
                accountsNode.add(acc);
            }

            output.set("involvedAccounts", accountsNode);
        }

        if (amountForUsers != null) {
            ArrayNode amountsNode = objectMapper.createArrayNode();

            for (Double amount : this.amountForUsers) {
                amountsNode.add(amount);
            }

            output.set("amountForUsers", amountsNode);
        }

        if (error != null) {
            output.put("error", this.error);
        }

        if (newPlanType != null) {
            output.put("newPlanType", this.newPlanType);
        }

        return output;
    }
}
