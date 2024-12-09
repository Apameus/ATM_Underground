# Technical Documentation

### High Lever Architecture
- Client

```mermaid
flowchart LR
    UI --> CS[CardService] --> CR[CardRepo] --> P[Protocol] --> S[TCP Server]

```

- Server

```mermaid
flowchart LR
    S[TCP Server] --> C[Controller] --> CS[Service] --> R[Repo] --> FILE
```



### Application startup:
- Arguments for server:
```
--server ip port dataSourceFilePath
```
- Arguments for client:
```
--client ip port
```
## Packages:

```
- AuthorizeRequest(ID, Pin)
- DepositRequest(ID, Amount)
- WithdrawRequest(ID, Amount)
- TransferRequest(fromID, toID, Amount)
- ViewBalanceRequest(ID)

- AuthorizeResponse()
- DepositResponse()
- WithdrawResponse()
- TransferResponse()
- ViewBalanceResponse(int Amount)
- ErrorResponse(int ExceptionType)
  | AuthorizationFailedExc (91) |
  | InvalidAmountExc (92)       |
  | CreditCardNotFoundExc (93)  |
  | NotEnoughMoneyExc (94)      |
  | IllegalStateExc (95)        |
```



## Authorization:
```mermaid
sequenceDiagram
    actor U as User
    participant AS as AuthorizationService
    participant CR as CardRepository
    participant P as Protocol
    participant S as Server
    U ->> AS: authorize(creditID, pin)
    AS ->> CR: authorize(creditID, pin)
    CR ->> P: authorizeRequest(creditID,pin)
    P ->> S: Request
    alt auth failed
        S -->> P: ErrorResponse(91)
    end
    S -->> U: AuthorizeResponse
```
```mermaid
sequenceDiagram
    participant S as Server
    participant C as Controller
    participant SE as Service
    participant R as Repo
    S ->> C: handleRequest
    C ->> SE: authorize(id,pin)
    SE ->> R: findCreditBy(id)
    R -->> SE: Credit
    alt Credit == null || Credit.pin != pin
        SE -->> C: AuthFailedException
        C -->> S: ErrorResponse(91)
    end
   C -->> S: AuthResponse 
```

## Deposit:
```mermaid
sequenceDiagram
    actor U as User
    participant CS as CreditService
    participant CR as CreditRepository
    participant P as Protocol
    participant S as Server
    U ->> CS: deposit(creditID,amount)
    CS ->> CR: deposit(creditID,amount)
    CR ->> P: depositRequest(creditID,amount)
    P ->> S: Request
    alt InvalidAmountException
        S -->> P: ErrorResponse(92)
    end
    alt CreditNotFoundException
        S -->> P: ErrorResponse(93)
    end
    P -->> U: DepositResponse
```
```mermaid
sequenceDiagram
    participant S as Server
    participant C as Controller
    participant SE as Service
    participant R as Repo
    S ->> C: handleRequest
    C ->> SE: deposit(id,amount)
    alt amount <= 0
        SE -->> C: InvalidAmountExc
        C -->> S: ErrorResponse(92)
    end
    SE ->> R: findCreditBy(id)
    R -->> SE: Credit
    alt Credit == null
        SE -->> C: IllegalStateException
    end
    SE ->> R: UpdateAmount(id,amount)
    C -->> S: DepositResponse 
```

## Withdraw:
```mermaid
sequenceDiagram
    actor U as User
    participant CS as CreditService
    participant CR as CreditRepository
    participant P as Protocol
    participant S as Server
    U ->> CS: withdraw(creditID,amount)
    CS ->> CR: withdraw(creditID,amount)
    CR ->> P: withdrawRequest(creditID,amount)
    P ->> S: Request
    alt InvalidAmountException
        S -->> P: ErrorResponse(92)
    end
    alt CreditNotFoundException
        S -->> P: ErrorResponse(93)
    end
    alt NotEnoughMoneyException
        S -->> P: ErrorResponse(94)
    end
    P -->> U: WithdrawResponse
```

```mermaid
sequenceDiagram
    participant S as Server
    participant C as Controller
    participant SE as Service
    participant R as Repo
    S ->> C: handleRequest
    C ->> SE: withdraw(id,amount)
    alt amount <= 0
        SE -->> C: InvalidAmountExc
        C -->> S: ErrorResponse(92)
    end
    SE ->> R: findCreditBy(id)
    R -->> SE: Credit
    alt Credit == null
        SE -->> C: IllegalStateException
    end
    alt Credit.amount < amount
        SE -->> C: NotEnoughMoneyExc
        C -->> S: ErrorResponse(94)
    end
    SE ->> R: UpdateAmount(id,amount)
    C -->> S: WithdrawResponse 
```

## Transfer:
```mermaid
sequenceDiagram
    actor U as User
    participant CS as CreditService
    participant CR as CreditRepository
    participant P as Protocol
    participant S as Server
    U ->> CS: transfer(fromID,toID,amount)
    CS ->> CR: transfer(fromID,toID,amount)
    CR ->> P: transferRequest(fromID,toID,amount)
    P ->> S: Request
    alt InvalidAmountException
        S -->> P: ErrorResponse(92)
    end
    alt CreditNotFoundException
        S -->> P: ErrorResponse(93)
    end
    alt NotEnoughMoneyException
        S -->> P: ErrorResponse(94)
    end
    P -->> U: transferResponse
```
```mermaid
sequenceDiagram
    participant S as Server
    participant C as Controller
    participant SE as Service
    participant R as Repo
    S ->> C: handleRequest
    C ->> SE: transfer(fromID,toID,amount)
    alt amount <= 0
        SE -->> C: InvalidAmountExc
        C -->> S: ErrorResponse(92)
    end
    SE ->> R: findCreditBy(fromID)
    R -->> SE: fromCredit
    alt fromCredit == null
        SE -->> C: IllegalStateException
    end
    SE ->> R: findCreditBy(toID)
    R -->> SE: otherCredit
    alt toCredit == null
        SE -->> C: CreditNotFoundExc
        C -->> S: ErrorResponse(93)
    end
    alt fromCredit.amount < amount
        SE -->> C: NotEnoughMoneyExc
        C -->> S: ErrorResponse(94)
    end
    SE ->> R: UpdateAmount(fromID,amount)
    SE ->> R: UpdateAmount(toID,amount)
    C -->> S: WithdrawResponse 
```

## ViewBalance:
```mermaid
sequenceDiagram
    actor U as User
    participant CS as CreditService
    participant CR as CreditRepository
    participant P as Protocol
    participant S as Server
    U ->> CS: viewBalanceRequest(creditID)
    CS ->> CR: viewBalance(creditID)
    CR ->> P: viewBalanceRequest(creditID)
    P ->> S: Request
    alt CreditNotFoundException
        S -->> P: IllegalStateExc
    end
    S -->> U: ViewBalanceResponse(amount)
```
```mermaid
sequenceDiagram
    participant S as Server
    participant C as Controller
    participant SE as Service
    participant R as Repo
    S ->> C: handleRequest
    C ->> SE: viewBalance(id)
    SE ->> R: findCreditBy(id)
    R -->> SE: Credit
    alt Credit == null
        SE -->> C: IllegalStateException
    end
    C -->> S: DepositResponse(Credit.amount) 
```
