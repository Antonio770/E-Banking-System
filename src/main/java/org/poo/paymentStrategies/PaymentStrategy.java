package org.poo.paymentStrategies;

import org.poo.fileio.CommandInput;

public interface PaymentStrategy {
    /**
     * The payment strategy to be implemented
     * @param input the payment information
     * @return true if the payment is successful, false if not
     */
    boolean pay(CommandInput input);
}
