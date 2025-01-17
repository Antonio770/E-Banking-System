package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.managers.BankManager;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;

@Getter
@Setter
public abstract class Command {
    private final BankManager bankManager;
    private final CommandInput input;

    public Command(final CommandInput input) {
        bankManager = BankManager.getInstance();
        this.input = input;
    }

    /**
     * Executes a command
     * @return The object node to be written to the output
     */
    public abstract ObjectNode execute();

    public ObjectNode getErrorNode(final String description) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", getInput().getCommand());

        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("timestamp", getInput().getTimestamp());
        outputNode.put("description", description);

        objectNode.set("output", outputNode);
        objectNode.put("timestamp", getInput().getTimestamp());
        return objectNode;
    }

    public Transaction getSimpleTransaction(final int timestamp, final String description) {
        return new Transaction.Builder().timestamp(timestamp)
                                        .custom("description", description)
                                        .build();
    }
}
