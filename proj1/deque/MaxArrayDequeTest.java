package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;


public class MaxArrayDequeTest {

    @Test
    public void testMax() {
        Comparator<Integer> comparator = Comparator.naturalOrder();
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(comparator);
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        int max = deque.max();
        assertEquals("should be 3", 3, max);
        deque.addLast(4);
        deque.addLast(5);
        deque.addLast(6);
        max = deque.max();
        assertEquals("should be 6", 6, max);
        MaxArrayDeque<Integer> deque2 = new MaxArrayDeque<>(comparator);
        assertNull("should be null", deque2.max());
    }

    @Test
    public void testMaxHappyPath() {
        Comparator<Integer> comparator = Comparator.naturalOrder();
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(comparator);
        deque.addLast(5);
        deque.addLast(9);
        deque.addLast(3);
        int max = deque.max();
        assertEquals("should be 9", 9, max);
    }

    @Test
    public void testMaxEmptyDeque() {
        Comparator<Integer> comparator = Comparator.naturalOrder();
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(comparator);
        assertNull(deque.max());
    }

    @Test
    public void testMaxWithCustomComparator() {
        Comparator<Integer> reverseOrder = Comparator.reverseOrder();
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(reverseOrder);
        deque.addLast(5);
        deque.addLast(9);
        deque.addLast(3);
        int max = deque.max();
        assertEquals("should be 3", 3, max);
    }

    @Test
    public void testMaxWithNullComparator() {
        Comparator<String> naturalOrder = Comparator.naturalOrder();
        MaxArrayDeque<String> deque = new MaxArrayDeque<>(naturalOrder);
        deque.addLast("abc");
        deque.addLast("def");
        String max = deque.max();
        assertEquals("def", max);
    }
}
