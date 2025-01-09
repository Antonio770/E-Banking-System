package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;
import org.poo.visitors.ChangeInterestVisitor;
import org.poo.visitors.Visitor;

public final class ChangeInterestRateCommand extends Command {
    public ChangeInterestRateCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        Account account = getBankManager().getAccount(getInput().getAccount());
        Visitor visitor = new ChangeInterestVisitor(getInput().getInterestRate());

        // Visit the account using the ChangeInterestVisitor. If the accept method
        // returns false, the account was not a savings one and an error is
        // printed to the JSON file by calling notSavingsAccount().
        if (account.accept(visitor)) {
            Transaction transaction = new Transaction.Builder()
                                          .timestamp(getInput().getTimestamp())
                                          .description("Interest rate of the account changed to "
                                                       + getInput().getInterestRate())
                                          .build();

            User user = getBankManager().getUserByAccount(account);
            user.addTransaction(transaction);
            account.addTransaction(transaction);
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

        returnNode.put("command", "changeInterestRate");
        returnNode.set("output", outputNode);
        returnNode.put("timestamp", getInput().getTimestamp());
        return returnNode;
    }
}
