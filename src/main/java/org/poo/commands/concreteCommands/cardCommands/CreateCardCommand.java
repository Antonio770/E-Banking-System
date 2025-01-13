package org.poo.commands.concreteCommands.cardCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public final class CreateCardCommand extends Command {
    public CreateCardCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        try {
            Account account = getBankManager().getAccount(getInput().getAccount());
            User user = getBankManager().getUserByEmail(getInput().getEmail());

            // If the user is not the owner of the account,
            // he cannot create a card
            if (!user.hasAccount(account)) {
                return null;
            }

            // Adds the card to the account and add a transaction to both
            // the user's and the account's list of transactions
            account.addCard(getInput());
            Card card = account.getCards().getLast();

            Transaction transaction = new Transaction.Builder()
                                     .timestamp(getInput().getTimestamp())
                                     .custom("description", "New card created")
                                     .custom("email", getInput().getEmail())
                                     .custom("card", card.getCardNumber())
                                     .custom("cardHolder", user.getEmail())
                                     .custom("account", account.getIban())
                                     .build();

            user.addTransaction(transaction);
            account.addTransaction(transaction);

        } catch (NullPointerException e) {
            return null;
        }

        return null;
    }
}
