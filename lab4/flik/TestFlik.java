package flik;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestFlik {
    @Test
    public void testFlik() {
        assertTrue(Flik.isSameNumber(1,1));
        assertTrue(Flik.isSameNumber(2,2));
        assertTrue(Flik.isSameNumber(3,3));
        assertTrue(Flik.isSameNumber(4,4));
        assertTrue(Flik.isSameNumber(5,5));
        assertTrue(Flik.isSameNumber(500,500));
        assertTrue(Flik.isSameNumber(7,7));
        assertTrue(Flik.isSameNumber(8,8));

    }
}
