package com.nomagicsoftware.functional.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thurston
 */
public class Course 
{
    String name, subject;
    final List<Student> registered = new ArrayList<>();

    public Course(String name, String subject)
    {
        this.name = name;
        this.subject = subject;
    }

    public String getName()
    {
        return this.name;
    }

    public String getSubject()
    {
        return this.subject;
    }

    public List<Student> getRegistered()
    {
        return this.registered;
    }
    
    
    
}
