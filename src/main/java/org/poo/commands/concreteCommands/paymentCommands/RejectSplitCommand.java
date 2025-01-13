package org.poo.commands.concreteCommands.paymentCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;

public final class RejectSplitCommand extends Command {
    public RejectSplitCommand(CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        return null;
    }
}
