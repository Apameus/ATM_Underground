package underground.atm.server.repositories;

import underground.atm.common.codec.CreditCardStreamCodec;
import underground.atm.common.data.CreditCard;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class FileBasedCreditCardDataSourceImpl implements CreditCardDataSource {
    private final Path path;
    CreditCardStreamCodec creditCardStreamCodec;
    private HashMap<Integer, CreditCard> creditCardCache;

    public FileBasedCreditCardDataSourceImpl(Path path) {
        this.path = path;
        creditCardCache = new HashMap<>();
        creditCardStreamCodec = new CreditCardStreamCodec();
    }

    /*  FORM **
    totalCredits - CreditCard - CreditCard ..
     */

    @Override
    public void save(Map<Integer, CreditCard> creditCards) {
        try (var out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(path)))) {
            out.writeInt(creditCards.size()); //TODO: Implementation incomplete !
            for (var entry : creditCards.entrySet()) {
                creditCardStreamCodec.encodeCreditCard(out, entry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Integer, CreditCard> load() {
        try (var inputStream = new DataInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
            if (inputStream.available() == 0) {
                creditCardCache = new HashMap<>();
                return creditCardCache;
            }
            int amountOfCredits = inputStream.readInt(); //TODO: Implementation incomplete !
            creditCardCache = HashMap.newHashMap(amountOfCredits);
            for (int i = 0; i < amountOfCredits; i++) {
                CreditCard creditCard = creditCardStreamCodec.decodeCreditCard(inputStream);
                creditCardCache.put(creditCard.id(), creditCard);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return creditCardCache;
    }


}
