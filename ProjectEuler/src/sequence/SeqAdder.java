package sequence;

public class SeqAdder implements SeqConsumer<Integer> {
    private Integer $acc$; // just testing stupid '$' char usage in variable name
    private Integer last_result;

    public void Process(SeqGen<Integer> gen) {
        Integer val = gen.next();
        if (val != null) {
            $acc$ = new Integer(0);
        }
        while (val != null) {
            $acc$ += val;
            val = gen.next();
        }
        last_result = $acc$;
    }

    public Integer GetResult() { return last_result; }
}
