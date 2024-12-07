package underground.atm;

import underground.atm.client.services.AuthorizationService;
import underground.atm.client.ClientImpl;
import underground.atm.client.repositories.CreditCardRepository;
import underground.atm.client.services.CreditCardService;
import underground.atm.server.CreditCardController;
import underground.atm.server.TCPServer;
import underground.atm.server.repositories.CreditCardDataSource;
import underground.atm.server.repositories.CreditCardDataSourceImpl;
import underground.atm.server.repositories.Server_CreditCardRepository;
import underground.atm.server.repositories.Server_CreditCardRepositoryImpl;
import underground.atm.server.serializers.CreditCardSerializer;
import underground.atm.server.services.Server_AuthorizationService;
import underground.atm.server.services.Server_CreditCardService;
import underground.atm.ui.TerminalUI;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;

public final class App {

    public static void main(String[] args) throws IOException {
        AppRole appRole = parseArguments(args);
        switch (appRole) {
            case AppRole.Server server -> startServer(server);
            case AppRole.Client client -> startClient(client);
        }
    }

    public static AppRole parseArguments(String[] args){
        if (args.length == 0) return new AppRole.Client("localhost", 8080);
        if (args[0].equals("--server")){
            return new AppRole.Server(args[1], Integer.parseInt(args[2]), args[3]);
        }
        else {
            return new AppRole.Client(args[1], Integer.parseInt(args[2]));
        }
    }

    public sealed interface AppRole {
        record Server(String address, int port, String creditCardsFile) implements AppRole {}
        record Client(String serverAddress, int serverPort) implements AppRole {}
    }

    public static void startServer(AppRole.Server config) throws IOException {
        var creditCardSerializer = new CreditCardSerializer();
        var creditCardDataSource = new CreditCardDataSourceImpl(Path.of(config.creditCardsFile()), creditCardSerializer);
        var server_creditCardRepository = new Server_CreditCardRepositoryImpl(creditCardDataSource);

        Server_AuthorizationService server_authorizationService = new Server_AuthorizationService(server_creditCardRepository);
        Server_CreditCardService server_creditCardService = new Server_CreditCardService(server_creditCardRepository);


        CreditCardController creditCardController = new CreditCardController(server_creditCardService, server_authorizationService);

        InetSocketAddress address = new InetSocketAddress(config.address(), config.port());
        System.out.println("Server running in: " + address.getAddress() + " at port: " + address.getPort());
        TCPServer server = new TCPServer(address,creditCardController);
        server.run();
    }

    public static void startClient(AppRole.Client client){
        InetSocketAddress address = new InetSocketAddress(client.serverAddress, client.serverPort());
        CreditCardRepository creditCardRepository = new CreditCardRepository(new ClientImpl(address));

        AuthorizationService authorizationService = new AuthorizationService(creditCardRepository);
        CreditCardService creditCardService = new CreditCardService(creditCardRepository);

        System.out.printf("Connecting client into %s at port %s%n", client.serverAddress(), client.serverPort());
        TerminalUI ui = new TerminalUI(authorizationService, creditCardService);
        ui.start();
    }
}
