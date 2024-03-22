package deque;
/**
 * @author R7CKB
 * @date 2024/3/14
 */

import java.util.Iterator;

/**
 * An array-based implementation of a deque.
 *
 * @param <T> the type of elements held in this deque.
 */
public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private static final int DEFAULT_CAPACITY = 8;
    private static final double DEFAULT_UTILIZATION = 0.25;
    private static final int MIN_USAGE_LENGTH = 16;

    /**
     * Initializes an empty deque.
     */
    public ArrayDeque() {
        items = (T[]) new Object[DEFAULT_CAPACITY];
        nextFirst = DEFAULT_CAPACITY - 1;
        nextLast = 0;
        size = 0;
    }

    /**
     * Resizes the array to the given capacity.
     *
     * @param capacity the new capacity of the array.
     * @source <a href="https://github.com/ZonePG/CS61B/blob/main/proj1/deque/ArrayDeque.java">...</a>
     */
    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        int first = (nextFirst + 1) % items.length;
        int last = (nextLast + items.length - 1) % items.length;
        // exchange the nextFirst and nextLast, first equals to nextLast, last equals to nextFirst
        if (first < last) { // only addLast/removeLast
            System.arraycopy(items, first, newItems, 0, size);
        } else { // addFirst/removeFirst
            System.arraycopy(items, first, newItems, 0, items.length - first);
            System.arraycopy(items, 0, newItems, items.length - first, last + 1);
        }
        items = newItems;
        nextFirst = items.length - 1;
        nextLast = size;
    }

    /**
     * Adds an item to the front of the deque.
     *
     * @param t the item to add.
     */
    @Override
    public void addFirst(T t) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextFirst] = t;
        size += 1;
        nextFirst = (nextFirst + items.length - 1) % items.length;
    }

    /**
     * Adds an item to the end of the deque.
     *
     * @param t the item to add.
     */
    @Override
    public void addLast(T t) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextLast] = t;
        size += 1;
        nextLast = (nextLast + 1) % items.length;
    }


    /**
     * Returns the number of items in the deque.
     *
     * @return the number of items in the deque.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque.
     */
    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            T item = get(i);
            System.out.print(item + " ");
        }
        System.out.println();
    }

    /**
     * Checks if the utilization of the array is less than 25% and resizes the array if necessary.
     */
    private void checkUtilization() {
        double utilization = (double) size / items.length;
        if (utilization < DEFAULT_UTILIZATION) {
            resize(items.length / 2);
        }
    }

    /**
     * Removes and returns the item at the front of the deque.
     *
     * @return the item at the front of the deque, or null if the deque is empty.
     */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (items.length >= MIN_USAGE_LENGTH) {
            checkUtilization();
        }
        nextFirst = (nextFirst + 1) % items.length;
        T first = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;
        return first;
    }

    /**
     * Removes and returns the item at the end of the deque.
     *
     * @return the item at the end of the deque, or null if the deque is empty.
     */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (items.length >= MIN_USAGE_LENGTH) {
            checkUtilization();
        }
        nextLast = (nextLast + items.length - 1) % items.length;
        T last = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        return last;
    }

    /**
     * Returns the item at the given index in the deque.
     *
     * @param index the index of the item to return.
     * @return the item at the given index, or null if the index is out of range.
     */
    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        } else {
            return items[(nextFirst + index + 1) % items.length];
        }
    }

    /**
     * Override the equals method to compare two deques.
     *
     * @param o the other deque to compare with.
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
            for (int i = 0; i < size; i += 1) {
                if (!((get(i).equals(other.get(i))))) {
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
    private class ArrayDequeIterator implements Iterator<T> {
        private int index = 0;


        ArrayDequeIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public T next() {
            if (index >= size) {
                throw new IndexOutOfBoundsException();
            }
            T item = get(index);
            index += 1;
            return item;
        }
    }

    /**
     * Returns an iterator over the elements in this deque in a proper sequence.
     *
     * @return an iterator over the elements in this deque in a proper sequence.
     */
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
}
