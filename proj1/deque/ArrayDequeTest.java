package deque;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    /**
     * Test the addFirst() methods of ArrayDeque.
     */
    public void testAddFirst() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        assertTrue("should be empty", deque.isEmpty());
        deque.addFirst("1");
        assertEquals("should have size:1", 1, deque.size());
        deque.addFirst("2");
        assertEquals("should have size:2", 2, deque.size());
        deque.addFirst("3");
        assertEquals("should have size:3", 3, deque.size());
        System.out.println("Printing out deque: ");
        deque.printDeque();
    }

    @Test
    /**
     * Test the addLast() methods of ArrayDeque.
     */
    public void testAddLast() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.addLast("1");
        assertEquals("should have size:1", 1, deque.size());
        deque.addLast("2");
        assertEquals("should have size:2", 2, deque.size());
        deque.addLast("3");
        assertEquals("should have size:3", 3, deque.size());
        System.out.println("Printing out deque: ");
        deque.printDeque();
    }

    @Test
    /**
     * Test the removeFirst() and removeLast() methods of ArrayDeque.
     */
    public void testRemoveFirst() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.addFirst("1");
        assertEquals("should remove first element:1", "1", deque.removeLast());
        deque.addFirst("2");
        deque.addFirst("3");
        assertEquals("should remove first element:2", "2", deque.removeLast());
        assertEquals("should have size:1", 1, deque.size());
        deque.addFirst("4");
        deque.addLast("5");
        System.out.println("Printing out deque: ");
        deque.printDeque();

        ArrayDeque<String> newDeque = new ArrayDeque<>();
        newDeque.addFirst("1");
        assertEquals("should remove first element:1", "1", newDeque.removeFirst());
        newDeque.addFirst("2");
        newDeque.addFirst("3");
        assertEquals("should remove first element:2", "3", newDeque.removeFirst());
        assertEquals("should have size:1", 1, newDeque.size());
        newDeque.addLast("4");
        newDeque.addFirst("5");
        System.out.println("Printing out new deque: ");
        newDeque.printDeque();
    }


    @Test
    /**
     * Test the isEmpty() methods of ArrayDeque.
     */
    public void testIsEmpty() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        assertTrue("should be empty", deque.isEmpty());
        deque.addLast("1");
        assertFalse("should not be empty", deque.isEmpty());
    }

    @Test
    /**
     * Test the size() and resize() methods of ArrayDeque.
     */
    public void testSize() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        assertEquals("should have size:0", 0, deque.size());
        deque.addLast("1");
        assertEquals("should have size:1", 1, deque.size());
        deque.addLast("2");
        assertEquals("should have size:2", 2, deque.size());
        deque.addLast("3");
        assertEquals("should have size:3", 3, deque.size());
        deque.addLast("4");
        assertEquals("should have size:4", 4, deque.size());
        deque.addLast("5");
        assertEquals("should have size:5", 5, deque.size());
        deque.addLast("6");
        assertEquals("should have size:6", 6, deque.size());
        deque.addLast("7");
        assertEquals("should have size:7", 7, deque.size());
        deque.addLast("8");
        assertEquals("should have size:8", 8, deque.size());
        deque.addLast("9");
        assertEquals("should have size:9", 9, deque.size());
        assertEquals("should get element:9", "9", deque.get(8));
        deque.printDeque();
        for (int i = 0; i < 1000; i++) {
            deque.addLast(String.valueOf(i));
        }
        assertEquals("should have size:1009", 1009, deque.size());
    }

    @Test
    /**
     * Test the get() methods of ArrayDeque.
     */
    public void testGet() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.addLast("1");
        deque.addLast("2");
        assertEquals("should get element:1", "1", deque.get(0));
        deque.addFirst("3");
        deque.addFirst("4");
        assertEquals("should get element:3", "3", deque.get(1));
        assertEquals("should get element:1", "1", deque.get(2));
        deque.printDeque();

        ArrayDeque<String> newDeque = new ArrayDeque<>();
        newDeque.addFirst("1");
        newDeque.addFirst("2");
        assertEquals("should get element:2", "2", newDeque.get(0));
        newDeque.addLast("3");
        newDeque.addLast("4");
        assertEquals("should get element:1", "1", newDeque.get(1));
        assertEquals("should get element:3", "3", newDeque.get(2));
        newDeque.printDeque();
    }
}
