package underground.atm.server;

import underground.atm.data.CreditCard;

import java.io.DataOutputStream;
import java.io.IOException;

public class ResponseCodec {
    public void encode(DataOutputStream dataOutput, Response response) throws IOException {
        switch (response) {
            case Response.FindCreditCardResponse(CreditCard creditCard) -> {
                dataOutput.writeInt(creditCard.id());
                dataOutput.write(creditCard.pin().getBytes());
                dataOutput.writeInt(creditCard.amount());
            }
            case Response.UpdateAmountResponse() -> {

            }
        }

    }
}
