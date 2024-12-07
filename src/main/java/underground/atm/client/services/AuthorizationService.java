package underground.atm.client.services;

import underground.atm.client.repositories.CreditCardRepository;
import underground.atm.common.exceptions.exceptions.AuthorizationFailedException;

public final class AuthorizationService {
    private final CreditCardRepository creditCardRepository; //TODO: Make AuthRepo

    public AuthorizationService(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public void authorize(int cardID, String pin) throws AuthorizationFailedException {
        creditCardRepository.authorize(cardID,pin);
    }
}
