
package com.nomagicsoftware.functional;

import static com.nomagicsoftware.functional.Transducers.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.IntStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author thurston
 */
public class TransducerTest
{
    
    @Test
    public void testCall()
    {
    }

   

    @Test
    public void basicCompose()
    {
        Transducer<Integer, Integer> smallSquares = Transducers.map((Integer i) -> i * i).compose(Transducers.filter(i -> i < 100));
        int count  = smallSquares.transduce(() -> IntStream.range(0, 20).iterator(), counter(), 0);
        assertEquals("", 10, count);
    }

    @Test
    public void mapWordLength()
    {
        Transducer<String, Integer> map = Transducers.map((String s) -> s.length());
        assertEquals("", 6, (int) map.transduce(Arrays.asList("one", "two"), Integer::sum, 0));
    }

    @Test
    public void filterEven()
    {
        Transducer<Integer, Integer> filter = Transducers.filter(TransducerTest::isEven);
        int sumEven  = filter.transduce(Arrays.asList(1, 3, 22, 77, 8), Integer::sum, 0);
        assertEquals("", 22 + 8, sumEven);
        
    }

    
    
    
    
    @Test
    public void flattenArray()
    {
        List<String> lines = new ArrayList<>();
        lines.add("Well, What do we have here?");
        Transducer<String, String> trans = Transducers.flatMapA(line -> line.split("\\W+"));
        trans = trans.compose(Transducers.filter(s -> ! s.isEmpty() && Character.isUpperCase(s.charAt(0))));
        assertEquals("", 2, (int) trans.transduce(lines, counter(), 0));
    }
    
    @Test
    public void flattenIterator()
    {
        List<String> lines = new ArrayList<>();
        lines.add("Well, What do we have here?");
        Transducer<String, String> trans = Transducers.flatMapI(line -> TestUtils.asIterator(line.split("\\W+")));
        int wordCount = trans.transduce(lines, counter(), 0);
        assertEquals("", 6, wordCount);
        
    }
    
    @Test
    public void flatMap()
    {
        List<String> lines = new ArrayList<>();
        lines.add("Well, What do we have here?");
        lines.add("Well, What do we have here?");
        Transducer<String, String> trans = Transducers.flatMap(line -> (Iterable<String>) () -> TestUtils.asIterator(line.split("\\W+")));
        int wordCount = trans.transduce(lines, counter(), 0);
        assertEquals("", 12, wordCount);
    }
   
    @Test
    public void flattenOverload()
    {
        final String splitNonWords = "\\W+";
        Transducer<String, String> trans = Transducers.flatMap(line -> (Iterable<String>) () -> TestUtils.asIterator(line.split("\\W+")));
        int wordCount = trans.transduce(mantra(), counter(), 0);
        assertEquals("", 24, wordCount);
        
        trans = Transducers.flatMapA(line -> line.split(splitNonWords));
        wordCount = trans.transduce(mantra(), counter(), 0);
        assertEquals("", 24, wordCount);
        
        trans = Transducers.flatMapI(line -> TestUtils.asIterator(line.split(splitNonWords)));
        wordCount = trans.transduce(mantra(), counter(), 0);
        assertEquals("", 24, wordCount);
    }
    
    @Test
    public void emptyWords()
    {
        final String splitNonWords = "\\W+";
        Transducer<String, String> trans = Transducers.flatMap(line -> (Iterable<String>) () -> TestUtils.asIterator(line.split(splitNonWords)));
        int wordCount = trans.transduce(Collections.emptyList(), counter(), 0);
        assertEquals("", 0, wordCount);
        
        trans = Transducers.flatMapA(line -> line.split(splitNonWords));
        wordCount = trans.transduce(Collections.emptyList(), counter(), 0);
        assertEquals("", 0, wordCount);
        
        trans = Transducers.flatMapI(line -> TestUtils.asIterator(line.split(splitNonWords)));
        wordCount = trans.transduce(Collections.emptyList(), counter(), 0);
        assertEquals("", 0, wordCount);
        
        
    }
    
    @Test
    public void reducerSanity()
    {
        assertEquals("", 1, (int) counter().reduce(0, ""));
        
        assertEquals("", 2, (int) counter().reduce(Arrays.asList(1, 78), 0));
    }
    
    @Test
    public void gatheringSanity()
    {
        Collection<? super String> copy = Transducers.<String>gather().reduce(mantra(), new ArrayList<>());
        assertEquals("", copy, mantra());
    }
    static List<String> mantra()
    {
        List<String> mantra = new ArrayList<>();
        mantra.add("Types should be immutable by default");
        mantra.add("Distributed systems are just concurrent systems writ large");
        mantra.add("If there are no tests -> then it does not work");
        
        return mantra;
    }
    
    
    static boolean isEven(int i)
    {
        return (i & -2) == i;
    }
    static Iterable<String> iterator(String[] s)
    {
        return () -> Spliterators.iterator(Arrays.spliterator(s));
    }
    
   
    
}
