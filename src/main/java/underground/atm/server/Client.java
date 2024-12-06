package underground.atm.server;

public interface Client {
    Response send(Request request);
}
