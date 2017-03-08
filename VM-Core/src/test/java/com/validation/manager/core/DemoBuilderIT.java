package com.validation.manager.core;

import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.test.AbstractVMTestCase;
import static junit.framework.TestCase.*;
import org.junit.Test;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DemoBuilderIT extends AbstractVMTestCase {

    /**
     * Test of buildDemoProject method, of class DemoBuilder.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testBuildDemoProject() throws Exception {
        System.out.println("buildDemoProject");
        try {
            DemoBuilder.buildDemoProject();
            assertEquals(6 /*Root and 5 sub projects*/,
                    ProjectServer.getProjects().size());
        } catch (Exception e) {
            fail();
        }
    }
}
