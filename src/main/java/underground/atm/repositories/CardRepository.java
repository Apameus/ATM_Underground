package underground.atm.repositories;

import underground.atm.data.Card;

public interface CardRepository {
    Card findCardBy(int creditID);

    void updateAmount(int cardID, int amount);


}
