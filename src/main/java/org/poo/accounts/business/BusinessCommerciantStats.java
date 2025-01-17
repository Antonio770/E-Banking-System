package org.poo.accounts.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.commerciant.Commerciant;
import org.poo.user.User;

import java.util.ArrayList;

@Getter
@Setter
public final class BusinessCommerciantStats {
    private Commerciant commerciant;
    private double totalReceived;
    private ArrayList<User> users;

    public BusinessCommerciantStats(final Commerciant commerciant) {
        this.commerciant = commerciant;
        totalReceived = 0;
        users = new ArrayList<>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void addTotalReceived(double totalReceived) {
        this.totalReceived += totalReceived;
    }
}
