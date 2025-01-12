package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public final class AddAccountCommand extends Command {
    public AddAccountCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        Transaction transaction = new Transaction.Builder()
                                      .timestamp(getInput().getTimestamp())
                                      .custom("description", "New account created")
                                      .build();
        try {
            User user = getBankManager().getUserByEmail(getInput().getEmail());
            user.addAccount(getInput());

            Account account = user.getAccounts().getLast();

            user.addTransaction(transaction);
            account.addTransaction(transaction);
        } catch (NullPointerException e) {
            return null;
        }

        return null;
    }
}
