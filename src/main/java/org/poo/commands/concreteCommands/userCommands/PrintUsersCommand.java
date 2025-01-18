package org.poo.commands.concreteCommands.userCommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.user.User;

import java.util.ArrayList;

public final class PrintUsersCommand extends Command {
    public PrintUsersCommand(final CommandInput input) {
        super(input);
    }

    @Override
    public ObjectNode execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", getInput().getCommand());

        ArrayList<User> users = getBankManager().getUsers();
        users.forEach(User::roundBalances);
        objectNode.set("output", objectMapper.valueToTree(users));

        objectNode.put("timestamp", getInput().getTimestamp());

        return objectNode;
    }
}
