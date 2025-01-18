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
    private Double amount;
    private List<String> involvedAccounts;
    private List<Double> amountForUsers;
    private HashMap<String, String> stringMap;

    public static final class Builder {
        private int timestamp;
        private Double amount = null;
        private List<String> involvedAccounts = null;
        private List<Double> amountForUsers = null;
        private final HashMap<String, String> stringMap = new HashMap<>();

        /**
         * Adds a custom field to the transaction
         * @param key a string representing the key
         * @param value a string representing the value
         * @return the Builder instance
         */
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
         * Sets the amount of money sent in the transaction
         * @param amnt the amount to be set
         * @return the Builder instance
         */
        public Builder amount(final double amnt) {
            this.amount = amnt;
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

        /**
         * Sets the amount of money that needs to be paid by every user in a split payment
         * @param amounts the list of amounts
         * @return the Builder instance
         */
        public Builder amountForUsers(final List<Double> amounts) {
            this.amountForUsers = amounts;
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
        this.amount = builder.amount;
        this.involvedAccounts = builder.involvedAccounts;
        this.amountForUsers = builder.amountForUsers;
        this.stringMap = builder.stringMap;
    }

    /**
     * Creates an object node containing the fields of the
     * transaction that have been initialized
     * @return the object node to be written to the output JSON file
     */
    public ObjectNode getObjectNode() {
        if (stringMap.containsValue("Added funds")) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode output = objectMapper.createObjectNode();

        output.put("timestamp", this.timestamp);

        if (stringMap != null && !stringMap.isEmpty()) {
            stringMap.entrySet().stream()
                     .filter(entry -> !entry.getKey().equals("email"))
                     .forEach(entry -> output.put(entry.getKey(), entry.getValue()));
        }

        // Only put the double amount filed to output node if there is no string amount field
        if (amount != null && (stringMap == null || !stringMap.containsKey("amount"))) {
            output.put("amount", this.amount);
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

            for (Double amnt : this.amountForUsers) {
                amountsNode.add(amnt);
            }

            output.set("amountForUsers", amountsNode);
        }

        return output;
    }
}
