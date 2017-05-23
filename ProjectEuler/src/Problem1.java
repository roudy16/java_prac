/**
 * Problem 1 from https://projecteuler.net/problem=1
 */
public class Problem1 {
    static int SumMultiplesOf(int num0, int num1) {
        int sum = 0;
        for (int i = 1; i < 1000; i++) {
            if (i % num0 == 0 || i % num1 == 0) {
               sum += i;
            }
        }
        return sum;
    }

    public static void main (String[] args) {
        System.out.println(SumMultiplesOf(3, 5));
    }
}
