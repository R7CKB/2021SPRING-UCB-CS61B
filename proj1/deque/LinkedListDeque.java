package deque;
/*
  @author R7CKB
 */

import java.util.Iterator;

/**
 * A deque implementation using a doubly linked list.
 *
 * @param <T> the type of elements stored in the deque.
 */
public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    private class Node {
        private T item;
        private Node prev;
        private Node next;

        /**
         * Construct a node with the given item, previous node, and next node.
         *
         * @param item the item to store in the node.
         * @param prev the previous node in the deque.
         * @param next the next node in the deque.
         */
        Node(T item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private Node sentinel;

    private int size;

    /**
     * Construct an empty linked list deque (only with sentinel node).
     */
    public LinkedListDeque() {
        Node loyalSentinel = new Node(null, null, null);
        loyalSentinel.prev = loyalSentinel;
        loyalSentinel.next = loyalSentinel;
        this.sentinel = loyalSentinel;
        size = 0;
    }

    /**
     * Add an item to the front of the deque.
     *
     * @param item the item to add to the front of the deque.
     */
    @Override
    public void addFirst(T item) {
        sentinel.next = new Node(item, sentinel, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
    }

    /**
     * Add an item to the end of the deque.
     *
     * @param item the item to add to the end of the deque.
     */
    @Override
    public void addLast(T item) {
        sentinel.prev = new Node(item, sentinel.prev, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size += 1;
    }

    /**
     * Return the number of elements in the deque.
     *
     * @return the number of elements in the deque.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Print the contents of the deque.
     */
    @Override
    public void printDeque() {
        Node current = sentinel.next;
        while (current != sentinel) {
            System.out.print(current.item + " ");
            current = current.next;
        }
        System.out.println();
    }

    /**
     * Remove and return the item at the front of the deque.
     *
     * @return the item at the front of the deque.
     */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node first = sentinel.next;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return first.item;
    }

    /**
     * Remove and return the item at the end of the deque.
     *
     * @return the item at the end of the deque.
     */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node last = sentinel.prev;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
        return last.item;
    }

    /**
     * Return the item at the specified index (0 is front, size-1 is the end).
     *
     * @param index the index of the item to return.
     * @return the item at the specified index.
     */
    @Override
    public T get(int index) {
        if (isEmpty() || index < 0 || index >= size) {
            return null;
        }
        Node current = sentinel.next;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.item;
    }

    /**
     * The recursive helper method to return the item.
     *
     * @param current the current node to start from.
     * @param index   the index of the item to return.
     * @return the item at the specified index.
     */
    private T helperGetRecursive(Node current, int index) {
        if (index == 0) {
            return current.item;
        }
        return helperGetRecursive(current.next, index - 1);
    }

    /**
     * Return the item at the specified index (0 is front, size-1 is the end) using recursion.
     *
     * @param index the index of the item to return.
     * @return the item at the specified index.
     */
    public T getRecursive(int index) {
        if (index < 0 || index >= this.size()) {
            return null;
        } else if (index == 0) {
            return sentinel.next.item;
        }
        return helperGetRecursive(sentinel.next.next, index - 1);
    }

    /**
     * Override the equals method to compare two deques.
     *
     * @param o the object to compare.
     * @return true if the two deques are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Deque) {
            Deque<T> other = (Deque<T>) o;
            if (size != other.size()) {
                return false;
            }
            // get method may be not equal, so we need to compare each element.
            for (int i = 0; i < size; i += 1) {
                if (!(get(i).equals(other.get(i)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * A private class to implement the iterator for the deque.
     */
    private class LinkedListDequeIterator implements Iterator<T> {
        private Node current;

        LinkedListDequeIterator() {
            current = sentinel.next;
        }

        @Override
        public boolean hasNext() {
            return current != sentinel;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            T item = current.item;
            current = current.next;
            return item;
        }
    }

    /**
     * Return an iterator over the elements in the deque.
     *
     * @return an iterator over the elements in the deque.
     */
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

}
