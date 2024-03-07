package IntList;

import static org.junit.Assert.*;

import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }

    /**
     * new test for Part C, it only squares the first prime number.
     */
    @Test
    public void testSquarePrimesComplex() {
        IntList lst = IntList.of(17, 13, 6, 20, 27);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("289 -> 169 -> 6 -> 20 -> 27", lst.toString());
        assertTrue(changed);
    }

    /**
     * if all numbers are prime numbers.
     */
    @Test
    public void testSquarePrimeCOmplex2() {
        IntList lst = IntList.of(2, 3, 5, 7, 11);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 9 -> 25 -> 49 -> 121", lst.toString());
        assertTrue(changed);
    }

    /**
     * if all numbers are non-prime numbers.
     */
    @Test
    public void testSquarePrimeComplex3() {
        IntList lst = IntList.of(4, 6, 8, 9, 10);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 6 -> 8 -> 9 -> 10", lst.toString());
        assertFalse(changed);
    }

    /**
     * random test
     */
    @Test
    public void testSquarePrimeComplex4() {
        IntList lst = IntList.of(9, 4, 6, 8, 11);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("9 -> 4 -> 6 -> 8 -> 121", lst.toString());
        assertTrue(changed);
    }

    // so the problem is that the square method only squares the first prime number.
    // change the square method to fix this bug.
}
