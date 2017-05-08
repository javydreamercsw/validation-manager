package com.validation.manager.core;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.TestCase.*;
import org.junit.Test;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DemoBuilderIT extends AbstractVMTestCase {

    private static final Logger LOG
            = Logger.getLogger(DemoBuilderIT.class.getSimpleName());

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
            List<Project> projects = ProjectServer.getProjects();
            assertEquals(6 /*Root and 5 sub projects*/,
                    projects.size());
            projects.forEach(p -> {
                p.getTestProjectList().forEach(tp -> {
                    assertTrue(tp.getProjectList().size() > 0);
                    tp.getTestPlanList().forEach(plan -> {
                        plan.getTestCaseList().forEach(tc -> {
                            assertTrue(tc.getStepList().size() > 0);
                            tc.getStepList().forEach(s -> {
                                assertTrue(s.getRequirementList().size() > 0);
                            });
                        });
                    });
                });
                TestCaseExecutionServer.getExecutions(p).forEach(ex -> {
                    assertTrue(ex.getExecutionStepList().size() > 0);
                    ex.getExecutionStepList().forEach(es -> {
                        assertTrue(es.getHistoryList().size() > 0);
                    });
                });
            });
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            fail();
        }
    }
}
