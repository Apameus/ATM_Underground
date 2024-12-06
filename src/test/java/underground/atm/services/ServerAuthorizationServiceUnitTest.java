package underground.atm.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import underground.atm.data.CreditCard;
import underground.atm.exceptions.AuthorizationFailedException;
import underground.atm.repositories.Server_CreditCardRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ServerAuthorizationServiceUnitTest {
    private final Server_CreditCardRepository creditCardRepository = Mockito.mock(Server_CreditCardRepository.class);
    private final CreditCard creditCard = new CreditCard(1111,"11",100);
    private final Server_AuthorizationService serverAuthorizationService = new Server_AuthorizationService(creditCardRepository);

    @Test
    @DisplayName("Authorize with valid credentials")
    void authorizeWithValidCredentials() {
        when(creditCardRepository.findCardBy(creditCard.id())).thenReturn(creditCard);
        CreditCard creditCard = assertDoesNotThrow(() -> serverAuthorizationService.authorize(this.creditCard.id(), this.creditCard.pin()));
        assertThat(creditCard).isEqualTo(this.creditCard);
    }

    @Test
    @DisplayName("Authorize with invalid ID")
    void authorizeWithInvalidId() {
//        when(mockRepo.findCardBy(anyInt())).thenReturn(null);
        assertThrows(AuthorizationFailedException.class, () -> serverAuthorizationService.authorize(1111, "11"));
    }

    @Test
    @DisplayName("Authorize with valid ID but invalid pin")
    void authorizeWithValidIdButInvalidPin() {
        when(creditCardRepository.findCardBy(creditCard.id())).thenReturn(creditCard);
        assertThrows(AuthorizationFailedException.class, () -> serverAuthorizationService.authorize(creditCard.id(), "00"));
    }
}