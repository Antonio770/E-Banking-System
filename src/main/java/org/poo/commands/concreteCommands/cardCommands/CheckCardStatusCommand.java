package org.poo.commands.concreteCommands.cardCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public final class CheckCardStatusCommand extends Command {
    public CheckCardStatusCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", getInput().getCommand());

        Card card = getBankManager().getCardByNumber(getInput().getCardNumber());

        // If the card was not found, print an error to the output
        if (card == null) {
            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("timestamp", getInput().getTimestamp());
            outputNode.put("description", "Card not found");
            objectNode.set("output", outputNode);

            objectNode.put("timestamp", getInput().getTimestamp());

            return objectNode;
        }

        // If the card is frozen, there is no need to check again for the status
        if (card.getStatus().equals("frozen")) {
            return null;
        }

        // Checks and updates the status of the card
        User user = getBankManager().getUserByCard(card);
        Account account = getBankManager().getAccountOfCard(card);

        account.updateStatus(card);
        String status = card.getStatus();

        // If the card becomes frozen, print a warning to the output file
        if (status.equals("frozen")) {
            Transaction transaction = new Transaction.Builder()
                                          .timestamp(getInput().getTimestamp())
                                          .custom("description", "You have reached the minimum" +
                                                  "amount of funds, the card will be frozen")
                                          .build();
            user.addTransaction(transaction);
            account.addTransaction(transaction);
        }

        return null;
    }
}
