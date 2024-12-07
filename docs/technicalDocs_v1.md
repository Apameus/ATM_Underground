# Technical Documentation

### High Level Architecture

```mermaid
flowchart LR
TerminalUI --> AuthorizationService --> AccountManagementRepo
TerminalUI --> AccountManagementService --> AccountManagementRepo
```

## Authorization:
```mermaid
sequenceDiagram
actor U as User
participant S as AuthService
participant R as AuthRepo

U ->> S: authorize(creditID, pin)
S ->> R: findAccBy(creditID)
R -->> S: Account
alt Account not found OR wrong pin
    S -->> U: AuthorizationFailedException
end
```

## Deposit:
```mermaid
sequenceDiagram
actor U as User
participant S as AccManagementService
participant R as AccManagementRepo

U ->> S: deposit(amount)
S ->> R: deposit(creditID, amount)
alt  someCheck
    S -->> U: msg
end
%%S -->> U: msg(succeed)
```

## Withdraw
```mermaid
sequenceDiagram
actor U as User
participant S as AccManagementService
participant R as AccManagementRepo

U ->> S: withdraw(amount)
alt not enough money in acc
    S -->> U : NotEnoughMoneyException
end
S ->> R: withdraw(amount, creditID)
```

## Balance
```mermaid
sequenceDiagram
actor U as User
participant S as AccManagementService

U ->> S: viewBalance()
S -->> U: balance
```