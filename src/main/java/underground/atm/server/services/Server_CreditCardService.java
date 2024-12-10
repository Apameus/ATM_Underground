package underground.atm.server.services;

import underground.atm.common.data.CreditCard;
import underground.atm.common.exceptions.exceptions.CreditCardNotFoundException;
import underground.atm.common.exceptions.exceptions.InvalidAmountException;
import underground.atm.common.exceptions.exceptions.NotEnoughMoneyException;
import underground.atm.common.log.Logger;
import underground.atm.server.repositories.Server_CreditCardRepository;

public class Server_CreditCardService {
    private final Server_CreditCardRepository serverCreditCardRepository;
    private final Logger logger;

    public Server_CreditCardService(Server_CreditCardRepository serverCreditCardRepository, Logger.Factory logFactory) {
        this.serverCreditCardRepository = serverCreditCardRepository;
        this.logger = logFactory.create("Server_CreditCardService");
    }

    public void deposit(int cardID, int amount) throws CreditCardNotFoundException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException();
//        else if (amount > 1000) throw new ExtraValidationNeededException();
        CreditCard creditCard = serverCreditCardRepository.findCardBy(cardID);
        if (creditCard == null) throw new IllegalStateException();
        serverCreditCardRepository.updateAmount(cardID, creditCard.amount() + amount);
        logger.log("Depositing the amount of %s for creditID: %s", amount, cardID);
    }

    public void withdraw(int cardID, int amount) throws CreditCardNotFoundException, NotEnoughMoneyException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException();
//        else if (amount > 1000) throw new ExtraValidationNeededException();
        CreditCard creditCard = serverCreditCardRepository.findCardBy(cardID);
        if (creditCard == null) throw new IllegalStateException();
        if (creditCard.amount() < amount) throw new NotEnoughMoneyException();
        serverCreditCardRepository.updateAmount(cardID, creditCard.amount() - amount);
        logger.log("Withdrawing the amount of %s for creditID: %s", amount, cardID);
    }

    public int viewBalance(int cardID) throws CreditCardNotFoundException {
        CreditCard creditCard = serverCreditCardRepository.findCardBy(cardID);
        if (creditCard == null) throw new IllegalStateException();
        logger.log("Viewing the balance (%s) for creditID: %s", creditCard.amount(), cardID);
        return creditCard.amount();
    }

    //TODO: Check if toID == fromID
    public void transfer(int fromID, int toID, int amount) throws InvalidAmountException, CreditCardNotFoundException, NotEnoughMoneyException {
        if (amount <= 0) throw new InvalidAmountException();
        CreditCard fromCreditCard = serverCreditCardRepository.findCardBy(fromID);
        CreditCard toCreditCard = serverCreditCardRepository.findCardBy(toID);
        if (fromCreditCard == null) throw new IllegalStateException();
        if (toCreditCard == null) throw new CreditCardNotFoundException();
        if (fromID == toID) ; //TODO..
        if (fromCreditCard.amount() < amount) throw new NotEnoughMoneyException();

        serverCreditCardRepository.updateAmount(fromID, fromCreditCard.amount() - amount);
        serverCreditCardRepository.updateAmount(toID, toCreditCard.amount() + amount);

        logger.log("Transferring the amount of %s from creditID: %s, to creditID: %s", amount, fromID, toID);
    }





}
