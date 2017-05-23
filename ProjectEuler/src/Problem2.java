/**
 * Problem 2 from https://projecteuler.net/problem=2
 */

import java.util.function.*;

/* The goal is to find the sum of all even Fibinacci numbers that don't exceed
 * 4000000. I want to process the sequence as it is generated to save some space.
 * The processing for this problem is filtering the sequence and doing an operation
 * using the numbers that pass the filter as input.
 */
public class Problem2 {
    public static IntPredicate isEven = (val) -> {return val % 2 == 0; };

    public interface SeqGen {
        public int next();
    }

    public interface SeqConsumer {
        public void Process(SeqGen gen, IntPredicate sentinal);
        public int GetResult();
    }

    public class SeqFilter implements SeqGen {
        private SeqGen m_gen;
        private IntPredicate m_filter;

        public SeqFilter(SeqGen gen, IntPredicate filter) {
            m_gen = gen;
            m_filter = filter;
        }

        public int next() {
            int retval = m_gen.next();
            while (!m_filter.test(retval)) {
                retval = m_gen.next();
            }
            return retval;
        }
    }

    public class SeqAdder implements SeqConsumer {
        private int sum = 0;
        public void Process(SeqGen gen, IntPredicate sentinal) {
            int val = gen.next();
            while (!sentinal.test(val)) {
                sum += val;
                val = gen.next();
            }
        }

        public int GetResult() { return sum; }
    }

    public class FibGen implements SeqGen{
        private int first = 0;
        private int second = 1;

        public FibGen() {
            first = 0;
            second = 1;
        }
        public int next() {
            int retval = first + second;
            first = second;
            second = retval;
            return retval;
        }
    }

    public static void main (String[] args) {
        // This instantiation of Problem2 is needed to use non static nested classes,
        // there really is no reason I can't declare the nested classes static
        Problem2 prob = new Problem2();

        // A generator for Fibinacci numbers
        SeqGen Fibs = prob.new FibGen();

        // A generator for only even Fibinacci numbers
        SeqGen EvenFibs = prob.new SeqFilter(Fibs, isEven);

        // A sequence consumer that sums the numbers in a sequence
        SeqConsumer adder = prob.new SeqAdder();

        // Adds the numbers in the Even Fibinacci sequence until a number greater than
        // 4000000 is seen in the sequence
        adder.Process(EvenFibs, (val) -> val > 4000000);

        System.out.println(adder.GetResult());
    }
}
