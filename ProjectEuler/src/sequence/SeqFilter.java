package sequence;

import java.util.function.IntPredicate;

public class SeqFilter implements SeqGen {
    private SeqGen m_gen;
    private IntPredicate m_filter;
    private Integer next_val = null;

    public SeqFilter(SeqGen gen, IntPredicate filter) {
        m_gen = gen;
        m_filter = filter;
    }

    public Integer next() {
        seek_next();
        return next_val;
    }

    private boolean seek_next() {
        next_val = m_gen.next();
        while (next_val != null) {
            // we found a next value for the filtered sequence
            if (m_filter.test(next_val.intValue())) {
                return true;
            }

            next_val = m_gen.next();
        }

        // no next value was found
        return false;
    }
}

