import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class NonBlockingHashMap<K, V> {
    AtomicReference<HashMap<K, V>> arMap;

    public NonBlockingHashMap() {
        this.arMap = new AtomicReference<>(new HashMap<>());
    }

    public void put(K key, V value) {
        update(map -> {
            var updated = new HashMap<>(map);
            updated.put(key, value);
            return updated;
        });
    }

    private void update(Function<HashMap<K, V>, HashMap<K, V>> f) {
        HashMap<K, V> oldMap;
        HashMap<K, V> newMap;
        do {
            oldMap = arMap.get();
            newMap = f.apply(oldMap);
        } while (!arMap.compareAndSet(oldMap, newMap));
    }

    public V get(K key) {
        var modifiedMap = new HashMap<>(arMap.get());
        return modifiedMap.get(key);
    }

    public void remove(K key) {
        update(map -> {
            var updated = new HashMap<>(map);
            updated.remove(key);
            return updated;
        });
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }


}