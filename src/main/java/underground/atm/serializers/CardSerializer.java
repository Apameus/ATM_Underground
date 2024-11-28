package underground.atm.serializers;

import underground.atm.data.Card;

public final class CardSerializer {
    public Card parse(String line){
        String[] values = line.split(",");
        return new Card(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
    }

    public String serialize(Card card){
        return card.id() + "," + card.pin() + "," + card.amount();
    }
}
