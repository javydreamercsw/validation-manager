package com.validation.manager.core.server.core;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import static com.validation.manager.test.TestHelper.createRequirement;
import static com.validation.manager.test.TestHelper.createRequirementSpec;
import static com.validation.manager.test.TestHelper.createRequirementSpecNode;
import static com.validation.manager.test.TestHelper.createTestCase;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import static junit.framework.TestCase.*;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestCaseExecutionServerTest extends AbstractVMTestCase {

    private static final Logger LOG
            = getLogger(TestCaseExecutionServerTest.class.getName());
    private RequirementSpec rss = null;
    private RequirementSpecNode rsns = null;
    private TestCase tc;
    private TestCaseServer tcs;
    private TestProject tp;
    private Project p;

    @Override
    protected void postSetUp() {
        try {
            p = TestHelper.createProject("Project", "Notes");
            //Create requirements
            LOG.info("Create Requirement Spec");
            try {
                rss = createRequirementSpec("Test", "Test",
                        p, 1);
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            LOG.info("Create Requirement Spec Node");
            try {
                rsns = createRequirementSpecNode(
                        rss, "Test", "Test", "Test");
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            Requirement r = createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            //Create Test Case
            tc = createTestCase("Dummy", "Summary");
            tcs = new TestCaseServer(tc);
            //Add steps
            List<Requirement> reqs = new ArrayList<>();
            reqs.add(r);
            for (int i = 1; i < 6; i++) {
                LOG.info(MessageFormat.format("Adding step: {0}", i));
                Step step = tcs.addStep(i, "Step " + i, "Note " + i,
                        "Criteria " + i, reqs);
                assertEquals(1, new StepServer(step).getRequirementList().size());
                tcs.update();
                assertEquals(i, tcs.getStepList().size());
                assertEquals(i, new RequirementServer(r).getStepList().size());
            }
            tp = TestHelper.createTestProject("TP");
            TestPlan plan = TestHelper.createTestPlan(tp, "Notes", true, true);
            TestHelper.addTestCaseToPlan(plan, tc);
            TestHelper.addTestProjectToProject(tp, p);
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Test of addTestCase method, of class TestCaseExecutionServer.
     */
    @Test
    public void testAddRemoveTestCase() {
        try {
            System.out.println("addTestCase");
            TestCaseExecutionServer instance = new TestCaseExecutionServer();
            instance.write2DB();
            assertEquals(0, instance.getEntity().getExecutionStepList().size());
            instance.addTestCase(tc);
            assertEquals(tc.getStepList().size(),
                    instance.getEntity().getExecutionStepList().size());
            instance.removeTestCase(tc);
            assertEquals(0, instance.getEntity().getExecutionStepList().size());
            instance.addTestCase(tc);
            assertEquals(tc.getStepList().size(),
                    instance.getEntity().getExecutionStepList().size());
            //Add issues and attachments to the step
            IssueServer issue = new IssueServer();
            issue.setTitle("Title");
            issue.setDescription("Description");
            issue.setCreationTime(new Date());
            issue.setIssueType(IssueTypeServer.getType("observation.name"));
            issue.write2DB();
            VMUserServer user = new VMUserServer(6);
            AttachmentServer attachment = new AttachmentServer();
            attachment.setTextValue("Test");
            attachment.setAttachmentType(AttachmentTypeServer
                    .getTypeForExtension("comment"));
            attachment.write2DB();
            instance.getExecutionStepList().forEach(es -> {
                ExecutionStepServer ess = new ExecutionStepServer(es);
                try {
                    ess.addIssue(issue, user);
                    ess.addAttachment(attachment);
                }
                catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            });
            instance.removeTestCase(tc);
            assertEquals(0, instance.getEntity().getExecutionStepList().size());
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }

    /**
     * Test of addTestProject method, of class TestCaseExecutionServer.
     */
    @Test
    public void testAddTestProject() {
        try {
            System.out.println("addTestProject");
            TestCaseExecutionServer instance = new TestCaseExecutionServer();
            instance.write2DB();
            assertEquals(0, instance.getEntity().getExecutionStepList().size());
            instance.addTestProject(tp);
            assertEquals(tc.getStepList().size(),
                    instance.getEntity().getExecutionStepList().size());
            instance.removeTestCase(tc);
            assertEquals(0, instance.getEntity().getExecutionStepList().size());
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }

    /**
     * Test of getExecutions method, of class TestCaseExecutionServer.
     */
    @Test
    public void testGetExecutions() {
        try {
            System.out.println("getExecutions");
            List<TestCaseExecution> r = TestCaseExecutionServer.getExecutions(p);
            assertEquals(0, r.size());
            TestCaseExecutionServer instance = new TestCaseExecutionServer();
            instance.write2DB();
            instance.addTestCase(tc);
            r = TestCaseExecutionServer.getExecutions(p);
            assertEquals(tc.getStepList().size(), r.size());
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
