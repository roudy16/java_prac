/**
 * Problem 2 from https://projecteuler.net/problem=2
 */

import java.util.function.IntPredicate;

import sequence.SeqAdder;
import sequence.SeqGen;
import sequence.IntSeqFilter;
import sequence.FibGen;

/* The goal is to find the sum of all even Fibinacci numbers that don't exceed
 * 4000000. I want to process the sequence as it is generated to save some space.
 * The processing for this problem is filtering the sequence and doing an operation
 * using the numbers that pass the filter as input.
 */
public class Problem2 {
    private static IntPredicate isEven = (val) -> val % 2 == 0;

    public static void main (String[] args) {
        // A generator for Fibinacci numbers
        SeqGen Fibs = new FibGen(4000000);

        // A generator for only even Fibinacci numbers
        SeqGen EvenFibs = new IntSeqFilter(Fibs, isEven);

        // A sequence consumer that sums the numbers in a sequence
        SeqAdder adder = new SeqAdder();

        // Adds the numbers in the Even Fibinacci sequence until a number greater than
        // 4000000 is seen in the sequence
        adder.Process(EvenFibs);
        Integer evenFibSum = adder.GetResult();

        System.out.println(evenFibSum);
    }
}
