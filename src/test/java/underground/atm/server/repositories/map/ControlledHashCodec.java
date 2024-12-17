package underground.atm.server.repositories.map;

import underground.atm.server.codec.Codec;
import underground.atm.server.codec.StringCodec;

import java.io.IOException;
import java.io.RandomAccessFile;

record ControlledHashCodec(StringCodec stringCodec) implements Codec<ControlledHashObject> {
    //hashCode -> KEY.
    //KEY -> Value.

    @Override
    public int maxBytesSize() {
        return Integer.BYTES + stringCodec.maxBytesSize();
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
