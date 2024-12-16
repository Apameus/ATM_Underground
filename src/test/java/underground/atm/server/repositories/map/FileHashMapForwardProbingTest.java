package underground.atm.server.repositories.map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import underground.atm.common.data.CreditCard;
import underground.atm.server.codec.CreditCardCodec;
import underground.atm.server.codec.IntegerCodec;
import underground.atm.server.codec.StringCodec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FileHashMapForwardProbingTest {

    FileHashMap_ForwardProbing<Integer,CreditCard> fileHashMapForwardProbing;
    Path file;
    IntegerCodec integerCodec = new IntegerCodec();
    CreditCardCodec creditCardCodec = new CreditCardCodec(new StringCodec(4));

    @BeforeEach
    void initialize() throws IOException {
        file = Files.createTempFile("map", ".data");
        fileHashMapForwardProbing = new FileHashMap_ForwardProbing<>(file, integerCodec,creditCardCodec,2);
    }

    @AfterEach
    void deleteFile() throws IOException {
        Files.delete(file);
    }

    @Test
    @DisplayName("Test simple put and get operation")
    void testSimplePutAndGetOperation() throws IOException {
        CreditCard creditCard = new CreditCard(2004, "0004", 100);
        fileHashMapForwardProbing.put(creditCard.id(), creditCard);

        assertThat(fileHashMapForwardProbing.get(creditCard.id())).isEqualTo(creditCard);
    }

    @Test
    @DisplayName("Get with key that doesn't exist should return null")
    void getWithKeyThatDoesNotExistShouldReturnNull() throws IOException {
        assertThat(fileHashMapForwardProbing.get(0001)).isNull();
    }

    @Test
    @DisplayName("Override value of already existing key")
    void overrideValueOfAlreadyExistingKey() throws IOException {
        CreditCard creditCardA = new CreditCard(2004, "0004", 100);
        CreditCard creditCardB = new CreditCard(2004, "5000", 5000);

        fileHashMapForwardProbing.put(creditCardA.id(), creditCardA);
        fileHashMapForwardProbing.put(creditCardA.id(), creditCardB);

        assertThat(fileHashMapForwardProbing.get(creditCardA.id())).isEqualTo(creditCardB);
        assertThat(fileHashMapForwardProbing.getMaxEntries()).isEqualTo(2);
    }

    @Test
    @DisplayName("Put and Get from collision")
    void getFromCollision(@TempDir Path dir) throws IOException {
        var keyAsValueCodec = new StringCodec(4);
        var hashAsKeyCodec = new ControlledHashCodec(keyAsValueCodec);
//        var file = Files.createFile(dir).resolve("map.data");
        var map = new FileHashMap_ForwardProbing<>(dir.resolve("map.data"), hashAsKeyCodec, keyAsValueCodec);

        var a = new ControlledHashObject(500, "A");
        var b = new ControlledHashObject(500, "B");

        map.put(a, "A50");
        map.put(b, "B50");

        assertThat(map.get(a)).isEqualTo("A50");
        assertThat(map.get(b)).isEqualTo("B50");
    }

    @Test
    @DisplayName("Resize when put method is called to full file")
    void resizeWhenPutToFullFile() throws IOException { // initial size of maxEntries is set to 2
        CreditCard creditCardA = new CreditCard(1008, "0004", 100);
        CreditCard creditCardB = new CreditCard(2016, "5000", 5000);
        CreditCard creditCardC = new CreditCard(1008, "0004", 100);

        fileHashMapForwardProbing.put(creditCardA.id(),creditCardA);
        fileHashMapForwardProbing.put(creditCardB.id(),creditCardB);

        assertDoesNotThrow(() -> fileHashMapForwardProbing.put(creditCardC.id(), creditCardC));
        assertThat(fileHashMapForwardProbing.get(creditCardC.id())).isEqualTo(creditCardC);
        assertThat(fileHashMapForwardProbing.getMaxEntries()).isEqualTo(4);
    }

    @Test
    @DisplayName("Overload PUT and RESIZE methods tests")
    void overloadPutAndResizeMethodsTests() throws IOException {
        for (int i = 0; i < 500; i++) {
            fileHashMapForwardProbing.put(i, new CreditCard(i, String.valueOf(i), i));
        }
        for (int i = 0; i < 500; i++) {
            CreditCard creditCard = fileHashMapForwardProbing.get(i);
            assertThat(creditCard.id()).isEqualTo(i);
            assertThat(creditCard.pin()).isEqualTo(String.valueOf(i));
            assertThat(creditCard.amount()).isEqualTo(i);
        }
    }

    @Test
    @DisplayName("Get from collision where previous element is removed")
    void getFromCollisionWherePreviousElementIsRemoved(@TempDir Path dir) throws IOException {
        var keyAsValueCodec = new StringCodec(50);
        var hashAsKeyCodec = new ControlledHashCodec(keyAsValueCodec);
        var cDynamicFileManager = new FileHashMap_ForwardProbing<>(dir.resolve("cDynamicFileManager.data"), hashAsKeyCodec, keyAsValueCodec);

        var a = new ControlledHashObject(500, "A");
        var b = new ControlledHashObject(500, "B");

        cDynamicFileManager.put(a, "A50");
        cDynamicFileManager.put(b, "B50");
        cDynamicFileManager.remove(a);

        assertThat(cDynamicFileManager.get(b)).isEqualTo("B50");
    }

    @Test
    @DisplayName("gamiete o ioannis")
    void gamieteOIoannis(@TempDir Path dir) throws IOException {
        var keyAsValueCodec = new StringCodec(4);
        var hashAsKeyCodec = new ControlledHashCodec(keyAsValueCodec);
        var cDynamicFileManager = new FileHashMap_ForwardProbing<>(dir.resolve("cDynamicFileManager.data"), hashAsKeyCodec, keyAsValueCodec);

        for (int i = 0; i < 500; i++) { // Put 500 entries with collision
            String stupidValue = String.valueOf(i);
            var collisionKey = new ControlledHashObject(500, stupidValue);
            System.out.println(i);
            cDynamicFileManager.put(collisionKey, stupidValue);
        }

        var target = new ControlledHashObject(500, "A500"); // Put another entry with collision
        cDynamicFileManager.put(target, "A500");

        for (int i = 0; i < 500; i++) { // Remove the first 500 collision entries
            ControlledHashObject collisionKey = new ControlledHashObject(500, String.valueOf(i));
            cDynamicFileManager.remove(collisionKey);
        }
        // Find the last motherf*cker
        assertThat(cDynamicFileManager.get(target)).isEqualTo("A500");
    }

    @Test
    @DisplayName("Put and Get from collision where the object is placed in the beginning of the file")
    void putAndGetFromCollisionWhereTheObjectIsPlacedInTheBeginningOfTheFile(@TempDir Path dir) throws IOException { //TODO
        var keyAsValueCodec = new StringCodec(2);
        var hashAsKeyCodec = new ControlledHashCodec(keyAsValueCodec);
        var conManager = new FileHashMap_ForwardProbing<>(dir.resolve("map.data"),hashAsKeyCodec,keyAsValueCodec,4);

        var a1 = new ControlledHashObject(1,"A1");
        var a2 = new ControlledHashObject(1,"A2");
        var b1 = new ControlledHashObject(3,"B1");
        var a3 = new ControlledHashObject(1,"A3"); // should put to 0 index

        conManager.put(a1, a1.key());
        conManager.put(a2, a2.key());
        conManager.put(b1, b1.key());
        conManager.put(a3, a3.key());

        assertThat(conManager.get(a3)).isEqualTo(a3.key());

    }

}
