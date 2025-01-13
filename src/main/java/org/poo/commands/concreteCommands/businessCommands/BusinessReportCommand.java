package org.poo.commands.concreteCommands.businessCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.business.BusinessAccount;
import org.poo.accounts.business.BusinessRoles;
import org.poo.accounts.business.BusinessUserStats;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class BusinessReportCommand extends Command {
    private int start;
    private int end;

    public BusinessReportCommand(CommandInput input) {
        super(input);
        start = getInput().getStartTimestamp();
        end = getInput().getEndTimestamp();
    }

    @Override
    public ObjectNode execute() {
         Account account = getBankManager().getAccount(getInput().getAccount());
         String type = getInput().getType();
         int start = getInput().getStartTimestamp();
         int end = getInput().getEndTimestamp();

         if (account == null) {
             // TODO: account not found
             return null;
         }

         if (!account.getType().equals("business")) {
             // TODO: account not of type business
             return null;
         }

        BusinessAccount businessAccount = (BusinessAccount) account;

         if (type.equals("transaction")) {
            return getTransactionReport(businessAccount);
         } else if (type.equals("commerciant")) {
             return getCommerciantReport(businessAccount);
         }

        return null;
    }

    private ObjectNode getTransactionReport(final BusinessAccount account) {
        // Get a sorted list of all users of the account and a hashmap for user stats
        ArrayList<User> users = account.getAllUsersSorted();
        HashMap<User, BusinessUserStats> userStats = getUserStats(users);

        // Get the list of transactions that happened in the specified interval
        ArrayList<Transaction> transactions = getTransactionsInInterval(account);

        // For each transaction, update the user's spendings and deposits
//        transactions.forEach(transaction -> {
        for (Transaction transaction : transactions) {
            String description = transaction.getStringMap().get("description");
            String transferType = transaction.getStringMap().get("type");
            User user = getBankManager().getUserByEmail(transaction.getStringMap().get("email"));
            BusinessUserStats stats = userStats.get(user);

            // Online payments and sent money through bank transfers
            if (description.equals("Card payment") || (transferType != null && transferType.equals("sent"))) {
                stats.addSpend(transaction.getAmount());
            }

            // Money received from bank transfers
            if (transferType != null && transferType.equals("received")) {
                stats.addDeposited(transaction.getAmount());
            }
        }

        return getTransactionReportNode(userStats);
    }

    private ObjectNode getCommerciantReport(final BusinessAccount account) {
        // Get a sorted list of all users of the account
        ArrayList<User> users = account.getAllUsersSorted();

        // Get the list of transactions that happened in the specified interval
        ArrayList<Transaction> transactions = getTransactionsInInterval(account);

        return null;
    }

    private HashMap<User, BusinessUserStats> getUserStats(ArrayList<User> users) {
        ArrayList<BusinessUserStats> stats = new ArrayList<>();
        HashMap<User, BusinessUserStats> userStats = new HashMap<>();

        users.forEach(user -> stats.add(new BusinessUserStats(user)));
        stats.forEach(stat -> userStats.put(stat.getUser(), stat));

        return userStats;
    }

    private ArrayList<Transaction> getTransactionsInInterval(final BusinessAccount account) {
        return account.getTransactions().stream()
                      .filter(transaction -> transaction.getTimestamp() >= start)
                      .filter(transaction -> transaction.getTimestamp() <= end)
                      .collect(Collectors.toCollection(ArrayList::new));
    }

    private ObjectNode getTransactionReportNode(HashMap<User, BusinessUserStats> userStats) {
        BusinessAccount account = (BusinessAccount) getBankManager()
                                .getAccount(getInput().getAccount());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode returnNode = mapper.createObjectNode();

        returnNode.put("command", getInput().getCommand());
        returnNode.put("timestamp", getInput().getTimestamp());

        ObjectNode output = mapper.createObjectNode();
        output.put("IBAN", getInput().getAccount());
        output.put("balance", account.getBalance());
        output.put("currency", account.getCurrency());
        output.put("spending limit", account.getSpendingLimit());
        output.put("deposit limit", account.getDepositLimit());
        output.put("statistics type", getInput().getType());

        ArrayNode managersNode = mapper.createArrayNode();
        ArrayNode employeesNode = mapper.createArrayNode();

        userStats.entrySet().stream()
                .filter(entry -> account.getRole(entry.getKey()) == BusinessRoles.MANAGER)
                .forEach(entry -> managersNode.add(entry.getValue().getObjectNode()));

        userStats.entrySet().stream()
                .filter(entry -> account.getRole(entry.getKey()) == BusinessRoles.EMPLOYEE)
                .forEach(entry -> employeesNode.add(entry.getValue().getObjectNode()));

        output.set("managers", managersNode);
        output.set("employees", employeesNode);

        returnNode.set("output", output);
        return returnNode;
    }
}
