package org.poo.managers;

import lombok.Getter;
import lombok.Setter;

import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.merchant.Merchant;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.UserInput;
import org.poo.splitPayment.SplitPayment;
import org.poo.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public final class BankManager {
    private static BankManager instance = null;
    private ArrayList<User> users;
    private ArrayList<Merchant> merchants;
    private ArrayList<SplitPayment> splitPayments;

    private BankManager() {
        users = new ArrayList<User>();
        merchants = new ArrayList<Merchant>();
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
        merchants.clear();
    }

    /**
     * Creates the users and adds them to the bank
     * @param usr The list of users
     */
    public void addUsers(final List<UserInput> usr) {
        usr.forEach(user -> users.add(new User(user)));
    }

    /**
     * Adds all the commerciants to the bank manager
     * @param comm a list of the commerciants
     */
    public void addCommerciants(final List<CommerciantInput> comm) {
        comm.forEach(commerciant -> merchants.add(new Merchant(commerciant)));
    }

    /**
     * Searches for a user by an email
     * @param email the email of the user
     * @return the user found, or null if there is no user with the given email
     */
    public User getUserByEmail(final String email) {
        return users.stream()
                    .filter(user -> user.getEmail().equals(email))
                    .findFirst()
                    .orElse(null);
    }

    /**
     * Searches for a user by a card
     * @param card the card of the user
     * @return the user found, or null if there is no user with the given card
     */
    public User getUserByCard(final Card card) {
        return users.stream()
                    .filter(user -> user.getAccounts().stream()
                                        .anyMatch(account -> account.getCards().contains(card)))
                    .findFirst()
                    .orElse(null);
    }

    /**
     * Searches for a user by an account
     * @param account the account of the user
     * @return the user found, or null if there is no user with the given account
     */
    public User getUserByAccount(final Account account) {
        return users.stream()
                    .filter(user -> user.getAccounts().contains(account))
                    .findFirst()
                    .orElse(null);
    }

    /**
     * Searches for the account that contains the specified card in the bank's data
     * @param card the card to be searched
     * @return the account, or null if it doesn't exist
     */
    public Account getAccountOfCard(final Card card) {
        return users.stream()
                    .flatMap(user -> user.getAccounts().stream())
                    .filter(account -> account.getCards().contains(card))
                    .findFirst()
                    .orElse(null);
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
        return users.stream()
                    .flatMap(user -> user.getAccounts().stream())
                    .flatMap(account -> account.getCards().stream())
                    .filter(card -> card.getCardNumber().equals(cardNumber))
                    .findFirst()
                    .orElse(null);
    }

    /**
     * Removes a card from the bank's system
     * @param card the card to be removed
     */
    public void removeCard(final Card card) {
        users.stream()
             .flatMap(user -> user.getAccounts().stream())
             .filter(account -> account.getCards().contains(card))
             .forEach(account -> account.getCards().remove(card));
    }

    /**
     * Searches for a specific commerciant
     * @param name the name of the commerciant
     * @return the commerciant, or null if not found
     */
    public Merchant getCommerciantByName(final String name) {
        return merchants.stream()
                           .filter(comm -> comm.getName().equals(name))
                           .findFirst()
                           .orElse(null);
    }

    /**
     * Searches for a commerciant using its IBAN
     * @param iban the IBAN of the commerciant
     * @return the commerciant, or null if not found
     */
    public Merchant getCommerciantByIban(final String iban) {
        return merchants.stream()
                           .filter(comm -> comm.getAccount().equals(iban))
                           .findFirst()
                           .orElse(null);
    }

    /**
     * Searches for the first split payment of the specified type that contains an account of user
     * @param user the user involved in the split payment
     * @param type the type of the split payment ("equal" or "custom")
     * @return the split payment, or null if not found
     */
    public SplitPayment getSplitPaymentOfUser(final User user, final String type) {
        return splitPayments.stream()
                .filter(sp -> sp.getType().equals(type) && sp.getUsers().contains(user))
                .filter(sp -> sp.getPendingAccounts().stream()
                                .anyMatch(account -> user.getAccounts().contains(account)))
                .findFirst()
                .orElse(null);

    }
}
