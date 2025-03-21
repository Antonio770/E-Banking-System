package org.poo.commands.concreteCommands.accountCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class SpendingsReportCommand extends Command {
    public SpendingsReportCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        Account account = getBankManager().getAccount(getInput().getAccount());

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode returnNode = objectMapper.createObjectNode();
        ObjectNode outputNode = objectMapper.createObjectNode();

        returnNode.put("command", "spendingsReport");

        try {
            // If the account is a savings account, print an error message
            if (account.getType().equals("savings")) {
                outputNode.put("error",
                               "This kind of report is not supported for a saving account");
                returnNode.set("output", outputNode);
                returnNode.put("timestamp", getInput().getTimestamp());
                return returnNode;
            }

            // If the account is not a savings one, the spending record can be generated
            outputNode.put("IBAN", account.getIban());
            outputNode.put("balance", account.getBalance());
            outputNode.put("currency", account.getCurrency());
        } catch (NullPointerException e) {

            // If the account is not found, print an error message
            if (account == null) {
                outputNode.put("description", "Account not found");
                outputNode.put("timestamp", getInput().getTimestamp());

                returnNode.set("output", outputNode);
                returnNode.put("timestamp", getInput().getTimestamp());
                return returnNode;
            }
        }

        // Create two array nodes for the transactions and commerciants
        // to add them to the output node
        ArrayNode transactionsNode = objectMapper.createArrayNode();
        ArrayNode commerciantsNode = objectMapper.createArrayNode();

        // Create a TreeMap to store the commerciants in alphabetical order
        Map<String, Double> commerciants = new TreeMap<String, Double>();

        // Filter the transactions to only contain the ones that happened
        // in the specified interval and are of type "Card payment"
        int start = getInput().getStartTimestamp();
        int end = getInput().getEndTimestamp();
        ArrayList<Transaction> transactions = getTransactionsInInterval(account, start, end);

        for (Transaction transaction : transactions) {
            // Don't add the "AddFunds" command to the transactions output
            if (transaction.getStringMap().get("description").equals("Added funds")) {
                continue;
            }

            transactionsNode.add(transaction.getObjectNode());
            String commerciant = transaction.getStringMap().get("commerciant");
            double amount = transaction.getAmount();

            // If the commerciant is not in the TreeMap, add it.
            // If it's already in the TreeMap, update the total money sent to that commerciant
            double total = commerciants.getOrDefault(commerciant, 0.0);
            commerciants.put(commerciant, total + amount);
        }

        // For every commerciant, add the name and
        // the total money spent to the output node
        for (String commerciant : commerciants.keySet()) {
            ObjectNode commerciantNode = objectMapper.createObjectNode();
            commerciantNode.put("commerciant", commerciant);
            commerciantNode.put("total", commerciants.get(commerciant));

            commerciantsNode.add(commerciantNode);
        }

        outputNode.set("transactions", transactionsNode);
        outputNode.set("commerciants", commerciantsNode);
        returnNode.set("output", outputNode);
        returnNode.put("timestamp", getInput().getTimestamp());

        return returnNode;
    }

    private ArrayList<Transaction> getTransactionsInInterval(final Account account,
                                                             final int start, final int end) {
        return account.getTransactions().stream()
                .filter(transaction -> transaction.getTimestamp() >= start)
                .filter(transaction -> transaction.getTimestamp() <= end)
                .filter(tr -> tr.getStringMap().get("description").equals("Card payment"))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
