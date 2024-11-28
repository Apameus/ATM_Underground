package underground.atm;

import underground.atm.repositories.CreditCardDataSource;
import underground.atm.repositories.CreditCardDataSourceImpl;
import underground.atm.repositories.CreditCardRepository;
import underground.atm.repositories.CreditCardRepositoryImpl;
import underground.atm.serializers.CreditCardSerializer;
import underground.atm.services.AuthorizationService;
import underground.atm.services.CreditCardService;
import underground.atm.ui.TerminalUI;

import java.nio.file.Path;

public final class App {

    public static void main(String[] args) {
        CreditCardSerializer creditCardSerializer = new CreditCardSerializer();

        CreditCardDataSource creditCardDataSource = new CreditCardDataSourceImpl(Path.of("src/main/resources/Credits"), creditCardSerializer);

        CreditCardRepository creditCardRepository = new CreditCardRepositoryImpl(creditCardDataSource);

        CreditCardService cardService = new CreditCardService(creditCardRepository);
        AuthorizationService authorizationService = new AuthorizationService(creditCardRepository);

        TerminalUI ui = new TerminalUI(authorizationService, cardService);
        ui.start();
    }
}
