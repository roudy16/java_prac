package sequence;

import java.util.ArrayList;

/**
 * Stores elements of a sequence so we can access them again later
 */
public class RandAccessSeq<T extends Number> {
    private static final int default_reserve_size = 32;

    private SeqGen<T>    m_gen;
    private ArrayList<T> m_nums;
    private int          m_max_index;

    public RandAccessSeq(SeqGen<T> gen) {
        m_gen = gen;
        m_nums = new ArrayList<>(default_reserve_size);
        m_max_index = Integer.MAX_VALUE;
    }

    public T get(int index) {
        if (index < m_nums.size()) {
            return m_nums.get(index);
        }

        if (index > m_max_index) {
            return null;
        }

        // Determine how much to grow the sequence storage
        T temp = null;
        int grow_amount = index - m_nums.size() + 1;
        for(int i = grow_amount; i > 0; i--) {
            temp = m_gen.next();

            if (temp == null) {
                m_max_index = m_nums.size() - 1;
                break;
            }

            m_nums.add(temp);
        }

        return temp;
    }
}
