package underground.atm.server.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import underground.atm.common.data.CreditCard;
import underground.atm.common.exceptions.exceptions.CreditCardNotFoundException;
import underground.atm.common.exceptions.exceptions.InvalidAmountException;
import underground.atm.common.exceptions.exceptions.NotEnoughMoneyException;
import underground.atm.server.repositories.Server_CreditCardRepository;
import underground.atm.server.services.Server_CreditCardService;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class CreditCreditCardServiceUnitTest {

    private final Server_CreditCardRepository creditCardRepository = Mockito.mock(Server_CreditCardRepository.class);
    private final CreditCard creditCard = new CreditCard(1111, "11", 100);
    private final Server_CreditCardService creditCardService = new Server_CreditCardService(creditCardRepository);


    @Test
    @DisplayName("Deposit with valid id and amount")
    void depositWithValidIdAndAmount() {
        when(creditCardRepository.findCardBy(anyInt())).thenReturn(creditCard);

        assertDoesNotThrow(() -> creditCardService.deposit(creditCard.id(), 100));
        verify(creditCardRepository, times(1)).updateAmount(creditCard.id(), creditCard.amount() + 100);
    }

    @Test
    @DisplayName("Deposit with valid id but invalid amount")
    void depositWithValidIdButInvalidAmount() {
        when(creditCardRepository.findCardBy(creditCard.id())).thenReturn(creditCard);

        assertThrows(InvalidAmountException.class, () -> creditCardService.deposit(creditCard.id(), -10));
    }

    @Test
    @DisplayName("Deposit with invalid id")
    void depositWithInvalidId() { //TODO: Should this be CreditCardNotFoundExc ?
        assertThrows(IllegalStateException.class, () -> creditCardService.deposit(11, 11));
    }

    @Test
    @DisplayName("Withdraw valid amount")
    void testWithdraw() {
        when(creditCardRepository.findCardBy(creditCard.id())).thenReturn(creditCard);

        assertDoesNotThrow(() -> creditCardService.withdraw(creditCard.id(), 10));
        verify(creditCardRepository, times(1)).updateAmount(creditCard.id(), creditCard.amount() - 10);
    }

    @Test
    @DisplayName("Withdraw with valid id but amount greater than balance")
    void withdrawWithValidIdButAmountGreaterThanBalance() {
        when(creditCardRepository.findCardBy(anyInt())).thenReturn(creditCard);

        assertThrows(NotEnoughMoneyException.class, () -> creditCardService.withdraw(creditCard.id(), creditCard.amount() + 1));
    }

    @Test
    @DisplayName("Withdraw with valid id but invalid amount")
    void withdrawWithValidIdButInvalidAmount() {
        when(creditCardRepository.findCardBy(anyInt())).thenReturn(creditCard);
        assertThrows(InvalidAmountException.class, () -> creditCardService.withdraw(creditCard.id(), -10));
    }

    @Test
    @DisplayName("Withdraw with invalid id")
    void withdrawWithInvalidId() { //TODO: Should this be CreditCardNotFoundExc ?
        assertThrows(IllegalStateException.class ,() -> creditCardService.withdraw(anyInt(), 10));
    }

    @Test
    @DisplayName("View balance with valid id test")
    void viewBalanceWithValidIDTest() {
        when(creditCardRepository.findCardBy(creditCard.id())).thenReturn(creditCard);
        int balance = assertDoesNotThrow(() -> creditCardService.viewBalance(creditCard.id()));
        assertThat(balance).isEqualTo(creditCard.amount());
    }

    @Test
    @DisplayName("View balance with invalid id")
    void viewBalanceWithInvalidId() {
        assertThrows(IllegalStateException.class, () -> creditCardService.viewBalance(anyInt()));
    }

    @Test
    @DisplayName("Transfer with valid card id's and amount test")
    void transferWithValidCardIdSAndAmountTest() {
        CreditCard otherMockCreditCard = new CreditCard(9999, "99", 0);
        when(creditCardRepository.findCardBy(creditCard.id())).thenReturn(creditCard);
        when(creditCardRepository.findCardBy(otherMockCreditCard.id())).thenReturn(otherMockCreditCard);

        int amount = 10;
        assertDoesNotThrow(() -> creditCardService.transfer(creditCard.id(), otherMockCreditCard.id(), amount));
        verify(creditCardRepository, times(1))
                .updateAmount(creditCard.id(), creditCard.amount() - amount);
        verify(creditCardRepository, times(1))
                .updateAmount(otherMockCreditCard.id(), otherMockCreditCard.amount() + amount);
    }

    @Test
    @DisplayName("Transfer with invalid card id's")
    void transferWithInvalidCardIdS() {
        when(creditCardRepository.findCardBy(creditCard.id())).thenReturn(creditCard);
        assertThrows(CreditCardNotFoundException.class, () -> creditCardService.transfer(creditCard.id(), 1234, 10));
    }

    @Test
    @DisplayName("Transfer with valid id's but amount greater than balance")
    void transferWithValidIdSButAmountGreaterThanBalance() {
        CreditCard otherMockCreditCard = new CreditCard(9999, "99", 0);
        when(creditCardRepository.findCardBy(creditCard.id())).thenReturn(creditCard);
        when(creditCardRepository.findCardBy(otherMockCreditCard.id())).thenReturn(otherMockCreditCard);

        assertThrows(NotEnoughMoneyException.class, () -> creditCardService.transfer(creditCard.id(), otherMockCreditCard.id(), creditCard.amount() + 1));
        verify(creditCardRepository, times(0)).updateAmount(otherMockCreditCard.id(), otherMockCreditCard.amount() + creditCard.amount() + 1);
    }


}