package org.poo.cards;

import org.poo.fileio.CommandInput;

public final class OneTimeCard extends Card {
    public OneTimeCard(final CommandInput input) {
        super(input);
        setType("OneTime");
    }
}
