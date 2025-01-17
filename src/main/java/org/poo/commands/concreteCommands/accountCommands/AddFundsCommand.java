package org.poo.commands.concreteCommands.accountCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.business.BusinessAccount;
import org.poo.accounts.business.BusinessRoles;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public final class AddFundsCommand extends Command {
    public AddFundsCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        Account account = getBankManager().getAccount(getInput().getAccount());

        if (!canAddFunds(account)) {
            return null;
        }

        try {
            account.addFunds(getInput().getAmount());

            Transaction transaction = new Transaction.Builder()
                                                     .timestamp(getInput().getTimestamp())
                                                     .amount(getInput().getAmount())
                                                     .custom("description", "Added funds")
                                                     .custom("email", getInput().getEmail())
                                                     .build();
            account.addTransaction(transaction);
        } catch (NullPointerException e) {
            return null;
        }

        return null;
    }

    private boolean canAddFunds(Account account) {
        User user = getBankManager().getUserByEmail(getInput().getEmail());

        if (account == null) {
            return false;
        }

        if (account.getType().equals("business")) {
            BusinessAccount businessAccount = (BusinessAccount) account;

            if (!businessAccount.getUsers().contains(user)) {
                return false;
            }

            return !(businessAccount.getRole(user) == BusinessRoles.EMPLOYEE
                   && getInput().getAmount() > businessAccount.getDepositLimit());
        }

        return true;
    }
}
