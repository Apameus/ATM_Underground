 package underground.atm.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import underground.atm.common.codec.CodecUtils;
import underground.atm.common.codec.RequestCodec;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class RequestCodecTest {
    RequestCodec requestCodec = new RequestCodec();

    // ENCODE

    @Test
    @DisplayName("Encode Authorize Request")
    void encodeAuthorizeRequest() throws IOException {

        var out = new ByteArrayOutputStream();
        Request.AuthorizeRequest authorizeRequest = new Request.AuthorizeRequest(2004, "11");
        requestCodec.encode(new DataOutputStream(out), authorizeRequest);

        assertThat(requestCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(authorizeRequest);
    }

    @Test
    @DisplayName("Encode Deposit Request")
    void encodeDepositRequest() throws IOException {
        var out = new ByteArrayOutputStream();
        Request.DepositRequest depositRequest = new Request.DepositRequest(2003, 100);
        requestCodec.encode(new DataOutputStream(out), depositRequest);

        assertThat(requestCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(depositRequest);
    }

    @Test
    @DisplayName("Encode Withdraw Request")
    void encodeWithdrawRequest() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Request.WithdrawRequest withdrawRequest = new Request.WithdrawRequest(2004, 100);
        requestCodec.encode(new DataOutputStream(out), withdrawRequest);

        assertThat(requestCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(withdrawRequest);
    }

    @Test
    @DisplayName("Encode Transfer Request")
    void encodeTransferRequest() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Request.TransferRequest transferRequest = new Request.TransferRequest(2004, 2002, 100);
        requestCodec.encode(new DataOutputStream(out),transferRequest);

        assertThat(requestCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(transferRequest);
    }

    @Test
    @DisplayName("Encode ViewBalance Request")
    void encodeViewBalanceRequest() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Request.ViewBalanceRequest viewBalanceRequest = new Request.ViewBalanceRequest(2004);
        requestCodec.encode(new DataOutputStream(out), viewBalanceRequest);

        assertThat(requestCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(viewBalanceRequest);
    }

    // DECODE

    @Test
    @DisplayName("Decode Authorize Request")
    void decodeAuthorizeRequest() throws IOException {
        var out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        dataOutputStream.write(Request.AuthorizeRequest.TYPE);
        dataOutputStream.writeInt(2004);
        CodecUtils.encodeString(dataOutputStream, "11");
//        dataOutputStream.writeInt(2);
//        byte[] str = {'1','1'};
//        dataOutputStream.write(str);

        assertThat(requestCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isInstanceOfSatisfying(Request.AuthorizeRequest.class, request -> {
                    assertThat(request.id()).isEqualTo(2004);
                    assertThat(request.pin()).isEqualTo("11");
                });
    }

    @Test
    @DisplayName("Decode Deposit Request")
    void decodeDepositRequest() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        dataOutputStream.write(Request.DepositRequest.TYPE);
        dataOutputStream.writeInt(2004);
        dataOutputStream.writeInt(100);

        assertThat(requestCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isInstanceOfSatisfying(Request.DepositRequest.class, request -> {
                    assertThat(request.id()).isEqualTo(2004);
                    assertThat(request.amount()).isEqualTo(100);
                });
    }

    @Test
    @DisplayName("Decode Withdraw Request")
    void decodeWithdrawRequest() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        dataOutputStream.write(Request.WithdrawRequest.TYPE);
        dataOutputStream.writeInt(2004);
        dataOutputStream.writeInt(100);

        assertThat(requestCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isInstanceOfSatisfying(Request.WithdrawRequest.class, request -> {
                    assertThat(request.id()).isEqualTo(2004);
                    assertThat(request.amount()).isEqualTo(100);
                });
    }

    @Test
    @DisplayName("Decode Transfer Request")
    void decodeTransferRequest() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        dataOutputStream.write(Request.TransferRequest.TYPE);
        dataOutputStream.writeInt(2004);
        dataOutputStream.writeInt(2002);
        dataOutputStream.writeInt(100);

        assertThat(requestCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isInstanceOfSatisfying(Request.TransferRequest.class, request -> {
                    assertThat(request.fromId()).isEqualTo(2004);
                    assertThat(request.toId()).isEqualTo(2002);
                    assertThat(request.amount()).isEqualTo(100);
                });
    }

    @Test
    @DisplayName("Decode ViewBalance Request")
    void decodeViewBalanceRequest() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        dataOutputStream.write(Request.ViewBalanceRequest.TYPE);
        dataOutputStream.writeInt(2004);

        assertThat(requestCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isInstanceOfSatisfying(Request.ViewBalanceRequest.class, request -> {
                    assertThat(request.id()).isEqualTo(2004);
                });
    }
    

}