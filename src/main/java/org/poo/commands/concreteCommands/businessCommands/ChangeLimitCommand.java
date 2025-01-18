package org.poo.commands.concreteCommands.businessCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.business.BusinessAccount;
import org.poo.accounts.business.BusinessRoles;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.user.User;

public final class ChangeLimitCommand extends Command {
    public ChangeLimitCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        User user = getBankManager().getUserByEmail(getInput().getEmail());
        Account account = getBankManager().getAccount(getInput().getAccount());
        double amount = getInput().getAmount();

        // Account not of type "business"
        if (account == null || !account.getType().equals("business")) {
            return getErrorNode("This is not a business account");
        }

        BusinessAccount businessAccount = (BusinessAccount) account;

        // The user doesn't have permission to change the limit
        if (businessAccount.getRole(user) != BusinessRoles.OWNER) {
            if (getInput().getCommand().equals("changeDepositLimit")) {
                return getErrorNode("You must be owner in order to change deposit limit.");
            } else {
                return getErrorNode("You must be owner in order to change spending limit.");
            }
        }

        switch (getInput().getCommand()) {
            case "changeSpendingLimit":
                businessAccount.setSpendingLimit(amount);
                break;
            case "changeDepositLimit":
                businessAccount.setDepositLimit(amount);
                break;
            default:
                break;
        }

        return null;
    }
}
