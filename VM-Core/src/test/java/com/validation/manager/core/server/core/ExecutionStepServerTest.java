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

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ExecutionStepServerTest extends AbstractVMTestCase {

    private static int tcCounter = 1, tpCounter = 1, reqCounter = 1;
    private VMUserServer assignee;//Tester
    private VMUserServer assigner;//Tester

    @Override
    protected void postSetUp() {
        try {
            DataBaseManager.getEntityManagerFactory();
            assignee = new VMUserServer(2);//Tester
            assigner = new VMUserServer(6);//Tester
            ProjectJpaController controller
                    = new ProjectJpaController(DataBaseManager
                            .getEntityManagerFactory());
            Project p = new Project("Demo");
            controller.create(p);
            for (int i = 0; i < 5; i++) {
                //Create a spec
                RequirementSpecServer temp
                        = new RequirementSpecServer("Spec "
                                + (i + 1), "Description " + (i + 1),
                                p.getId(), 1);
                temp.write2DB();
                RequirementSpecNodeServer node
                        = temp.addSpecNode("Node " + (i + 1),
                                "Description " + (i + 1), "Scope " + (i + 1));
                for (int y = 0; y < 5; y++) {
                    RequirementServer req
                            = new RequirementServer("Requirement "
                                    + reqCounter,
                                    "Description " + reqCounter,
                                    node.getRequirementSpecNodePK(),
                                    "Notes", 1, 1);
                    req.write2DB();
                    node.getRequirementList().add(req.getEntity());
                    reqCounter++;
                }
                node.write2DB();
                p.getRequirementSpecList().add(temp.getEntity());
            }
            new ProjectJpaController(DataBaseManager
                    .getEntityManagerFactory()).edit(p);
            TestProjectServer tp
                    = new TestProjectServer("Test Project", true);
            tp.setName("Test Project");
            tp.setNotes("Notes");
            tp.setActive(true);
            tp.write2DB();
            //Add the test structure
            TestPlanServer tps = new TestPlanServer(tp.getEntity(),
                    true, true);
            tps.setName("Test Plan #" + (tpCounter++));
            tps.setNotes("Notes");
            tps.write2DB();
            for (int i = 0; i < 5; i++) {
                //Add steps
                TestCaseServer tcs
                        = new TestCaseServer("Test Case #"
                                + (tcCounter++),
                                new Date(),
                                new TestCaseTypeServer(5).getEntity());
                tcs.write2DB();
                for (int j = 0; j < 5; j++) {
                    List<Requirement> requirements
                            = new RequirementJpaController(DataBaseManager
                                    .getEntityManagerFactory())
                                    .findRequirementEntities()
                                    .subList(j * 5, j * 5 + 5);
                    tcs.addStep((j + 1), "Step #" + (j + 1), "Note",
                            "Criteria",
                            requirements);
                }
                tcs.write2DB();
                tps.addTestCase(tcs.getEntity());
            }
            tps.write2DB();
            tp.getTestPlanList().add(tps.getEntity());
            tp.write2DB();
            TestCaseExecutionServer tce = new TestCaseExecutionServer();
            tce.write2DB();
            tce.addTestProject(tp.getEntity());
            tps.getTestCaseList().forEach((tc) -> {
                assignee.assignTestCase(tce.getEntity(), tc, assigner);
            });
            ProjectServer ps = new ProjectServer(p);
            if (ps.getTestProjectList() == null) {
                ps.setTestProjectList(new ArrayList<>());
            }
            ps.getTestProjectList().add(tp.getEntity());
            //Save it
            ps.write2DB();
            int i = 1;
            TestCaseExecutionServer tces
                    = new TestCaseExecutionServer("Execution " + i,
                            "Test Scope " + i);
            tces.setConclusion("Conclusion!");
            tces.write2DB();
            p.getTestProjectList().forEach((t) -> {
                tces.addTestProject(t);
            });
            tces.write2DB();
            ps.update(p, ps.getEntity());
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Test of assignUser method, of class ExecutionStepServer.
     */
    @Test
    public void testAssignUser() {
        try {
            System.out.println("assignUser");
            ProjectServer ps = new ProjectServer(ProjectServer.getProjects().get(0));
            TestCaseExecutionServer tces
                    = new TestCaseExecutionServer(ps.getTestProjectList().get(0)
                            .getTestPlanList().get(0).getTestCaseList().get(0)
                            .getStepList().get(0).getExecutionStepList().get(0)
                            .getTestCaseExecution());
            ExecutionStepServer instance
                    = new ExecutionStepServer(tces.getExecutionStepList().get(0));
            instance.assignUser(assignee.getEntity(), assigner.getEntity());
            assertEquals(assignee.getId(), instance.getAssignee().getId());
            assertEquals(assigner.getId(), instance.getAssigner().getId());
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }

    /**
     * Test of addAttachment method, of class ExecutionStepServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddAttachment() throws Exception {
        System.out.println("addAttachment");
        AttachmentServer attachment = new AttachmentServer();
        attachment.setTextValue("Test");
        attachment.setAttachmentType(AttachmentTypeServer
                .getTypeForExtension("comment"));
        attachment.write2DB();
        ProjectServer ps = new ProjectServer(ProjectServer.getProjects().get(0));
        TestCaseExecutionServer tces
                = new TestCaseExecutionServer(ps.getTestProjectList().get(0)
                        .getTestPlanList().get(0).getTestCaseList().get(0)
                        .getStepList().get(0).getExecutionStepList().get(0)
                        .getTestCaseExecution());
        ExecutionStepServer instance
                = new ExecutionStepServer(tces.getExecutionStepList().get(0));
        assertEquals(0, instance.getExecutionStepHasAttachmentList().size());
        instance.addAttachment(attachment);
        assertEquals(1, instance.getExecutionStepHasAttachmentList().size());
        instance.removeAttachment(attachment);
        assertEquals(0, instance.getExecutionStepHasAttachmentList().size());
    }

    /**
     * Test of addIssue method, of class ExecutionStepServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddIssue() throws Exception {
        System.out.println("addIssue");
        IssueServer issue = new IssueServer();
        issue.setTitle("Title");
        issue.setDescription("Description");
        issue.setCreationTime(new Date());
        issue.setIssueType(IssueTypeServer.getType("observation.name"));
        issue.write2DB();
        ProjectServer ps = new ProjectServer(ProjectServer.getProjects().get(0));
        TestCaseExecutionServer tces
                = new TestCaseExecutionServer(ps.getTestProjectList().get(0)
                        .getTestPlanList().get(0).getTestCaseList().get(0)
                        .getStepList().get(0).getExecutionStepList().get(0)
                        .getTestCaseExecution());
        ExecutionStepServer instance
                = new ExecutionStepServer(tces.getExecutionStepList().get(0));
        assertEquals(0, instance.getExecutionStepHasIssueList().size());
        instance.addIssue(issue, assigner);
        instance.write2DB();
        instance = new ExecutionStepServer(tces.getExecutionStepList().get(0));
        assertEquals(1, instance.getExecutionStepHasIssueList().size());
        assertEquals(assigner.getId(),
                instance.getExecutionStepHasIssueList().get(0)
                        .getVmUserList().get(0).getId());
        instance.removeIssue(issue);
        assertEquals(0, instance.getExecutionStepHasIssueList().size());
    }
}
