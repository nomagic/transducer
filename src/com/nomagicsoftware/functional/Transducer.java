package com.nomagicsoftware.functional;

import java.util.function.BiFunction;


/**
 * A {@code Transducer} represents a transformation of some sequence that is reduced into a single
 * output<br>
 * <p>
 * Formally, a {@code Transducer} is just a higher-order function that takes a {@link Reducer} as input,
 * and returns another, <em>transformed</em> {@code Reducer}
 * </p>
 * Say you want to convert a List&lt;String&gt; to List&lt;int&gt;, e.g. by computing the length of each string
 * Then a "mapping transducer" should be of type &lt;String, int&gt;
 * <p>
 * Note:  although formally, "reduction" can take a [A, ? super T -> B extends A], transducers 
 * restrict reducers to [A, ? super T -> A]
 * Think about filtering transducers - they can not produce reducers that somehow convert A's into
 * B's - because they may never invoke the provided reducer arg!!!
 * In practice, this not really much of a restriction, and can essentially callers can work around it
 * by providing a B identity value, and then casting the returned value to B (or casting an A, T -> B
 * to an B, T -> B)
 * </p>
 * <h4>Example usage</h4>
 * <p>
 * Let's implement word count<br>
 * Say you have a {@literal List<String> lines}, where each item in the {@literal lines}, represents
 * a . . . line of text; and you want to count the total # of words
 * </p>
 * 
 * <pre>
 * {@code 
 *     List<String> lines = . . .
 *     int words = flatMapA(line -> line.split("\\W+")).transduce(lines, counter(), 0);
 * }
 * </pre>
 * <p>
 * Simple enough.  But the real power of transducers is in <em>composing</em> them; say you want
 * to get a list of words that are capitalized
 * </p>
 * <pre>
 * {@code 
 *     List<String> lines = . . .
 *     List<String> importantWords = flatMapA(line -> line.split("\\W+")).compose(filter(::capitalized)).
 *                                            transduce(lines, gather(), new ArrayList<String>());}
 * </pre>
 * @author thurston
 * @param <T> the type of the input collection elements
 * @param <R> the type of the 'transformed' inputs <br>
 * Note: it isn't unusual that T and R are the same
 */
@FunctionalInterface
public interface Transducer<T, R> 
{
    /**
     * Formally, a transducer is just a higher-order function that takes a 'reducer'
     * and returns another, transformed 'reducer'
     * @param <V> the type of the 'accumulator' (first) argument of <dd>reducer</dd>
     * @param reducer a source reducer that is "transformed" by *this*
     * @return a new, 'enhanced' reducer
     */
    <V> Reducer<V, T> call(BiFunction<V, ? super R, V> reducer);
    
    /**
     * Perform a reduction on {@code input}, first applying the chain of transformations
     * represented by *this*, and finally applying {@code reducer} <br>
     * <h4>Implementation Note:</h4>
     * {@code transduce} is not <em>short-circuiting</em>, all transformations will be applied
     * to each of {@code input's} items
     * @param <V> the return type of the <em>reduction</em>
     * @param input the items to be reduced 
     * @param reducer a reducer which is applied last
     * @param initial the initial value of the reduction (sometimes referred to as identity)
     * @return the reduced value
     */
    default <V> V transduce(Iterable<? extends T> input, BiFunction<V, ? super R, V> reducer, V initial)
    {
        Reducer<V, T> transReducer = call(reducer);
        for (T element : input)
            initial = transReducer.apply(initial, element);
        return initial;
    }
    
     /**
     * The real power of {@code Transducers} is when composing them
     * @param <O> the output type of <dd>next</dd>
     * @param next a transducer to compose with this
     * @return a composed transducer that accepts elements of T and produces elements of O
     */
    default <O> Transducer<T, O> compose(final Transducer<? super R, ? extends O> next)
    {
        return new Transducer<T, O>()
        {
            @Override
            public <V> Reducer<V, T> call(BiFunction<V, ? super O, V> reducer)
            {
                return Transducer.this.call(next.call(reducer));
            }
        };
    }
    
    
    /**
     * A {@link BiFunction} whose return type is the same as its first argument
     * @param <A> the type of the initial value, and the return type
     * @param <B> the type of the collection item
     */
    @FunctionalInterface
    interface Reducer<A, B> extends BiFunction<A, B, A>
    {

        /**
         * Just an alias for {@link BiFunction#apply(Object, Object) 
         * @param acc the default or {@code identity} value
         * @param element an input element
         * @return the result of applying *this*
         */
        default A reduce(A acc, B element)
        {
            return apply(acc, element);
        }
        
        /**
         * Reduces {@code input} by applying *this* to each of its elements
         * @param input a sequence of &lt;B&gt;s
         * @param initial the default or {@code identity} value
         * @return the result of applying *this*
         */
        default A reduce(Iterable<? extends B> input, A initial)
        {
            for (B element : input)
                initial = apply(initial, element);
            return initial;
        }
    }
}
