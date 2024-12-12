package underground.atm.server.codec;

import java.io.IOException;
import java.io.RandomAccessFile;

public record IntegerCodec() implements Codec<Integer> {

    @Override
    public int maxSize() {
        return 4;
    }

    @Override
    public Integer read(RandomAccessFile accessFile) throws IOException {
        return accessFile.readInt();
    }

    @Override
    public void write(RandomAccessFile accessFile, Integer obj) throws IOException {
        accessFile.writeInt(obj);
    }
}
