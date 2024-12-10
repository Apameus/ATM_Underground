package underground.atm.server;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import underground.atm.client.ClientImpl;
import underground.atm.common.Request.AuthorizeRequest;
import underground.atm.common.Request.DepositRequest;
import underground.atm.common.Response;
import underground.atm.common.data.CreditCard;
import underground.atm.common.log.CompositeLoggerFactory;
import underground.atm.common.log.ConsoleLogger;
import underground.atm.server.repositories.Server_CreditCardRepositoryImpl;
import underground.atm.server.services.Server_AuthorizationService;
import underground.atm.server.services.Server_CreditCardService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TCPServerIntegrationTest { //ToDo: add tests

    static SocketAddress address;
    static ClientImpl client;
    static TCPServer server;
    static CreditCard creditCard;
    static Server_CreditCardRepositoryImpl serverCreditCardRepository;

    @BeforeAll
    static void setup() throws IOException {
        address = new InetSocketAddress(8080);
        client = new ClientImpl(address);
        serverCreditCardRepository = Mockito.mock(Server_CreditCardRepositoryImpl.class);

        var logFactory = new CompositeLoggerFactory(new ConsoleLogger());

        var serverCreditCardService = new Server_CreditCardService(serverCreditCardRepository, logFactory);
        var serverAuthorizationService = new Server_AuthorizationService(serverCreditCardRepository, logFactory);
        var creditCardController = new CreditCardController(serverCreditCardService, serverAuthorizationService);

        server = new TCPServer(address, creditCardController, logFactory);

        Thread.ofVirtual().start(() -> {
            try {
                server.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @BeforeEach
    void setCreditCard(){
        creditCard = new CreditCard(2004,"4",100);
        when(serverCreditCardRepository.findCardBy(creditCard.id())).thenReturn(creditCard);
    }

    @Test
    @DisplayName("If authorize request with valid credentials return authorize response")
    void ifAuthorizeRequestWithValidCredentialsReturnAuthorizeResponse() {
        assertThat(client.send(new AuthorizeRequest(2004, "4"))).isEqualTo(new Response.AuthorizeResponse());
    }

    @Test
    @DisplayName("If invalid authorize request return ErrorResponse(91)")
    void ifInvalidAuthorizeRequestReturnErrorResponse91() {
        assertThat(client.send(new AuthorizeRequest(creditCard.id(), "123213123")))
                .isInstanceOfSatisfying(Response.ErrorResponse.class, response ->{
                    assertThat(response.exceptionType()).isEqualTo(91);
                });
    }

    @Test
    @DisplayName("If valid deposit request then return deposit response")
    void ifValidDepositRequestThenReturnDepositResponse() {
        assertThat(client.send(new DepositRequest(creditCard.id(), 10)))
                .isEqualTo(new Response.DepositResponse());
    }

    @Test
    @DisplayName("If deposit request with invalid amount then return error response 92")
    void ifDepositRequestWithInvalidAmountThenReturnErrorResponse92() {
        assertThat(client.send(new DepositRequest(creditCard.id(), -10)))
                .isInstanceOfSatisfying(Response.ErrorResponse.class, response -> {
                    assertThat(response.exceptionType()).isEqualTo(92);
                });
    }
}