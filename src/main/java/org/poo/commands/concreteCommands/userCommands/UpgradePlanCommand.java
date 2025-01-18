package org.poo.commands.concreteCommands.userCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.managers.ExchangeManager;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public final class UpgradePlanCommand extends Command {
    public UpgradePlanCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        Account account = getBankManager().getAccount(getInput().getAccount());

        if (account == null) {
            return getErrorNode("Account not found");
        }

        User user = account.ownerOfAccount();

        String newPlan = getInput().getNewPlanType();
        double upgradePrice = user.getPlan().getUpgradePrice(newPlan);

        if (user.getPlan().getType().equals(newPlan)) {
            Transaction transaction = new Transaction.Builder()
                    .timestamp(getInput().getTimestamp())
                    .custom("description", "The user already has the " + newPlan + " plan.")
                    .build();

            user.addTransaction(transaction);
            account.addTransaction(transaction);
            return null;
        }

        // You cannot downgrade your plan
        if (upgradePrice == -1) {
            return null;
        }

        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        double convPrice = exchangeManager.getAmount("RON", account.getCurrency(), upgradePrice);

        if (account.canPay(upgradePrice, "RON")) {
            account.spendFunds(convPrice);
            user.setPlan(user.getPlan().upgradeTo(newPlan));

            Transaction transaction = new Transaction.Builder()
                                    .timestamp(getInput().getTimestamp())
                                    .custom("description", "Upgrade plan")
                                    .custom("accountIBAN", getInput().getAccount())
                                    .custom("newPlanType", newPlan)
                                    .build();

            user.addTransaction(transaction);
            account.addTransaction(transaction);
            return null;
        }

        user.addTransaction(new Transaction.Builder()
                            .timestamp(getInput().getTimestamp())
                            .custom("description", "Insufficient funds")
                            .build());

        return null;
    }
}
