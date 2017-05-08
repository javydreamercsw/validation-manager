/*
 * This class builds the demo data.
 */
package com.validation.manager.core;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.server.core.RequirementSpecNodeServer;
import com.validation.manager.core.server.core.RequirementSpecServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.TestPlanServer;
import com.validation.manager.core.server.core.TestProjectServer;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DemoBuilder {

    private static final Logger LOG
            = Logger.getLogger(DemoBuilder.class.getSimpleName());
    private static int tcCounter = 1, tpCounter = 1, reqCounter = 1;

    public static void buildDemoProject() throws Exception {
        LOG.info("Creating demo projects...");
        ProjectJpaController controller
                = new ProjectJpaController(DataBaseManager
                        .getEntityManagerFactory());
        Project rootProject = new Project("Demo");
        controller.create(rootProject);
        for (int i = 0; i < 5; i++) {
            Project temp = new Project("Sub " + (i + 1));
            temp.setParentProjectId(rootProject);
            controller.create(temp);
            ProjectServer ps = new ProjectServer(temp);
            addDemoProjectRequirements(ps.getEntity());
            addDemoProjectTestProject(ps.getEntity());
            addDemoExecution(ps.getEntity());
            rootProject.getProjectList().add(ps.getEntity());
        }
        addDemoProjectRequirements(rootProject);
        controller.edit(rootProject);
        LOG.info("Done!");
    }

    private static void addDemoProjectRequirements(Project p)
            throws Exception {
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
    }

    private static void addDemoProjectTestProject(Project p) throws
            NonexistentEntityException, Exception {
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
    }

    private static void addDemoExecution(Project p)
            throws NonexistentEntityException, Exception {
        int i = 1;
        TestCaseExecutionServer tces
                = new TestCaseExecutionServer("Execution " + i,
                        "Test Scope " + i);
        tces.setConclusion("Conclusion!");
        tces.write2DB();
        p.getTestProjectList().forEach((tp) -> {
            tces.addTestProject(tp);
        });
        tces.write2DB();
        VMUserServer tester1 = new VMUserServer(2);//Tester
        VMUserServer tester2 = new VMUserServer(3);//Tester
        VMUserServer assigner = new VMUserServer(6);//Tester
        Random r = new Random();
        tces.getExecutionStepList().stream().map((es)
                -> new ExecutionStepServer(es)).forEachOrdered((ess)
                -> {
            ess.getStep().getRequirementList().forEach(req -> {
                ess.getHistoryList().add(req.getHistoryList().get(0));
            });
            ess.assignUser(r.nextBoolean() ? tester1.getEntity()
                    : tester2.getEntity(), assigner.getEntity());
        });
    }
}
