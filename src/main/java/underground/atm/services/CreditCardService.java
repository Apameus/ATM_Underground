package underground.atm.services;

import underground.atm.data.Card;
import underground.atm.exceptions.AuthorizationFailedException;
import underground.atm.exceptions.CardNotFoundException;
import underground.atm.exceptions.InvalidAmountException;
import underground.atm.exceptions.NotEnoughMoneyException;
import underground.atm.repositories.CardRepository;

public final class CreditCardService {
    private final CardRepository cardRepository;

    public CreditCardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void deposit(int cardID, int amount) throws CardNotFoundException, InvalidAmountException {
        if (amount < 0) throw new InvalidAmountException();
//        else if (amount > 1000) throw new ExtraValidationNeededException();
        Card card = cardRepository.findCardBy(cardID);
        if (card == null) throw new CardNotFoundException();
        cardRepository.updateAmount(cardID,card.amount() + amount);
    }

    public void withdraw(int cardID, int amount) throws CardNotFoundException, NotEnoughMoneyException, InvalidAmountException {
        if (amount < 0) throw new InvalidAmountException();
//        else if (amount > 1000) throw new ExtraValidationNeededException();
        Card card = cardRepository.findCardBy(cardID);
        if (card == null) throw new CardNotFoundException();
        if (card.amount() < amount) throw new NotEnoughMoneyException();
        cardRepository.updateAmount(cardID,card.amount() - amount);
    }

    public int viewBalance(int cardID) throws CardNotFoundException {
        Card card = cardRepository.findCardBy(cardID);
        if (card == null) throw new CardNotFoundException();
        return card.amount();
    }

    public void transfer(int fromID, int toID, int amount) throws InvalidAmountException, CardNotFoundException, NotEnoughMoneyException {
        if (amount <= 0) throw new InvalidAmountException();
        Card fromCard = cardRepository.findCardBy(fromID);
        Card toCard = cardRepository.findCardBy(toID);
        if (fromCard == null || toCard == null) throw new CardNotFoundException();
        if (fromCard.amount() < amount) throw new NotEnoughMoneyException();

        cardRepository.updateAmount(fromID, fromCard.amount() - amount);
        cardRepository.updateAmount(toID, toCard.amount() + amount);
    }





}
