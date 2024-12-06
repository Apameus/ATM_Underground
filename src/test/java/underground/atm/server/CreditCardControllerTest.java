package underground.atm.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import underground.atm.data.CreditCard;
import underground.atm.services.CreditCardService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

class CreditCardControllerTest {

    final CreditCardService serverCreditCardService = Mockito.mock(CreditCardService.class);
    final CreditCardController creditCardController = new CreditCardController(serverCreditCardService);


    @Test
    @DisplayName("Test")
    void test() {
        CreditCard creditCard = new CreditCard(2004,"11",100);
        when(serverCreditCardService.findCreditCardBy(creditCard.id())).thenReturn(creditCard);

        Request.FindCreditCardRequest request = new Request.FindCreditCardRequest(creditCard.id());
        Response response = assertDoesNotThrow(() -> creditCardController.handleRequest(request));
        assertThat(response).isEqualTo(new Response.FindCreditCardResponse(creditCard));
    }


    // Low Level:

//    @Test
//    @DisplayName("Test")
//    void test() throws IOException {
//        CreditCard creditCard = new CreditCard(2004, "11", 100);
//        when(serverCreditCardService.findCreditCardBy(creditCard.id())).thenReturn(creditCard);
//
//        var request = ByteBuffer.allocate(50)
//                .put((byte) 1)
//                .putInt(creditCard.id())
//                .flip();
//        var outputStream = new ByteArrayOutputStream();
//        creditCardController.handleClient(new ByteArrayInputStream(request.array(), 0, request.limit()),
//                outputStream);
//
//        ByteBuffer buf = ByteBuffer.allocate(50)
//                .putInt(2004)
//                .put((byte) '1')
//                .put((byte) '1')
//                .putInt(100)
//                .flip();
//        assertThat(outputStream.toByteArray()).containsExactly(Arrays.copyOf(buf.array(), buf.limit()));
//    }
  
}