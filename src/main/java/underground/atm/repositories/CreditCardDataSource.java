package underground.atm.repositories;

import underground.atm.data.CreditCard;

import java.util.Collection;

public interface CreditCardDataSource {
    Collection<CreditCard> load();
    void save(Collection<CreditCard> creditCreditCards);
}
