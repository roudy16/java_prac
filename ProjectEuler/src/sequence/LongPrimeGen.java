package sequence;

import java.util.ArrayList;
import java.util.Arrays;

public class LongPrimeGen implements SeqGen<Long> {
    private static ArrayList<Long> primes;
    static {
        primes = new ArrayList(Arrays.asList(2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L, 23L, 29L, 31L));
    }

    public static boolean isPrime(long q) {
        boolean is_prime = false;

        synchronized (primes) {
            Long temp = primes.get(primes.size() - 1);
            if (q > temp.longValue()) {
                // linear search as we grow primes ArrayList
                do {
                    temp = seek_next_prime();
                    if (temp == null) {
                        break;
                    }

                    if (q == temp.longValue()) {
                        is_prime = true;
                        break;
                    }
                } while (q > temp.longValue());
            }
            else if (q == temp.longValue()) {
                is_prime = true;
            }
            else {
                // binary search the existing primes ArrayList
                int left_bound = 0;
                int right_bound = primes.size() - 1;

                while (right_bound >= left_bound) {
                    int mid = left_bound + (right_bound - left_bound) / 2;
                    long prime = primes.get(mid).longValue();

                    if (q == prime) {
                        is_prime = true;
                        break;
                    }

                    if (q < prime) {
                        right_bound = mid - 1;
                    } else {
                        left_bound = mid + 1;
                    }
                }
            }
        } // end synchronized (primes)

        return is_prime;
    }

    private int primes_idx;


    public LongPrimeGen() {
        primes_idx = 0;
    }

    public Long next() {
        Long next_prime;

        synchronized (primes) {
            if (primes_idx >= primes.size()) {
                next_prime = seek_next_prime();
            } else {
                next_prime = primes.get(primes_idx);
            }
        }

        primes_idx++;
        return next_prime;
    }

    private static Long seek_next_prime() {
        int stride = 2;
        int sz = primes.size();
        long last_prime = primes.get(sz - 1).longValue();
        long query_val = last_prime + stride;

        while (query_val > 0) {
            boolean is_prime = true;
            for (Long prm : primes) {
                if (query_val % prm.longValue() == 0) {
                    is_prime = false;
                    break;
                }
            }

            if (is_prime) {
                Long new_prime = new Long(query_val);
                primes.add(new_prime);
                return new_prime;
            }

            query_val += stride;
        }

        return null;
    }
}
