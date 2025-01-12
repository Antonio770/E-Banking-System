package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.managers.BankManager;
import org.poo.managers.ExchangeManager;
import org.poo.splitPayments.SplitPayment;
import org.poo.transaction.Transaction;
import org.poo.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class SplitPaymentCommand extends Command {
    public SplitPaymentCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        // If the split payment type is "equal", initialize the amountForUser list
        // to contain equal amounts for each user
        if (getInput().getSplitPaymentType().equals("equal")) {
            getInput().setAmountForUsers(initAmountPerUser());
        }

        // Create the split payment and add it to the bank manager
        getBankManager().getSplitPayments().add(new SplitPayment(getInput()));

        return null;
    }

    private List<Double> initAmountPerUser() {
        int nrAccounts = getInput().getAccounts().size();
        double amountPerUser = getInput().getAmount() / nrAccounts;
        return new ArrayList<>(Collections.nCopies(nrAccounts, amountPerUser));
    }


}
