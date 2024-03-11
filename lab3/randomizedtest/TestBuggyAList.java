package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        BuggyAList<Integer> buggyAList = new BuggyAList<>();
        AList<Integer> aList = new AList<>();
        buggyAList.addLast(4);
        aList.addLast(4);
        buggyAList.addLast(5);
        aList.addLast(5);
        buggyAList.addLast(6);
        aList.addLast(6);
        assertEquals(buggyAList.size(), aList.size());
        assertEquals(buggyAList.removeLast(), aList.removeLast());
        assertEquals(buggyAList.removeLast(), aList.removeLast());
        assertEquals(buggyAList.removeLast(), aList.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                buggyAList.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                assertEquals(size, buggyAList.size());
            } else if (operationNumber == 2 && L.size() != 0) {
                int value = L.getLast();
                int buggyValue = buggyAList.getLast();
                assertEquals(value, buggyValue);
                int removeValue = L.removeLast();
                int buggyRemoveValue = buggyAList.removeLast();
                assertEquals(removeValue, buggyRemoveValue);
            }
        }
    }
}
