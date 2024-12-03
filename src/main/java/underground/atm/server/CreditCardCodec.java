package underground.atm.server;

import underground.atm.data.CreditCard;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class CreditCardCodec {

    public void encodeCreditCard(DataOutputStream outputStream, CreditCard creditCard) throws IOException {
        outputStream.writeInt(creditCard.id());
        outputStream.writeInt(Integer.parseInt(creditCard.pin()));
        outputStream.writeInt(creditCard.amount());
    }

    public CreditCard decodeCreditCard(DataInputStream inputStream) throws IOException {
        int id = inputStream.readInt();
        int pin = inputStream.readInt();
        int amount = inputStream.readInt();
        return new CreditCard(id, String.valueOf(pin), amount);
    }
}
