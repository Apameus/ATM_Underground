package underground.atm.server.services;

import underground.atm.common.data.CreditCard;
import underground.atm.common.exceptions.exceptions.AuthorizationFailedException;
import underground.atm.server.repositories.Server_CreditCardRepository;

import java.util.Objects;

public final class Server_AuthorizationService {
    private final Server_CreditCardRepository creditCardRepository;

    public Server_AuthorizationService(Server_CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public CreditCard authorize(int cardID, String pin) throws AuthorizationFailedException {
        CreditCard creditCard = creditCardRepository.findCardBy(cardID);
        if (creditCard == null || !Objects.equals(creditCard.pin(), pin)) throw new AuthorizationFailedException();
        return creditCard;
    }
}