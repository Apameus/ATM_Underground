package underground.atm.server.repositories;

import underground.atm.common.data.CreditCard;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public final class Server_CreditCardRepositoryImpl implements Server_CreditCardRepository {
    private final CreditCardDataSource creditCardDataSource;
    private final HashMap<Integer,CreditCard> creditCardCache ;

    public Server_CreditCardRepositoryImpl(CreditCardDataSource creditCardDataSource) {
        this.creditCardDataSource = creditCardDataSource;
        creditCardCache = (HashMap<Integer, CreditCard>) creditCardDataSource.load();
    }

    @Override
    public CreditCard findCardBy(int creditID) {
        var creditCard = creditCardCache.get(creditID);
        if (creditCard.id() == creditID) return creditCard;
        return null;
    }

    @Override
    public void updateAmount(int cardID, int amount) {
        CreditCard creditCard = creditCardCache.get(cardID);
        if (creditCard == null) throw new IllegalStateException();
        creditCardCache.put(cardID,creditCard.withAmount(amount));
        creditCardDataSource.save(creditCardCache);
    }
}
