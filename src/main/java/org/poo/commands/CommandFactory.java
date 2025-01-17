package org.poo.commands;

import org.poo.commands.concreteCommands.accountCommands.*;
import org.poo.commands.concreteCommands.businessCommands.AddAssociateCommand;
import org.poo.commands.concreteCommands.businessCommands.BusinessReportCommand;
import org.poo.commands.concreteCommands.businessCommands.ChangeLimitCommand;
import org.poo.commands.concreteCommands.cardCommands.CashWithdrawalCommand;
import org.poo.commands.concreteCommands.cardCommands.CheckCardStatusCommand;
import org.poo.commands.concreteCommands.cardCommands.CreateCardCommand;
import org.poo.commands.concreteCommands.cardCommands.DeleteCardCommand;
import org.poo.commands.concreteCommands.paymentCommands.*;
import org.poo.commands.concreteCommands.userCommands.*;
import org.poo.fileio.CommandInput;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class CommandFactory {
    private static CommandFactory instance = null;

    private CommandFactory() { }

    /**
     * Returns the instance of the factory. In case of
     * a null instance, create it and then return it.
     */
    public static CommandFactory getInstance() {
        if (instance == null) {
            instance = new CommandFactory();
        }

        return instance;
    }

    private static final Map<String, Function<CommandInput, Command>> COMMANDS =
                                            new HashMap<String, Function<CommandInput, Command>>();

    static {
        COMMANDS.put("printUsers", PrintUsersCommand::new);
        COMMANDS.put("addAccount", AddAccountCommand::new);
        COMMANDS.put("createCard", CreateCardCommand::new);
        COMMANDS.put("createOneTimeCard", CreateCardCommand::new);
        COMMANDS.put("addFunds", AddFundsCommand::new);
        COMMANDS.put("deleteAccount", DeleteAccountCommand::new);
        COMMANDS.put("deleteCard", DeleteCardCommand::new);
        COMMANDS.put("setMinBalance", SetMinBalanceCommand::new);
        COMMANDS.put("checkCardStatus", CheckCardStatusCommand::new);
        COMMANDS.put("payOnline", PayOnlineCommand::new);
        COMMANDS.put("sendMoney", SendMoneyCommand::new);
        COMMANDS.put("setAlias", SetAliasCommand::new);
        COMMANDS.put("printTransactions", PrintTransactions::new);
        COMMANDS.put("splitPayment", SplitPaymentCommand::new);
        COMMANDS.put("report", ReportCommand::new);
        COMMANDS.put("spendingsReport", SpendingsReportCommand::new);
        COMMANDS.put("addInterest", AddInterestCommand::new);
        COMMANDS.put("changeInterestRate", ChangeInterestRateCommand::new);
        COMMANDS.put("withdrawSavings", WithdrawSavingsCommand::new);
        COMMANDS.put("cashWithdrawal", CashWithdrawalCommand::new);
        COMMANDS.put("upgradePlan", UpgradePlanCommand::new);
        COMMANDS.put("acceptSplitPayment", AcceptSplitCommand::new);
        COMMANDS.put("rejectSplitPayment", RejectSplitCommand::new);
        COMMANDS.put("addNewBusinessAssociate", AddAssociateCommand::new);
        COMMANDS.put("changeSpendingLimit", ChangeLimitCommand::new);
        COMMANDS.put("changeDepositLimit", ChangeLimitCommand::new);
        COMMANDS.put("businessReport", BusinessReportCommand::new);
    }

    /**
     * Creates a command based on its type
     * @param input The information of the command
     * @return The command created, or null in case of wrong input given
     */
    public Command create(final CommandInput input) {
        Function<CommandInput, Command> constructor = COMMANDS.get(input.getCommand());

        try {
            return constructor.apply(input);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
