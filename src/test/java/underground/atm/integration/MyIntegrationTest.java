package underground.atm.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import underground.atm.App;
import underground.atm.client.ClientImpl;
import underground.atm.client.repositories.CreditCardRepository;
import underground.atm.client.services.AuthorizationService;
import underground.atm.client.services.CreditCardService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.Files.readString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public final class MyIntegrationTest {

    static Path file;
    static Thread thread;
    static InetSocketAddress serverAddress = new InetSocketAddress("localhost", 9997);
    static CreditCardService creditCardService;
    static AuthorizationService authorizationService;

    
    @BeforeAll
    static void setup() throws IOException {
        file = Files.createTempFile("integration-test-", "");
        Files.write(file, List.of(
                "2050,50,50"
        ));

        CreditCardRepository creditCardRepository = new CreditCardRepository(new ClientImpl(serverAddress));
        creditCardService = new CreditCardService(creditCardRepository);
        authorizationService = new AuthorizationService(creditCardRepository);

        thread = Thread.ofVirtual().start(() -> {
            try {
                App.startServer(new App.AppRole.Server(serverAddress.getHostName(), serverAddress.getPort(), file.toAbsolutePath().toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    @DisplayName("Test client")
    void testClient() {
        assertDoesNotThrow(() -> authorizationService.authorize(2050, "50"));

        assertDoesNotThrow(() -> creditCardService.deposit(2050, 500));

        assertThat(assertDoesNotThrow(() -> Files.readAllLines(file))).containsExactly("2050,50,550");
    }

    @AfterAll
    static void cleanup() throws IOException {
        Files.delete(file);
        thread.interrupt();
    }


}
