/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nomagicsoftware.functional;

import com.nomagicsoftware.functional.Transducer.Reducer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author thurston
 */
public class TransducerTest
{
    
    public TransducerTest()
    {
    }

    @Test
    public void testCall()
    {
    }

    @Test
    public void testTransduce()
    {
    }

    @Test
    public void testCompose()
    {
    }

    @Test
    public void testMapTransducer()
    {
    }

    @Test
    public void testFilterTransducer()
    {
    }

    @Test
    public void testFlatMap()
    {
        
        List<List<String>> stringFlow = new ArrayList<>();
        stringFlow.add(Arrays.asList("The", "end", "is"));
        stringFlow.add(Arrays.asList("arriving", "soon"));
        
//        Transducer<Iterable<? extends String>, Integer> flatMap = Transducers.flatMap(String::length);
//        
//        assertEquals("", 20, (int) flatMap.transduce(stringFlow, (init, l) -> init + l, 0));
//        
        
    }
    
    @Test
    public void fm()
    {
        List<String> lines = new ArrayList<>();
        //Iterator<String> iterator = Spliterators.iterator(Arrays.spliterator(lines));
        String _line = "The end of the world";
        _line.split("\\s+");
        lines.add(_line);
        lines.add(_line);
        
        Transducer<String, String> trans = Transducers.flatMap(line -> iterator(line.split("\\s+")));
        int wordCount = trans.transduce(lines, (total, _w) -> ++total, 0);
        assertEquals("", 10, wordCount);
        
        wordCount = trans.transduce(Arrays.asList("hello!"), (total, _w) -> ++total, 0);
        assertEquals("", 1, wordCount);
    }
    
    @Test
    public void flattenArray()
    {
        List<String> lines = new ArrayList<>();
        lines.add("Well, What do we have here?");
        Transducer<String, String> trans = Transducers.flatMapA(line -> line.split("\\W+"));
        trans = trans.compose(Transducers.filter(s -> ! s.isEmpty() && Character.isUpperCase(s.charAt(0))));
        assertEquals("", 2, (int) trans.transduce(lines, (total, _w) -> ++total, 0));
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
   
    static <T> Reducer<Integer, T> counter()
    {
        return (total, _ignore) -> ++total;
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
