package underground.atm.data;

public record Card(int id, int pin, int amount) {
    public Card withAmount(int amount) {
        return new Card(id, pin, amount);
    }
}
