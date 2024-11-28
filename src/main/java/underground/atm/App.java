package underground.atm;

import underground.atm.repositories.CardDataSource;
import underground.atm.repositories.CardDataSourceImpl;
import underground.atm.repositories.CardRepository;
import underground.atm.repositories.CardRepositoryImpl;
import underground.atm.serializers.CardSerializer;
import underground.atm.services.AuthorizationService;
import underground.atm.services.CreditCardService;
import underground.atm.ui.TerminalUI;

import java.nio.file.Path;

public final class App {

    public static void main(String[] args) {
        CardSerializer cardSerializer = new CardSerializer();

        CardDataSource cardDataSource = new CardDataSourceImpl(Path.of("src/main/resources/Credits"), cardSerializer);

        CardRepository cardRepository = new CardRepositoryImpl(cardDataSource);

        CreditCardService cardService = new CreditCardService(cardRepository);
        AuthorizationService authorizationService = new AuthorizationService(cardRepository);

        TerminalUI ui = new TerminalUI(authorizationService, cardService);
        ui.start();
    }
}
