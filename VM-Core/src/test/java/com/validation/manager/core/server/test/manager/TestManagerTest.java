package com.validation.manager.core.server.test.manager;

import com.googlecode.flyway.core.util.StopWatch;
import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.ExecutionResult;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.ExecutionResultJpaController;
import com.validation.manager.core.db.controller.ExecutionStepJpaController;
import com.validation.manager.core.server.core.StepServer;
import com.validation.manager.core.server.core.TestPlanServer;
import com.validation.manager.core.server.core.VMUserServer;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
        try {
            TestProject tp
                    = TestHelper.createTestProject("Test Project");
            TestPlan p = TestHelper.createTestPlan(tp,
                    "Notes", true, true);
            TestPlanServer plan = new TestPlanServer(p);
            TestManager instance = new TestManager();
            TestCase tc = TestHelper.createTestCase("Sample",
                    "Pass", "Summary");
            for (int i = 0; i < 5; i++) {
                tc = TestHelper.addStep(tc, (i + 1), "Text "
                        + (i + 1), "Note " + (i + 1));
            }
            plan.getTestCaseList().add(tc);
            plan.write2DB();
            TestCaseExecution r
                    = instance.createTestExecutionFromPlan(Arrays
                            .asList(new TestPlanServer(plan.getEntity())
                                    .getEntity()));
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

    /**
     * Test of createTestExecutionFromTestCase method, of class TestManager.
     */
    @Test
    public void testCreateTestExecutionFromTestCase() {
        System.out.println("createTestExecutionFromTestCase");
        try {
            TestManager instance = new TestManager();
            TestCase tc = TestHelper.createTestCase("Sample",
                    "Pass", "Summary");
            for (int i = 0; i < 5; i++) {
                tc = TestHelper.addStep(tc, (i + 1), "Text "
                        + (i + 1), "Note " + (i + 1));
            }
            TestCaseExecution r
                    = instance.createTestExecutionFromTestCase(Arrays.asList(tc));
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

    @Test
    public void testAssignExecution() {
        try {
            System.out.println("Assign Execution");
            TestManager instance = new TestManager();
            TestCase tc = TestHelper.createTestCase("Sample",
                    "Pass", "Summary");
            for (int i = 0; i < 5; i++) {
                tc = TestHelper.addStep(tc, (i + 1), "Text "
                        + (i + 1), "Note " + (i + 1));
                if (i == 4) {
                    TestHelper.addRequirementToStep(tc.getStepList()
                            .get(tc.getStepList().size() - 1),
                            TestHelper.createRequirement("SRS-0001",
                                    "Description",
                                    TestHelper.createRequirementSpecNode(
                                            TestHelper.createRequirementSpec("Spec",
                                                    "Desc",
                                                    TestHelper.createProject("Project", ""), 1),
                                            "Node", "", "").getRequirementSpecNodePK(),
                                    "Notes", 1, 1));
                }
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
            VMUserServer user = new VMUserServer(1);
            VMUserServer test = new VMUserServer(TestHelper
                    .createUser("Tester", "test",
                            "tester@test.com", "Mr.", "Tester"));
            instance.assignUser(test.getEntity(), user.getEntity(),
                    r.getExecutionStepList());
            instance.update(r);
            r.getExecutionStepList().forEach((es) -> {
                assertEquals((int) test.getId(),
                        (int) es.getVmUserId().getId());
                assertNotNull(es.getAssignedTime());
                assertEquals(user.getId(), es.getAssignedByUserId());
            });
            assertEquals(r.getExecutionStepList().size(),
                    test.getEntity().getExecutionStepList().size());
            //Now execute some steps
            Random random = new Random();
            ExecutionStepJpaController controller
                    = new ExecutionStepJpaController(DataBaseManager
                            .getEntityManagerFactory());
            List<ExecutionResult> results = new ExecutionResultJpaController(DataBaseManager
                    .getEntityManagerFactory()).findExecutionResultEntities();
            assertTrue(results.size() > 0);
            System.out.println("Simulating executions...");
            for (ExecutionStep es : test.getEntity().getExecutionStepList()) {
                es.setExecutionStart(new Date());
                StopWatch sw = new StopWatch();
                sw.start();
                es.setComment("Step comment");
                Thread.sleep((random.nextInt(10) + 1) * 1000);//1-10 seconds
                es.setExecutionEnd(new Date());
                sw.stop();
                es.setExecutionTime(sw.getTotalTimeMillis());//Around the time above.
                es.setResultId(results.get(random.nextInt(results.size())));
                controller.edit(es);
                ExecutionStep temp
                        = controller.findExecutionStep(es
                                .getExecutionStepPK());
                assertNotNull(temp);
                assertNotNull(temp.getExecutionStart());
                assertNotNull(temp.getExecutionEnd());
                assertNotNull(temp.getExecutionTime());
                assertNotNull(temp.getResultId());
                System.out.println("Step: "
                        + temp.getStep().getStepSequence());
                System.out.println("Assigned: "
                        + temp.getAssignedTime());
                System.out.println("Started: "
                        + temp.getExecutionStart());
                System.out.println("Completed: "
                        + temp.getExecutionEnd());
                long timeelapsed = temp.getExecutionTime();
                long milliseconds = timeelapsed / 1000;
                long seconds = (timeelapsed / 1000) % 60;
                long minutes = (timeelapsed / 60000) % 60;
                System.out.println("Elapsed time: " + " ("
                        + minutes + ":" + seconds
                        + ":" + milliseconds + ")");
                System.out.println("Comments: " + temp.getComment());
                System.out.println("Result: "
                        + temp.getResultId().getResultName());
                System.out.println("Executed by: "
                        + temp.getVmUserId().getFirstName() + " "
                        + temp.getVmUserId().getLastName());
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
