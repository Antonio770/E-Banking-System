package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.managers.ExchangeManager;
import org.poo.planStrategies.Plan;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public class UpgradePlanCommand extends Command {
    public UpgradePlanCommand(CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        Account account = getBankManager().getAccount(getInput().getAccount());
        User user = getBankManager().getUserByAccount(account);

        if (user == null || account == null) {
            System.out.println("Account not found");
        }

        String newPlan = getInput().getNewPlanType();
        double upgradePrice = user.getPlan().getUpgradePrice(newPlan);

        if (upgradePrice == -1) {
            System.out.println("You cannot downgrade your plan");
        }

        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        double convPrice = exchangeManager.getAmount("RON", account.getCurrency(), upgradePrice);

        if (account.canPay(upgradePrice, "RON")) {
            account.spendFunds(convPrice);
            user.setPlan(user.getPlan().upgradeTo(newPlan));

            Transaction transaction = new Transaction.Builder()
                                    .accountIban(getInput().getAccount())
                                    .description("Upgrade plan")
                                    .newPlanType(newPlan)
                                    .timestamp(getInput().getTimestamp())
                                    .build();

            user.addTransaction(transaction);
        }

        return null;
    }
}
