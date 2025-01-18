package org.poo.commands.concreteCommands.paymentCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.commerciant.Commerciant;
import org.poo.fileio.CommandInput;
import org.poo.managers.ExchangeManager;
import org.poo.transaction.Transaction;
import org.poo.user.User;

public final class SendMoneyCommand extends Command {
    public SendMoneyCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        try {
            User senderUser = getBankManager().getUserByEmail(getInput().getEmail());
            Account senderAccount = getBankManager().getAccount(getInput().getAccount());

            Account receiverAccount = getBankManager().getAccount(getInput().getReceiver());
            User receiverUser = getBankManager().getUserByAccount(receiverAccount);

            Commerciant comm = getBankManager().getCommerciantByIban(getInput().getReceiver());
            boolean receiverIsComm = comm != null;

            if (senderUser == null || (receiverUser == null && !receiverIsComm)) {
                return getErrorNode("User not found");
            }

            // Check if the sender has enough money to send to the receiver.
            // If it does, send the money and add a transaction for the sender
            if (senderUser.sendMoney(getInput())) {
                Transaction senderTransaction = getTransaction(senderAccount.getCurrency(),
                                                               senderAccount.getCurrency(),
                                                               "sent");

                senderUser.addTransaction(senderTransaction);
                senderAccount.addTransaction(senderTransaction);

                // If the receiver is not a commerciant,
                // also add a transaction in the receiver's account
                if (!receiverIsComm) {
                    Transaction receiverTransaction = getTransaction(senderAccount.getCurrency(),
                            receiverAccount.getCurrency(),
                            "received");

                    receiverUser.addTransaction(receiverTransaction);
                    receiverAccount.addTransaction(receiverTransaction);
                }

                return null;
            }
        } catch (NullPointerException e) {
            return null;
        }

        return null;
    }

    /**
     * Builds a transaction for the sender and receiver
     * @param from the initial currency
     * @param to the converted currency
     * @param transactionType "sent" / "received", depending on who sent and who received the money
     * @return the Transaction created
     */
    private Transaction getTransaction(final String from, final String to,
                                       final String transactionType) {
        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        double convertedAmount = exchangeManager.getAmount(from, to, getInput().getAmount());

        return new Transaction.Builder()
                              .timestamp(getInput().getTimestamp())
                              .amount(convertedAmount)
                              .custom("description", getInput().getDescription())
                              .custom("senderIBAN", getInput().getAccount())
                              .custom("receiverIBAN", getInput().getReceiver())
                              .custom("amount", convertedAmount + " " + to)
                              .custom("transferType", transactionType)
                              .custom("email", getInput().getEmail())
                              .build();
    }
}
