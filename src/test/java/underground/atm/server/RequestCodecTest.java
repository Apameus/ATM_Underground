package underground.atm.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import underground.atm.data.CreditCard;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RequestCodecTest {

    //    ServerCreditCardService serverCreditCardService = Mockito.mock(ServerCreditCardService.class);
    RequestCodec requestCodec = new RequestCodec();

    @Test
    @DisplayName("Decode FindCreditCardRequest")
    void decodeFindCreditCardRequest() throws IOException {
        CreditCard creditCard = new CreditCard(2004, "11", 100);
        var out = new ByteArrayOutputStream();
        var dataOutput = new DataOutputStream(out);

        dataOutput.write(Request.FindCreditCardRequest.TYPE);
        dataOutput.writeInt(creditCard.id());

        assertThat(requestCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isInstanceOfSatisfying(Request.FindCreditCardRequest.class, request -> {
                    assertThat(request.id()).isEqualTo(creditCard.id());
                });
    }

    @Test
    @DisplayName("Encode FindCreditCardRequest")
    void encodeFindCreditCardRequest() throws IOException {
//        CreditCard creditCard = new CreditCard(2004,"11",100);
//        when(serverCreditCardService.findCreditCardBy(creditCard.id())).thenReturn(creditCard);
//
        var out = new ByteArrayOutputStream();
        Request.FindCreditCardRequest request = new Request.FindCreditCardRequest(2004);
        requestCodec.encode(new DataOutputStream(out), request);

        assertThat(requestCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray()))))
                .isEqualTo(request);

    }

}