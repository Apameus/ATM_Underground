package underground.atm;

import underground.atm.repositories.CreditCardDataSource;
import underground.atm.repositories.CreditCardDataSourceImpl;
import underground.atm.repositories.Server_CreditCardRepository;
import underground.atm.repositories.Server_CreditCardRepositoryImpl;
import underground.atm.serializers.CreditCardSerializer;
import underground.atm.services.Server_AuthorizationService;
import underground.atm.services.Server_CreditCardService;
import underground.atm.ui.TerminalUI;

import java.nio.file.Path;

public final class App {

    public static void main(String[] args) {
        CreditCardSerializer creditCardSerializer = new CreditCardSerializer();

        CreditCardDataSource creditCardDataSource = new CreditCardDataSourceImpl(Path.of("src/main/resources/Credits"), creditCardSerializer);

        Server_CreditCardRepository creditCardRepository = new Server_CreditCardRepositoryImpl(creditCardDataSource);

        Server_CreditCardService cardService = new Server_CreditCardService(creditCardRepository);
        Server_AuthorizationService authorizationService = new Server_AuthorizationService(creditCardRepository);

        TerminalUI ui = new TerminalUI(authorizationService, cardService);
        ui.start();
    }
}
