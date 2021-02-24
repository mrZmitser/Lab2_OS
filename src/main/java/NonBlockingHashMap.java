import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class NonBlockingHashMap<K, V> extends HashMap<K, V> {
    private AtomicReferenceArray<Node<K, V>> hashTable;
    private final int DEFAULT_SIZE = 8192;
    private int size;

    private void initHashTable(int size) {
        assert (size > 0);
        this.size = size;
        hashTable = new AtomicReferenceArray<>(this.size);
    }

    public NonBlockingHashMap(int size) {
        initHashTable(size);
    }

    public NonBlockingHashMap() {
        initHashTable(DEFAULT_SIZE);
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public V get(Object key) {
        var node = getNode(key);
        return node == null ? null : node.getValue();
    }

    private Node<K, V> getNode(Object key) {
        if (key == null)
            throw new NullPointerException();
        int hash = Math.abs((key.hashCode() % size));
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
    public V put(K key, V value) {
        if (key == null || value == null)
            throw new NullPointerException();
        int hash = Math.abs((key.hashCode() % size));
        if (hashTable.compareAndSet(hash, null, new Node<>(key, value))) {
            return null;
        }

        var firstNode = hashTable.get(hash);
        synchronized (firstNode) {
            var curNode = getNode(key);
            if (curNode != null) {
                return curNode.setValue(value);
            }
            Node<K, V> prevNode = null;
            for (curNode = firstNode; curNode != null; curNode = curNode.next){
                prevNode = curNode;
            }
            prevNode.next = new Node<>(key, value);
            return null;
        }
    }

    @Override
    public V remove(Object key) {
        if (key == null)
            throw new NullPointerException();
        int hash = Math.abs((key.hashCode() % size));
        var firstNode
                = hashTable.get(hash);
        V oldValue = null;
        synchronized (firstNode) {
            var curNode = firstNode;
            Node<K, V> prevNode = null;
            while (curNode != null) {
                if (key.equals(curNode.key)) {
                    oldValue = curNode.getValue();
                    if (prevNode != null) {
                        if (curNode.next != null)
                            prevNode.next = curNode.next;
                        else
                            prevNode.next = null;
                    } else {
                        hashTable.set(hash, curNode.next);
                    }
                    break;
                }
                prevNode = curNode;
                curNode = curNode.next;
            }
        }
        return oldValue;
    }

    static class Node<K, V> implements Map.Entry<K, V> {
        final K key;
        AtomicReference<V> value = new AtomicReference<>(null);
        volatile Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value.set(value);
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value.get();
        }

        @Override
        public V setValue(V value) {
            return this.value.getAndSet(value);
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