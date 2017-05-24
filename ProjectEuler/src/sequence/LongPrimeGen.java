package sequence;

import java.util.ArrayList;
import java.util.Arrays;

public class LongPrimeGen implements SeqGen<Long> {
    private static ArrayList<Long> primes;
    static {
        primes = new ArrayList(Arrays.asList(2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L, 23L, 29L, 31L));
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
