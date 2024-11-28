package underground.atm.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import underground.atm.data.Card;
import underground.atm.exceptions.AuthorizationFailedException;
import underground.atm.repositories.CardRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class AuthorizationServiceUnitTest {
    private final CardRepository mockRepo = Mockito.mock(CardRepository.class);
    private final Card mockCard = new Card(1111,11,100);
    private final AuthorizationService service = new AuthorizationService(mockRepo);

    @Test
    @DisplayName("Authorize with valid credentials")
    void authorizeWithValidCredentials() {
        when(mockRepo.findCardBy(mockCard.id())).thenReturn(mockCard);
        Card card = assertDoesNotThrow(() -> service.authorize(mockCard.id(), mockCard.pin()));
        assertThat(card).isEqualTo(mockCard);
    }

    @Test
    @DisplayName("Authorize with invalid ID")
    void authorizeWithInvalidId() {
//        when(mockRepo.findCardBy(anyInt())).thenReturn(null);
        assertThrows(AuthorizationFailedException.class, () -> service.authorize(1111, 11));
    }

    @Test
    @DisplayName("Authorize with valid ID but invalid pin")
    void authorizeWithValidIdButInvalidPin() {
        when(mockRepo.findCardBy(mockCard.id())).thenReturn(mockCard);
        assertThrows(AuthorizationFailedException.class, () -> service.authorize(mockCard.id(), 00));
    }
}