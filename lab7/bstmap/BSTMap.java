package bstmap;
/*
 * @author R7CKB
 */

import java.util.*;

/**
 * A simple implementation of a binary search tree-based map.
 *
 * @param <K> the type of keys in this map
 * @param <V> the type of values in this map
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class BSTNode {
        K key;
        V value;
        BSTNode left, right;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            left = null;
            right = null;
        }

        /**
         * Finds the node with the given key in the tree rooted at T.
         *
         * @param T   the root of the tree
         * @param key the key to search for
         * @return the node with the given key, or null if not found
         */
        public BSTNode find(BSTNode T, K key) {
            if (T == null) return null;
            if (key.equals(T.key)) return T;
            else if (key.compareTo(T.key) < 0) return find(T.left, key);
            else return find(T.right, key);
        }

        /**
         * Inserts a new node into the tree with the given key and value.
         * If the key already exists, the value is updated.
         *
         * @param T     the root of the tree
         * @param key   the key of the new node
         * @param value the value of the new node
         * @return the root of the updated tree
         */
        public BSTNode insert(BSTNode T, K key, V value) {
            if (T == null) return new BSTNode(key, value);
            if (key.compareTo(T.key) < 0) T.left = insert(T.left, key, value);
            else if (key.compareTo(T.key) > 0) T.right = insert(T.right, key, value);
            return T;
        }
    }

    private BSTNode root;
    private int size;

    /**
     * Removes all the mappings from this map.
     */
    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     */
    @Override
    public boolean containsKey(K key) {
        if (root == null) return false; // this isn't necessary
        return root.find(root, key) != null;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        if (root == null) return null;
        BSTNode T = root.find(root, key);
        if (T == null) {
            return null;
        }
        return T.value;
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Helper method to insert a new key-value pair into the tree rooted at T.
     */
    private void put(K key, V value, BSTNode T) {
        size += 1;
        BSTNode Node = T.insert(root, key, value);
    }

    /**
     * Associates the specified value with the specified key in this map.
     */
    @Override
    public void put(K key, V value) {
        if (root != null) {
            BSTNode T = root.find(root, key);
            if (T == null) {
                put(key, value, root);
            } else {
                T.value = value;
            }
        } else {
            root = new BSTNode(key, value);
            size += 1;
        }
    }

    /**
     * Helper method to print out the BSTMap in order of increasing Key
     * use mutual recursion to traverse the left subtree and then the right subtree
     */
    private void printInOrder(BSTNode T) {
        if (T != null) {
            printInOrder(T.left);
            System.out.println(T.key);
            printInOrder(T.right);
        }
    }

    /**
     * prints out the BSTMap in order of increasing Key
     * use recursion to traverse the left subtree and then the right subtree
     */
    public void printInOrder() {
        printInOrder(root);
    }

    /**
     * Helper function to help KeySet, as the same as printInOrder().
     */
    private Set<K> keySet(BSTNode T, Set<K> set) {
        if (T != null) {
            keySet(T.left, set);
            set.add(T.key);
            keySet(T.right, set);
        }
        return set;
    }

    /**
     * Return a Set view of the keys contained in this map.
     *
     * @return a Set view of the keys contained in this map.
     */
    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        return keySet(root, set);
    }

    /**
     * The other remove method which takes a key and a value as input.
     *
     * @param key the key to search for
     * @return the value associated with the key, or null if the key isn't found
     */
    @Override
    public V remove(K key, V value) {
        remove(key);
        return value;
    }

    /**
     * Removes the mapping for a key from this map if it's present.
     *
     * @param key key for which mapping should be removed
     * @return the previous value associated with key, or null if there was no mapping for key.
     */
    @Override
    public V remove(K key) {
        BSTNode T = root.find(root, key);
        if (key == null || !containsKey(key)) return null;
        root = removeHelper(root, key);
        size -= 1;
        return T.value;
    }

    /**
     * Helper function to remove a key-value pair from the BSTMap.
     * If the key isn't found, return null.
     *
     * @param T   the root of the BSTMap
     * @param key the key to be removed
     * @return the value associated with the key, or null if the key isn't found
     * @source <a href="https://inst.eecs.berkeley.edu//~cs61b/fa14/book2/data-structures.pdf">...</a>
     */
    private BSTNode removeHelper(BSTNode T, K key) {
        if (T == null) return null;
        if (key.compareTo(T.key) < 0) {
            T.left = removeHelper(T.left, key);
        } else if (key.compareTo(T.key) > 0) {
            T.right = removeHelper(T.right, key);
            // otherwise, we've found the key
        } else if (T.left == null) {
            return T.right;

        } else if (T.right == null) {
            return T.left;
        } else {
            // with two children, swap with smallest in right subtree
            T.right = swapSmallest(T.right, T);
        }
        return T;
    }

    private BSTNode swapSmallest(BSTNode T, BSTNode R) {
        // replace with the successor
        if (T.left == null) {
            R.key = T.key;
            R.value = T.value;
            return T.right;
        } else {
            T.left = swapSmallest(T.left, R);
            return T;
        }
    }


    /**
     * An iterator that iterates over the keys of the dictionary.
     * The iterator starts at the first key in the dictionary and continues until all keys have been visited.
     *
     * @source <a href="https://github.com/turing0/CS61B/blob/master/lab7/bstmap/BSTMap.java">...</a>
     */
    private class BSTMapSetIterator implements Iterator<K> {
        private final List<BSTNode> list;
        private BSTNode current;

        public BSTMapSetIterator() {
            list = new ArrayList<>();
            current = root;
            fillList(current);
        }

        private void fillList(BSTNode T) {
            if (T == null) return;
            fillList(T.left);
            list.add(T);
            fillList(T.right);
        }

        @Override
        public boolean hasNext() {
            return !list.isEmpty();
        }

        public K next() {
            if (!hasNext()) throw new NoSuchElementException("No more elements");
            current = list.remove(0);
            return current.key;
        }
    }

    /**
     * Returns an iterator over the keys in the map.
     * The iterator starts at the first key in the map and continues until all keys have been visited.
     *
     * @return an iterator over the keys in the map.>
     */
    @Override
    public Iterator<K> iterator() {
        return new BSTMapSetIterator();
    }
}
