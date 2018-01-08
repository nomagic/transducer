package com.nomagicsoftware.functional;

import java.util.Iterator;

/**
 *
 * @author thurston
 */
public final class TestUtils 
{

    private TestUtils()
    {
    }
    
    static <T> Iterator<T> asIterator(final T[] array)
    {
        return new Iterator<T>()
        {
            int cursor;
            @Override
            public boolean hasNext()
            {
                return this.cursor < array.length;
            }

            @Override
            public T next()
            {
                return array[this.cursor++];
            }
        };
    }
    
    static <T> Iterable<T> selfIteration(Iterator<T> iterator)
    {
        return () -> iterator;
    }
}
