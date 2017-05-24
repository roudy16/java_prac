import sequence.LongPrimeGen;

import java.util.ArrayList;

/**
 * Problem 3 from https://projecteuler.net/problem=3
 */
public class Problem3 {
    public static ArrayList<Long> GetPrimeFactorizationOf(long num) {
        if (num <= 0L) {
            return null;
        }

        ArrayList<Long> factors = new ArrayList<>(16);
        LongPrimeGen primegen = new LongPrimeGen();
        Long max_factor = new Long((long)Math.sqrt((double)num) + 1L);

        boolean factors_found = false;
        Long prime = primegen.next();
        while (prime != null && prime < max_factor && num > 1) {
            while (num > 1 && num % prime.longValue() == 0) {
                factors_found = true;
                num = num / prime.longValue();
                factors.add(prime);
            }

            prime = primegen.next();
        }

        if (!factors_found) {
            factors.add(new Long(num));
        }

        return factors;
    }

    public static void main (String[] args) {

        long query_val = 600851475143L;
        ArrayList<Long> factors = GetPrimeFactorizationOf(query_val);

        Long largest = factors.get(factors.size() - 1);

        System.out.println(largest);
    }
}
