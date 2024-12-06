package underground.atm.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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

    public void encode(DataOutputStream outputStream, Request request) throws IOException {
        switch (request) {
            case Request.FindCreditCardRequest(int id) -> {
                outputStream.write(Request.FindCreditCardRequest.TYPE);
                outputStream.writeInt(id);
            }
            case Request.UpdateAmountRequest(int id, int amount) -> {
                outputStream.write(Request.UpdateAmountRequest.TYPE);
                outputStream.writeInt(id);
                outputStream.writeInt(amount);
            }
        }
    }

}
