package underground.atm.server.repositories.map;

record ControlledHashObject(int hashcode, String key) {
    @Override
    public int hashCode() {
        return hashcode;
    }
}
