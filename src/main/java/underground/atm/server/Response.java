package underground.atm.server;

public sealed interface Response {
    record FindCreditCardResponse() implements Response {
    }

    record UpdateAmountResponse() implements Response {
    }
}
