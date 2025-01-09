package org.poo.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.managers.BankManager;
import org.poo.fileio.CommandInput;

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
}
