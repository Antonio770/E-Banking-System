package org.poo.commands.concreteCommands.cardCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.managers.ExchangeManager;
import org.poo.plans.Plan;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public final class CashWithdrawalCommand extends Command {
    public CashWithdrawalCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        Card card = getBankManager().getCardByNumber(getInput().getCardNumber());
        if (card == null) {
            return getErrorNode("Card not found");
        }

        Account account = getBankManager().getAccountOfCard(card);
        if (account == null) {
            return getErrorNode("Account not found");
        }

        User user = getBankManager().getUserByEmail(getInput().getEmail());
        if (user == null || getInput().getEmail().isEmpty()) {
            return getErrorNode("User not found");
        }

        if (!user.hasCard(card) && !account.getType().equals("business")) {
            return getErrorNode("Card not found");
        }

        double convertedAmount = getConvertedAmount(account);

        if (account.canPay(convertedAmount, account.getCurrency())) {
            account.spendFunds(convertedAmount);

            Transaction transaction = new Transaction.Builder()
                                    .timestamp(getInput().getTimestamp())
                                    .custom("description", "Cash withdrawal of "
                                            + getInput().getAmount())
                                    .amount(getInput().getAmount())
                                    .build();
            user.addTransaction(transaction);

            return null;
        }

        Transaction transaction = new Transaction.Builder()
                                .timestamp(getInput().getTimestamp())
                                .custom("description", "Insufficient funds")
                                .build();
        user.addTransaction(transaction);
        return null;
    }

    /**
     * Converts the amount of money to be withdrawn from RON to the account's currency
     * @param account the account from which the money will be withdrawn
     * @return the converted amount
     */
    private double getConvertedAmount(final Account account) {
        Plan plan = account.ownerOfAccount().getPlan();

        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        double feeAdded = plan.addFee(getInput().getAmount(), "RON");
        double convertedAmount = exchangeManager.getAmount("RON", account.getCurrency(), feeAdded);
        return convertedAmount;
    }
}
