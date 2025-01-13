package org.poo.commands.concreteCommands.businessCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.business.BusinessAccount;
import org.poo.accounts.business.BusinessRoles;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.user.User;

public class ChangeLimitCommand extends Command {
    public ChangeLimitCommand(CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        User user = getBankManager().getUserByEmail(getInput().getEmail());
        Account account = getBankManager().getAccount(getInput().getAccount());
        double amount = getInput().getAmount();

        if (account != null && account.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) account;

            if (businessAccount.getRole(user) == BusinessRoles.OWNER) {
                switch (getInput().getCommand()) {
                    case "changeSpendinglimit":
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

            // TODO: you are not authorized to make this transaction
        }

        return null;
    }
}
