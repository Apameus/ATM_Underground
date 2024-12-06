package underground.atm.services;

import underground.atm.data.CreditCard;

import java.util.HashMap;

public class CreditCardService {
    // Hashmap with constructor injection
    HashMap<Integer, CreditCard> creditCardHashMap;

    public CreditCardService(HashMap<Integer, CreditCard> creditCardHashMap) {
        this.creditCardHashMap = creditCardHashMap;
    }

    public CreditCard findCreditCardBy(int id) {
        return creditCardHashMap.get(id);
    }

    public void updateAmount(int id, int amount) {
        CreditCard creditCard = creditCardHashMap.get(id);
        creditCardHashMap.put(id, creditCard.withAmount(amount));
    }
}
