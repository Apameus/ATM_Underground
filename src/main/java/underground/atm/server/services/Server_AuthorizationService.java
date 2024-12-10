package underground.atm.server.services;

import underground.atm.common.data.CreditCard;
import underground.atm.common.exceptions.exceptions.AuthorizationFailedException;
import underground.atm.common.log.Logger;
import underground.atm.server.repositories.Server_CreditCardRepository;

import java.util.Objects;

public final class Server_AuthorizationService {
    private final Server_CreditCardRepository creditCardRepository;
    private final Logger logger;

    public Server_AuthorizationService(Server_CreditCardRepository creditCardRepository, Logger.Factory logFactory) {
        this.creditCardRepository = creditCardRepository;
        this.logger = logFactory.create("Server_AuthorizationService");
    }

    public CreditCard authorize(int cardID, String pin) throws AuthorizationFailedException {
        CreditCard creditCard = creditCardRepository.findCardBy(cardID);
        if (creditCard == null || !Objects.equals(creditCard.pin(), pin)) {
            logger.log("Authorization failed !");
            throw new AuthorizationFailedException();
        }
        logger.log("CreditCard authorized");
        return creditCard;
    }
}
