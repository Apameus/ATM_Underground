package underground.atm.server.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import underground.atm.common.data.CreditCard;
import underground.atm.server.codec.Codec;
import underground.atm.server.codec.CreditCardCodec;
import underground.atm.server.codec.IntegerCodec;
import underground.atm.server.codec.StringCodec;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DynamicFileManagerTest {

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
    void getFromCollision(@TempDir Path dir) throws IOException {
        var keyAsValueCodec = new StringCodec(4);
        var hashAsKeyCodec = new ControlledHashCodec(keyAsValueCodec);
//        var file = Files.createFile(dir).resolve("map.data");
        var map = new DynamicFileManager<>(dir.resolve("map.data"), hashAsKeyCodec, keyAsValueCodec);

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

        dynamicFileManager.put(creditCardA.id(),creditCardA);
        dynamicFileManager.put(creditCardB.id(),creditCardB);

        assertDoesNotThrow(() -> dynamicFileManager.put(creditCardC.id(), creditCardC));
        assertThat(dynamicFileManager.get(creditCardC.id())).isEqualTo(creditCardC);
        assertThat(dynamicFileManager.getMaxEntries()).isEqualTo(4);
    }

    @Test
    @DisplayName("Overload PUT and RESIZE methods tests")
    void overloadPutAndResizeMethodsTests() throws IOException {
        for (int i = 0; i < 500; i++) {
            dynamicFileManager.put(i, new CreditCard(i, String.valueOf(i), i));
        }
        for (int i = 0; i < 500; i++) {
            CreditCard creditCard = dynamicFileManager.get(i);
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
        var cDynamicFileManager = new DynamicFileManager<>(dir.resolve("cDynamicFileManager.data"), hashAsKeyCodec, keyAsValueCodec);

        var a = new ControlledHashObject(500, "A");
        var b = new ControlledHashObject(500, "B");

        cDynamicFileManager.put(a, "A50");
        cDynamicFileManager.put(b, "B50");
        cDynamicFileManager.remove(a);

        assertThat(cDynamicFileManager.get(b)).isEqualTo("B50");
    }

    @Test
    @DisplayName("Put and Get from collision where the object is placed in the beginning of the file")
    void putAndGetFromCollisionWhereTheObjectIsPlacedInTheBeginningOfTheFile(@TempDir Path dir) throws IOException { //TODO
        var keyAsValueCodec = new StringCodec(2);
        var hashAsKeyCodec = new ControlledHashCodec(keyAsValueCodec);
        var conManager = new DynamicFileManager<>(dir.resolve("map.data"),hashAsKeyCodec,keyAsValueCodec,4);

        var a1 = new ControlledHashObject(1,"A1");
        var a2 = new ControlledHashObject(1,"A2");
        var b1 = new ControlledHashObject(3,"B1");
        var a3 = new ControlledHashObject(1,"A3"); // should put to 0 index

        conManager.put(a1, a1.key);
        conManager.put(a2, a2.key);
        conManager.put(b1, b1.key);
        conManager.put(a3, a3.key);

        assertThat(conManager.get(a3)).isEqualTo(a3.key);

    }






    record ControlledHashObject(int hashcode, String key){
        @Override
        public int hashCode() {
            return hashcode;
        }
    }

    record ControlledHashCodec(StringCodec stringCodec) implements Codec<ControlledHashObject> {
        //hashCode -> KEY.
        //KEY -> Value.

        @Override
        public int maxSize() {
            return Integer.BYTES + stringCodec.maxSize();
        }

        @Override
        public ControlledHashObject read(RandomAccessFile accessFile) throws IOException {
            int hashcode = accessFile.readInt();
            String read = stringCodec.read(accessFile);
            return new ControlledHashObject(hashcode, read);
        }

        @Override
        public void write(RandomAccessFile accessFile, ControlledHashObject obj) throws IOException {
            accessFile.writeInt(obj.hashcode()); //hashCode -> KEY
            stringCodec.write(accessFile, obj.key()); //KEY -> VALUE
        }
    }

}
