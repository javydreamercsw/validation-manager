package com.validation.manager.core.server.core;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class StepServerTest extends AbstractVMTestCase {

    /**
     * Test of getEntity method, of class StepServer.
     */
    @Test
    public void testDatabase() {
        try {
            System.out.println("Database interaction");
            System.out.println("Create a project");
            Project root = TestHelper.createProject("Test", "Notes");
            RequirementSpec mainSpec
                    = TestHelper.createRequirementSpec("Spec", "Desc", root, 1);
            System.out.println("Create Spec for sub project.");
            RequirementSpecNode node = TestHelper.createRequirementSpecNode(mainSpec,
                    "Requirement Doc", "Desc", "Scope");
            Requirement req = TestHelper.createRequirement("REQ-001", "Desc",
                    node.getRequirementSpecNodePK(), "Notes", 1, 1);
            Requirement req2 = TestHelper.createRequirement("REQ-002", "Desc",
                    node.getRequirementSpecNodePK(), "Notes", 1, 1);
            //Add a test case
            com.validation.manager.core.db.Test test
                    = TestHelper.createTest("Test", "Purpose", "Scope");
            //Create Test Case
            TestCase tc = TestHelper.createTestCase("Dummy", new Short("1"),
                    "Expected Results", test, /*user,*/ "Summary");
            tc = TestHelper.addStep(tc, 1, "Step " + 1, "Note " + 1);
            Step step = tc.getStepList().get(0);
            TestHelper.addRequirementToStep(step, req);
            new TestCaseServer(tc).write2DB();
            StepServer ss = new StepServer(step);
            RequirementServer rs = new RequirementServer(req);
            RequirementServer rs2 = new RequirementServer(req2);
            assertEquals(1, ss.getRequirementList().size());
            assertEquals(1, rs.getStepList().size());
            //Try adding the same requirement
            ss.addRequirement(req);
            assertEquals(1, ss.getRequirementList().size());
            assertEquals(1, rs.getStepList().size());
            ss.addRequirement(req2);
            assertEquals(2, ss.getRequirementList().size());
            assertEquals(1, rs.getStepList().size());
            rs2.update();
            assertEquals(1, rs2.getStepList().size());
            ss.removeRequirement(req);
            rs.update();
            assertEquals(1, ss.getRequirementList().size());
            assertEquals(0, rs.getStepList().size());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
