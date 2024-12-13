package underground.atm.server.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import underground.atm.common.data.CreditCard;
import underground.atm.server.codec.CreditCardCodec;
import underground.atm.server.codec.IntegerCodec;
import underground.atm.server.codec.StringCodec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DynamicFileManagerTest { //TODO: Add tests

    DynamicFileManager<Integer,CreditCard> dynamicFileManager;
    Path file;
    IntegerCodec integerCodec = new IntegerCodec();
    CreditCardCodec creditCardCodec = new CreditCardCodec(new StringCodec(4));

    @BeforeEach
    void initialize() throws IOException {
        file = Files.createTempFile("map", ".data");
        dynamicFileManager = new DynamicFileManager<>(file, integerCodec,creditCardCodec,2);
    }

    @AfterEach
    void deleteFile() throws IOException {
        Files.delete(file);
    }

    @Test
    @DisplayName("Test simple put and get operation")
    void testSimplePutAndGetOperation() throws IOException {
        CreditCard creditCard = new CreditCard(2004, "0004", 100);
        dynamicFileManager.put(creditCard.id(), creditCard);

        assertThat(dynamicFileManager.get(creditCard.id())).isEqualTo(creditCard);
    }

    @Test
    @DisplayName("Get with key that doesn't exist should return null")
    void getWithKeyThatDoesNotExistShouldReturnNull() throws IOException {
        assertThat(dynamicFileManager.get(0001)).isNull();
    }

    @Test
    @DisplayName("Override value of already existing key")
    void overrideValueOfAlreadyExistingKey() throws IOException {
        CreditCard creditCardA = new CreditCard(2004, "0004", 100);
        CreditCard creditCardB = new CreditCard(2004, "5000", 5000);

        dynamicFileManager.put(creditCardA.id(), creditCardA);
        dynamicFileManager.put(creditCardA.id(), creditCardB);

        assertThat(dynamicFileManager.get(creditCardA.id())).isEqualTo(creditCardB);
    }

    @Test
    @DisplayName("Put and Get from collision")
    void getFromCollision() throws IOException {
        CreditCard creditCardA = new CreditCard(1008, "0004", 100);
        CreditCard creditCardB = new CreditCard(2016, "5000", 5000);

        dynamicFileManager.put(creditCardA.id(),creditCardA);
        dynamicFileManager.put(creditCardB.id(),creditCardB);

        assertThat(dynamicFileManager.get(creditCardA.id())).isEqualTo(creditCardA);
        assertThat(dynamicFileManager.get(creditCardB.id())).isEqualTo(creditCardB);
    }

    @Test
    @DisplayName("Put and Get from collision where the object is placed in the beginning of the file")
    void putAndGetFromCollisionWhereTheObjectIsPlacedInTheBeginningOfTheFile() { //TODO

    }

    @Test
    @DisplayName("When put to full file, resize")
    void whenPutToFullFileResize() throws IOException { // initial size of maxEntries is set to 2
        CreditCard creditCardA = new CreditCard(1008, "0004", 100);
        CreditCard creditCardB = new CreditCard(2016, "5000", 5000);
        CreditCard creditCardC = new CreditCard(1008, "0004", 100);

        dynamicFileManager.put(creditCardA.id(),creditCardA);
        dynamicFileManager.put(creditCardB.id(),creditCardB);

        assertDoesNotThrow(() -> dynamicFileManager.put(creditCardC.id(), creditCardC));
        assertThat(dynamicFileManager.get(creditCardC.id())).isEqualTo(creditCardC);
        assertThat(dynamicFileManager.getMaxEntries()).isEqualTo(4);
    }


}