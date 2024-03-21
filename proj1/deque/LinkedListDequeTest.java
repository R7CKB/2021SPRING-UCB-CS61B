package deque;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Performs some basic linked list tests.
 */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        ArrayDeque<String> lld1 = new ArrayDeque<>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();

    }

    @Test
    public void testAddFirstAndRemoveFirst() {
        LinkedListDeque<String> list = new LinkedListDeque<>();
        list.addFirst("first");
        assertEquals(1, list.size());
        assertEquals("first", list.removeFirst());
        assertTrue(list.isEmpty());
    }

    @Test
    public void testAddLastAndRemoveLast() {
        LinkedListDeque<String> list = new LinkedListDeque<>();
        list.addLast("last");
        assertEquals(1, list.size());
        assertEquals("last", list.removeLast());
        assertTrue(list.isEmpty());
    }

    @Test
    public void testIsEmptyAndSize() {
        LinkedListDeque<String> list = new LinkedListDeque<>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        list.addFirst("item1");
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    public void testGetAndRemove() {
        LinkedListDeque<String> deque = new LinkedListDeque<>();
        deque.addLast("item1");
        deque.addLast("item2");
        deque.addLast("item3");
        assertEquals("item2", deque.get(1));
        assertEquals("item1", deque.removeFirst());
        assertEquals("item3", deque.removeLast());
        assertEquals(1, deque.size());
        assertEquals("item2", deque.removeFirst());
        assertEquals(0, deque.size());
    }

    @Test
    /** Tests the getRecursive() method. */
    public void testGetRecursive() {
        LinkedListDeque<String> deque = new LinkedListDeque<>();
        deque.addLast("item1");
        deque.addLast("item2");
        deque.addLast("item3");
        assertEquals("item2", deque.getRecursive(1));
    }

    @Test
    /** Tests the equality and inequality operators. */
    public void testEqual() {
        LinkedListDeque<String> deque1 = new LinkedListDeque<>();
        deque1.addLast("item1");
        deque1.addLast("item2");
        deque1.addLast("item3");

        LinkedListDeque<String> deque2 = new LinkedListDeque<>();
        deque2.addLast("item1");
        deque2.addLast("item2");
        deque2.addLast("item3");

        LinkedListDeque<String> deque3 = new LinkedListDeque<>();
        deque3.addLast("item1");
        deque3.addLast("item2");
        deque3.addLast("item4");

        assertEquals(deque1, deque2);
        assertNotEquals(deque1, deque3);
    }

    @Test
    /** Tests the iterator. */
    public void testIterator() {
        LinkedListDeque<String> deque = new LinkedListDeque<>();
        deque.addLast("item1");
        deque.addLast("item2");
        deque.addLast("item3");

        int count = 0;
        for (String item : deque) {
            count++;
            System.out.println(item);
        }
        assertEquals(3, count);
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {


        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {


        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {


        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double> lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {


        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());


    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {


        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }


    }
}
