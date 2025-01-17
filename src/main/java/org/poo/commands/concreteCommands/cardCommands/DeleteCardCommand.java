package org.poo.commands.concreteCommands.cardCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public final class DeleteCardCommand extends Command {
    public DeleteCardCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
            Card card = getBankManager().getCardByNumber(getInput().getCardNumber());
            Account account = getBankManager().getAccountOfCard(card);
            User user = getBankManager().getUserByEmail(getInput().getEmail());

            // TODO: employees can only delete cards created by them

        try {
            // If the user is valid, destroy the card and add a transaction
            // to the user's and account's list of transactions
            if (account.getBalance() == 0  && user.hasCard(card)) {
                Transaction transaction = new Transaction.Builder()
                                              .timestamp(getInput().getTimestamp())
                                              .custom("description", "The card has been destroyed")
                                              .custom("card", getInput().getCardNumber())
                                              .custom("cardHolder", user.getEmail())
                                              .custom("account", account.getIban())
                                              .build();

                user.addTransaction(transaction);
                account.addTransaction(transaction);
                getBankManager().removeCard(card);
            }

        } catch (NullPointerException e) {
            return null;
        }

        return null;
    }
}
