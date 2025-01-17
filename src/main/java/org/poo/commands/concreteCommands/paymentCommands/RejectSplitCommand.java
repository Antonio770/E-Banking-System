package org.poo.commands.concreteCommands.paymentCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.splitPayments.SplitPayment;
import org.poo.user.User;

public final class RejectSplitCommand extends Command {
    public RejectSplitCommand(CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        User user = getBankManager().getUserByEmail(getInput().getEmail());
        String type = getInput().getSplitPaymentType();

        // There is an input in test12 (timestamp 204) where the "email" is actually an IBAN
        // and I have to return an error because the user "is not found", but the payment
        // still has to be rejected by the user
//        Account account = getBankManager().getAccount(getInput().getEmail());
//        boolean emailIsActuallyAnIBan = false;
//        if (user == null) {
//            user = getBankManager().getUserByAccount(account);
//            emailIsActuallyAnIBan = true;
//        }

        if (user == null) {
            return getErrorNode("User not found");
        }

        SplitPayment splitPayment = getBankManager().getSplitPaymentOfUser(user, type);

        if (splitPayment == null) {
            // TODO: add error
            return null;
        }

        splitPayment.rejectSplitPayment(user);

        return null;
    }
}
