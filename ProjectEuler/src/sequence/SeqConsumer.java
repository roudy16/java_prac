package sequence;

import java.util.function.IntPredicate;

public interface SeqConsumer {
    public void Process(SeqGen gen, IntPredicate sentinel);
    public int GetResult();
}
