# E-Banking System

## Overview
A modular and flexible Java banking system that simulates real-world banking
operations. Supports multiple account types, cards, payments, cashback, and
business logic, demonstrating object-oriented programming, design patterns,
and software engineering skills.

## Features

- **Multiple account types:** Classic, Savings, and Business accounts
- **Card management:** Normal and One-Time cards
- **Transaction management:** Deposits, withdrawals, transfers, split payments
- **Plans and fees:** Dynamic user plans with bank fee reductions and benefits
- **Cashback:** Configurable per merchant with different strategies
- **Business accounts:** Track associates, roles, and statistics
- **Command system:** Execute actions using the Command design pattern
- **Interest management:** Visitor pattern for applying interest to accounts
- **Payment strategies:** Bank transfer and online payments using Strategy pattern

## Tech Stack

- Java
- Maven
- Object-Oriented Programming & Design Patterns
- Streams API and Collections
- JSON Input/Output handling

## Project Structure & Packages

- **accounts:** Abstract & concrete account types and Account Factory
- **cards:** Card types and Card Factory
- **cashbackStrategies:** Different cashback strategies (Strategy pattern)
- **command:** Command Invoker, Command Factory, and concrete commands
- **merchant:** Merchant integration
- **managers:** Singleton managers for bank operations and exchange rates
- **paymentStrategies:** Payment types and Payment Factory
- **plans:** User plans (Student, Standard, Silver, Gold)
- **splitPayment:** Split payment logic
- **transaction:** Builder pattern for transactions
- **user:** User accounts and information
- **visitors:** Visitor pattern for interest management

## Design Patterns

- **Singleton:** Managers and factories
- **Factory:** Accounts, cards, payments, commands
- **Strategy:** Payment methods and cashback strategies
- **Builder:** Transactions
- **Visitor:** Interest application
- **Command:** Execute user actions via commands
