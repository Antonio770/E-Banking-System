package org.poo.commands.concreteCommands.paymentCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.business.BusinessAccount;
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
        Account account = getBankManager().getAccountOfCard(card);

        try {
            // If the account is a business one, check if the user is an associate
            if (account.getType().equals("business")) {
                BusinessAccount businessAccount = (BusinessAccount) account;

                if (!businessAccount.getUsers().contains(user)) {
                    return getErrorNode("Card not found");
                }

                user.sendMoney(getInput());
                return null;
            }

            // If the user and card data are correct, do the payment
            if (user.hasCard(card)) {
                user.sendMoney(getInput());
                return null;
            }

        } catch (NullPointerException e) {
            return getErrorNode("Card not found");
        }

        return getErrorNode("Card not found");
    }
}
