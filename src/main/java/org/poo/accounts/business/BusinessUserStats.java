package org.poo.accounts.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.user.User;

@Getter
@Setter
public final class BusinessUserStats {
    private User user;
    private double spent;
    private double deposited;

    public BusinessUserStats(final User user) {
        this.user = user;
        this.spent = 0;
        this.deposited = 0;
    }

    public void addSpend(final double amount) {
        this.spent += amount;
    }

    public void addDeposited(final double amount) {
        this.deposited += amount;
    }

    public ObjectNode getObjectNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("username", user.getLastName() + " " + user.getFirstName());
        node.put("spent", spent);
        node.put("deposited", deposited);

        return node;
    }
}
