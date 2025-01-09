package org.poo.cards;

import org.poo.fileio.CommandInput;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class CardFactory {
    private static CardFactory instance = null;

    private CardFactory() { }

    /**
     * Returns the instance of the factory. In case of
     * a null instance, create it and then return it.
     */
    public static CardFactory getInstance() {
        if (instance == null) {
            instance = new CardFactory();
        }

        return instance;
    }

    private static final Map<String, Function<CommandInput, Card>> CARDS =
                                               new HashMap<String, Function<CommandInput, Card>>();

    static {
        CARDS.put("createCard", NormalCard::new);
        CARDS.put("createOneTimeCard", OneTimeCard::new);
    }

    /**
     * Creates a card based on its type
     * @param input The information of the card
     * @return The card created, or null in case of wrong input given
     */
    public Card create(final CommandInput input) {
        Function<CommandInput, Card> constructor = CARDS.get(input.getCommand());

        try {
            return constructor.apply(input);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
