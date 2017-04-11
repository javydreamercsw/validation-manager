package com.validation.manager.core.db;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.server.core.ProjectServer;
import static com.validation.manager.core.server.core.ProjectServer.deleteProject;
import static com.validation.manager.core.server.core.ProjectServer.getRequirements;
import com.validation.manager.core.server.core.StepServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.test.AbstractVMTestCase;
import static com.validation.manager.test.TestHelper.addRequirementToStep;
import static com.validation.manager.test.TestHelper.addStep;
import static com.validation.manager.test.TestHelper.addTestCaseToPlan;
import static com.validation.manager.test.TestHelper.createProject;
import static com.validation.manager.test.TestHelper.createRequirement;
import static com.validation.manager.test.TestHelper.createRequirementSpec;
import static com.validation.manager.test.TestHelper.createRequirementSpecNode;
import static com.validation.manager.test.TestHelper.createTestCase;
import static com.validation.manager.test.TestHelper.createTestPlan;
import static com.validation.manager.test.TestHelper.createTestProject;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
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
            = getLogger(ProjectTest.class.getName());

    @After
    public void clear() {
        if (p != null) {
            try {
                deleteProject(p);
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
            p = createProject("New Project", "Notes");
            assertEquals(0, getRequirements(p).size());
            ProjectServer project = new ProjectServer(p);
            project.setNotes("Notes 2");
            project.write2DB();
            Project temp = new ProjectJpaController(
                    getEntityManagerFactory())
                    .findProject(project.getId());
            assertTrue(temp.getNotes().equals(project.getNotes()));
            //Create requirements
            LOG.info("Create Requirement Spec");
            RequirementSpec rss = null;
            try {
                rss = createRequirementSpec("Test", "Test",
                        project, 1);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            LOG.info("Create Requirement Spec Node");
            RequirementSpecNode rsns = null;
            try {
                rsns = createRequirementSpecNode(
                        rss, "Test", "Test", "Test");
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            Requirement r = createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            //Create Test Case
            TestCase tc = createTestCase("Dummy", "Summary");
            //Add steps
            for (int i = 1; i < 6; i++) {
                LOG.info(MessageFormat.format("Adding step: {0}", i));
                tc = addStep(tc, i, "Step " + i, "Note " + i);
                Step step = tc.getStepList().get(i - 1);
                addRequirementToStep(step, r);
                new TestCaseServer(tc).write2DB();
                assertEquals(1, new StepServer(step).getRequirementList().size());
            }
            //Create test Project
            TestProject tp = createTestProject("Test Project");
            //Create test plan
            TestPlan plan = createTestPlan(tp, "Notes", true, true);
            //Add test to plan
            addTestCaseToPlan(plan, tc);
            assertEquals(1, getRequirements(project.getEntity()).size());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
