package underground.atm.server.codec;

import underground.atm.common.data.CreditCard;
import java.io.IOException;
import java.io.RandomAccessFile;

public record CreditCardCodec(StringCodec stringCodec) implements Codec<CreditCard> {

    @Override
    public int maxBytesSize() {
        return 25;
    } // 4 (ID) + 1 (PinLen) + 16 (maxPin) + 4 (Amount)

    @Override
    public CreditCard read(RandomAccessFile accessFile) throws IOException {
        int id = accessFile.readInt();
        String pin = stringCodec.read(accessFile);
        int amount = accessFile.readInt();
        return new CreditCard(id, pin, amount);
    }

    @Override
    public void write(RandomAccessFile accessFile, CreditCard creditCard) throws IOException {
        accessFile.writeInt(creditCard.id());
        stringCodec.write(accessFile, creditCard.pin());
        accessFile.writeInt(creditCard.amount());
    }


//    public void updateAmountOfCredit(int CreditID, int amount) throws IOException {
//        accessFile.seek(offset(CreditID) + 25); // 25 = Bytes (Key + Credit) - amount
//        accessFile.writeInt(amount);
//    }
}
