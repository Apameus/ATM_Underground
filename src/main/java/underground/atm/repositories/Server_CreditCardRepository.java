package underground.atm.repositories;

import underground.atm.data.CreditCard;

public interface Server_CreditCardRepository {
    CreditCard findCardBy(int creditID);

    void updateAmount(int cardID, int amount);


}
