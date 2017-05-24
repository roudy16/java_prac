package sequence;

public class FibGen implements SeqGen<Integer> {
    private int first;
    private int second;
    private int limit;
    private boolean done;

    public FibGen() {
        first = 0;
        second = 1;
        limit = Integer.MAX_VALUE;
        done = false;
    }

    public FibGen(int _limit) {
        this();
        limit = _limit;
    }

    public Integer next() {
        if (done) {
            return null;
        }

        Integer retval = Integer.valueOf(first + second);

        // conditions for end of sequence
        if (retval.intValue() < second || retval.intValue() > limit) {
            done = true;
            return null;
        }

        first = second;
        second = retval.intValue();
        return retval;
    }
}

