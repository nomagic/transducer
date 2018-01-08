
package com.nomagicsoftware.functional;

import com.nomagicsoftware.functional.Transducer.Reducer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
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
    public void testCompose()
    {
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
    static List<String> mantra()
    {
        List<String> mantra = new ArrayList<>();
        mantra.add("Types should be immutable by default");
        mantra.add("Distributed systems are just concurrent systems writ large");
        mantra.add("If there are no tests -> then it does not work");
        
        return mantra;
    }
    static <T> Reducer<Integer, T> counter()
    {
        return (total, _ignore) -> ++total;
    }
    
    static boolean isEven(int i)
    {
        return (i & -2) == i;
    }
    static Iterable<String> iterator(String[] s)
    {
        return () -> Spliterators.iterator(Arrays.spliterator(s));
    }
    
    static void lines()
    {
        String[][] lines = new String[3][];
        Spliterator<String[]> spliterator = Arrays.spliterator(lines);
        Iterator<String[]> iterator = Spliterators.iterator(spliterator);
        
        
    }

    static void disassemble()
    {
        String[] s = new String[] { };
        for (String string : s)
        {
            System.err.println(s);
        }
        
    }
    
}
