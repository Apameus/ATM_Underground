package underground.atm.repositories;

import underground.atm.data.CreditCard;
import underground.atm.serializers.CreditCardSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CreditCardDataSourceImpl implements CreditCardDataSource {
    private final Path path;
    private final CreditCardSerializer serializer;

    public CreditCardDataSourceImpl(Path path, CreditCardSerializer serializer) {
        this.path = path;
        this.serializer = serializer;
    }

    @Override
    public Collection<CreditCard> load() {
        try {
            List<CreditCard> creditCards = new ArrayList<>();
            Files.readAllLines(path).forEach(line -> creditCards.add(serializer.parse(line)));
            return creditCards;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Collection<CreditCard> creditCreditCards) {
        List<String> lines = new ArrayList<>();
        creditCreditCards.forEach(card -> lines.add(serializer.serialize(card)));
        try {
            Files.write(path,lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
