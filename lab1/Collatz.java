/**
 * Class that prints the Collatz sequence starting from a given number.
 *
 * @author R7CKB
 */
public class Collatz {

    /**
     * if n is even return n/2
     * else return 3*n+1
     * if n is equal to one
     * return 1
     */
    public static int nextNumber(int n) {
        if (n == 1) {
            return 1;
        } else if (n % 2 == 0) {
            return n / 2;
        } else {
            return n * 3 + 1;
        }
    }

    public static void main(String[] args) {
        int n = 5;
        System.out.print(n + " ");
        while (n != 1) {
            n = nextNumber(n);
            System.out.print(n + " ");
        }
    }
}

