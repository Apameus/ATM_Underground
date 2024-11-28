package underground.atm.repositories;

import underground.atm.data.Card;

import java.util.Collection;
import java.util.Iterator;

public final class CardRepositoryImpl implements CardRepository {
    // Instead of cache we will take the data straight from the source
    // ( parse & serialize file in every transaction

    private final CardDataSource cardDataSource;

    public CardRepositoryImpl(CardDataSource cardDataSource) {
        this.cardDataSource = cardDataSource;
    }

    @Override
    public Card findCardBy(int creditID) {
        for (Card card : cardDataSource.load()) {
            if (card.id() == creditID) return card;
        } return null;
    }

    @Override
    public void updateAmount(int cardID, int amount) {
        Collection<Card> cards = cardDataSource.load();
        Card card = null;
        forloop:
        for (Iterator<Card> iterator = cards.iterator(); iterator.hasNext(); ) {
            card = iterator.next();
            if (card.id() == cardID) {
                iterator.remove();
                break forloop;
            }
        }
        if (card == null) throw new IllegalStateException();
        cards.add(card.withAmount(amount));
        cardDataSource.save(cards);
    }
}
