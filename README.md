# Project Structure

The project is organized into the following packages:

### 1. `accounts`:
- `Account`: Abstract account class
- `ClassicAccount` and `SavingsAccount`: The two types of account
- `AccountFactory`: Singleton factory that creates the accounts
- `business` package:
    - `BusinessAccount`: Account of type business
    - `BusinessRoles`: Enum containing roles for business associates
    - `BusinessUserStats` and `BusinessCommerciantStats`: Classes that contain
  stats regarding transactions made by associates to different commerciants

### 2. `cards`:
- `Card`: Abstract card class
- `NormalCard` and `OneTimeCard`: The two types of cards
- `CardFactory`: Singleton factory that creates the cards

### 3. `cashbackStrategies`:
- `CashbackStrategy`: Common interface for the different types of cashback
strategies
- `NrOfTransaction` and `SpendingThreshold`: Concrete cashback strategy classes
that implement the `CashbackStrategy` interface

### 4. `command`:
- `Command`: Abstract command class
- `CommandInvoker`: The class that calls the execute method of every command
- `CommandFactory`: Singleton factory that creates the concrete commands
- `concreteCommands`: Subpackage containing a class for every command type
    - `accountCommands`: Concrete commands that affect the accounts
    - `businessCommands`: Concrete commands for the business accounts
    - `cardCommands`: Concrete commands for the cards
    - `paymentCommands`: Concrete commands regarding the payments
    - `userCommands`: Concrete commands for the users

### 5. `commerciant`:
- `Commerciant`: Class that integrates the commerciants into the banking
application

### 6. `managers`:
- `BankManager`: A singleton class that manages the users
- `ExchangeRateManager`: A singleton class that manages the conversion rates
  between any two currencies
- `ExchangeRate`: Class that represents an edge in the exchange rate graph

### 7. `paymentStrategies`
- `PaymentStrategy`: Common interface for all payment methods
- `PaymentFactory`: Singleton factory that creates the payment classes
- `PayOnlineStrategy` and `BankTransferStrategy`: The two types of payments

### 8. `plans`:
- `Plan`: Common interface for all plans
- `StudentPlan`, `StandardPlan`, `SilverPlan`, `GoldPlan`: Classes for the
different types of plans

### 9. `splitPayment`:
- `SplitPayment`: Class that handles the split payments

### 10. `transaction`
- `Transaction`: Class that contains multiple optional fields and uses the
  builder design pattern to build transactions displaying different information

### 11. `user`
- `User`: Class containing information about a user

### 12. `visitors`
- `Visitor`: Interface for the visitors
- `Visitable`: Interface for the visitable classes
- `AddInterestVisitor` and `ChangeInterestVisitor`: The two concrete visitors


# Design Patterns

### 1. Singleton
The singleton design pattern is used for the managers and the factories. This
approach ensures that all classes of the project interact with the same
instances of these classes.

### 2. Factory
The factory design pattern makes the creation of different classes easier.
There are multiple types of cards, accounts, commands and payment methods.
When using a factory, adding a new type of class only requires adding a new
registration to the factory's hashmap.

### 3. Strategy
The strategy design pattern is used to manage the different types of payment
without needing to add new methods to the user that makes the payment. The
`paymentFactory` creates the type of payment and the user calls the `pay()`
method without knowing anything about the code that will be executed. The same
approach was used for the different cashback strategies. Every user has a
`Plan` field that is dynamically updated to apply the correct cashback.

### 4. Builder
The builder design pattern is used to create different types of transactions.
There are multiple transactions that can be made and each of them stores
information about different details. The `Transaction` class contains a
`HashMap` that stores custom fields, so any type of transaction can be created
easily.

### 5. Visitor
The visitor design pattern is used to add interest functionalities to the
accounts without having to add the methods to every type of account. This is
done because the interest can be changed and added to the balance only for the
savings account.

### 6. Command
The command design pattern is used to execute every type of command given from
input. Each type of command has a concrete command class that implements the
`Command` interface. The `CommandInvoker` iterates through every command and
calls the `execute()` method for each of them.


# Overview

In the second phase of the project, a few changes were made and there were some
new additions.

Changes:
- The `Transaction` class does not contain each possible transaction field
that is initialized to `null`. Instead, the `Transaction.Builder` contains
a `HashMap<String, String>` that can contain any custom field. When calling
the `getObjectNode` method, it iterates through the custom list and adds all
of them to the object node that will be printed in JSON format.


- Methods from the `BankManager` like `getUserByAccount` contained a lot of
nested for loops and conditionals, which made it hard to follow. Most of those
methods have been rewritten using `Streams` and are now much easier to read.


- `Split Payments` can now be of type `equal` or `custom` and one account can
be involved in multiple split payments at a time. This adds the need to create
a class for the payments and add them to the `BankManager`.

New additions:
- `Bank fee`: Most of the transactions now come with a bank fee that is added
on top of the initial amount. The bank fee is added using the `addFee` method
of the `Plan` interface.


- `Plans`: Users can now have different types of plans for their accounts, that
come with benefits like reduced bank fees. `Composition` was used to implement
this feature. The `User` class contains a `Plan` field that is dynamically
updated throughout the code, in classes like `UpgradePlanCommand`.


- `Cashback`: Each commerciant has a type of cashback assigned to it at the
beginning of the application execution. The `strategy design pattern` was used
to implement the cashback functionality.

  - For the `NrOfTransactions` cashback, each account has contains a hashmap
  that keeps track of how many transactions that account has done that involved
  a specific commerciant. When the number of transactions reaches threshold,
  the account gets a discount. The discounts are also put in a
  `HashMap<String, Boolean>`. Using the value of the boolean, we can determine
  if the discount was already used or not. This way, a discount will not be
  used more than one time.

  - For the `SpendingThreshold` cashback, the accounts keep track of the total
  amount of money spent. Each time a new transaction is made, the `cashback`
  method checks weather a threshold has been passed or not. If the amount
  of money spent is big enough, the user gets a cashback.


- `Business accounts`: A new type of account was implemented. To keep track
of all the business associates of an account, all users are added to an
`ArrayList` and their roles are mapped using a `HashMap`. When creating the
business account, the user that created it is set as the owner and has no
restrictions. The `BusinessAccount` class extends the abstract `Account` class,
so most of the functionalities are the same as the ones from the rest of the
accounts.
