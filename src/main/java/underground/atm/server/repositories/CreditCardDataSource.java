package underground.atm.server.repositories;

import underground.atm.common.data.CreditCard;

import java.util.Collection;
import java.util.Map;

public interface CreditCardDataSource {
    Map<Integer,CreditCard> load();
    void save(Map<Integer,CreditCard> creditCreditCards);
}
