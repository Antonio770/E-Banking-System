package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ObjectInput;

public final class CommandInvoker {
    private final CommandFactory commandFactory = CommandFactory.getInstance();
    private ArrayNode output;

    public CommandInvoker(final ArrayNode output) {
        this.output = output;
    }

    /**
     * Calls the execute() method for every command read from input
     * @param input the input information
     */
    public void processCommands(final ObjectInput input) {
        for (CommandInput commandInput : input.getCommands()) {
            Command command = commandFactory.create(commandInput);

            if (commandInput.getAccount() != null && commandInput.getAccount().equals("RO90POOB5450777208072365")) {
                System.out.println();
            }

            if (commandInput.getCardNumber() != null && commandInput.getCardNumber().equals("9281102140265301")) {
                System.out.println();
            }

            if (commandInput.getTimestamp() == 563) {
                System.out.println();
            }

            try {
                ObjectNode objectNode = command.execute();
                if (objectNode != null) {
                    output.add(objectNode);
                }
            } catch (NullPointerException e) {
                continue;
            }
        }
    }
}
