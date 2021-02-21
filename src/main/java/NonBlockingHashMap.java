import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NonBlockingHashMap<K, V> extends AbstractMap<K, V> {
    private Node<K, V>[] hashTable;
    private final int DEFAULT_SIZE = 32;
    private int size;
    //TODO: make some fields volatile

    public NonBlockingHashMap(int size) {
        this.size = size > 0 ? size : DEFAULT_SIZE;
        hashTable = (Node<K, V>[]) new Node<?, ?>[size];
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
        int hash = key.hashCode() % size;
        var node = hashTable[hash];
        while (node != null) {
            if (key.equals(node.key)) {
                return node.value;
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
        //TODO: realize
        return null;
    }


    static class Node<K, V> implements Map.Entry<K, V> {
        K key;
        V value;
        Node<K, V> next;
        //TODO: make some fields volatile

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

