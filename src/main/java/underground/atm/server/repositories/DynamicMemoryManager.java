package underground.atm.server.repositories;

import underground.atm.server.codec.Codec;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class DynamicMemoryManager<K, V> {

    private static final byte EXISTS_FLAG = 1;
    final RandomAccessFile accessFile;
    long maxEntries;
    long storedEntries;

    final Codec<K> keyCodec;
    final Codec<V> valueCodec;

    final int maxSizeOfEntry;

    DynamicMemoryManager(Path path, Codec<K> keyCodec, Codec<V> valueCodec) throws IOException {
        accessFile = new RandomAccessFile(path.toFile(), "rw");
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;

        maxSizeOfEntry = keyCodec.maxSize() + valueCodec.maxSize() + 1; // +1 for the flag
        maxEntries = 16;

        if (accessFile.length() == 0) accessFile.setLength(maxEntries * maxSizeOfEntry); // If the file is empty -> we create new file
        else {
            //TODO: Determine the entries that the file previously had, so when you re-reed it you know what to expect
        }
    }

    void put(K key, V value) throws IOException {
        if (storedEntries == maxEntries) resize();
        long offset = offset(key);
        accessFile.seek(offset);
        while (accessFile.readByte() == EXISTS_FLAG) {
            if (keyCodec.read(accessFile).equals(key)) { // Override value
                accessFile.seek(offset); // return the pointer to the start of entry
                break;
            }
            offset += maxSizeOfEntry; // Collision: forward probing
            accessFile.seek(offset); // move pointer to next entry
            if (storedEntries >= maxEntries) offset = 0; // continue from the start
        }
        accessFile.writeByte(EXISTS_FLAG);
        keyCodec.write(accessFile, key);
        valueCodec.write(accessFile, value);

        storedEntries++; //TODO: If we override we shouldn't increase
    }

    V get(K key) throws IOException {
        long offset = offset(key);
        // Collision check
        while (true) {
            if (offset >= accessFile.length()) return null; // key doesn't exist
            if (accessFile.readByte() == EXISTS_FLAG) {}  // Check the flag
            accessFile.seek(offset);
            if (keyCodec.read(accessFile).equals(key)) break;
            offset += maxSizeOfEntry;
        }
        return valueCodec.read(accessFile);
    }

    private void resize() throws IOException {
//        long previousLength = accessFile.length();
        maxEntries *= 2;
        long newLength = maxEntries * maxSizeOfEntry;
        accessFile.setLength(newLength);
        var hashMap = HashMap.<K,V>newHashMap((int) storedEntries);
        for (long offset = 0; offset < newLength; offset += maxSizeOfEntry) { //TODO: ! if you loop (override flag to 0 and parse key & value) until new length you'll also put the empty slots !
            accessFile.seek(offset);
            accessFile.writeByte(0);
            K key = keyCodec.read(accessFile);
            V value = valueCodec.read(accessFile);
            hashMap.put(key,value);
        }
        for (Map.Entry<K, V> entry : hashMap.entrySet()) {
            put(entry.getKey(),entry.getValue());
        }

    }


    private long offset(K key) {
        long has = Math.abs(key.hashCode()) % maxEntries;
        return has * maxSizeOfEntry;
    }

}
