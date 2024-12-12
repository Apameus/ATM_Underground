package underground.atm.common.codec;

import underground.atm.common.Request;
import underground.atm.common.Request.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class RequestStreamCodec {
    public Request decode(DataInputStream inputStream) throws IOException {
        byte type = inputStream.readByte();
        return switch (type) {
            case AuthorizeRequest.TYPE -> {
                int id = inputStream.readInt();
                String pin = StreamCodecUtils.decodeString(inputStream);
                yield new AuthorizeRequest(id, pin);
            }
            case DepositRequest.TYPE -> {
                int id = inputStream.readInt();
                int amount = inputStream.readInt();
                yield new DepositRequest(id,amount);
            }
            case WithdrawRequest.TYPE -> {
                int id = inputStream.readInt();
                int amount = inputStream.readInt();
                yield new WithdrawRequest(id,amount);
            }
            case TransferRequest.TYPE -> {
                int fromId = inputStream.readInt();
                int toId = inputStream.readInt();
                int amount = inputStream.readInt();
                yield new TransferRequest(fromId, toId, amount);
            }
            case ViewBalanceRequest.TYPE -> {
                int id = inputStream.readInt();
                yield new ViewBalanceRequest(id);
            }
            default -> throw new IllegalStateException("Unknown Request");
        };
    }

    public void encode(DataOutputStream outputStream, Request request) throws IOException {
        switch (request) {
            case AuthorizeRequest(int id, String pin) -> {
                outputStream.write(AuthorizeRequest.TYPE);
                outputStream.writeInt(id);
                StreamCodecUtils.encodeString(outputStream, pin);
            }
            case DepositRequest(int id, int amount) -> {
                outputStream.write(DepositRequest.TYPE);
                outputStream.writeInt(id);
                outputStream.writeInt(amount);
            }
            case WithdrawRequest(int id , int amount) -> {
                outputStream.write(WithdrawRequest.TYPE);
                outputStream.writeInt(id);
                outputStream.writeInt(amount);
            }
            case TransferRequest(int fromId, int toId, int amount) -> {
                outputStream.write(TransferRequest.TYPE);
                outputStream.writeInt(fromId);
                outputStream.writeInt(toId);
                outputStream.writeInt(amount);
            }
            case ViewBalanceRequest(int id) -> {
                outputStream.write(ViewBalanceRequest.TYPE);
                outputStream.writeInt(id);
            }
        }
    }

}
