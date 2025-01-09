package org.poo.paymentStrategies;

import java.util.HashMap;
import java.util.Map;

public final class PaymentFactory {
    private static PaymentFactory instance = null;

    private PaymentFactory() { }

    /**
     * Returns the instance of the factory. In case of
     * a null instance, create it and then return it.
     */
    public static PaymentFactory getInstance() {
        if (instance == null) {
            instance = new PaymentFactory();
        }

        return instance;
    }

    private static final Map<String, PaymentStrategy> STRATEGIES =
                                                            new HashMap<String, PaymentStrategy>();

    static {
        STRATEGIES.put("payOnline", new PayOnlineStrategy());
        STRATEGIES.put("sendMoney", new BankTransferStrategy());
    }

    /**
     * Creates a payment strategy
     * @param input the type of payment strategy
     */
    public PaymentStrategy create(final String input) {
        return STRATEGIES.get(input);
    }
}
