package org.poo.commands.concreteCommands.userCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.user.User;

public final class PrintTransactions extends Command {
    public PrintTransactions(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        User user = getBankManager().getUserByEmail(getInput().getEmail());

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", getInput().getCommand());

        ArrayNode outputNode = objectMapper.createArrayNode();

        try {
            // Sort the transactions by timestamp
            user.sortTransactionsByTimestamp();

            // For every transaction in the user's list, call the getObjectNode
            // method to get the output node in the wanted format
            user.getTransactions().stream()
                    .filter(tr -> !tr.getStringMap().containsValue("Added funds"))
                    .forEach(tr -> outputNode.add(tr.getObjectNode()));

            objectNode.set("output", outputNode);
            objectNode.put("timestamp", getInput().getTimestamp());

        } catch (NullPointerException e) {
            return null;
        }

        return objectNode;
    }
}
