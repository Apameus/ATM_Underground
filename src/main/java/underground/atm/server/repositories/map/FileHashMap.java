package underground.atm.server.repositories.map;

import java.io.IOException;

public interface FileHashMap<K,V> {
    void put(K key, V value) throws IOException;
    V get(K key) throws IOException;
    void remove(K key) throws IOException;
}
