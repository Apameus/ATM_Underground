package underground.atm.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import underground.atm.data.CreditCard;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CreditCardControllerTest {

    final ServerCreditCardService serverCreditCardService = Mockito.mock(ServerCreditCardService.class);
    final CreditCardController creditCardController = new CreditCardController(serverCreditCardService);

    @Test
    @DisplayName("Test")
    void test() throws IOException {
        CreditCard creditCard = new CreditCard(2004, "11", 100);
        when(serverCreditCardService.findCreditCardBy(creditCard.id())).thenReturn(creditCard);

        var request = ByteBuffer.allocate(50)
                .put((byte) 1)
                .putInt(creditCard.id())
                .flip();
        var outputStream = new ByteArrayOutputStream();
        creditCardController.handleClient(new ByteArrayInputStream(request.array(), 0, request.limit()),
                outputStream);

//        2,0,0,4;
//        1,1;
//        0100;

//        assertThat(outputStream.toByteArray()).containsExactly(2004,'1','1',100);
        ByteBuffer buf = ByteBuffer.allocate(50)
                .putInt(2004)
                .put((byte) '1')
                .put((byte) '1')
                .putInt(100)
                .flip();
        assertThat(outputStream.toByteArray()).containsExactly(Arrays.copyOf(buf.array(), buf.limit()));
    }
  
}