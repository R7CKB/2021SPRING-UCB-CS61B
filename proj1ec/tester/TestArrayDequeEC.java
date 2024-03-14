package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import java.util.Optional;

public class TestArrayDequeEC {
    @Test
    /**
     * Test the methods of the ArrayDequeSolution class and compare it to the StudentArrayDeque class.
     * @source tester/StudentArrayDequeLauncher.java
     */
    public void testMethods() {
        StudentArrayDeque<Integer> deque = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> solution = new ArrayDequeSolution<>();
        String message = "";

        for (int i = 0; i < 200; i++) {
            double numberBetweenZeroAndOne = StdRandom.uniform();
            if (numberBetweenZeroAndOne < 0.5) {
                deque.addFirst(i);
                solution.addFirst(i);
                message += "addFirst(" + i + ")\n";
            } else {
                deque.addLast(i);
                solution.addLast(i);
                message += "addLast(" + i + ")\n";
            }
        }
        Integer value;
        Integer expected;
        for (int i = 0; i < 200; i++) {
            double numberBetweenOneAndTwo = StdRandom.uniform(2);
            if (numberBetweenOneAndTwo < 1) {
                value = deque.removeFirst();
                expected = solution.removeFirst();
                message += "removeFirst()\n";
            } else {
                value = deque.removeLast();
                expected = solution.removeLast();
                message += "removeLast()\n";
            }
            assertEquals(message, value, expected);
        }


    }
}
