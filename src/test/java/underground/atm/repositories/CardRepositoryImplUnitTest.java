package underground.atm.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import underground.atm.data.Card;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CardRepositoryImplUnitTest {
    private final CardDataSource mockDataSource = Mockito.mock(CardDataSource.class);
    private final Card mockCard = new Card(1111,11,100);
    private final CardRepositoryImpl cardRepo = new CardRepositoryImpl(mockDataSource);

    @Test
    @DisplayName("Find card by valid ID")
    void findCardByValidID() {
        when(mockDataSource.load()).thenReturn(List.of(mockCard));
        assertThat(cardRepo.findCardBy(mockCard.id())).isEqualTo(mockCard);
    }

    @Test
    @DisplayName("Update amount test")
    void updateAmountTest() {
        when(mockDataSource.load()).thenReturn(new ArrayList<> (List.of(mockCard)));
        cardRepo.updateAmount(mockCard.id(), 1);
        verify(mockDataSource, times(1)).save(List.of(mockCard.withAmount(1)));
    }
}