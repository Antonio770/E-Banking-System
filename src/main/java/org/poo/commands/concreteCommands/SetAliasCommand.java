package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.user.User;

public final class SetAliasCommand extends Command {
    public SetAliasCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        User user = getBankManager().getUserByEmail(getInput().getEmail());
        String iban = getInput().getAccount();

        try {
            user.setAlias(getInput().getAlias(), iban);
        } catch (NullPointerException e) {
            return null;
        }

        return null;
    }
}
