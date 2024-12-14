package underground.atm.server.repositories;

import underground.atm.server.codec.Codec;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class FileHashMap_ForwardProbing<K, V> {

    private final RandomAccessFile accessFile;
    private static final byte EXISTS_FLAG = 1;

    private final int maxSizeOfEntry;
    private int storedEntries; // Index 0-3 (fixed)
    private int maxEntries;

    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;

    FileHashMap_ForwardProbing(Path path, Codec<K> keyCodec, Codec<V> valueCodec, int maxEntries) throws IOException {
        accessFile = new RandomAccessFile(path.toFile(), "rw");
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;

        maxSizeOfEntry = keyCodec.maxSize() + valueCodec.maxSize() + 1; // +1 for the flag
        this.maxEntries = maxEntries;
        if (accessFile.length() == 0) accessFile.setLength((long) maxEntries * maxSizeOfEntry + Integer.BYTES); // If the file is empty, we create new file (+4 for the storedEntries)
        else { // Read the storedEntries that the file previously had
            accessFile.seek(0);
            storedEntries = accessFile.readInt();
            this.maxEntries = storedEntries * 2;
        }
    }
    FileHashMap_ForwardProbing(Path path, Codec<K> keyCodec, Codec<V> valueCodec) throws IOException {
        this(path,keyCodec,valueCodec,16);
    }


    void put(K key, V value) throws IOException {
        if (storedEntries == maxEntries) resize();
        long offset = offset(key);
        accessFile.seek(offset);
        while (accessFile.readByte() == EXISTS_FLAG) {
            if (offset >= accessFile.length() - Integer.BYTES) offset = 4; // Continue from the start (excluding storedEntries)
            if (keyCodec.read(accessFile).equals(key)) { // Override value
                writeEntry(key, value, offset);
                break;
            }
            offset += maxSizeOfEntry; // Collision: forward probing
        }
        writeEntry(key, value, offset);
        storedEntries++;

         // Update storedEntries
        accessFile.seek(0);
        accessFile.writeInt((int) storedEntries);
    }

    V get(K key) throws IOException {
        long offset = offset(key);
        // Collision check
        for (int i = 0; i < storedEntries; i ++) {
            if (offset >= accessFile.length() - Integer.BYTES) offset = 4; // Continue from the start (excluding storedEntries)
            accessFile.seek(offset + 1);
            if (keyCodec.read(accessFile).equals(key)) {
                return valueCodec.read(accessFile);
            }
            offset += maxSizeOfEntry;
        }
        return null;
    }

    public void remove(K key) throws IOException {
        long offset = offset(key);
        // Collision check
        for (int i = 0; i < storedEntries; i ++) {
            if (offset >= accessFile.length() - Integer.BYTES) offset = 4; // Continue from the start (excluding storedEntries)
            accessFile.seek(offset + 1);
            if (keyCodec.read(accessFile).equals(key)) {
                accessFile.seek(offset);
                accessFile.write(0); // 0 the flag
                break;
            }
            offset += maxSizeOfEntry;
        }
    }

    private void resize() throws IOException {
        long previousLength = accessFile.length();
        maxEntries *= 2;
        long newLength = (long) maxEntries * maxSizeOfEntry + Integer.BYTES; // + 4 for the storedEntries
        accessFile.setLength(newLength);
        var hashMap = HashMap.<K,V>newHashMap((int) storedEntries);
        for (long offset = 4; offset < previousLength; offset += maxSizeOfEntry) { // Offset should start from Index 4 to avoid overriding storedEntries
            accessFile.seek(offset);
            accessFile.writeByte(0);
            K key = keyCodec.read(accessFile);
            V value = valueCodec.read(accessFile);
            hashMap.put(key,value);
        }
        for (long offset = previousLength; offset < newLength; offset += maxSizeOfEntry) { // Make sure that all the new flag indexes are 0
            accessFile.seek(offset);
            accessFile.write(0);
        }
        storedEntries = 0;
        for (Map.Entry<K, V> entry : hashMap.entrySet()) {
            put(entry.getKey(),entry.getValue());
        }
    }

    private long offset(K key) {
        long has = Math.abs(key.hashCode()) % maxEntries;
        return has * maxSizeOfEntry + Integer.BYTES; // + 4 to exclude index 0-3 (storedEntries)
    }

    private void writeEntry(K key, V value, long offset) throws IOException {
        accessFile.seek(offset);
        accessFile.writeByte(EXISTS_FLAG);
        keyCodec.write(accessFile, key);
        valueCodec.write(accessFile, value);
    }

    public int getMaxEntries(){
        return maxEntries;
    }
}




