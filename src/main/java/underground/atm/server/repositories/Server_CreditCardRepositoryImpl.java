package underground.atm.server.repositories;

import underground.atm.common.data.CreditCard;
import underground.atm.server.repositories.map.FileHashMap;

import java.io.IOException;

public final class Server_CreditCardRepositoryImpl implements Server_CreditCardRepository {
//    private final CreditCardDataSource creditCardDataSource;
//    private final HashMap<Integer,CreditCard> creditCardCache ; //

    private final FileHashMap<Integer,CreditCard> fileHashMap;

    public Server_CreditCardRepositoryImpl(FileHashMap<Integer, CreditCard> fileHashMap) {
        this.fileHashMap = fileHashMap;
    }

//    public Server_CreditCardRepositoryImpl(CreditCardDataSource creditCardDataSource) {
//        this.creditCardDataSource = creditCardDataSource;
//        creditCardCache = (HashMap<Integer, CreditCard>) creditCardDataSource.load();
//    }

    @Override
    public CreditCard findCardBy(int creditID) {
        try {
            CreditCard creditCard = fileHashMap.get(creditID);
            if (creditCard.id() == creditID) return creditCard;
            return null;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void updateAmount(int cardID, int amount) {
        try {
            CreditCard creditCard = fileHashMap.get(cardID);
            if (creditCard == null) throw new IllegalStateException();
            fileHashMap.put(cardID, creditCard.withAmount(amount));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
