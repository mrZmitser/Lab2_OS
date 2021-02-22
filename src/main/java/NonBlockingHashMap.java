import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class NonBlockingHashMap<K, V> extends AbstractMap<K, V> {
    //Внимание!!! volatile-массив заменён на AtomicReferenceArray.
    //Причина: volatile-массив не предоставляет синхронизацию
    //для своих полей
    private final AtomicReferenceArray<Node<K, V>> hashTable;
    private final int DEFAULT_SIZE = 32;
    private final int size;

    public NonBlockingHashMap(int size) {
        this.size = size > 0 ? size : DEFAULT_SIZE;
        hashTable = new AtomicReferenceArray<>(this.size);
    }

    public NonBlockingHashMap() {
        this(-1); //to init with the DEFAULT_SIZE values
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public V get(Object key) {
        var node = getNode(key);
        return node == null ? null : node.value;
    }

    private Node<K, V> getNode(Object key) {
        if (key == null)
            throw new NullPointerException();
        int hash = key.hashCode() % size;
        var node = hashTable.get(hash);
        while (node != null) {
            if (key.equals(node.key)) {
                return node;
            }
            node = node.next;
        }
        return null;

    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V put(K key, V value) {
        if (key == null || value == null)
            throw new NullPointerException();
        var node = getNode(key);
        var hash = key.hashCode() % size;
        if (node == null) {
            node = new Node<>(key, value, hashTable.get(hash));
            hashTable.set(hash, node);
            return null;
        } else {
            var oldValue = node.getValue();
            node.value = value;
            return oldValue;
        }
    }

    @Override
    public V remove(Object key) {
        //TODO: realize
        return null;
    }

    static class Node<K, V> implements Map.Entry<K, V> {
        final K key;
        volatile V value;
        volatile Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public Node(K key, V value, Node<K, V> next) {
            this(key, value);
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<K, V> node = (Node<K, V>) o;
            return Objects.equals(key, node.key) && Objects.equals(value, node.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }
}

