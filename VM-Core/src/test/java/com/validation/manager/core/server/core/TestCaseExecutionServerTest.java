/* 
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core.server.core;

import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.TestPlan;
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
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TestCaseExecutionServerTest extends AbstractVMTestCase {

    private static final Logger LOG
            = getLogger(TestCaseExecutionServerTest.class.getName());
    private RequirementSpec rss = null;
    private RequirementSpecNode rsns = null;
    private TestCaseServer tcs;
    private TestProjectServer tps;
    private ProjectServer ps;

    @Override
    protected void postSetUp() {
        try {
            ps = new ProjectServer(TestHelper.createProject("Project", "Notes"));
            //Create requirements
            LOG.info("Create Requirement Spec");
            try {
                rss = createRequirementSpec("Test", "Test",
                        ps.getEntity(), 1);
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
            tcs = new TestCaseServer(createTestCase("Dummy", "Summary"));
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
            tps = new TestProjectServer(TestHelper.createTestProject("TP"));
            TestPlan plan = TestHelper.createTestPlan(tps.getEntity(), "Notes", true, true);
            TestHelper.addTestCaseToPlan(plan, tcs.getEntity());
            TestHelper.addTestProjectToProject(tps.getEntity(), ps.getEntity());
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
            instance.addTestCase(tcs.getEntity());
            assertEquals(tcs.getStepList().size(),
                    instance.getEntity().getExecutionStepList().size());
            instance.removeTestCase(tcs.getEntity());
            assertEquals(0, instance.getEntity().getExecutionStepList().size());
            instance.addTestCase(tcs.getEntity());
            assertEquals(tcs.getStepList().size(),
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
            instance.removeTestCase(tcs.getEntity());
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
            instance.addTestProject(tps.getEntity());
            assertTrue(tcs.getStepList().size() > 0);
            assertEquals(tcs.getStepList().size(),
                    instance.getEntity().getExecutionStepList().size());
            instance.getEntity().getExecutionStepList().forEach(es -> {
                assertNotNull(es.getStep());
                assertNotNull(es.getStepHistory());
                assertFalse(es.getLocked());
                assertNull(es.getResultId());
                assertNull(es.getReviewResultId());
                assertNotNull(es.getTestCaseExecution());
            });
            instance.removeTestCase(tcs.getEntity());
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
            List<TestCaseExecution> r
                    = TestCaseExecutionServer.getExecutions(ps.getEntity());
            assertEquals(0, r.size());
            TestCaseExecutionServer instance = new TestCaseExecutionServer();
            instance.write2DB();
            instance.addTestCase(tcs.getEntity());
            r = TestCaseExecutionServer.getExecutions(ps.getEntity());
            assertEquals(tcs.getEntity().getStepList().size(), r.size());
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
