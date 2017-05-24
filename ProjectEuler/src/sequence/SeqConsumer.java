package sequence;

public interface SeqConsumer <T extends Number> {
    void Process(SeqGen<T> gen);
    T GetResult();
}
