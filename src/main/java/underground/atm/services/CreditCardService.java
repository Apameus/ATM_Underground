package underground.atm.services;

import underground.atm.data.CreditCard;
import underground.atm.exceptions.CreditCardNotFoundException;
import underground.atm.exceptions.InvalidAmountException;
import underground.atm.exceptions.NotEnoughMoneyException;
import underground.atm.repositories.CreditCardRepository;

public final class CreditCardService {
    private final CreditCardRepository creditCardRepository;

    public CreditCardService(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public void deposit(int cardID, int amount) throws CreditCardNotFoundException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException();
//        else if (amount > 1000) throw new ExtraValidationNeededException();
        CreditCard creditCard = creditCardRepository.findCardBy(cardID);
        if (creditCard == null) throw new IllegalStateException();//TODO: Should this be CreditCardNotFoundExc ?
        creditCardRepository.updateAmount(cardID, creditCard.amount() + amount);
    }

    public void withdraw(int cardID, int amount) throws CreditCardNotFoundException, NotEnoughMoneyException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException();
//        else if (amount > 1000) throw new ExtraValidationNeededException();
        CreditCard creditCard = creditCardRepository.findCardBy(cardID);
        if (creditCard == null) throw new IllegalStateException(); //TODO: Should this be CreditCardNotFoundExc ?
        if (creditCard.amount() < amount) throw new NotEnoughMoneyException();
        creditCardRepository.updateAmount(cardID, creditCard.amount() - amount);
    }

    public int viewBalance(int cardID) throws CreditCardNotFoundException {
        CreditCard creditCard = creditCardRepository.findCardBy(cardID);
        if (creditCard == null) throw new IllegalStateException();
        return creditCard.amount();
    }

    //TODO: Check if toID == fromID
    public void transfer(int fromID, int toID, int amount) throws InvalidAmountException, CreditCardNotFoundException, NotEnoughMoneyException {
        if (amount <= 0) throw new InvalidAmountException();
        CreditCard fromCreditCard = creditCardRepository.findCardBy(fromID);
        CreditCard toCreditCard = creditCardRepository.findCardBy(toID);
        if (fromCreditCard == null) throw new IllegalStateException(); //TODO ..?
        if (toCreditCard == null) throw new CreditCardNotFoundException();
        if (fromID == toID) ; //TODO..
        if (fromCreditCard.amount() < amount) throw new NotEnoughMoneyException();

        creditCardRepository.updateAmount(fromID, fromCreditCard.amount() - amount);
        creditCardRepository.updateAmount(toID, toCreditCard.amount() + amount);
    }





}
