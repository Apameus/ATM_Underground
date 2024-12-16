package underground.atm.server.repositories.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import underground.atm.server.codec.Codec;
import underground.atm.server.codec.StringCodec;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FileHashMap_CloseAddressingTest {

    FileHashMap_CloseAddressing<ControlledHashObject,String> fileHashMap_closeAddressing;
    Path path;
    StringCodec stringCodec;
    ControlledHashCodec controlledHashCodec;


    @BeforeEach
    void initialize(@TempDir Path dir) throws IOException {
        stringCodec = new StringCodec(2);
        controlledHashCodec = new ControlledHashCodec(stringCodec);
        fileHashMap_closeAddressing = new FileHashMap_CloseAddressing<>(dir.resolve("map.data"),
                controlledHashCodec,
                stringCodec,
                2);
    }

    @Test
    @DisplayName("Put and Get operations test")
    void putAndGetOperationsTest() throws IOException {
        ControlledHashObject a1 = new ControlledHashObject(10, "A1");
        fileHashMap_closeAddressing.put(a1,a1.key());
        assertThat(fileHashMap_closeAddressing.get(a1)).isEqualTo(a1.key());
    }

    @Test
    @DisplayName("Get with non-existing key")
    void getWithNonExistingKey() throws IOException {
        assertThat(fileHashMap_closeAddressing.get(new ControlledHashObject(99, "00"))).isNull();;
    }


    @Test
    @DisplayName("Override value of existing exist")
    void overrideValueOfExistingExist() throws IOException {
        ControlledHashObject a1 = new ControlledHashObject(10, "A1");
        ControlledHashObject a2 = new ControlledHashObject(10, "A2");

        fileHashMap_closeAddressing.put(a1,a1.key());
        fileHashMap_closeAddressing.put(a1,a2.key());

        assertThat(fileHashMap_closeAddressing.get(a1)).isEqualTo(a2.key());
    }

    @Test
    @DisplayName("Get and Put from collision")
    void getAndPutFromCollision() throws IOException {
        var a = new ControlledHashObject(500, "A");
        var b = new ControlledHashObject(500, "B");

        fileHashMap_closeAddressing.put(a, a.key());
        fileHashMap_closeAddressing.put(b, b.key());

        assertThat(fileHashMap_closeAddressing.get(a)).isEqualTo(a.key());
        assertThat(fileHashMap_closeAddressing.get(b)).isEqualTo(b.key());
    }

    @Test
    @DisplayName("Resize when Put function is called to full file")
    void resizeWhenPutFunctionIsCalledToFullFile() throws IOException {
        var a = new ControlledHashObject(500, "A");
        var b = new ControlledHashObject(500, "B");
        var c = new ControlledHashObject(500,"C");

        fileHashMap_closeAddressing.put(a, a.key());
        fileHashMap_closeAddressing.put(b, b.key());

        assertDoesNotThrow(() -> fileHashMap_closeAddressing.put(c, c.key()));

        assertThat(fileHashMap_closeAddressing.get(a)).isEqualTo(a.key());
        assertThat(fileHashMap_closeAddressing.get(b)).isEqualTo(b.key());
        assertThat(fileHashMap_closeAddressing.get(c)).isEqualTo(c.key());

        assertThat(fileHashMap_closeAddressing.maxEntries()).isEqualTo(4);
    }

    @Test
    @DisplayName("Overloaded Put and Get methods")
    void overloadedPutAndGetMethods() throws IOException {
        for (int i = 0; i < 500; i++) {
            ControlledHashObject obj = new ControlledHashObject(4, String.valueOf(i));
            fileHashMap_closeAddressing.put(obj,obj.key());
            System.out.println(i);
        }
        for (int i = 0; i < 500; i++) {
            ControlledHashObject obj = new ControlledHashObject(i, String.valueOf(i));
            assertThat(fileHashMap_closeAddressing.get(obj)).isEqualTo(obj.key());
        }
    }
    
}