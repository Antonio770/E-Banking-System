package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;

public final class AddFundsCommand extends Command {
    public AddFundsCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        try {
            Account account = getBankManager().getAccount(getInput().getAccount());
            account.addFunds(getInput().getAmount());
        } catch (NullPointerException e) {
            return null;
        }

        return null;
    }
}
