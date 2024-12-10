package underground.atm.server.repositories;

import underground.atm.common.codec.CreditCardCodec;
import underground.atm.common.data.CreditCard;
import underground.atm.server.serializers.CreditCardSerializer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class FileBasedCreditCardDataSourceImpl implements CreditCardDataSource {
    private final Path path;
    CreditCardCodec creditCardCodec;
    private HashMap<Integer, CreditCard> creditCardCache;
    //    private final CreditCardSerializer serializer;
    //    private List<CreditCard> creditCardCache;

    public FileBasedCreditCardDataSourceImpl(Path path) {
        this.path = path;
        creditCardCache = new HashMap<>();
        creditCardCodec = new CreditCardCodec();
    }

    /*  FORM **
    totalCredits - CreditCard - CreditCard ..
     */

    @Override
    public void save(Map<Integer, CreditCard> creditCards) {
        try (var out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(path)))) {
            out.writeInt(creditCards.size()); //TODO: Implementation incomplete !
            for (var entry : creditCards.entrySet()) {
                creditCardCodec.encodeCreditCard(out, entry.getValue());
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
                CreditCard creditCard = creditCardCodec.decodeCreditCard(inputStream);
                creditCardCache.put(creditCard.id(), creditCard);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return creditCardCache;
    }


    //    @Override
//    public Collection<CreditCard> load() {
//        try {
//            List<CreditCard> creditCards = new ArrayList<>();
//            Files.readAllLines(path).forEach(line -> creditCards.add(serializer.parse(line)));
//            return creditCards;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


//    @Override
//    public void save(Collection<CreditCard> creditCreditCards) {
//        List<String> lines = new ArrayList<>();
//        creditCreditCards.forEach(card -> lines.add(serializer.serialize(card)));
//        try {
//            Files.write(path,lines);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
