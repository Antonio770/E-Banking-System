package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;
import org.poo.visitors.AddInterestVisitor;
import org.poo.visitors.Visitor;

public final class AddInterestCommand extends Command {
    public AddInterestCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        Account account = getBankManager().getAccount(getInput().getAccount());
        User user = getBankManager().getUserByAccount(account);
        Visitor visitor = new AddInterestVisitor();

        if (account == null || user == null) {
            return null;
        }

        double interestAmount = account.accept(visitor);
        if (interestAmount > 0) {
            account.addFunds(interestAmount);

            Transaction transaction = new Transaction.Builder()
                                    .timestamp(getInput().getTimestamp())
                                    .custom("description", "Interest rate income")
                                    .amount(interestAmount)
                                    .custom("currency", account.getCurrency())
                                    .build();
            user.addTransaction(transaction);
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
