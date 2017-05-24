package sequence;

public class FibGen implements SeqGen{
    private int first = 0;
    private int second = 1;

    public FibGen() {
        first = 0;
        second = 1;
    }
    public Integer next() {
        Integer retval = Integer.valueOf(first + second);
        first = second;
        second = retval.intValue();
        return retval;
    }
}

