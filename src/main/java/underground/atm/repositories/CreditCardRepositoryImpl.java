package underground.atm.repositories;

import underground.atm.data.CreditCard;

import java.util.Collection;
import java.util.Iterator;

public final class CreditCardRepositoryImpl implements CreditCardRepository {
    // Instead of cache we will take the data straight from the source
    // ( parse & serialize file in every transaction

    private final CreditCardDataSource creditCardDataSource;

    public CreditCardRepositoryImpl(CreditCardDataSource creditCardDataSource) {
        this.creditCardDataSource = creditCardDataSource;
    }

    @Override
    public CreditCard findCardBy(int creditID) {
        for (CreditCard creditCard : creditCardDataSource.load()) {
            if (creditCard.id() == creditID) return creditCard;
        } return null;
    }

    @Override
    public void updateAmount(int cardID, int amount) {
        Collection<CreditCard> creditCards = creditCardDataSource.load();
        CreditCard creditCard = null;
        forloop:
        for (Iterator<CreditCard> iterator = creditCards.iterator(); iterator.hasNext(); ) {
            creditCard = iterator.next();
            if (creditCard.id() == cardID) {
                iterator.remove();
                break forloop;
            }
        }
        if (creditCard == null) throw new IllegalStateException();
        creditCards.add(creditCard.withAmount(amount));
        creditCardDataSource.save(creditCards);
    }
}
