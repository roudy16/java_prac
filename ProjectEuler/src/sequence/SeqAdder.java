package sequence;

import java.util.function.IntPredicate;

public class SeqAdder implements SeqConsumer {
    private int $acc$; // just testing stupid '$' char usage in variable name
    private int last_result;
    public void Process(SeqGen gen, IntPredicate sentinel) {
        $acc$ = 0;
        Integer val = gen.next();
        while (val != null && !sentinel.test(val.intValue())) {
            $acc$ += val.intValue();
            val = gen.next();
        }
        last_result = $acc$;
    }

    public int GetResult() { return last_result; }
}
