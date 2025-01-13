package org.poo.commands.concreteCommands.accountCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;

public final class SetMinBalanceCommand extends Command {
    public SetMinBalanceCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        try {
            Account account = getBankManager().getAccount(getInput().getAccount());
            account.setMinBalance(getInput().getMinBalance());
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }


        return null;
    }
}
