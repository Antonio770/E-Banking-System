package org.poo.accounts;

import org.poo.accounts.business.BusinessAccount;
import org.poo.fileio.CommandInput;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class AccountFactory {
    private static AccountFactory instance = null;

    private AccountFactory() { }

    /**
     * Returns the instance of the factory. In case of
     * a null instance, create it and then return it.
     */
    public static AccountFactory getInstance() {
        if (instance == null) {
            instance = new AccountFactory();
        }

        return instance;
    }

    private static final Map<String, Function<CommandInput, Account>> ACCOUNTS =
                                            new HashMap<String, Function<CommandInput, Account>>();

    static {
        ACCOUNTS.put("savings", SavingsAccount::new);
        ACCOUNTS.put("classic", ClassicAccount::new);
        ACCOUNTS.put("business", BusinessAccount::new);
    }

    /**
     * Creates an account based on its type
     * @param input The information of the account
     * @return The account created, or null in case of wrong input given
     */
    public Account create(final CommandInput input) {
        Function<CommandInput, Account> constructor = ACCOUNTS.get(input.getAccountType());

        try {
            return constructor.apply(input);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
