package underground.atm.server.repositories;

import underground.atm.common.data.CreditCard;

import java.util.Collection;

public interface CreditCardDataSource {
    Collection<CreditCard> load();
    void save(Collection<CreditCard> creditCreditCards);
}
