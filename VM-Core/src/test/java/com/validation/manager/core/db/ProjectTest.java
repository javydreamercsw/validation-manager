package com.validation.manager.core.db;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.StepServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.TestCase.assertEquals;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectTest extends AbstractVMTestCase {

    Project p;
    private static final Logger LOG
            = Logger.getLogger(ProjectTest.class.getName());

    @After
    public void clear() {
        if (p != null) {
            try {
                ProjectServer.deleteProject(p);
            } catch (VMException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Test of toString method, of class Project.
     */
    @Test
    public void testCreateAndDestroy() {
        try {
            p = TestHelper.createProject("New Project", "Notes");
            assertEquals(0, ProjectServer.getRequirements(p).size());
            ProjectServer project = new ProjectServer(p);
            project.setNotes("Notes 2");
            project.write2DB();
            assertTrue(new ProjectJpaController(
                    DataBaseManager.getEntityManagerFactory())
                    .findProject(project.getId()).getNotes().equals(project.getNotes()));
            //Create requirements
            LOG.info("Create Requirement Spec");
            RequirementSpec rss = null;
            try {
                rss = TestHelper.createRequirementSpec("Test", "Test",
                        project, 1);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            LOG.info("Create Requirement Spec Node");
            RequirementSpecNode rsns = null;
            try {
                rsns = TestHelper.createRequirementSpecNode(
                        rss, "Test", "Test", "Test");
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            Requirement r = TestHelper.createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            //Create Test
            com.validation.manager.core.db.Test test
                    = TestHelper.createTest("Test #1", "Testing",
                            "Test #1 scope");
            //Create Test Case
            TestCase tc = TestHelper.createTestCase("Dummy", new Short("1"),
                    "Expected Results", test, /*user,*/ "Summary");
            //Add steps
            for (int i = 1; i < 6; i++) {
                LOG.info(MessageFormat.format("Adding step: {0}", i));
                tc = TestHelper.addStep(tc, i, "Step " + i, "Note " + i);
                Step step = tc.getStepList().get(i - 1);
                TestHelper.addRequirementToStep(step, r);
                new TestCaseServer(tc).write2DB();
                assertEquals(1, new StepServer(step).getRequirementList().size());
            }
            //Create test Project
            TestProject tp = TestHelper.createTestProject("Test Project");
            //Create test plan
            TestPlan plan = TestHelper.createTestPlan(tp, "Notes", true, true);
            //Add test case to test
            TestHelper.addTestCaseToTest(test, tc);
            //Add test to plan
            TestHelper.addTestToPlan(plan, test);
            assertEquals(1, ProjectServer.getRequirements(p).size());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
