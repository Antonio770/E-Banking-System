package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public class WithdrawSavingsCommand extends Command {
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
            Transaction transaction = new Transaction.Builder()
                                        .timestamp(getInput().getTimestamp())
                                        .description("You don't have the minimum age required.")
                                        .build();

            user.addTransaction(transaction);
            return null;
        }

        return null;
    }
}
