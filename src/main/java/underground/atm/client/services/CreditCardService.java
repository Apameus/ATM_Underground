package underground.atm.client.services;

import underground.atm.client.repositories.CreditCardRepository;
import underground.atm.common.exceptions.exceptions.CreditCardNotFoundException;
import underground.atm.common.exceptions.exceptions.InvalidAmountException;
import underground.atm.common.exceptions.exceptions.NotEnoughMoneyException;

public class CreditCardService {
    private final CreditCardRepository creditCardRepository;

    public CreditCardService(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public void withdraw(int authorizedCreditCardID, int amount) throws NotEnoughMoneyException, InvalidAmountException, CreditCardNotFoundException {
        creditCardRepository.withdraw(authorizedCreditCardID, amount);
    }

    public void transfer(int fromID, int toID, int amount) throws NotEnoughMoneyException, InvalidAmountException, CreditCardNotFoundException {
        creditCardRepository.transfer(fromID, toID, amount);
    }

    public int viewBalance(int authorizedCreditCardID) throws CreditCardNotFoundException {
        return creditCardRepository.viewBalance(authorizedCreditCardID);
    }

    public void deposit(int authorizedCreditCardID, int amount) throws InvalidAmountException, CreditCardNotFoundException {
        creditCardRepository.deposit(authorizedCreditCardID, amount);
    }
}
