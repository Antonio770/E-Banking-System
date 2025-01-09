package org.poo.cards;

import org.poo.fileio.CommandInput;

public final class NormalCard extends Card {
    public NormalCard(final CommandInput input) {
        super(input);
        setType("Normal");
    }
}
