package org.poo.commands.concreteCommands.accountCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public final class WithdrawSavingsCommand extends Command {
    public WithdrawSavingsCommand(CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        Account account = getBankManager().getAccount(getInput().getAccount());
        User user = getBankManager().getUserByAccount(account);

        if (user == null || account == null) {
            return null;
        }

        if (user.getAge() < 21) {
            Transaction transaction = getTransaction("You don't have the minimum age required.");
            user.addTransaction(transaction);
            return null;
        }

        Account classicAccount = user.getFirstClassicAccount();
        if (classicAccount == null) {
            Transaction transaction = getTransaction("You do not have a classic account.");
            user.addTransaction(transaction);
            return null;
        }

        account.spendFunds(getInput().getAmount());
        classicAccount.addFunds(getInput().getAmount());

        return null;
    }

    private Transaction getTransaction(final String description) {
        return new Transaction.Builder().timestamp(getInput().getTimestamp())
                                        .custom("description", description)
                                        .build();
    }
}
