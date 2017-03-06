package com.validation.manager.core.server.test.manager;

import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.server.core.StepServer;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import java.util.List;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestManagerTest extends AbstractVMTestCase {

    /**
     * Test of createTestExecutionFromPlan method, of class TestManager.
     */
    @Test
    public void testCreateTestExecutionFromPlan() {
        System.out.println("createTestExecutionFromPlan");
        List<TestPlan> plans = null;
        TestManager instance = new TestManager();

    }

    /**
     * Test of createTestExecutionFromTestCase method, of class TestManager.
     */
    @Test
    public void testCreateTestExecutionFromTestCase() {
        System.out.println("createTestExecutionFromTestCase");
        List<TestCase> testCases = null;
        TestManager instance = new TestManager();
    }

    /**
     * Test of createTestExecutionFromStep method, of class TestManager.
     */
    @Test
    public void testCreateTestExecutionFromStep() {
        try {
            System.out.println("createTestExecutionFromStep");
            TestManager instance = new TestManager();
            TestCase tc = TestHelper.createTestCase("Sample",
                    "Pass", "Summary");
            for (int i = 0; i < 5; i++) {
                tc = TestHelper.addStep(tc, (i + 1), "Text "
                        + (i + 1), "Note " + (i + 1));
            }
            TestCaseExecution r
                    = instance.createTestExecutionFromStep(tc
                            .getStepList());
            assertEquals(tc.getStepList().size(),
                    r.getExecutionStepList().size());
            for (Step s : tc.getStepList()) {
                assertEquals(1, new StepServer(s)
                        .getExecutionStepList().size());
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
