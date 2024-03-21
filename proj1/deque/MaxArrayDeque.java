package deque;

import java.util.Comparator;

/**
 * A deque implementation using an array to store the elements.
 * The maximum item in the deque can be accessed using the max() method.
 *
 * @param <T> the type of elements in the deque.
 */
public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> comparator;

    /**
     * Creates a new empty deque with a given comparator.
     */
    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.comparator = c;
    }

    /**
     * Returns the maximum item in the deque.
     *
     * @return the maximum item in the deque, or null if the deque is empty.
     */
    public T max() {
        return max(comparator);
    }

    /**
     * Returns the maximum item in the deque using the default comparator.
     *
     * @param c the comparator to use for comparing items.
     * @return the maximum item in the deque, or null if the deque is empty.
     */
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maxItem = get(0);
        for (int i = 1; i < size(); i += 1) {
            T item = get(i);
            if (c.compare(item, maxItem) > 0) {
                maxItem = item;
            }
        }
        return maxItem;
    }

}
