package org.poo.commands.concreteCommands.paymentCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.splitPayment.SplitPayment;
import org.poo.user.User;

public final class AcceptSplitCommand extends Command {
    public AcceptSplitCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        User user = getBankManager().getUserByEmail(getInput().getEmail());
        String type = getInput().getSplitPaymentType();

        if (user == null) {
            return getErrorNode("User not found");
        }

        SplitPayment splitPayment = getBankManager().getSplitPaymentOfUser(user, type);

        try {
            splitPayment.acceptSplitPayment(user);
        } catch (NullPointerException e) {
            return null;
        }

        return null;
    }
}
