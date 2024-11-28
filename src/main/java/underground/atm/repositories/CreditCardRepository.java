package underground.atm.repositories;

import underground.atm.data.CreditCard;

public interface CreditCardRepository {
    CreditCard findCardBy(int creditID);

    void updateAmount(int cardID, int amount);


}
