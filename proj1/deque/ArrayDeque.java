package deque;
/**
 * @author R7CKB
 * @date 2024/3/14
 */

/**
 * An array-based implementation of a deque.
 *
 * @param <T> the type of elements held in this deque.
 */
public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    /**
     * Initializes an empty deque.
     */
    public ArrayDeque() {
        int initialSize = 8;
        items = (T[]) new Object[initialSize];
        nextFirst = initialSize - 1;
        nextLast = 0;
        size = 0;
    }

    /**
     * Resizes the array to the given capacity.
     *
     * @param capacity the new capacity of the array.
     */
    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        int newNextFirst = capacity - (size - nextFirst);
        int startIndex = newNextFirst + 1;
        System.arraycopy(items, 0, newItems, 0, nextLast); // copy the last items
        System.arraycopy(items, nextLast, newItems, startIndex, items.length - nextLast);
        nextFirst = newNextFirst;
        items = newItems;
    }

    /**
     * Adds an item to the front of the deque.
     *
     * @param t the item to add.
     */
    public void addFirst(T t) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextFirst] = t;
        size += 1;
        nextFirst -= 1;
    }

    /**
     * Adds an item to the end of the deque.
     *
     * @param t the item to add.
     */
    public void addLast(T t) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextLast] = t;
        size += 1;
        nextLast += 1;
    }

    /**
     * Removes and returns the item at the front of the deque.
     *
     * @return the item at the front of the deque, or null if the deque is empty.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of items in the deque.
     *
     * @return the number of items in the deque.
     */
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque.
     */
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
        double utilization = 0.25;
        while (size < items.length * utilization) {
            resize(items.length / 2);
        }
    }

    /**
     * Removes and returns the item at the front of the deque.
     *
     * @return the item at the front of the deque, or null if the deque is empty.
     */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (items.length >= 16) {
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
    public T removeLast() {
        if (isEmpty()) {
            return null;
        } else if (items.length >= 16) {
            checkUtilization();
        }
        nextLast = (nextLast - 1 + items.length) % items.length;
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
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        } else if (nextFirst == items.length - 1) {
            return items[index];
        } else {
            return items[(nextFirst + index + 1) % items.length];
        }
    }

//    public boolean equals(Object o) {
//        if (o instanceof ArrayDeque){
//
//        }
//    }


    //    public Iterator<T> iterator() {
//        return null;
//    }
}
