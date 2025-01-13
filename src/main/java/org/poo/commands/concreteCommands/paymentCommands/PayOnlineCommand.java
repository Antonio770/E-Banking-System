package org.poo.commands.concreteCommands.paymentCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.user.User;

public final class PayOnlineCommand extends Command {
    public PayOnlineCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        User user = getBankManager().getUserByEmail(getInput().getEmail());
        Card card = getBankManager().getCardByNumber(getInput().getCardNumber());

        try {
            Account account = user.getAccountOfCard(card);

            // If the user and card data are correct, do the payment
            if (user.hasCard(card) || account.getType().equals("business")) {
                user.sendMoney(getInput());
                return null;
            }

        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }

        return getErrorNode("Card not found");
    }
}
