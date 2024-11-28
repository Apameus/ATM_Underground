package underground.atm.services;

import underground.atm.data.Card;
import underground.atm.exceptions.AuthorizationFailedException;
import underground.atm.repositories.CardRepository;

public final class AuthorizationService {
    private final CardRepository cardRepository;

    public AuthorizationService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Card authorize(int cardID, int pin) throws AuthorizationFailedException {
        Card card = cardRepository.findCardBy(cardID);
        if (card == null || card.pin() != pin) throw new AuthorizationFailedException();
        return card;
    }
}
