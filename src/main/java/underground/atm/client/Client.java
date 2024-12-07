package underground.atm.client;

import underground.atm.common.Request;
import underground.atm.common.Response;

public interface Client {
    Response send(Request request);
}
