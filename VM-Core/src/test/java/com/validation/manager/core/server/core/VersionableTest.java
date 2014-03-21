package com.validation.manager.core.server.core;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VersionableTest {
    
    public VersionableTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of compareTo method, of class Versionable.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        Versionable o = new Versionable();
        o.setMajorVersion(1);
        o.setMidVersion(1);
        o.setMinorVersion(1);
        Versionable o2 = new Versionable();
        o2.setMajorVersion(1);
        o2.setMidVersion(1);
        o2.setMinorVersion(1);
        //Same
        assertEquals(0, o.compareTo(o2));
        //Bigger Major
        o.setMajorVersion(2);
        assertEquals(1, o.compareTo(o2));
        //Bigger Major
        o.setMajorVersion(0);
        assertEquals(-1, o.compareTo(o2));
        o.setMajorVersion(1);
        //Bigger Mid
        o.setMidVersion(2);
        assertEquals(1, o.compareTo(o2));
        //Bigger Mid
        o.setMidVersion(0);
        assertEquals(-1, o.compareTo(o2));
        o.setMidVersion(1);
        //Bigger Minor
        o.setMinorVersion(2);
        assertEquals(1, o.compareTo(o2));
        //Bigger Minor
        o.setMinorVersion(0);
        assertEquals(-1, o.compareTo(o2));
    }
}
