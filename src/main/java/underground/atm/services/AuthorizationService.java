package underground.atm.services;

import underground.atm.data.CreditCard;
import underground.atm.exceptions.AuthorizationFailedException;
import underground.atm.repositories.CreditCardRepository;

import java.util.Objects;

public final class AuthorizationService {
    private final CreditCardRepository creditCardRepository;

    public AuthorizationService(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public CreditCard authorize(int cardID, String pin) throws AuthorizationFailedException {
        CreditCard creditCard = creditCardRepository.findCardBy(cardID);
        if (creditCard == null || !Objects.equals(creditCard.pin(), pin)) throw new AuthorizationFailedException();
        return creditCard;
    }
}
