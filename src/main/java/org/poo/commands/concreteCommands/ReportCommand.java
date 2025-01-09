package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;

public final class ReportCommand extends Command {
    public ReportCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        Account account = getBankManager().getAccount(getInput().getAccount());

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode returnNode = objectMapper.createObjectNode();
        returnNode.put("command", getInput().getCommand());

        ObjectNode outputNode = objectMapper.createObjectNode();

        try {
            // Build the output node
            outputNode.put("IBAN", account.getIban());
            outputNode.put("balance", account.getBalance());
            outputNode.put("currency", account.getCurrency());
        } catch (NullPointerException e) {
            // If the account was not found, display an error
            outputNode.put("description", "Account not found");
            outputNode.put("timestamp", getInput().getTimestamp());

            returnNode.set("output", outputNode);
            returnNode.put("timestamp", getInput().getTimestamp());
            return returnNode;
        }

        ArrayNode transactionsNode = objectMapper.createArrayNode();

        // Iterate through the list of transactions of the account and
        // call the getObjectNode() method to get the output node format
        for (Transaction transaction : account.getTransactions()) {
            if (transaction.getTimestamp() >= getInput().getStartTimestamp()
                && transaction.getTimestamp() <= getInput().getEndTimestamp()) {
                transactionsNode.add(transaction.getObjectNode());
            }
        }

        outputNode.set("transactions", transactionsNode);
        returnNode.set("output", outputNode);
        returnNode.put("timestamp", getInput().getTimestamp());

        return returnNode;
    }
}
