package org.poo.managers;

import lombok.Getter;
import lombok.Setter;

import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.commerciant.Commerciant;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.UserInput;
import org.poo.splitPayments.SplitPayment;
import org.poo.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public final class BankManager {
    private static BankManager instance = null;
    private ArrayList<User> users;
    private ArrayList<Commerciant> commerciants;
    private ArrayList<SplitPayment> splitPayments;

    private BankManager() {
        users = new ArrayList<User>();
        commerciants = new ArrayList<Commerciant>();
        splitPayments = new ArrayList<SplitPayment>();
    }

    /**
     * Returns the instance of the factory. In case of
     * a null instance, create it and then return it.
     */
    public static BankManager getInstance() {
        if (instance == null) {
            instance = new BankManager();
        }

        return instance;
    }

    /**
     * Clear the list of users
     */
    public void resetBankData() {
        users.clear();
        commerciants.clear();
    }

    /**
     * Creates the users and adds them to the bank
     * @param usr The list of users
     */
    public void addUsers(final List<UserInput> usr) {
        for (UserInput user : usr) {
            User newUser = new User(user);
            this.users.add(newUser);
        }
    }

    public void addCommerciants(final List<CommerciantInput> comm) {
        for (CommerciantInput commerciant : comm) {
            Commerciant newComm = new Commerciant(commerciant);
            this.commerciants.add(newComm);
        }
    }

    /**
     * Searches for a user by an email
     * @param email the email of the user
     * @return the user found, or null if there is no user with the given email
     */
    public User getUserByEmail(final String email) {
        for (User user : this.users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }

        return null;
    }

    /**
     * Searches for a user by a card
     * @param card the card of the user
     * @return the user found, or null if there is no user with the given card
     */
    public User getUserByCard(final Card card) {
        for (User user : this.users) {
            for (Account account : user.getAccounts()) {
                if (account.getCards().contains(card)) {
                    return user;
                }
            }
        }

        return null;
    }

    /**
     * Searches for a user by an account
     * @param account the account of the user
     * @return the user found, or null if there is no user with the given account
     */
    public User getUserByAccount(final Account account) {
        for (User user : users) {
            if (user.getAccounts().contains(account)) {
                return user;
            }
        }

        return null;
    }

    public Account getAccountOfCard(final Card card) {
        for (User user : users) {
            Account account = user.getAccountOfCard(card);
            if (account != null) {
                return account;
            }
        }

        return null;
    }

    /**
     * Searches for an account by an IBAN or alias
     * @param acc the IBAN or alias of the account
     * @return the account, or null if not found
     */
    public Account getAccount(final String acc) {
        for (User user : users) {
            Map<String, String> map = user.getAliasMap();

            for (Account account : user.getAccounts()) {
                String iban = account.getIban();

                if (acc.equals(iban)) {
                    return account;
                }

                if (map.get(acc) != null && map.get(acc).equals(iban)) {
                    return account;
                }
            }
        }

        return null;
    }

    /**
     * Removes an account from the bank's list
     * @param acc the IBAN or alias of the account to be removed
     * @return true if the account could be deleted, false otherwise
     */
    public boolean removeAccount(final String acc) {
        for (User user : users) {
            Map<String, String> map = user.getAliasMap();

            for (Account account : user.getAccounts()) {
                String iban = account.getIban();

                if (iban.equals(acc) && account.getBalance() == 0) {
                    user.getAccounts().remove(account);
                    map.remove(acc);
                    return true;
                }

                if (map.get(acc) != null && map.get(acc).equals(iban)) {
                    user.getAccounts().remove(account);
                    map.remove(acc);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Searches for a card by a card number
     * @param cardNumber the card number of the card
     * @return the card, or null if not found
     */
    public Card getCardByNumber(final String cardNumber) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(cardNumber)) {
                        return card;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Removes a card from the bank's system
     * @param card the card to be removed
     */
    public void removeCard(final Card card) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getCards().contains(card)) {
                    account.removeCard(card);
                    return;
                }
            }
        }
    }

    public Commerciant getCommerciantByName(final String name) {
        for (Commerciant comm : commerciants) {
            if (comm.getName().equals(name)) {
                return comm;
            }
        }

        return null;
    }

    public Commerciant getCommerciantByIban(final String iban) {
        for (Commerciant comm : commerciants) {
            if (comm.getAccount().equals(iban)) {
                return comm;
            }
        }

        return null;
    }

    public SplitPayment getSplitPaymentOfUser(final User user, final String type) {
        // TODO: make this look better

        for (SplitPayment sp : splitPayments) {
            if (sp.getType().equals(type) && sp.getUsers().contains(user)) {
                for (Account account : sp.getPendingAccounts()) {
                    if (user.getAccounts().contains(account)) {
                        return sp;
                    }
                }
            }
        }

        return null;
    }
}
