package underground.atm.common.codec;

import underground.atm.common.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ResponseCodec {
//    private CreditCardCodec creditCardCodec = new CreditCardCodec();

    public void encode(DataOutputStream dataOutput, Response response) throws IOException {
        switch (response) {
            case Response.AuthorizeResponse() -> dataOutput.write(Response.AuthorizeResponse.TYPE);
            case Response.DepositResponse() -> dataOutput.write(Response.DepositResponse.TYPE);
            case Response.WithdrawResponse() -> dataOutput.write(Response.WithdrawResponse.TYPE);
            case Response.TransferResponse() -> dataOutput.write(Response.TransferResponse.TYPE);
            case Response.ViewBalanceResponse(int balance) -> {
                dataOutput.write(Response.ViewBalanceResponse.TYPE);
                dataOutput.writeInt(balance);
            }
            case Response.ErrorResponse(int exceptionType) -> dataOutput.write(exceptionType);
        }
    }

    public Response decode(DataInputStream inputStream) throws IOException {
        byte type = inputStream.readByte();
        return switch (type) {
            case Response.AuthorizeResponse.TYPE -> { yield new Response.AuthorizeResponse();}
            case Response.DepositResponse.TYPE -> { yield new Response.DepositResponse();}
            case Response.WithdrawResponse.TYPE -> { yield new Response.WithdrawResponse();}
            case Response.TransferResponse.TYPE -> { yield  new Response.TransferResponse();}
            case Response.ViewBalanceResponse.TYPE -> {
                int balance = inputStream.readInt();
                yield new Response.ViewBalanceResponse(balance);
            }
            case Response.ErrorResponse.TYPE -> { yield new Response.ErrorResponse(90);}
            default -> {yield new Response.ErrorResponse(type);}
        };
    }
}
