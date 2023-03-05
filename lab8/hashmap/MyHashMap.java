package hashmap;

import org.checkerframework.checker.units.qual.C;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private double loadFactor = 0.75;
    private int initialSize = 16;
    private HashSet<K> KeyStorage = new HashSet<K>();
    private int Size = 0;

    /** Constructors */
    public MyHashMap() {
        this.buckets = new Collection[16];
    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        this.buckets = new Collection[initialSize];
    }

    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        this.buckets = new Collection[initialSize];
    }

    private Node createNode(K key, V value) {
        return new Node(key, value);
    }


    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    private void insertBucket(int size) {
        for (int x = 0; x < size; x += 1) {
            buckets[x] = createBucket();
        }
    }


    public void clear() {
        this.buckets = new Collection[16];
        this.Size = 0;
        this.KeyStorage = new HashSet<K>();
    }

    public boolean containsKey(K key) {
        return KeyStorage.contains(key);
    }

    public int size () {
        return this.Size;
    }

    public void put (K key, V value) {
        int Hcode = key.hashCode();
        int index = Math.floorMod(Hcode, initialSize);

        if (containsKey(key)) {
            for (Node n : buckets[index]) {
                if (n.key.equals(key)) {
                    n.value = value;
                    return;
                }
            }
        }
        else if (buckets[index] == null) {
            buckets[index] = createBucket();
        }

        buckets[index].add(createNode(key, value));
        this.KeyStorage.add(key);
        this.Size += 1;
        if (Size / initialSize > loadFactor) {
            resize();
        }
    }

    private void resize() {
        Collection<Node>[] copy = buckets;
        clear();
        initialSize = initialSize * 2;
        buckets = new Collection[initialSize];
        insertBucket(initialSize);
        for (Collection<Node> c : copy) {
            if (!(c == null) ) {
                for (Node e : c) {
                    this.put(e.key, e.value);
                }
            }
        }
    }

    public V get(K key) {
        if (!containsKey(key)) {
            return null;
        }
        else {
            int index = Math.floorMod(key.hashCode(), initialSize);
            for (Node n : buckets[index]) {
                if (n.key.equals(key)) {
                    return n.value;
                }
            }
        }
        return null;
    }

    public Set<K> keySet() {
        return this.KeyStorage;
    }

    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public V remove(K key) {
        if (!containsKey(key)) {
            return null;
        }
        int index = Math.floorMod(key.hashCode(), initialSize);
        for (Node e : buckets[index]) {
            if (e.key.equals(key)) {
                buckets[index].remove(e);
                KeyStorage.remove(key);
                Size -= 1;
                return e.value;
            }
        }
        return null;
    }

    public V remove(K key, V value) {
        if (!containsKey(key)) {
            return null;
        }
        int index = Math.floorMod(key.hashCode(), initialSize);
        Node copy = null;
        for (Node e : buckets[index]) {
            if (e.key.equals(key) && e.value.equals(value)) {
                copy = e;
                break;
            }
        }
        if (copy != null) {
            buckets[index].remove(copy);
            KeyStorage.remove(key);
            Size -= 1;
            return copy.value;
        }
        return null;
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return null;
    }

    private class MyHashMapIter implements Iterator<K> {
        private Iterator<K> iter;

        public MyHashMapIter() {
            iter = KeyStorage.iterator();
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public K next() {
            return iter.next();
        }
    }

}
