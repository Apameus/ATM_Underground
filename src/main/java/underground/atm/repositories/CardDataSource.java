package underground.atm.repositories;

import underground.atm.data.Card;

import java.util.Collection;

public interface CardDataSource {
    Collection<Card> load();
    void save(Collection<Card> creditCards);
}
