package org.poo.managers;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.ExchangeInput;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public final class ExchangeManager {
    private static ExchangeManager instance = null;
    private ArrayList<String> currencies;
    private ArrayList<ExchangeRate> exchangeRates;
    private double[][] graph;

    private static final double ERROR = 10e8;

    private ExchangeManager() {
        currencies = new ArrayList<String>();
        exchangeRates = new ArrayList<ExchangeRate>();
    }

    /**
     * Returns the instance of the exchange rate manager.
     * It the instance is null, create it and then return it.
     * @return the instance of the exchange rate manager
     */
    public static ExchangeManager getInstance() {
        if (instance == null) {
            instance = new ExchangeManager();
        }

        return instance;
    }

    /**
     * Resets the currency conversion graph
     */
    public void resetData() {
        currencies = new ArrayList<String>();
        exchangeRates = new ArrayList<ExchangeRate>();
        graph = new double[0][0];
    }

    /**
     * Creates the currency graph and the currency conversion matrix
     * @param rates list of the exchange rates
     */
    public void generateExchangeRates(final List<ExchangeInput> rates) {
        addExchangeRates(rates);
        initGraph();
        floydWarshall();
    }

    /**
     * Adds the exchange rates to the graph
     * @param rates list of the exchange rates
     */
    public void addExchangeRates(final List<ExchangeInput> rates) {
        for (ExchangeInput exchangeInput : rates) {
            String from = exchangeInput.getFrom();
            String to = exchangeInput.getTo();
            double rate = exchangeInput.getRate();

            // Add the direct edge to the graph.
            // To go return to the initial currency, add the reverse edge
            // with an inverse cost
            this.exchangeRates.add(new ExchangeRate(from, to, rate));
            this.exchangeRates.add(new ExchangeRate(to, from,
                                                    Math.ceil(1.00 / rate * ERROR) / ERROR));

            // Adds the currencies to the list of currencies if
            // it doesn't already contain them
            if (!this.currencies.contains(from)) {
                this.currencies.add(from);
            }

            if (!this.currencies.contains(to)) {
                this.currencies.add(to);
            }
        }
    }

    /**
     * Creates the currency conversion matrix. Fills the matrix with positive infinity
     * if there is no direct conversion between two currencies.
     */
    private void initGraph() {
        graph = new double[currencies.size()][currencies.size()];

        // Initialize the matrix with infinity values and
        // values of 1 on the diagonal
        for (int i = 0; i < currencies.size(); i++) {
            for (int j = 0; j < currencies.size(); j++) {
                graph[i][j] = Double.POSITIVE_INFINITY;

                if (i == j) {
                    graph[i][j] = 1;
                }
            }
        }

        // If there is an edge between two currencies,
        // set the cost to be the conversion rate between them
        for (ExchangeRate exchangeRate : exchangeRates) {
            int from = currencies.indexOf(exchangeRate.getFrom());
            int to = currencies.indexOf(exchangeRate.getTo());

            graph[from][to] = exchangeRate.getRate();
        }
    }

    /**
     * Apply the Floyd-Warshall algorithm on the graph to determine
     * the best conversion between any two currencies
     */
    private void floydWarshall() {
        for (int k = 0; k < currencies.size(); k++) {
            for (int i = 0; i < currencies.size(); i++) {
                for (int j = 0; j < currencies.size(); j++) {
                    if (graph[i][k] != Double.POSITIVE_INFINITY
                        && graph[k][j] != Double.POSITIVE_INFINITY) {
                        graph[i][j] = Math.min(graph[i][j],
                                      Math.ceil(graph[i][k] * graph[k][j] * ERROR) / ERROR);
                    }
                }
            }
        }
    }

    /**
     * Gets the conversion of a currency to another from the currency conversion graph
     * @param from the initial currency
     * @param to the converted currency
     * @return the conversion value
     */
    public double getConversionRate(final String from, final String to) {
        return graph[currencies.indexOf(from)][currencies.indexOf(to)];
    }

    /**
     * Converts an amount of money from one currency to another
     * @param from the initial currenct
     * @param to the final currency
     * @param amount the amount of money to be converted
     * @return the converted amount
     */
    public double getAmount(final String from, final String to, final double amount) {
        return amount * getConversionRate(from, to);
    }
}
