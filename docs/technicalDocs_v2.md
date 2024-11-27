# Technical Documentation

### High Level Architecture
```mermaid
flowchart LR
UI --> AuthorizationService --> CardRepo
UI --> CardService --> CardRepo
```


## Authorization:
```mermaid
sequenceDiagram
actor U as User
participant S as AuthService
participant R as CardRepo

U ->> S: authorize(creditID, pin)
S ->> R: findCardBy(creditID)
R -->> S: Card
alt Account not found OR wrong pin
    S -->> U: AuthorizationFailedException
end
```

## Deposit:
```mermaid
sequenceDiagram
actor U as User
participant S as CardService
participant R as CardRepo

U ->> S: deposit(cardID, amount)
S ->> R: findCardBy(cardID)
R -->> S: card
alt card == null
    S -->> U: InvalidCardIdException
end
alt amount <= 0
    S -->> U: EXC
end
S ->> R: updateAmount(cardID, amount)
%%S -->> U: msg(succeed)
```

## Withdraw:
```mermaid
sequenceDiagram
actor U as User
participant S as CardService
participant R as CardRepo

U ->> S: withdraw(cardID, amount)
S ->> R: findCardBy(cardID)
R -->>S: card
alt card == null 
    S -->> U: InvalidCardIdException
end
alt amount <= 0 
    S -->> U: EXC
end
S ->> R: updateAmount(cardID, amount)
```

## Balance:
```mermaid
sequenceDiagram
actor U as User
participant S as CardService
%%participant R as CardRepo

U ->> S: viewBalance(cardID)
S ->> R: findCardBy(cardID)
R -->>S: card
alt card == null
        S -->>U: InvalidCardIdException
end
S ->> U: balance
```

## Transfer
```mermaid
sequenceDiagram
actor U as User
participant S as CardService
participant R as CardRepo

U ->> S: transfer(cardID, otherCardID, amount)
alt amount < 0
        S -->> U: InvalidAmountException
end
S ->> R: findCardByID(cardID)
R -->> S: thisCard
S ->> R: findCardByID(otherCardID) 
R -->> S: otherCard

alt thisCard == null || otherCard == null
    S ->> U: InvalidCredentialsException
end
alt card.balance < amount
    S ->> U: NotEnoughMoneyException
end

%%S ->> R: transfer(cardID, otherCardID, amount)
S ->> R: updateAmount(thisCard, amount)
S ->> R: updateAmount(otherCard, amount)
```