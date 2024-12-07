package underground.atm.common;

public sealed interface Request {

    //
    record AuthorizeRequest(int id, String pin) implements Request{
        public static final int TYPE = 1;
    }
    record DepositRequest(int id, int amount) implements Request{
        public static final int TYPE = 2;
    }
    record WithdrawRequest(int id, int amount) implements Request{
        public static final int TYPE = 3;
    }
    record TransferRequest(int fromId, int toId, int amount) implements Request{
        public static final int TYPE = 4;
    }
    record ViewBalanceRequest(int id) implements Request{
        public static final int TYPE = 5;
    }

//    record FindCreditCardRequest(int id) implements Request {
//        public static final int TYPE = 8;
//    }
//    record UpdateAmountRequest(int id, int amount) implements Request {
//        public static final int TYPE = 9;
//    }
}
