package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public final class DeleteAccountCommand extends Command {
    public DeleteAccountCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", getInput().getCommand());

        boolean result = getBankManager().removeAccount(getInput().getAccount());
        ObjectNode output = objectMapper.createObjectNode();

        if (result) {
            output.put("success", "Account deleted");
        } else {
            output.put("error",
                    "Account couldn't be deleted - see org.poo.transactions for details");

            Account account = getBankManager().getAccount(getInput().getAccount());
            User user = getBankManager().getUserByAccount(account);

            Transaction transaction = new Transaction.Builder()
                                          .timestamp(getInput().getTimestamp())
                                          .description("Account couldn't be deleted "
                                                       + "- there are funds remaining")
                                          .build();

            user.addTransaction(transaction);
            account.addTransaction(transaction);
        }

        output.put("timestamp", getInput().getTimestamp());

        objectNode.put("timestamp", getInput().getTimestamp());
        objectNode.set("output", output);
        return objectNode;
    }
}
