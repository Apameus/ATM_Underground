package underground.atm.server.codec;

import java.io.IOException;
import java.io.RandomAccessFile;

public interface Codec<T> {
    int maxSize();

    T read(RandomAccessFile accessFile) throws IOException;

    void write(RandomAccessFile accessFile, T obj) throws IOException;
}
