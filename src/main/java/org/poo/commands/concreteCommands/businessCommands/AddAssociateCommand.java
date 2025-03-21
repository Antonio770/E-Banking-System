package org.poo.commands.concreteCommands.businessCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.business.BusinessAccount;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.user.User;

public final class AddAssociateCommand extends Command {
    public AddAssociateCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        User user = getBankManager().getUserByEmail(getInput().getEmail());
        Account account = getBankManager().getAccount(getInput().getAccount());
        String role = getInput().getRole();

        if (account != null && account.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) account;

            if (businessAccount.getUsers().contains(user)) {
                return null;
            }

            businessAccount.addAssociate(user, role);
        }

        return null;
    }
}
