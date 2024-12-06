package underground.atm.common;

import underground.atm.server.CreditCardCodec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ResponseCodec {
//    private CreditCardCodec creditCardCodec = new CreditCardCodec();

    public void encode(DataOutputStream dataOutput, Response response) throws IOException {
        switch (response) {
            case Response.AuthorizeResponse() -> dataOutput.write(Response.AuthorizeResponse.TYPE);
            case Response.DepositResponse() -> dataOutput.write(Response.DepositResponse.TYPE);
            case Response.WithDrawResponse() -> dataOutput.write(Response.WithDrawResponse.TYPE);
            case Response.TransferResponse() -> dataOutput.write(Response.TransferResponse.TYPE);
            case Response.ViewBalanceResponse(int balance) -> {
                dataOutput.write(Response.ViewBalanceResponse.TYPE);
                dataOutput.write(balance);
            }
            case Response.ErrorResponse(int exceptionType) -> dataOutput.write(exceptionType);
        }
    }

    public Response decode(DataInputStream inputStream) throws IOException {
        byte type = inputStream.readByte();
        return switch (type) {
            case Response.AuthorizeResponse.TYPE -> { yield new Response.AuthorizeResponse();}
            case Response.DepositResponse.TYPE -> { yield new Response.DepositResponse();}
            case Response.WithDrawResponse.TYPE -> { yield new Response.WithDrawResponse();}
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
