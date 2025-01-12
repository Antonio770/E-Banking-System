package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.managers.ExchangeManager;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public final class CashWithdrawalCommand extends Command {
    public CashWithdrawalCommand(CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        Card card = getBankManager().getCardByNumber(getInput().getCardNumber());
        if (card == null) {
            return getErrorNode("Card not found");
        }

        User user = getBankManager().getUserByCard(card);
        if (user == null) {
            return getErrorNode("User not found");
        }

        Account account = user.getAccountOfCard(card);
        if (account == null) {
            return getErrorNode("Account not found");
        }

        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        double feeAdded = user.getPlan().addFee(getInput().getAmount(), "RON");
        double convertedAmount = exchangeManager.getAmount("RON", account.getCurrency(), feeAdded);

        if (account.getBalance() >= convertedAmount) {
            account.spendFunds(convertedAmount);

            Transaction transaction = new Transaction.Builder()
                                    .timestamp(getInput().getTimestamp())
                                    .custom("description", "Cash withdrawal of "
                                            + getInput().getAmount())
                                    .amount(getInput().getAmount())
                                    .build();
            user.addTransaction(transaction);
        } else {
            Transaction transaction = new Transaction.Builder()
                                    .timestamp(getInput().getTimestamp())
                                    .custom("description", "Insufficient funds")
                                    .build();
            user.addTransaction(transaction);
        }

        return null;
    }
}
