package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation.
 * Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author R7CKB
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
    // You should probably define some more.
    private static final int DEFAULT_CAPACITY = 16; // initial capacity of the backing array
    private static final double DEFAULT_LOAD_FACTOR = 0.75; // default load factor before rehashing
    private int capacity; // the size of the buckets
    private int size; // the number of items in the map
    private double loadFactor;  // the load factor before rehashing
    private Set<K> keySet;

    /**
     * Constructors
     */
    public MyHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        capacity = initialSize;
        loadFactor = maxLoad;
        size = 0;
        buckets = createTable(initialSize);
        // Initialize all buckets to empty collections
        for (int i = 0; i < capacity; i++) {
            buckets[i] = createBucket();
        }
        keySet = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    // @source https://inst.eecs.berkeley.edu//~cs61b/fa14/book2/data-structures.pdf(page 135 graph)

    /**
     * Removes all the mappings from this map.
     */
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            buckets[i] = createBucket();
        }
        size = 0;
        keySet.clear();
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     * @source <a href="https://algs4.cs.princeton.edu/34hash/SeparateChainingHashST.java.html">...</a>
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
     */
    public V get(K key) {
        int key_hash = key.hashCode();
        Collection<Node> bucket = buckets[Math.floorMod(key_hash, capacity)];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    public int size() {
        return size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value) {
        int key_hash = key.hashCode();
        Collection<Node> bucket = buckets[Math.floorMod(key_hash, capacity)];
        if (containsKey(key)) {
            for (Node node : bucket) {
                if (node.key.equals(key)) {
                    node.value = value;
                    return;
                }
            }
        } else if ((double) size() / capacity > loadFactor) {
            resize();
            put(key, value);
        } else {
            Node newNode = createNode(key, value);
            bucket.add(newNode);
            size += 1;
            keySet.add(key);
        }
    }

    /**
     * Helper method to resize the backing array and rehash all the items
     */
    private void resize() {
        capacity *= 2;
        Collection<Node>[] newBuckets = createTable(capacity);
        for (int i = 0; i < capacity; i++) {
            newBuckets[i] = createBucket();
        }
        for (int i = 0; i < capacity / 2; i++) {
            for (Node node : buckets[i]) {
                int key_hash = node.key.hashCode();
                Collection<Node> bucket = newBuckets[Math.floorMod(key_hash, capacity)];
                bucket.add(node);
            }
        }
        buckets = newBuckets;
    }


    /**
     * Returns a Set view of the keys contained in this map.
     *
     * @return a set view of the keys contained in this map
     */
    public Set<K> keySet() {
        return keySet;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no mapping for key.
     */
    public V remove(K key) {
        int key_hash = key.hashCode();
        Collection<Node> bucket = buckets[Math.floorMod(key_hash, capacity)];
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                bucket.remove(node);
                size -= 1;
                keySet.remove(key);
                return node.value;
            }
        }
        return null;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value.
     *
     * @param key   key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return true if the value was removed
     */
    public V remove(K key, V value) {
        remove(key);
        return value;
    }

    private class MyHashMapIterator implements Iterator<K> {
        private final List<Node> list;

        private MyHashMapIterator() {
            list = new ArrayList<>();
            for (int i = 0; i < capacity; i++) {
                Collection<Node> bucket = buckets[Math.floorMod(i, capacity)];
                list.addAll(bucket);
            }
        }

        @Override
        public boolean hasNext() {
            return !list.isEmpty();
        }

        @Override
        public K next() {
            Node node = list.remove(0);
            return node.key;
        }
    }

    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }
}
