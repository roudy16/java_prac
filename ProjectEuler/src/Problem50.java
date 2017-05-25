import sequence.RandAccessSeq;
import sequence.LongPrimeGen;

/**
 * Problem 50 from https://projecteuler.net/problem=50
 */
public class Problem50 {
    private static final long limit = 1000000;

    /**
     * The goal is to find the a prime less than 1,000,000 that is the sum of the most
     * consecutive primes, e.g. 41 = 2 + 3 + 5 + 7 + 11 + 13. The approach is to first
     * calculate the longest(widest) sequence of consecutive prime numbers whose sum is
     * less than 1,000,000. This is found adding prime numbers in sequence from least to
     * greatest starting with the smallest prime (2) until a sum greater than 1,000,000
     * is reached. For each width we check the sums of all sequences with that
     * width whose value is less than 1,000,000. That is done by started with the lowest
     * value sequence first then shifting the bounds of the sequence to right by one
     * until no more sequences of the current width have a sum less than 1,000,000. Once
     * all sequences of a certain width are exhausted we reduce the width by one and
     * check all sequences.
     */
    public static void main (String[] args) {
        LongPrimeGen primegen = new LongPrimeGen();
        RandAccessSeq<Long> primes = new RandAccessSeq<Long>(primegen);

        long sum = 0;
        int left_bound = 0;
        int right_bound = 0;

        // Find longest sequence of consecutive primes that sum less than 1,000,000
        while (sum < limit) {
            sum += primes.get(right_bound++).longValue();
        }

        // We must back up the right_bound by one since we passed sum 1,000,000 in
        // the final iteration of the above loop.
        right_bound--;
        sum -= primes.get(right_bound).longValue();

        // This is the max possible width of consecutive primes, we'll reduce this by
        // one each iteration of the outer loop.
        int width = right_bound;
        boolean prime_sum_found = false;

        while (width > 0) {
            // Check all sums for sequences of consecutive primes with the current width 
            while (sum < limit) {
                if (LongPrimeGen.isPrime(sum)) {
                    prime_sum_found = true;
                    break;
                }

                // Shift sequence right by one
                sum -= primes.get(left_bound++).longValue();
                sum += primes.get(right_bound++).longValue();
            }

            if (prime_sum_found) {
                break;
            }

            // Reduce the width by one and reset the left/right bounds and sum. The logic
            // here reduces the arithmetic operations needed to reset the sum, we don't need
            // to do anything with values that will still be in the span of the bounds once
            // they are reset.
            width--;
            for (int i = Math.max(left_bound, width); i < right_bound; i++) {
                sum -= primes.get(i).longValue();
            }
            for (int i = Math.min(left_bound, width) - 1; i >= 0; i--) {
                sum += primes.get(i).longValue();
            }

            left_bound = 0;
            right_bound = width;
        }

        if (!prime_sum_found) {
            System.out.println("Logic error, should have found a prime sum");
            return;
        }

        System.out.println("Width: " + Integer.toString(width) + "   Prime: " + Long.toString(sum));
    }
}
