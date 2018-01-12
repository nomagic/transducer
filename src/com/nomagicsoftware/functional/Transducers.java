package com.nomagicsoftware.functional;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Factory methods for common {@link Transducer transducers} and {@link Transducer.Reducer reducers}
 * @author thurston
 */
public final class Transducers 
{

    private Transducers()
    {
    }
    
    /**
     *
     * @param <In>
     * @param test the {@link Predicate predicate} to apply to each element of type In
     * @return
     */
    public static <In> Transducer<In, In> filter(final Predicate<? super In> test)
    {
        return new Transducer<In, In>()
        {
            @Override
            public <V> Reducer<V, In> call(BiFunction<V, ? super In, V> reducer)
            {
                return (acc, element) -> 
                {
                    if (test.test(element))
                        return reducer.apply(acc, element);
                    return acc;
                };
            }
        };
    }
    /**
     *
     * @param <In>
     * @param <Out>
     * @param mapper the function to apply to each element of type In
     * @return
     */
    public static <In, Out> Transducer<In, Out> map(final Function<? super In, ? extends Out> mapper)
    {
        return new Transducer<In, Out>()
        {
            @Override
            public <V> Reducer<V, In> call(BiFunction<V, ? super Out, V> reducer)
            {
                return (acc, element) -> reducer.apply(acc, mapper.apply(element));
            }
        };
    }

   
    
    /**
     * 
     * @param <T>
     * @param <R>
     * @param decompose
     * @return 
     */
    public static <T, R> Transducer<T, R> flatMap(final Function<? super T, Iterable<? extends R>> decompose)
    {
        return Transducers.<T, R>flatMapI(in -> decompose.apply(in).iterator());
    }    

    /**
     * Given a function {@code decompose}, produces a Transducer that can apply a {@link Reducer} <br>
     * to the "stream" of &lt;R&gt;'s decomposed from any # of &lt;T&gt's
     * @param <T> the type to "flatten"
     * @param <R> the decomposed type
     * @param decompose a {@link Function function} that decomposes a &lt;T&gt; into 0 or more &lt;R&gt;'s
     * @return
     */
    public static <T, R> Transducer<T, R> flatMapI(final Function<? super T, Iterator<? extends R>> decompose)
    {
        return new Transducer<T, R>()
        {
            @Override
            public <V> Reducer<V, T> call(final BiFunction<V, ? super R, V> reducer)
            {
                return (V acc, T element) ->
                {
                    for (Iterator<? extends R> iOverR = decompose.apply(element); iOverR.hasNext();)
                        acc = reducer.apply(acc, iOverR.next());
                    return acc;
                };
            }
        };
    }
    
    /**
     *
     * @param <T>
     * @param <R>
     * @param decompose
     * @return
     */
    public static <T, R> Transducer<T, R> flatMapA(final Function<? super T, ? extends R[]> decompose)
    {
        return new Transducer<T, R>()
        {
            @Override
            public <V> Reducer<V, T> call(final BiFunction<V, ? super R, V> reducer)
            {
                return (V accumulator, T group) ->
                {
                    for (R atom : decompose.apply(group))
                        accumulator = reducer.apply(accumulator, atom);
                    return accumulator;
                };
            }
        };
    }
    
    public static <T> Transducer<T, T> noop()
    {
        return new Transducer<T, T>()
        {
            @Override
            public <V> Reducer<V, T> call(final BiFunction<V, ? super T, V> reducer)
            {
                return (acc, element) -> reducer.apply(acc, element);
            }
        };
    }
    
}
