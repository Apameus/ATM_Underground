package underground.atm.repositories;

import underground.atm.data.Card;
import underground.atm.serializers.CardSerializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CardDataSourceImpl implements CardDataSource {
    private final Path path;
    private final CardSerializer serializer;

    public CardDataSourceImpl(Path path, CardSerializer serializer) {
        this.path = path;
        this.serializer = serializer;
    }

    @Override
    public Collection<Card> load() {
        try {
            List<Card> cards = new ArrayList<>();
            Files.readAllLines(path).forEach(line -> cards.add(serializer.parse(line)));
            return cards;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Collection<Card> creditCards) {
        List<String> lines = new ArrayList<>();
        creditCards.forEach(card -> lines.add(serializer.serialize(card)));
        try {
            Files.write(path,lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
