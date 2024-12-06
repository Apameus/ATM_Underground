package underground.atm.server;

import underground.atm.data.CreditCard;

public sealed interface Response {

    record AuthorizeResponse() implements Response{
        public static final int TYPE = 11;
    }
    record DepositResponse() implements Response{
        public static final int TYPE = 12;
    }
    record WithDrawResponse() implements Response{
        public static final int TYPE = 13;
    }
    record TransferResponse() implements Response{
        public static final int TYPE = 14;
    }
    record ViewBalanceResponse(int balance) implements Response{
        public static final int TYPE = 15;
    }

    record ErrorResponse(int exceptionType) implements Response{
        public static final int TYPE = 90;
    }

    //    record FindCreditCardResponse(CreditCard creditCard) implements Response {
//        public static final int TYPE = 1;
//    }
//    record UpdateAmountResponse() implements Response {
//        public static final int TYPE = 2;
//    }

}
