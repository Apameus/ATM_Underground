package underground.atm.server.repositories;

import underground.atm.common.data.CreditCard;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

class CreditCardDynamicMemoryManager { //TODO: Logic should be refactored to CreditCard functionality
    final RandomAccessFile accessFile;
    long maxEntries;
    long storedEntries;

    final int maxSizeOfEntry = 25; //  4 (ID) + 1 (PinLen) + 16 (Pin) + 4 (Amount)
    final int maxSizeOfPin = 16;

    CreditCardDynamicMemoryManager(Path path) throws IOException {
        accessFile = new RandomAccessFile(path.toFile(), "rw");
        maxEntries = 16;
        accessFile.setLength(maxEntries * maxSizeOfEntry);
    }

    void put(int creditCardID, CreditCard creditCard) throws IOException {
        accessFile.seek(offset(creditCardID));
        accessFile.writeInt(creditCard.id());
        accessFile.writeByte(creditCard.pin().length());
        accessFile.write(creditCard.pin().getBytes());
        accessFile.writeInt(creditCard.amount());

        storedEntries++;
    }

    CreditCard get(int creditCardID) throws IOException {
        long offset = offset(creditCardID);
        int id;

        // Collision check
        while (true){
            if (offset >= accessFile.length()) return null; // key doesn't exist
            accessFile.seek(offset); // key = CreditCard.ID (4B) | value = CreditCard (25B)
            id = accessFile.readInt();
            if (id == creditCardID) break;
            offset += maxSizeOfEntry;
        }

        int pinLen = (int) accessFile.readByte();
        byte[] bytes = new byte[pinLen];
        accessFile.read(bytes);
        String pin = new String(bytes);

        int amount = accessFile.readInt();
        return new CreditCard(id, pin, amount);
    }

    public void updateAmountOfCredit(int CreditID, int amount) throws IOException {
        accessFile.seek(offset(CreditID) + 21); // 21 = Bytes of Credit - amount
        accessFile.writeInt(amount);
    }

    private long offset(int key) {
        long has = Math.abs(key) % maxEntries;
        return has * maxSizeOfEntry;
    }

}
