package deque;
/**
 * @author R7CKB
 * @date 2024/3/14
 */

/**
 * An array-based implementation of a deque.
 *
 * @param <Item> the type of elements held in this deque.
 */
public class ArrayDeque<Item> {
    private Item[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    /**
     * Initializes an empty deque.
     */
    public ArrayDeque() {
        int initialSize = 8;
        items = (Item[]) new Object[initialSize];
        nextFirst = initialSize - 1;
        nextLast = 0;
        size = 0;
    }

    /**
     * Resizes the array to the given capacity.
     *
     * @param capacity the new capacity of the array.
     */
    public void resize(int capacity) {
        Item[] newItems = (Item[]) new Object[capacity];
        int newNextFirst = capacity - 1;
        int newNextLast = size;
        System.arraycopy(items, 0, newItems, 0, size);
        nextFirst = newNextFirst;
        nextLast = newNextLast;
        items = newItems;
    }

    /**
     * Adds an item to the front of the deque.
     *
     * @param item the item to add.
     */
    public void addFirst(Item item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextFirst] = item;
        size += 1;
        nextFirst -= 1;
    }

    /**
     * Adds an item to the end of the deque.
     *
     * @param item the item to add.
     */
    public void addLast(Item item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextLast] = item;
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
        int startIndex = nextFirst + 1;
        for (int i = startIndex; i < items.length; i++) {
            System.out.print(items[i] + " ");
        }
        for (int i = 0; i < nextLast; i++) {
            System.out.print(items[i] + " ");
        }
        System.out.println();
    }

    /**
     * Checks if the utilization of the array is less than 25% and resizes the array if necessary.
     */
    public void checkUtilization() {
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
    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        } else if (items.length >= 16) {
            checkUtilization();
        }
        int firstIndex = nextFirst == (items.length - 1) ? 0 : nextFirst + 1;
        Item first = items[firstIndex];
        items[firstIndex] = null;
        if (firstIndex == 0) {
            nextLast -= 1;
        } else {
            nextFirst += 1;
        }
        size -= 1;
        return first;
    }

    /**
     * Removes and returns the item at the end of the deque.
     *
     * @return the item at the end of the deque, or null if the deque is empty.
     */
    public Item removeLast() {
        if (isEmpty()) {
            return null;
        } else if (items.length >= 16) {
            checkUtilization();
        }
        int lastIndex = nextLast == 0 ? (items.length - 1) : (nextLast - 1);
        Item last = items[lastIndex];
        items[lastIndex] = null;
        if (lastIndex == items.length - 1) {
            nextFirst += 1;
        } else {
            nextLast -= 1;
        }
        size -= 1;
        return last;
    }

    /**
     * Returns the item at the given index in the deque.
     *
     * @param index the index of the item to return.
     * @return the item at the given index, or null if the index is out of range.
     */
    public Item get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[index];
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
