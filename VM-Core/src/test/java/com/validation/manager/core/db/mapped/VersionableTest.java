package com.validation.manager.core.db.mapped;

import com.validation.manager.test.AbstractVMTestCase;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class VersionableTest extends AbstractVMTestCase {

    /**
     * Test of compareTo method, of class Versionable.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        Versionable o = new Versionable() {
        };
        assertEquals(0, (int) o.getMajorVersion());
        assertEquals(0, (int) o.getMidVersion());
        assertEquals(1, (int) o.getMinorVersion());
        o.setMajorVersion(1);
        o.setMidVersion(1);
        o.setMinorVersion(1);
        Versionable o2 = new Versionable() {
        };
        assertEquals(0, (int) o2.getMajorVersion());
        assertEquals(0, (int) o2.getMidVersion());
        assertEquals(1, (int) o2.getMinorVersion());
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

    /**
     * Test of increaseMajor method, of class Versionable.
     */
    @Test
    public void testIncreaseMajor() {
        System.out.println("increaseMajor");
        Versionable o = new Versionable() {
        };
        o.setMajorVersion(1);
        o.setMidVersion(1);
        o.increaseMajor();
        assertEquals(2, (int) o.getMajorVersion());
        assertEquals(0, (int) o.getMidVersion());
        assertEquals(0, (int) o.getMinorVersion());
    }

    /**
     * Test of increaseMid method, of class Versionable.
     */
    @Test
    public void testIncreaseMid() {
        System.out.println("increaseMid");
        Versionable o = new Versionable() {
        };
        o.setMajorVersion(1);
        o.setMidVersion(1);
        o.increaseMid();
        assertEquals(1, (int) o.getMajorVersion());
        assertEquals(2, (int) o.getMidVersion());
        assertEquals(0, (int) o.getMinorVersion());
    }

    /**
     * Test of increaseMinor method, of class Versionable.
     */
    @Test
    public void testIncreaseMinor() {
        System.out.println("increaseMinor");
        Versionable o = new Versionable() {
        };
        o.setMajorVersion(1);
        o.setMidVersion(1);
        o.increaseMinor();
        assertEquals(1, (int) o.getMajorVersion());
        assertEquals(1, (int) o.getMidVersion());
        assertEquals(2, (int) o.getMinorVersion());
    }
}
