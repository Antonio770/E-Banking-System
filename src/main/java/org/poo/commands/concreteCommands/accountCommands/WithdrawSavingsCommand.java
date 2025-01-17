package org.poo.commands.concreteCommands.accountCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.managers.ExchangeManager;
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
            Transaction transaction = getSimpleTransaction(getInput().getTimestamp(),
                            "You don't have the minimum age required.");
            user.addTransaction(transaction);
            account.addTransaction(transaction);
            return null;
        }

        Account classicAccount = user.getFirstClassicAccount(getInput().getCurrency());
        if (classicAccount == null) {
            Transaction transaction = getSimpleTransaction(getInput().getTimestamp(),
                            "You do not have a classic account.");
            user.addTransaction(transaction);
            account.addTransaction(transaction);
            return null;
        }

        if (account.canPay(getInput().getAmount(), getInput().getCurrency())) {
            account.spendFunds(getInput().getAmount());
            classicAccount.addFunds(getInput().getAmount());

            user.addTransaction(getTransaction(classicAccount.getIban()));
            user.addTransaction(getTransaction(classicAccount.getIban()));
            account.addTransaction(getTransaction(classicAccount.getIban()));
        }

        return null;
    }

    private Transaction getTransaction(final String classic) {
        return new Transaction.Builder()
                    .timestamp(getInput().getTimestamp())
                    .amount(getInput().getAmount())
                    .custom("description", "Savings withdrawal")
                    .custom("classicAccountIBAN", classic)
                    .custom("savingsAccountIBAN", getInput().getAccount())
                    .build();
    }
}
