package com.nomagicsoftware.functional.model;

/**
 *
 * @author thurston
 */
public class Student 
{
    String first, last;
    Year year;
    
    public static enum Year
    {
        FRESHMAN, SOPHOMORE, JUNIOR, SENIOR;
    };
}
