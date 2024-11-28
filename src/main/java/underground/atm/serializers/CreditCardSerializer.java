package underground.atm.serializers;

import underground.atm.data.CreditCard;

public final class CreditCardSerializer {
    public CreditCard parse(String line){
        String[] values = line.split(",");
        return new CreditCard(Integer.parseInt(values[0]), values[1], Integer.parseInt(values[2]));
    }

    public String serialize(CreditCard creditCard){
        return creditCard.id() + "," + creditCard.pin() + "," + creditCard.amount();
    }
}
