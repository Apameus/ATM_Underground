package underground.atm.server.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import underground.atm.common.data.CreditCard;
import underground.atm.server.repositories.datasource.CreditCardDataSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CreditCardRepositoryImplUnitTest {
    private final CreditCardDataSource mockDataSource = Mockito.mock(CreditCardDataSource.class);
    private final CreditCard creditCard = new CreditCard(1111,"11",100);
    private Server_CreditCardRepositoryImpl cardRepo;

    @BeforeEach
    void loadCredit(){
        when(mockDataSource.load()).thenReturn(new HashMap<>(Map.of(creditCard.id(), creditCard)));
        cardRepo = new Server_CreditCardRepositoryImpl(mockDataSource);
    }

    @Test
    @DisplayName("Find card by valid ID")
    void findCardByValidID() {
        assertThat(cardRepo.findCardBy(creditCard.id())).isEqualTo(creditCard);
    }

    @Test
    @DisplayName("Find card by invalid ID should throw NullPointerException")
    void findCardByInvalidIdShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> cardRepo.findCardBy(50000));
    }

    @Test
    @DisplayName("Update amount test")
    void updateAmountTest() {
        cardRepo.updateAmount(creditCard.id(), 1);
        verify(mockDataSource, times(1)).save(Map.of(creditCard.id(),creditCard.withAmount(1)));
    }



}