package underground.atm.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import underground.atm.common.Request;
import underground.atm.common.Response;
import underground.atm.common.data.CreditCard;
import underground.atm.common.exceptions.exceptions.AuthorizationFailedException;
import underground.atm.common.exceptions.exceptions.CreditCardNotFoundException;
import underground.atm.common.exceptions.exceptions.InvalidAmountException;
import underground.atm.common.exceptions.exceptions.NotEnoughMoneyException;
import underground.atm.server.services.Server_AuthorizationService;
import underground.atm.server.services.Server_CreditCardService;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class CreditCardControllerTest {

    Server_CreditCardService serverCreditCardService = Mockito.mock(Server_CreditCardService.class);
    final Server_AuthorizationService serverAuthorizationService = Mockito.mock(Server_AuthorizationService.class);
    final CreditCardController creditCardController = new CreditCardController(serverCreditCardService, serverAuthorizationService);

    // AUTHORIZE
    @Test
    @DisplayName("Handle AuthorizeRequest with valid credentials")
    void handleAuthorizeRequest() throws AuthorizationFailedException {
        when(serverAuthorizationService.authorize(2004,"11")).thenReturn(new CreditCard(2004,"11",0));

        var authorizeRequest = new Request.AuthorizeRequest(2004,"11");
        Response response = creditCardController.handleRequest(authorizeRequest);

        assertThat(response).isEqualTo(new Response.AuthorizeResponse());
    }

    @Test
    @DisplayName("Handle AuthorizeRequest with invalid credentials")
    void handleAuthorizeRequestWithInvalidCredentials() throws AuthorizationFailedException {
        when(serverAuthorizationService.authorize(anyInt(), anyString())).thenThrow(new AuthorizationFailedException());
        var authorizeRequest = new Request.AuthorizeRequest(2004,"11");
        Response response = creditCardController.handleRequest(authorizeRequest);

        assertThat(response).isEqualTo(new Response.ErrorResponse(91));
    }

    // DEPOSIT
    @Test
    @DisplayName("Handle DepositRequest with valid credentials")
    void handleDepositRequestWithValidCredentials() {
        var depositRequest = new Request.DepositRequest(2004, 100);
        Response response = creditCardController.handleRequest(depositRequest);
        assertThat(response).isEqualTo(new Response.DepositResponse());
    }
    
    @Test
    @DisplayName("Handle DepositRequest with non-existing id")
    void handleDepositRequest() throws InvalidAmountException, CreditCardNotFoundException {
        doThrow(new CreditCardNotFoundException()).when(serverCreditCardService).deposit(1111,10);
        var depositRequest = new Request.DepositRequest(1111,10);
        Response response = creditCardController.handleRequest(depositRequest);

        assertThat(response).isEqualTo(new Response.ErrorResponse(93));
    }

    @Test
    @DisplayName("Handle DepositRequest with invalid amount ")
    void handleDepositRequestWithInvalidAmount() throws InvalidAmountException, CreditCardNotFoundException {
        doThrow(new InvalidAmountException()).when(serverCreditCardService).deposit(2004,-10);
        Request.DepositRequest depositRequest = new Request.DepositRequest(2004, -10);
        Response response = creditCardController.handleRequest(depositRequest);

        assertThat(response).isEqualTo(new Response.ErrorResponse(92));
    }

    // WITHDRAW
    @Test
    @DisplayName("Handle WithdrawRequest with valid credentials")
    void handleWithdrawRequestWithValidCredentials() {
        var withdrawRequest = new Request.WithdrawRequest(2004, 100);
        Response response = creditCardController.handleRequest(withdrawRequest);
        assertThat(response).isEqualTo(new Response.WithDrawResponse());
    }

    @Test
    @DisplayName("Handle WithdrawRequest with non-existing id")
    void handleWithdrawRequestWithNonExistingId() throws NotEnoughMoneyException, InvalidAmountException, CreditCardNotFoundException {
        doThrow(new CreditCardNotFoundException()).when(serverCreditCardService).withdraw(1111,10);

        var depositRequest = new Request.WithdrawRequest(1111,10);
        Response response = creditCardController.handleRequest(depositRequest);

        assertThat(response).isEqualTo(new Response.ErrorResponse(93));
    }

    @Test
    @DisplayName("Handle WithdrawRequest with invalid amount")
    void handleWithdrawRequestWithInvalidAmount() throws NotEnoughMoneyException, InvalidAmountException, CreditCardNotFoundException {
        doThrow(new InvalidAmountException()).when(serverCreditCardService).withdraw(1111,10);

        var depositRequest = new Request.WithdrawRequest(1111,10);
        Response response = creditCardController.handleRequest(depositRequest);

        assertThat(response).isEqualTo(new Response.ErrorResponse(92));
    }

    @Test
    @DisplayName("Handle WithdrawRequest with amount > balance")
    void handleWithdrawRequestWithAmountBalance() throws NotEnoughMoneyException, InvalidAmountException, CreditCardNotFoundException {
        doThrow(new NotEnoughMoneyException()).when(serverCreditCardService).withdraw(1111,10);

        var depositRequest = new Request.WithdrawRequest(1111,10);
        Response response = creditCardController.handleRequest(depositRequest);

        assertThat(response).isEqualTo(new Response.ErrorResponse(94));
    }
    
    // TRANSFER
    @Test
    @DisplayName("Handle TransferRequest with valid credentials")
    void handleTransferRequestWithValidCredentials() {
        Request.TransferRequest transferRequest = new Request.TransferRequest(2004, 2002, 100);
        Response response = creditCardController.handleRequest(transferRequest);
        assertThat(response).isEqualTo(new Response.TransferResponse());
    }

    @Test
    @DisplayName("HandleTransferRequest with invalid id")
    void handleTransferRequestWithInvalidId() throws NotEnoughMoneyException, InvalidAmountException, CreditCardNotFoundException {
        doThrow(new CreditCardNotFoundException()).when(serverCreditCardService).transfer(2004,99999,100);
        Request.TransferRequest transferRequest = new Request.TransferRequest(2004, 99999, 100);
        Response response = creditCardController.handleRequest(transferRequest);
        assertThat(response).isEqualTo(new Response.ErrorResponse(93));
    }

    @Test
    @DisplayName("Handle TransferRequest with invalid amount")
    void handleTransferRequestWithInvalidAmount() throws NotEnoughMoneyException, InvalidAmountException, CreditCardNotFoundException {
       doThrow(new InvalidAmountException()).when(serverCreditCardService).transfer(2004,2002,-10);
        Request.TransferRequest transferRequest = new Request.TransferRequest(2004, 2002, -10);
        Response response = creditCardController.handleRequest(transferRequest);
        assertThat(response).isEqualTo(new Response.ErrorResponse(92));
    }

    @Test
    @DisplayName("Handle TransferRequest with amount > balance")
    void handleTransferRequestWithAmountBalance() throws NotEnoughMoneyException, InvalidAmountException, CreditCardNotFoundException {
        doThrow(new NotEnoughMoneyException()).when(serverCreditCardService).transfer(2004,2002,5000);
        Request.TransferRequest transferRequest = new Request.TransferRequest(2004, 2002, 5000);
        Response response = creditCardController.handleRequest(transferRequest);
        assertThat(response).isEqualTo(new Response.ErrorResponse(94));
    }

    // VIEW_BALANCE
    @Test
    @DisplayName("Handle ViewBalanceRequest with valid id")
    void handleViewBalanceRequestWithValidId() throws CreditCardNotFoundException {
        when(serverCreditCardService.viewBalance(2004)).thenReturn(3700);
        var viewBalanceRequest = new Request.ViewBalanceRequest(2004);
        Response response = creditCardController.handleRequest(viewBalanceRequest);
        assertThat(response).isEqualTo(new Response.ViewBalanceResponse(3700));
    }

    @Test
    @DisplayName("Handle ViewBalanceRequest with invalid id")
    void handleViewBalanceRequestWithInvalidId() throws CreditCardNotFoundException {
        when(serverCreditCardService.viewBalance(anyInt())).thenThrow(new CreditCardNotFoundException());
        var viewBalanceRequest = new Request.ViewBalanceRequest(anyInt());
        Response response = creditCardController.handleRequest(viewBalanceRequest);
        assertThat(response).isEqualTo(new Response.ErrorResponse(93));
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