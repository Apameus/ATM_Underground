package underground.atm.server;

import underground.atm.data.CreditCard;

public sealed interface Response {
    record FindCreditCardResponse(CreditCard creditCard) implements Response {
    }

    record UpdateAmountResponse() implements Response {
    }
}
