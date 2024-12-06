package underground.atm.services;

import underground.atm.data.CreditCard;
import underground.atm.exceptions.CreditCardNotFoundException;
import underground.atm.exceptions.InvalidAmountException;
import underground.atm.exceptions.NotEnoughMoneyException;
import underground.atm.repositories.Server_CreditCardRepository;

public final class Server_CreditCardService {
    private final Server_CreditCardRepository serverCreditCardRepository;

    public Server_CreditCardService(Server_CreditCardRepository serverCreditCardRepository) {
        this.serverCreditCardRepository = serverCreditCardRepository;
    }

    public void deposit(int cardID, int amount) throws CreditCardNotFoundException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException();
//        else if (amount > 1000) throw new ExtraValidationNeededException();
        CreditCard creditCard = serverCreditCardRepository.findCardBy(cardID);
        if (creditCard == null) throw new IllegalStateException();//TODO: Should this be CreditCardNotFoundExc ?
        serverCreditCardRepository.updateAmount(cardID, creditCard.amount() + amount);
    }

    public void withdraw(int cardID, int amount) throws CreditCardNotFoundException, NotEnoughMoneyException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException();
//        else if (amount > 1000) throw new ExtraValidationNeededException();
        CreditCard creditCard = serverCreditCardRepository.findCardBy(cardID);
        if (creditCard == null) throw new IllegalStateException(); //TODO: Should this be CreditCardNotFoundExc ?
        if (creditCard.amount() < amount) throw new NotEnoughMoneyException();
        serverCreditCardRepository.updateAmount(cardID, creditCard.amount() - amount);
    }

    public int viewBalance(int cardID) throws CreditCardNotFoundException {
        CreditCard creditCard = serverCreditCardRepository.findCardBy(cardID);
        if (creditCard == null) throw new IllegalStateException();
        return creditCard.amount();
    }

    //TODO: Check if toID == fromID
    public void transfer(int fromID, int toID, int amount) throws InvalidAmountException, CreditCardNotFoundException, NotEnoughMoneyException {
        if (amount <= 0) throw new InvalidAmountException();
        CreditCard fromCreditCard = serverCreditCardRepository.findCardBy(fromID);
        CreditCard toCreditCard = serverCreditCardRepository.findCardBy(toID);
        if (fromCreditCard == null) throw new IllegalStateException(); //TODO ..?
        if (toCreditCard == null) throw new CreditCardNotFoundException();
        if (fromID == toID) ; //TODO..
        if (fromCreditCard.amount() < amount) throw new NotEnoughMoneyException();

        serverCreditCardRepository.updateAmount(fromID, fromCreditCard.amount() - amount);
        serverCreditCardRepository.updateAmount(toID, toCreditCard.amount() + amount);
    }





}
