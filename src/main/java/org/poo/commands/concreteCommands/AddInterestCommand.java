package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.visitors.AddInterestVisitor;
import org.poo.visitors.Visitor;

public final class AddInterestCommand extends Command {
    public AddInterestCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        Account account = getBankManager().getAccount(getInput().getAccount());
        Visitor visitor = new AddInterestVisitor();

        // Visit the account using the AddInterestVisitor. If the accept method
        // returns false, the account was not a savings one and an error is
        // printed to the JSON file by calling notSavingsAccount().
        if (account.accept(visitor)) {
            return null;
        }

        return notSavingsAccount();
    }

    private ObjectNode notSavingsAccount() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode returnNode = objectMapper.createObjectNode();
        ObjectNode outputNode = objectMapper.createObjectNode();

        outputNode.put("description", "This is not a savings account");
        outputNode.put("timestamp", getInput().getTimestamp());

        returnNode.put("command", "addInterest");
        returnNode.set("output", outputNode);
        returnNode.put("timestamp", getInput().getTimestamp());
        return returnNode;
    }
}
