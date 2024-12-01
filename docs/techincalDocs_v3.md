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
- FindCreditCardRequest(ID)

- FindCreditCardResponse(CreditCard)

- UpdateAmountRequest(ID, Amount)
```

```
FindCreditCardRequest
Type = 1 | ID

FindCreditCardResponse
 | CreditCard |

UpdateAmountRequest
Type = 2 | ID, Amount

* CreditCard = ID | PIN | Amount |
* ID = ID.length (4b) | ID (4b)
* PIN = PIN.length (4b) | PIN (4b)
* Amount =  Amount.length (4b) | Amount (4b)
```
