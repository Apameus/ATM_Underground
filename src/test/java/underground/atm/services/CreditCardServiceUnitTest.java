package underground.atm.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import underground.atm.data.Card;
import underground.atm.exceptions.AuthorizationFailedException;
import underground.atm.exceptions.CardNotFoundException;
import underground.atm.exceptions.InvalidAmountException;
import underground.atm.exceptions.NotEnoughMoneyException;
import underground.atm.repositories.CardRepository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class CreditCardServiceUnitTest {

    private final CardRepository mockRepo = Mockito.mock(CardRepository.class);
    private final Card mockCard = new Card(1111,11,100);
    private final CreditCardService creditCardService = new CreditCardService(mockRepo);



    @Test
    @DisplayName("Deposit with valid id and amount")
    void depositWithValidIdAndAmount()  {
        when(mockRepo.findCardBy(anyInt())).thenReturn(mockCard);

        assertDoesNotThrow(() -> creditCardService.deposit(mockCard.id(), 100));
        verify(mockRepo,times(1)).updateAmount(mockCard.id(),mockCard.amount() + 100);
    }

    @Test
    @DisplayName("Deposit with amount greater than balance")
    void depositWithAmountGreaterThanBalance() {
        when(mockRepo.findCardBy(mockCard.id())).thenReturn(mockCard);

        assertThrows(InvalidAmountException.class, () -> creditCardService.deposit(mockCard.id(), -10));
    }

    @Test
    @DisplayName("Withdraw valid amount")
    void testWithdraw() {
        when(mockRepo.findCardBy(mockCard.id())).thenReturn(mockCard);

        assertDoesNotThrow(() -> creditCardService.withdraw(mockCard.id(), 10));
        verify(mockRepo, times(1)).updateAmount(mockCard.id(), mockCard.amount() - 10);
    }
    
    @Test
    @DisplayName("Withdraw with amount greater than balance")
    void withdrawWithAmountGreaterThanBalance() {
        when(mockRepo.findCardBy(anyInt())).thenReturn(mockCard);

        assertThrows(NotEnoughMoneyException.class, () -> creditCardService.withdraw(mockCard.id(), mockCard.amount() + 1));
    }

    @Test
    @DisplayName("Withdraw with invalid amount")
    void withdrawWithInvalidAmount() {
        when(mockRepo.findCardBy(anyInt())).thenReturn(mockCard);
        assertThrows(InvalidAmountException.class, () -> creditCardService.withdraw(mockCard.id(), -10));
    }

    @Test
    @DisplayName("View balance test")
    void viewBalanceTest() throws CardNotFoundException {
        when(mockRepo.findCardBy(mockCard.id())).thenReturn(mockCard);
        assertThat(creditCardService.viewBalance(mockCard.id())).isEqualTo(mockCard.amount());
    }

    @Test
    @DisplayName("Transfer with valid card id's and amount test")
    void transferWithValidCardIdSAndAmountTest() {
        Card otherMockCard = new Card(9999, 99, 0);
        when(mockRepo.findCardBy(mockCard.id())).thenReturn(mockCard);
        when(mockRepo.findCardBy(otherMockCard.id())).thenReturn(otherMockCard);

        assertDoesNotThrow(() -> creditCardService.transfer(mockCard.id(), otherMockCard.id(), 10));
        verify(mockRepo,times(1)).updateAmount(mockCard.id(), mockCard.amount() - 10);
        verify(mockRepo, times(1)).updateAmount(otherMockCard.id(), otherMockCard.amount() + 10);
    }

    @Test
    @DisplayName("Transfer with invalid card id's")
    void transferWithInvalidCardIdS() {
        when(mockRepo.findCardBy(mockCard.id())).thenReturn(mockCard);
        assertThrows(CardNotFoundException.class, () -> creditCardService.transfer(mockCard.id(), 1234,10));
    }

    @Test
    @DisplayName("Transfer with valid id's but amount greater than balance")
    void transferWithValidIdSButAmountGreaterThanBalance() {
        Card otherMockCard = new Card(9999, 99, 0);
        when(mockRepo.findCardBy(mockCard.id())).thenReturn(mockCard);
        when(mockRepo.findCardBy(otherMockCard.id())).thenReturn(otherMockCard);

        assertThrows(NotEnoughMoneyException.class ,() -> creditCardService.transfer(mockCard.id(), otherMockCard.id(), mockCard.amount() + 1));
        verify(mockRepo, times(0)).updateAmount(otherMockCard.id(), otherMockCard.amount() + mockCard.amount() + 1);
    }


}