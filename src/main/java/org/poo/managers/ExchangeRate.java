package org.poo.managers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ExchangeRate {
    private String from;
    private String to;
    private double rate;

    public ExchangeRate(final String from, final String to, final double rate) {
        this.from = from;
        this.to = to;
        this.rate = rate;
    }
}
