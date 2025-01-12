package org.poo.commands.concreteCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
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

            if (senderUser == null || receiverUser == null) {
                return getErrorNode("User not found");
            }

            // Check if the sender has enough money to send to the receiver.
            // If it does, send the money and add a transaction for
            // both the sender and the receiver.
            if (senderUser.sendMoney(getInput())) {
                Transaction senderTransaction = getTransaction(senderAccount.getCurrency(),
                                                               senderAccount.getCurrency(),
                                                               "sent");

                senderUser.addTransaction(senderTransaction);
                senderAccount.addTransaction(senderTransaction);

                Transaction receiverTransaction = getTransaction(senderAccount.getCurrency(),
                                                                 receiverAccount.getCurrency(),
                                                                 "received");

                receiverUser.addTransaction(receiverTransaction);
                receiverAccount.addTransaction(receiverTransaction);

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
        double conversionRate = exchangeManager.getConversionRate(from, to);
        double convertedAmount = conversionRate * getInput().getAmount();

        return new Transaction.Builder()
                              .timestamp(getInput().getTimestamp())
                              .custom("description", getInput().getDescription())
                              .custom("senderIBAN", getInput().getAccount())
                              .custom("receiverIBAN", getInput().getReceiver())
                              .custom("amount", convertedAmount + " " + to)
                              .custom("transferType", transactionType)
                              .build();
    }
}
