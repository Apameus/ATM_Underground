package underground.atm.server;

import underground.atm.data.CreditCard;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ResponseCodec {
    private CreditCardCodec creditCardCodec = new CreditCardCodec();

    public void encode(DataOutputStream dataOutput, Response response) throws IOException {
        switch (response) {
            case Response.FindCreditCardResponse(CreditCard creditCard) -> {
                creditCardCodec.encodeCreditCard(dataOutput,creditCard);
            }
            case Response.UpdateAmountResponse() -> {

            }
        }
    }

//    public Response decode(DataInputStream inputStream){
//
//    }
}