package underground.atm.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import underground.atm.common.codec.ResponseStreamCodec;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseStreamCodecTest {

    private ResponseStreamCodec responseStreamCodec = new ResponseStreamCodec();


    // ENCODE
    
    @Test
    @DisplayName("Encode Authorize Response Test")
    void encodeAuthorizeResponseTest() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response.AuthorizeResponse authorizeResponse = new Response.AuthorizeResponse();

        responseStreamCodec.encode(new DataOutputStream(out),authorizeResponse);

        assertThat(responseStreamCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(authorizeResponse);

    }

    @Test
    @DisplayName("Encode Deposit Response Test")
    void encodeDepositResponseTest() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response.DepositResponse depositResponse = new Response.DepositResponse();

        responseStreamCodec.encode(new DataOutputStream(out),depositResponse);

        assertThat(responseStreamCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(depositResponse);

    }

    @Test
    @DisplayName("Encode Withdraw Response Test")
    void encodeWithdrawResponseTest() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response.WithdrawResponse withdrawResponse = new Response.WithdrawResponse();

        responseStreamCodec.encode(new DataOutputStream(out),withdrawResponse);

        assertThat(responseStreamCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(withdrawResponse);

    }

    @Test
    @DisplayName("Encode Transfer Response Test")
    void encodeTransferResponseTest() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response.WithdrawResponse withdrawResponse = new Response.WithdrawResponse();

        responseStreamCodec.encode(new DataOutputStream(out),withdrawResponse);

        assertThat(responseStreamCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(withdrawResponse);

    }

    @Test
    @DisplayName("Encode ViewBalance Response Test")
    void encodeViewBalanceResponseTest() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response.ViewBalanceResponse viewBalanceResponse = new Response.ViewBalanceResponse(100);

        responseStreamCodec.encode(new DataOutputStream(out),viewBalanceResponse);

        assertThat(responseStreamCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isInstanceOfSatisfying(Response.ViewBalanceResponse.class, response -> {
                    assertThat(response.balance()).isEqualTo(100);
                });

    }

    @Test
    @DisplayName("Encode Error Response")
    void encodeErrorResponse() throws IOException { //TODO
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Response.ErrorResponse errorResponse = new Response.ErrorResponse(92);

        responseStreamCodec.encode(new DataOutputStream(out),errorResponse);

        assertThat(responseStreamCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isInstanceOfSatisfying(Response.ErrorResponse.class, response -> {
                    assertThat(response.exceptionType()).isEqualTo(92);
                });
    }

    // DECODE

    @Test
    @DisplayName("Decode Authorize Response")
    void decodeAuthorizeResponse() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        dataOutputStream.write(Response.AuthorizeResponse.TYPE);

        assertThat(responseStreamCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(new Response.AuthorizeResponse());
    }

    @Test
    @DisplayName("Decode Deposit Response")
    void decodeDepositResponse() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        dataOutputStream.write(Response.DepositResponse.TYPE);

        assertThat(responseStreamCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(new Response.DepositResponse());
    }

    @Test
    @DisplayName("Decode Withdraw Response")
    void decodeWithdrawResponse() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        dataOutputStream.write(Response.WithdrawResponse.TYPE);

        assertThat(responseStreamCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(new Response.WithdrawResponse());
    }

    @Test
    @DisplayName("Decode Transfer Response")
    void decodeTransferResponse() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        dataOutputStream.write(Response.TransferResponse.TYPE);

        assertThat(responseStreamCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(new Response.TransferResponse());
    }

    @Test
    @DisplayName("Decode ViewBalance Response")
    void decodeBalanceResponse() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        dataOutputStream.write(Response.ViewBalanceResponse.TYPE);
        dataOutputStream.writeInt(100);

        assertThat(responseStreamCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isInstanceOfSatisfying(Response.ViewBalanceResponse.class, response -> {
                    assertThat(response.balance()).isEqualTo(100);
                });
    }

    @Test
    @DisplayName("Decode Error Response")
    void decodeErrorResponse() { //TODO

    }

}