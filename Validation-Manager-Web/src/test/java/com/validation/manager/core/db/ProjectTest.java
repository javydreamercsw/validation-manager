package com.validation.manager.core.db;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.test.TestHelper;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ProjectTest extends AbstractVMTestCase {

    Project p;

    public ProjectTest() {
    }

    @After
    public void clear() {
        if (p != null) {
            for (Requirement r : p.getRequirementList()) {
                RequirementServer.deleteRequirement(r);
            }
            ProjectServer.deleteProject(p);
        }
    }

    /**
     * Test of toString method, of class Project.
     */
    @Test
    public void testCreateAndDestroy() {
        try {
            VmUser user = TestHelper.createUser("test1",
                    "password", "first", "test@test.com", "last");
            p = TestHelper.createProject("New Project", "Notes");
            ProjectServer ps = new ProjectServer(p);
            ps.setNotes("Notes 2");
            ps.write2DB();
            assertTrue(new ProjectJpaController( DataBaseManager.getEntityManagerFactory()).findProject(ps.getId()).getNotes().equals(ps.getNotes()));
            //Create requirements
            Requirement r = TestHelper.createRequirement("SRS-SW-0001", "Sample requirement", p, "Notes", 1);
            //Create Test
            com.validation.manager.core.db.Test test = TestHelper.createTest("Test #1", "Testing", "Test #1 scope");
            //Create Test Case
            TestCase tc = TestHelper.createTestCase(1, new Short("1"), "Expected Results", test, user, "Summary");
            //Add steps
            for (int i = 1; i < 6; i++) {
                tc = TestHelper.addStep(tc, i, "Step " + i, "Note " + i);
            }
            tc.getStepList().get(0).getRequirementList().add(r);
            new StepJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(tc.getStepList().get(0));
            assertTrue(tc.getStepList().get(0).getRequirementList().size() == 1);
            //Create test Project
            TestProject tp = TestHelper.createTestProject("Test Project");
            //Create test plan
            TestPlan plan = TestHelper.createTestPlan(tp, "Notes", true, true);
            //Add test case to test
            TestHelper.addTestCaseToTest(test, tc);
            //Add test to plan
            TestHelper.addTestToPlan(plan, test);
        } catch (Exception ex) {
            Logger.getLogger(ProjectTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
