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

## Packages:

```
- AuthorizeRequest(ID, Pin)
- DepositRequest(ID, Amount)
- WithdrawRequest(ID, Amount)
- TransferRequest(fromID, toID, Amount)
- ViewBalanceRequest(ID)

//
- AuthorizeResponse()
- DepositResponse()
- WithdrawResponse()
- TransferResponse()
- ViewBalanceResponse(int Amount)

- ErrorResponse(int ExceptionType)
     |       AuthorizationFailedExc (91),
     InvalidAmountExc (92), CreditCardNotFoundExc (93),
     NotEnoughMoneyExc (94), IllegalStateExc (95)     |
```

```
FindCreditCardRequest
Type = 1 | ID

FindCreditCardResponse
 | CreditCard |

UpdateAmountRequest
Type = 2 | ID, Amount

* CreditCard = ID(4b) | PIN(4b) | Amount(4b) |
```
