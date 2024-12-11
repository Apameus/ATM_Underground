package underground.atm.common.codec;

import underground.atm.common.data.CreditCard;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class CreditCardCodec {


    public void encodeCreditCard(DataOutputStream outputStream, CreditCard creditCard) throws IOException {
        outputStream.writeInt(creditCard.id());
        CodecUtils.encodeString(outputStream,creditCard.pin());
        outputStream.writeInt(creditCard.amount());
    }

    public CreditCard decodeCreditCard(DataInputStream inputStream) throws IOException {
        int id = inputStream.readInt();
       String pin = CodecUtils.decodeString(inputStream);
        int amount = inputStream.readInt();
        return new CreditCard(id, pin, amount);
    }
}
