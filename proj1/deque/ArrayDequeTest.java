package deque;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class ArrayDequeTest {

    private static int MAX_SIZE = 100;
    private static int TEMP_SIZE = 80;
    private static int END_SIZE = 20;

    @Test
    /**
     * Test the addFirst() methods of ArrayDeque.
     */
    public void testAddFirst() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        assertTrue("should be empty", deque.isEmpty());
        deque.addFirst("1");
        deque.addFirst("2");
        deque.addFirst("3");
        deque.addFirst("4");
        deque.addFirst("5");
        deque.addFirst("6");
        deque.addFirst("7");
        deque.addFirst("8");
        deque.addFirst("9");
        System.out.println("Printing out deque: ");
        deque.printDeque();
    }

    @Test
    /**
     * Test the addLast() methods of ArrayDeque.
     */
    public void testAddLast() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        assertTrue("should be empty", deque.isEmpty());
        deque.addLast("1");
        deque.addLast("2");
        deque.addLast("3");
        deque.addLast("4");
        deque.addLast("5");
        deque.addLast("6");
        deque.addLast("7");
        deque.addLast("8");
        deque.addLast("9");
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
        deque.addFirst("1");
        deque.addLast("2");
        deque.addFirst("3");
        deque.addLast("4");
        deque.addFirst("5");
        deque.addLast("6");
        deque.addFirst("7");
        deque.addLast("8");
        deque.addFirst("9");
        assertEquals("should have size:9", 9, deque.size());
        assertEquals("should get element:8", "8", deque.get(8));
        deque.printDeque();
        ArrayDeque<String> newDeque = new ArrayDeque<>();
        newDeque.addLast("1");
        newDeque.addFirst("2");
        newDeque.addLast("3");
        newDeque.addFirst("4");
        newDeque.addLast("5");
        newDeque.addFirst("6");
        newDeque.addLast("7");
        newDeque.addFirst("8");
        newDeque.addLast("9");
        assertEquals("should have size:9", 9, newDeque.size());
        assertEquals("should get element:9", "9", newDeque.get(8));
        newDeque.printDeque();
    }

    @Test
    /**
     * Test the resize() methods of ArrayDeque.
     */
    public void testResize() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        for (int i = 0; i < MAX_SIZE; i++) {
            deque.addLast(String.valueOf(i));
        }
        for (int i = 0; i < TEMP_SIZE; i++) {
            deque.removeLast();
        }
        assertEquals("should have size:20", END_SIZE, deque.size());
        assertEquals("should get element:19", "19", deque.get(END_SIZE - 1));
        for (int i = 0; i < END_SIZE; i++) {
            deque.removeLast();
        }
        assertEquals("should have size:0", 0, deque.size());
        assertNull("should get element:null", deque.get(0));


        for (int i = 0; i < MAX_SIZE; i++) {
            deque.addFirst(String.valueOf(i));
        }
        for (int i = 0; i < TEMP_SIZE; i++) {
            deque.removeFirst();
        }
        assertEquals("should have size:20", END_SIZE, deque.size());
        assertEquals("should get element:0", "0", deque.get(END_SIZE - 1));
        for (int i = 0; i < END_SIZE; i++) {
            deque.removeFirst();
        }
        assertEquals("should have size:0", 0, deque.size());
        assertNull("should get element:null", deque.get(0));


        for (int i = 0; i < MAX_SIZE; i++) {
            deque.addLast(String.valueOf(i));
        }
        for (int i = 0; i < TEMP_SIZE; i++) {
            deque.removeFirst();
        }
        assertEquals("should have size:20", END_SIZE, deque.size());
        assertEquals("should get element:99", "99", deque.get(END_SIZE - 1));
        for (int i = 0; i < END_SIZE; i++) {
            deque.removeLast();
        }
        assertEquals("should have size:0", 0, deque.size());
        assertNull("should get element:null", deque.get(0));


        for (int i = 0; i < MAX_SIZE; i++) {
            deque.addFirst(String.valueOf(i));
        }
        for (int i = 0; i < TEMP_SIZE; i++) {
            deque.removeLast();
        }
        assertEquals("should have size:20", END_SIZE, deque.size());
        assertEquals("should get element:80", "80", deque.get(END_SIZE - 1));
        for (int i = 0; i < END_SIZE; i++) {
            deque.removeFirst();
        }
        assertEquals("should have size:0", 0, deque.size());
        assertNull("should get element:null", deque.get(0));
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

    @Test
    /**
     * Test the equals() methods of ArrayDeque.
     */
    public void testEquals() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.addLast("1");
        deque.addLast("2");
        deque.addLast("3");
        ArrayDeque<String> newDeque = new ArrayDeque<>();
        newDeque.addLast("1");
        newDeque.addLast("2");
        newDeque.addLast("3");
        assertEquals("should be equal", deque, newDeque);
        deque.addFirst("4");
        assertNotEquals("should not be equal", deque, newDeque);
    }

    @Test
    /**
     * Test the iterator() methods of ArrayDeque.
     */
    public void testIterator() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.addLast("1");
        deque.addLast("2");
        deque.addLast("3");
        Iterator<String> iter = deque.iterator();
        assertTrue("should have next", iter.hasNext());
        assertEquals("should get element:1", "1", iter.next());
        assertTrue("should have next", iter.hasNext());
        assertEquals("should get element:2", "2", iter.next());
        assertTrue("should have next", iter.hasNext());
        assertEquals("should get element:3", "3", iter.next());
        assertFalse("should not have next", iter.hasNext());
    }
}
