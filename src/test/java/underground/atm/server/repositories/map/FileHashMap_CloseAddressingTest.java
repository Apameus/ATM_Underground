package underground.atm.server.repositories.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import underground.atm.server.codec.StringCodec;
import java.io.IOException;
import java.nio.file.Path;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FileHashMap_CloseAddressingTest {

    FileHashMap_CloseAddressing<ControlledHashObject, String> fileHashMap;
    StringCodec stringCodec;
    ControlledHashCodec controlledHashCodec;


    @BeforeEach
    void initialize(@TempDir Path dir) throws IOException {
        stringCodec = new StringCodec(3);
        controlledHashCodec = new ControlledHashCodec(stringCodec);
        fileHashMap = new FileHashMap_CloseAddressing<>(dir.resolve("map.data"),
                controlledHashCodec,
                stringCodec,
                2);
    }

    @Test
    @DisplayName("Put and Get operations test")
    void putAndGetOperationsTest() throws IOException {
        var a1 = new ControlledHashObject(10, "A1");
        fileHashMap.put(a1, a1.key());
        assertThat(fileHashMap.get(a1)).isEqualTo(a1.key());
    }

    @Test
    @DisplayName("Get with non-existing key")
    void getWithNonExistingKey() throws IOException {
        assertThat(fileHashMap.get(new ControlledHashObject(99, "00"))).isNull();
    }


    @Test
    @DisplayName("Override value of existing exist")
    void overrideValueOfExistingExist() throws IOException {
        var a1 = new ControlledHashObject(10, "A1");
        var a2 = new ControlledHashObject(10, "A2");

        fileHashMap.put(a1, a1.key());
        fileHashMap.put(a1, a2.key());

        assertThat(fileHashMap.get(a1)).isEqualTo(a2.key());
    }

    @Test
    @DisplayName("Get and Put from collision")
    void getAndPutFromCollision() throws IOException {
        var a = new ControlledHashObject(500, "A");
        var b = new ControlledHashObject(500, "B");

        fileHashMap.put(a, a.key());
        fileHashMap.put(b, b.key());

        assertThat(fileHashMap.get(a)).isEqualTo(a.key());
        assertThat(fileHashMap.get(b)).isEqualTo(b.key());
    }

    @Test
    @DisplayName("Put and Get from collision, where the entry is placed in the begging of the file")
    void putAndGetFromCollisionWhereTheEntryIsPlacedInTheBeggingOfTheFile() throws IOException {
        var a1 = new ControlledHashObject(1, "A1");
        var a2 = new ControlledHashObject(1, "A2");
        var b1 = new ControlledHashObject(2, "B1");
        var a3 = new ControlledHashObject(1, "A3");

        fileHashMap.put(a1,a1.key());
        fileHashMap.put(a2,a2.key());
        fileHashMap.put(b1,b1.key());
        fileHashMap.put(a3,a3.key()); // should be placed to the first empty index of the file

        assertThat(fileHashMap.get(a3)).isEqualTo(a3.key());
    }

    @Test
    @DisplayName("Resize when Put function is called to full file")
    void resizeWhenPutFunctionIsCalledToFullFile() throws IOException {
        var a = new ControlledHashObject(500, "A");
        var b = new ControlledHashObject(500, "B");
        var c = new ControlledHashObject(500, "C");

        fileHashMap.put(a, a.key());
        fileHashMap.put(b, b.key());

        assertDoesNotThrow(() -> fileHashMap.put(c, c.key()));

        assertThat(fileHashMap.get(a)).isEqualTo(a.key());
        assertThat(fileHashMap.get(b)).isEqualTo(b.key());
        assertThat(fileHashMap.get(c)).isEqualTo(c.key());

        assertThat(fileHashMap.maxEntries()).isEqualTo(4);
    }

    @Test
    @DisplayName("Get from collision where prev entry is removed")
    void getFromCollisionWherePrevEntryIsRemoved() throws IOException {
        var a = new ControlledHashObject(10, "A");
        var b = new ControlledHashObject(10, "B");

        fileHashMap.put(a, a.key());
        fileHashMap.put(b, b.key());
        fileHashMap.remove(a);

        assertThat(fileHashMap.get(b)).isEqualTo(b.key());
    }

    @Test
    @DisplayName("Overloaded Put and Get methods")
    void overloadedPutAndGetMethods() throws IOException {
        for (int i = 0; i < 500; i++) { // Put 500 entries with same hash (collision)
            var obj = new ControlledHashObject(0, String.valueOf(i));
            fileHashMap.put(obj, obj.key());

        }
        for (int i = 0; i < 500; i++) { // Find each entry in the file
            var obj = new ControlledHashObject(0, String.valueOf(i));
            assertThat(fileHashMap.get(obj)).isEqualTo(obj.key());
        }
    }

    @Test
    @DisplayName("Overload Put and Remove methods")
    void overloadPutAndRemoveMethods() throws IOException {
        for (int i = 0; i < 500; i++) { // Put 500 entries with same hash (collision)
            var obj = new ControlledHashObject(5, String.valueOf(i));
            fileHashMap.put(obj, obj.key());
        }
        var extraEntry = new ControlledHashObject(500, "500"); // Put another to the end
        fileHashMap.put(extraEntry,extraEntry.key());
        for (int i = 0; i < 500; i++) { // Remove the first 500
            var obj = new ControlledHashObject(5, String.valueOf(i));
            fileHashMap.remove(obj);
        }

        assertThat(fileHashMap.get(extraEntry)).isEqualTo(extraEntry.key()); // Find the last
    }
}