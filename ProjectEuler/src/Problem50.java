import sequence.RandAccessSeq;
import sequence.LongPrimeGen;

/**
 * Created by Dell on 5/24/2017.
 */
public class Problem50 {
    private static final long limit = 1000000;

    public static void main (String[] args) {
        LongPrimeGen primegen = new LongPrimeGen();
        RandAccessSeq primes = new RandAccessSeq(primegen);

        long sum = 0;
        int left_bound = 0;
        int right_bound = 0;

        while (sum < limit) {
            sum += primes.get(right_bound++).longValue();
        }

        right_bound--;
        sum -= primes.get(right_bound).longValue();

        int width = right_bound;
        boolean prime_sum_found = false;

        while (width > 0) {
            while (sum < limit) {
                if (LongPrimeGen.isPrime(sum)) {
                    prime_sum_found = true;
                    break;
                }
                
                sum -= primes.get(left_bound++).longValue();
                sum += primes.get(right_bound++).longValue();
            }

            if (prime_sum_found) {
                break;
            }

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
