package org.poo.commands.concreteCommands.userCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;

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
        int start = getInput().getStartTimestamp();
        int end = getInput().getEndTimestamp();

        account.getTransactions()
               .stream()
               .filter(tr -> tr.getTimestamp() >= start)
               .filter(tr -> tr.getTimestamp() <= end)
               .filter(tr -> !tr.getStringMap().containsValue("Added funds"))
               .forEach(tr -> transactionsNode.add(tr.getObjectNode()));


        outputNode.set("transactions", transactionsNode);
        returnNode.set("output", outputNode);
        returnNode.put("timestamp", getInput().getTimestamp());

        return returnNode;
    }
}
