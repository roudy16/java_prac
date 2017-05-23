/**
 * Created by Dell on 5/22/2017.
 */

import java.util.function.*;
public class LambaTest {
    interface UnaryStringOp {
        void op(String str);
    }

    static public void main(String[] args) {
        Runnable func0 = () -> {System.out.println("Lambda0 as Runnable"); };
        UnaryStringOp func1 = (String word) -> { System.out.println(word); };
        Consumer<String> func2 = (String word) -> { System.out.println(word); };

        System.out.println("Funcs declared");

        func0.run();
        func1.op("Lambda1 as UnaryStringOp");
        func2.accept("Lambda2 as Consumer");
    }
}
