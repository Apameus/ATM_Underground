package underground.atm.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import underground.atm.data.Card;
import underground.atm.exceptions.AuthorizationFailedException;
import underground.atm.repositories.CardRepository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class CreditCardServiceUnitTest {

    private final CardRepository mockRepo = Mockito.mock(CardRepository.class);
    private final Card mockCard = new Card(1111,11,100);
    private CreditCardService creditCardService;

    @Test
    @DisplayName("Invalid creditCardID OR pin throws authorization exception")
    void invalidCreditCardIDThrowsAuthorizationException(){
        when(mockRepo.findCardBy(anyInt())).thenReturn(null);
        creditCardService = new CreditCardService(mockRepo);
        assertThrows(AuthorizationFailedException.class, () -> creditCardService.authorize(9999, 99));
    }

    @Test
    @DisplayName("Valid credentials returns card")
    void validCredentialsReturnsCard() throws AuthorizationFailedException {
        when(mockRepo.findCardBy(mockCard.id())).thenReturn(mockCard);
        creditCardService = new CreditCardService(mockRepo);

        assertThat(creditCardService.authorize(mockCard.id(), mockCard.pin())).isEqualTo(mockCard);
    }


}