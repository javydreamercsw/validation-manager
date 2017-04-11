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
import org.junit.Before;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ExecutionStepServerTest extends AbstractVMTestCase {

    private static int tcCounter = 1, tpCounter = 1, reqCounter = 1;

    @Before
    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
                            new Date());
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

    /**
     * Test of assignUser method, of class ExecutionStepServer.
     */
    @Test
    public void testAssignUser() {
        try {
            System.out.println("assignUser");
            VMUserServer assignee = new VMUserServer(2);//Tester
            VMUserServer assigner = new VMUserServer(6);//Tester
            ProjectServer ps = new ProjectServer(ProjectServer.getProjects().get(0));
            TestCaseExecutionServer tces
                    = new TestCaseExecutionServer(ps.getTestProjectList().get(0)
                            .getTestPlanList().get(0).getTestCaseList().get(0)
                            .getStepList().get(0).getExecutionStepList().get(0)
                            .getTestCaseExecution());
            tces.addTestCase(ps.getTestProjectList().get(0).getTestPlanList()
                    .get(0).getTestCaseList().get(0));
            ExecutionStepServer instance
                    = new ExecutionStepServer(tces.getExecutionStepList().get(0));
            instance.assignUser(assignee.getEntity(), assigner.getEntity());
            assertEquals(assignee.getId(), instance.getAssignee().getId());
            assertEquals(assigner.getId(), instance.getAssigner().getId());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }

    /**
     * Test of addAttachment method, of class ExecutionStepServer.
     */
    @Test
    public void testAddAttachment() throws Exception {
        System.out.println("addAttachment");
        AttachmentServer attachment = null;
        ExecutionStepServer instance = null;
        instance.addAttachment(attachment);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
