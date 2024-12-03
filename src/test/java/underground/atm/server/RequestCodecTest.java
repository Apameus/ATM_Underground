package underground.atm.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import underground.atm.data.CreditCard;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RequestCodecTest {

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

}