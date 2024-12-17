package underground.atm.server.repositories.map;

import underground.atm.server.codec.Codec;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class FileHashMap_CloseAddressing<K, V> implements FileHashMap<K, V> {
    private RandomAccessFile accessFile;
    private static final byte EXISTS_FLAG = 1;
    private static final byte FLAG_SIZE = 1;
    private static final byte NEXT_OFFSET_SIZE = 8;
    private static final byte STORED_ENTRIES_SIZE = 4;

    private final int maxSizeOfEntry;
    private int maxEntries;
    private int storedEntries;
    private final Codec<K> keyCodec;

    private final Codec<V> valueCodec;

    FileHashMap_CloseAddressing(Path path, Codec<K> keyCodec, Codec<V> valueCodec, int maxEntries) throws IOException {
        accessFile = new RandomAccessFile(path.toFile(), "rw");
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;

        maxSizeOfEntry = FLAG_SIZE + NEXT_OFFSET_SIZE + keyCodec.maxBytesSize() + valueCodec.maxBytesSize(); // +1 for the flag +8 for the offset of next entry
        this.maxEntries = maxEntries;
        if (accessFile.length() == 0)
            accessFile.setLength((long) maxEntries * maxSizeOfEntry + STORED_ENTRIES_SIZE); // If the file is empty, we create new file (+4 for the storedEntries)
        else { // Read the storedEntries that the file previously had
            accessFile.seek(0);
            storedEntries = accessFile.readInt();
            this.maxEntries = storedEntries * 2;
        }
    }

    public FileHashMap_CloseAddressing(Path path, Codec<K> keyCodec, Codec<V> valueCodec) throws IOException {
        this(path, keyCodec, valueCodec, 16);
    }


    @Override
    public void put(K key, V value) throws IOException {
        if (storedEntries == maxEntries) resize();
        long offset = offset(key);
        accessFile.seek(offset);
        while (accessFile.readByte() == EXISTS_FLAG) {
            accessFile.seek(offset + FLAG_SIZE + NEXT_OFFSET_SIZE);
            if (keyCodec.read(accessFile).equals(key)) {
                valueCodec.write(accessFile, value); // Override value
                return;
            }
            accessFile.seek(offset + FLAG_SIZE);
            var nextOffset = accessFile.readLong();
            accessFile.seek(nextOffset);
            if (accessFile.readByte() != EXISTS_FLAG || nextOffset == 0) { // Check if next entry exist
                nextOffset = offset + maxSizeOfEntry;
                if (nextOffset >= accessFile.length() - Integer.BYTES){
                    nextOffset = findFirstEmptyOffset(); // Continue from the start of the file
                }
                accessFile.seek(offset + FLAG_SIZE);
                accessFile.writeLong(nextOffset); // Write the nextOffset in the previous entry
            }
            offset = nextOffset; // Collision: close addressing
            accessFile.seek(nextOffset);
        }
        writeEntry(key, value, offset);
        storedEntries++;

        // Update storedEntries
        accessFile.seek(0);
        accessFile.writeInt((int) storedEntries);
    }

    @Override
    public V get(K key) throws IOException {
        long offset = offset(key);
        // Collision check
        for (int i = 0; i < storedEntries; i ++) {
            accessFile.seek(offset + FLAG_SIZE );
            var nextOffset = accessFile.readLong();
            if (keyCodec.read(accessFile).equals(key)) {
                return valueCodec.read(accessFile);
            }
            offset = nextOffset;
        }
        return null;
    }

    @Override
    public void remove(K key) throws IOException {
        long offset = offset(key);
        // Collision check
        for (int i = 0; i < storedEntries; i ++) {
            accessFile.seek(offset + FLAG_SIZE);
            var nextOffset = accessFile.readLong();
            if (keyCodec.read(accessFile).equals(key)) {
                accessFile.seek(offset);
                accessFile.write(0); // 0 the flag
                return;
            }
            offset = nextOffset;
        }
    }

    private void resize() throws IOException {
        long previousLength = accessFile.length();
        maxEntries *= 2;
        long newLength = (long) maxEntries * maxSizeOfEntry + STORED_ENTRIES_SIZE; // + 4 for the storedEntries
        accessFile.setLength(newLength);
        var hashMap = HashMap.<K,V>newHashMap((int) storedEntries);
        for (long offset = 4; offset <= previousLength; offset += maxSizeOfEntry) { // Offset should start from Index 4 to avoid overriding storedEntries
            accessFile.seek(offset);
            accessFile.writeByte(0);
            accessFile.writeLong(0);
            K key = keyCodec.read(accessFile);
            V value = valueCodec.read(accessFile);
            hashMap.put(key,value);
        }
        for (long offset = previousLength; offset <= newLength; offset += maxSizeOfEntry) { // Make sure that all the new flag indexes are 0
            accessFile.seek(offset);
            accessFile.write(0); // 0 All possible flags
            accessFile.writeLong(0); // 0 All possible nextOffsets
        }
        storedEntries = 0;
        for (Map.Entry<K, V> entry : hashMap.entrySet()) {
            put(entry.getKey(),entry.getValue());
        }
    }


    private long offset(K key) {
        long has = Math.abs(key.hashCode()) % maxEntries;
        return has * maxSizeOfEntry + STORED_ENTRIES_SIZE; // + 4 to exclude index 0-3 (storedEntries)
    }

    private long findFirstEmptyOffset() throws IOException {
        for (int offset = 4; offset < accessFile.length(); offset += maxSizeOfEntry) {
            accessFile.seek(offset);
            if (accessFile.readByte() != EXISTS_FLAG) return offset;
        }
        return 0;
    }

    private void writeEntry(K key, V value, long offset) throws IOException {
        accessFile.seek(offset); // ignore nextOffset
        accessFile.writeByte(EXISTS_FLAG);
        accessFile.seek(offset + FLAG_SIZE + NEXT_OFFSET_SIZE); // Skip the nextOffset
        keyCodec.write(accessFile, key);
        valueCodec.write(accessFile, value);
    }


    public int maxEntries() {
        return maxEntries;
    }




}
