package underground.atm.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import underground.atm.data.CreditCard;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CreditServerCreditCardRepositoryImplUnitTest {
    private final CreditCardDataSource mockDataSource = Mockito.mock(CreditCardDataSource.class);
    private final CreditCard creditCard = new CreditCard(1111,"11",100);
    private final Server_CreditCardRepositoryImpl cardRepo = new Server_CreditCardRepositoryImpl(mockDataSource);

    @Test
    @DisplayName("Find card by valid ID")
    void findCardByValidID() {
        when(mockDataSource.load()).thenReturn(List.of(creditCard));
        assertThat(cardRepo.findCardBy(creditCard.id())).isEqualTo(creditCard);
    }

    @Test
    @DisplayName("Update amount test")
    void updateAmountTest() {
        when(mockDataSource.load()).thenReturn(new ArrayList<> (List.of(creditCard)));
        cardRepo.updateAmount(creditCard.id(), 1);
        verify(mockDataSource, times(1)).save(List.of(creditCard.withAmount(1)));
    }
}