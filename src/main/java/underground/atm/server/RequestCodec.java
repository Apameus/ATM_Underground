package underground.atm.server;

import java.io.DataInputStream;
import java.io.IOException;

public final class RequestCodec {
    public Request decode(DataInputStream inputStream) throws IOException {
        byte type = inputStream.readByte();
        return switch (type) {
            case Request.FindCreditCardRequest.TYPE -> {
                int id = inputStream.readInt();
                yield new Request.FindCreditCardRequest(id);
            }
            case Request.UpdateAmountRequest.TYPE -> {
                int id = inputStream.readInt();
                int amount = inputStream.readInt();
                yield new Request.UpdateAmountRequest(id, amount);
            }
            default -> throw new IOException("Unknown Request");
        };
    }

//    public Response encode(Request request) {
//        return switch (request){
//            case Request.FindCreditCardRequest -> {
//
//            }
//            case Request.UpdateAmountRequest -> {}
//        }
//    }
}
