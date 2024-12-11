package underground.atm.server.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import underground.atm.common.data.CreditCard;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class CreditCardDynamicMemoryManagerTest {
    CreditCardDynamicMemoryManager dynamicMemoryManager;

    @BeforeEach
    void initialize(@TempDir Path path) throws IOException {
        var file = path.resolve("map.data");
         dynamicMemoryManager = new CreditCardDynamicMemoryManager(file);
    }

    @Test
    @DisplayName("Test simple put and get operation")
    void testSimplePutAndGetOperation() throws IOException {
        CreditCard creditCard = new CreditCard(2004, "0004", 100);
        dynamicMemoryManager.put(creditCard.id(), creditCard);

        assertThat(dynamicMemoryManager.get(creditCard.id())).isEqualTo(creditCard);
    }

    @Test
    @DisplayName("Get with key that doesn't exist should return null")
    void getWithKeyThatDoesNotExistShouldReturnNull() throws IOException {
        assertThat(dynamicMemoryManager.get(0001)).isNull();
    }

    @Test
    @DisplayName("Get from collision")
    void getFromCollision() {
        CreditCard creditCard = new CreditCard(2004, "0004", 100);
    }

    @Test
    @DisplayName("Put to collision")
    void putToCollision() {

    }
}