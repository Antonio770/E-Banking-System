package org.poo.commands.concreteCommands.businessCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.business.BusinessAccount;
import org.poo.accounts.business.BusinessCommerciantStats;
import org.poo.accounts.business.BusinessRoles;
import org.poo.accounts.business.BusinessUserStats;
import org.poo.commands.Command;
import org.poo.commerciant.Commerciant;
import org.poo.fileio.CommandInput;
import org.poo.transaction.Transaction;
import org.poo.user.User;

import java.lang.reflect.Array;
import java.security.KeyStore;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

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
        // Get a list of all users of the account and a hashmap for user stats
        ArrayList<User> users = account.getUsers();
        LinkedHashMap<User, BusinessUserStats> userStats = getUserStats(users);

        // Get the list of transactions that happened in the specified interval
        ArrayList<Transaction> transactions = getTransactionsInInterval(account);

        // For each transaction, update the user's spendings and deposits
        for (Transaction transaction : transactions) {
            String description = transaction.getStringMap().get("description");
            String transferType = transaction.getStringMap().get("type");
            User user = getBankManager().getUserByEmail(transaction.getStringMap().get("email"));
            BusinessUserStats stats = userStats.get(user);

            // Online payments and sent money through bank transfers
            if (description.equals("Card payment") || (transferType != null && transferType.equals("sent"))) {
                stats.addSpend(transaction.getAmount());
            }

            // Deposits using AddFunds command
            if (stats != null && description.equals("Added funds")) {
                stats.addDeposited(transaction.getAmount());
            }
        }

        return getTransactionReportNode(userStats, account);
    }

    private ObjectNode getCommerciantReport(final BusinessAccount account) {
        // Get the list of transactions that happened in the specified interval
        ArrayList<Transaction> transactions = getTransactionsInInterval(account);

        // Initialize a list of commerciant stats
        HashMap<Commerciant, BusinessCommerciantStats> stats = new HashMap<>();

        for (Transaction transaction : transactions) {
            String description = transaction.getStringMap().get("description");
            String transferType = transaction.getStringMap().get("transferType");
            Commerciant commerciant = null;
            User sender = null;

            // Online payments
            if (description.equals("Card payment")) {
                String name = transaction.getStringMap().get("commerciant");
                commerciant = getBankManager().getCommerciantByName(name);

                String email = transaction.getStringMap().get("email");
                sender = getBankManager().getUserByEmail(email);
            }

            // Bank transfers
            if (transferType != null && transferType.equals("sent")) {
                String receiver = transaction.getStringMap().get("receiverIBAN");
                commerciant = getBankManager().getCommerciantByIban(receiver);

                String senderIban = transaction.getStringMap().get("senderIBAN");
                Account acc = getBankManager().getAccount(senderIban);
                sender = getBankManager().getUserByAccount(acc);
            }

            // If there is no commerciant or the user was the owner, ignore the transaction
            if (commerciant == null || account.getRole(sender) == BusinessRoles.OWNER) {
                continue;
            }

            // If the commerciant isn't already in the hashmap, add it
            if (!stats.containsKey(commerciant)) {
                stats.put(commerciant, new BusinessCommerciantStats(commerciant));
            }

            // Update the stats of the commerciant
            BusinessCommerciantStats stat = stats.get(commerciant);
            stat.addUser(sender);
            stat.addTotalReceived(transaction.getAmount());
        }

        return getCommerciantReportNode(account, stats);
    }

    private LinkedHashMap<User, BusinessUserStats> getUserStats(ArrayList<User> users) {
        ArrayList<BusinessUserStats> stats = new ArrayList<>();
        LinkedHashMap<User, BusinessUserStats> userStats = new LinkedHashMap<>();

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

    private ObjectNode getTransactionReportNode(final HashMap<User, BusinessUserStats> userStats,
                                                final BusinessAccount account) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode returnNode = mapper.createObjectNode();

        returnNode.put("command", getInput().getCommand());

        ObjectNode output = mapper.createObjectNode();
        output.put("IBAN", getInput().getAccount());
        output.put("balance", account.getBalance());
        output.put("currency", account.getCurrency());
        output.put("spending limit", account.getSpendingLimit());
        output.put("deposit limit", account.getDepositLimit());
        output.put("statistics type", getInput().getType());

        ArrayNode managersNode = mapper.createArrayNode();
        ArrayNode employeesNode = mapper.createArrayNode();

        // Filter the users so by their role, and then add them to managersNode or employeesNode
        userStats.entrySet().stream()
                .filter(entry -> account.getRole(entry.getKey()) == BusinessRoles.MANAGER)
                .forEach(entry -> managersNode.add(entry.getValue().getObjectNode()));

        userStats.entrySet().stream()
                .filter(entry -> account.getRole(entry.getKey()) == BusinessRoles.EMPLOYEE)
                .forEach(entry -> employeesNode.add(entry.getValue().getObjectNode()));

        output.set("managers", managersNode);
        output.set("employees", employeesNode);

        // Calculate the total money spent and deposited
        double totalSpent = 0;
        double totalDeposit = 0;
        for (Map.Entry<User, BusinessUserStats> entry : userStats.entrySet()) {
            if (account.getRole(entry.getKey()) == BusinessRoles.OWNER) {
                continue;
            }

            totalSpent += entry.getValue().getSpent();
            totalDeposit += entry.getValue().getDeposited();
        }

        output.put("total spent", totalSpent);
        output.put("total deposited", totalDeposit);

        returnNode.put("timestamp", getInput().getTimestamp());

        returnNode.set("output", output);
        return returnNode;
    }

    private ObjectNode getCommerciantReportNode(final BusinessAccount account,
                                      final HashMap<Commerciant, BusinessCommerciantStats> stats) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode returnNode = mapper.createObjectNode();

        returnNode.put("command", getInput().getCommand());

        ObjectNode output = mapper.createObjectNode();
        output.put("IBAN", getInput().getAccount());
        output.put("balance", account.getBalance());
        output.put("currency", account.getCurrency());
        output.put("spending limit", account.getSpendingLimit());
        output.put("deposit limit", account.getDepositLimit());
        output.put("statistics type", getInput().getType());

        ArrayNode commerciantsNode = mapper.createArrayNode();

        // Sort the commerciants alphabetically
        List<Map.Entry<Commerciant, BusinessCommerciantStats>> sortedStats;

        sortedStats = stats.entrySet().stream()
                    .sorted(Comparator.comparing(entry -> entry.getKey().getName()))
                    .toList();

        for (Map.Entry<Commerciant, BusinessCommerciantStats> entry : sortedStats) {
            ObjectNode commerciantNode = mapper.createObjectNode();
            commerciantNode.put("commerciant", entry.getValue().getCommerciant().getName());
            commerciantNode.put("total received", entry.getValue().getTotalReceived());

            ArrayNode employeesNode = mapper.createArrayNode();
            ArrayNode managersNode = mapper.createArrayNode();

            entry.getValue().getUsers().stream()
                    .filter(user -> account.getRole(user) == BusinessRoles.EMPLOYEE)
                    .sorted(Comparator.comparing(User::getLastName))
                    .forEach(employee -> employeesNode.add(employee.getLastName() + " "
                                                           + employee.getFirstName()));

            entry.getValue().getUsers().stream()
                    .filter(user -> account.getRole(user) == BusinessRoles.MANAGER)
                    .sorted(Comparator.comparing(User::getLastName))
                    .forEach(employee -> managersNode.add(employee.getLastName() + " "
                                                          + employee.getFirstName()));

            commerciantNode.set("employees", employeesNode);
            commerciantNode.set("managers", managersNode);

            commerciantsNode.add(commerciantNode);
        }

        output.set("commerciants", commerciantsNode);

        returnNode.set("output", output);
        returnNode.put("timestamp", getInput().getTimestamp());
        return returnNode;
    }
}
