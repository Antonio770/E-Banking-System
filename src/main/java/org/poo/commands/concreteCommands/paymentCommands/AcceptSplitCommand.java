package org.poo.commands.concreteCommands.paymentCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.splitPayments.SplitPayment;
import org.poo.user.User;

public final class AcceptSplitCommand extends Command {
    public AcceptSplitCommand(CommandInput input) {
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

        if (splitPayment == null) {
            // TODO: add error
            return null;
        }

        splitPayment.acceptSplitPayment(user);

        return null;
    }
}
