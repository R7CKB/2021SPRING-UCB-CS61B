package bstmap;
/*
 * @author R7CKB
 */

import java.util.Iterator;
import java.util.Set;

/**
 * A simple implementation of a binary search tree-based map.
 *
 * @param <K> the type of keys in this map
 * @param <V> the type of values in this map
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private static class BSTNode<K extends Comparable<K>, V> {
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
        public BSTNode<K, V> find(BSTNode<K, V> T, K key) {
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
        public BSTNode<K, V> insert(BSTNode<K, V> T, K key, V value) {
            if (T == null) return new BSTNode<K, V>(key, value);
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
        BSTNode<K, V> T = root.find(root, key);
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
    private void put(K key, V value, BSTNode<K, V> T) {
        size += 1;
        BSTNode<K, V> Node = T.insert(root, key, value);
    }

    /**
     * Associates the specified value with the specified key in this map.
     */
    @Override
    public void put(K key, V value) {
        if (root != null) {
            BSTNode<K, V> T = root.find(root, key);
            if (T == null) {
                put(key, value, root);
            } else {
                T.value = value;
            }
        } else {
            root = new BSTNode<K, V>(key, value);
            size += 1;
        }
    }

    /**
     * Helper method to print out the BSTMap in order of increasing Key
     * use mutual recursion to traverse the left subtree and then the right subtree
     */
    private void printInOrder(BSTNode<K, V> T) {
        BSTNode left = T.left;
        BSTNode right = T.right;
        while (left != null) {
            printInOrder(left);
        }
        System.out.println(T.key);
        while (right != null) {
            printInOrder(right);
        }
    }

    /**
     * prints out the BSTMap in order of increasing Key
     * use recursion to traverse the left subtree and then the right subtree
     */
    public void printInOrder() {
        printInOrder(root);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("keySet() is not supported in BSTMap");
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("remove() is not supported in BSTMap");
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("remove() is not supported in BSTMap");
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("iterator() is not supported in BSTMap");
    }

}
