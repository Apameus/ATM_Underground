package underground.atm.server;

public sealed interface Request {
    record FindCreditCardRequest(int id) implements Request {
        public static final int TYPE = 1;
    }

    record UpdateAmountRequest(int id, int amount) implements Request {
        public static final int TYPE = 2;
    }
}
