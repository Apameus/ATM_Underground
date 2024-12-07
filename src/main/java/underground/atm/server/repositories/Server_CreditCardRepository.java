package underground.atm.server.repositories;

import underground.atm.common.data.CreditCard;

public interface Server_CreditCardRepository {
    CreditCard findCardBy(int creditID);

    void updateAmount(int cardID, int amount);


}
