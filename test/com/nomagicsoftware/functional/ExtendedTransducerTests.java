package com.nomagicsoftware.functional;

import java.util.Arrays;
import java.util.Iterator;
import static org.junit.Assert.*;
import static com.nomagicsoftware.functional.Transducers.*;
import java.util.Collections;
import org.junit.Test;
/**
 *
 * @author thurston
 */
public class ExtendedTransducerTests 
{
    @Test
    public void take()
    {
        /*
        short-circuiting, so in Transducer
         */
        int sumFirst3 = Transducers.<Integer>noop().takeTransduce(Arrays.asList(1, 7, 5, 10, 11), 
                                                                  Integer::sum, 0, 3);
        assertEquals("", 13, sumFirst3);
        
        //let's make sure of the short-circuiting, indirectly because must be fast
        
        int small = Transducers.<Integer>noop().takeTransduce(new IntRange(100_000_000), Integer::sum, 
                                                              0, 10); 
        
        assertEquals("", 45, small);
        
    }
    
    @Test
    public void takeR()
    {
        Transducer<String, Integer> length = Transducers.<String, String>flatMapA(line -> line.split("\\W+")).
                                                                                  compose(map(String::length));
        int sumFirst11Words = length.takeTransduceR(TransducerTest.mantra(), Integer::sum, 0, 11);
        assertEquals("", 66, sumFirst11Words);
        
        assertEquals("", 0, (int) length.takeTransduceR(TransducerTest.mantra(), Integer::sum, 0, 0));
        
        assertEquals("", 0, (int) length.takeTransduceR(Collections.emptyList(), Integer::sum, 0, 1_000));
        
        
    }
    
    @Test
    public void drop()
    {
        Transducer<Integer, Integer> ints = Transducers.<Integer> drop(4);
        assertEquals("", 6, (int) ints.transduce(new IntRange(10), counter(), 0));
        
        ints = Transducers.<Integer> drop(40);
        assertEquals("", 0, (int) ints.transduce(new IntRange(10), counter(), 0));
    }
}


class IntRange implements Iterable<Integer>
{
    final int start, end;

    IntRange(int start, int end)
    {
        this.start = start;
        this.end = end;
    }

    IntRange(int end)
    {
        this(0, end);
    }
    
    
    
    @Override
    public Iterator<Integer> iterator()
    {
        return new Iterator<Integer>()
        {
            int cursor = IntRange.this.start;
            @Override
            public boolean hasNext()
            {
                return this.cursor < IntRange.this.end;
            }

            @Override
            public Integer next()
            {
                return this.cursor++;
            }
        };
    }
    
}