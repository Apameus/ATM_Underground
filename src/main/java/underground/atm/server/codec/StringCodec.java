package underground.atm.server.codec;

import java.io.IOException;
import java.io.RandomAccessFile;

public record StringCodec(int maxSize) implements Codec<String> {

    @Override
    public String read(RandomAccessFile accessFile) throws IOException {
        int length = (int) accessFile.readByte();
        if (length == 0) return "";
        byte[] bytes = new byte[length];
        accessFile.read(bytes);

        return new String(bytes);
    }

    @Override
    public void write(RandomAccessFile accessFile, String obj) throws IOException {
        byte[] bytes = obj.getBytes();
        accessFile.writeByte(bytes.length);
        accessFile.write(bytes);
    }
}
