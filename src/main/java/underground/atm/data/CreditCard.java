package underground.atm.data;

public record CreditCard(int id, String pin, int amount) {
    public CreditCard withAmount(int amount) {
        return new CreditCard(id, pin, amount);
    }
}
