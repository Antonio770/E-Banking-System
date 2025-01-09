package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
            // If the card is not owned by the user, print an error to the output file
            if (!user.hasCard(card)) {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put("command", getInput().getCommand());

                ObjectNode outputNode = objectMapper.createObjectNode();
                outputNode.put("timestamp", getInput().getTimestamp());
                outputNode.put("description", "Card not found");

                objectNode.set("output", outputNode);
                objectNode.put("timestamp", getInput().getTimestamp());
                return objectNode;
            }

            // If the user and card data are correct, do the payment
            user.sendMoney(getInput());

        } catch (NullPointerException e) {
            return null;
        }

        return null;
    }
}
