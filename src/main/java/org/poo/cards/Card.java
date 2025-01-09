package org.poo.cards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.utils.Utils;

@Getter
@Setter
public abstract class Card {
    private String cardNumber;
    private String status;

    @JsonIgnore
    private String type;

    public Card(final CommandInput input) {
        this.cardNumber = Utils.generateCardNumber();
        this.status = "active";
    }
}
