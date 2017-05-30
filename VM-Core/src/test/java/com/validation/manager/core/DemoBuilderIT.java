package com.validation.manager.core;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.tool.Tool;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.TestCase.*;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier Ortiz Bultron javier.ortiz.78@gmail.com
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
            projects.forEach(p -> {
                p.getTestProjectList().forEach(tp -> {
                    //Check all Test Projects have related projects..
                    assertTrue(tp.getProjectList().size() > 0);
                    tp.getTestPlanList().forEach(plan -> {
                        plan.getTestCaseList().forEach(tc -> {
                            //Check all test cases have steps.
                            assertTrue(tc.getStepList().size() > 0);
                            tc.getStepList().forEach(s -> {
                                //Check all steps have related requirements.
                                assertTrue(s.getRequirementList().size() > 0);
                            });
                        });
                    });
                });
                TestCaseExecutionServer.getExecutions(p).forEach(ex -> {
                    //Check all execution has steps.
                    assertTrue(ex.getExecutionStepList().size() > 0);
                    ex.getExecutionStepList().forEach(es -> {
                        //Check all execution steps have history.
                        assertTrue(es.getHistoryList().size() > 0);
                    });
                });
                //Check all requirements have either children or a parent
                Tool.extractRequirements(p).forEach(r -> {
                    try {
                        RequirementServer rs = new RequirementServer(r);
                        assertTrue(rs.getParentRequirementId() != null
                                || rs.getRequirementList().size() > 0);
                        //Make sure all have a related step
                        if (rs.getStepList().isEmpty()) {
                            LOG.log(Level.INFO, "{0} has no steps!",
                                    rs.getUniqueId());
                        }
//                        assertTrue(rs.getStepList().size() > 0);
                    }
                    catch (VMException ex) {
                        Exceptions.printStackTrace(ex);
                        fail();
                    }
                });
            });
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            fail();
        }
    }
}
