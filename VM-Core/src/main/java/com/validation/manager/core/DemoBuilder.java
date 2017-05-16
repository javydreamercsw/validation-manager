/*
 * This class builds the demo data.
 */
package com.validation.manager.core;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.server.core.RequirementSpecNodeServer;
import com.validation.manager.core.server.core.RequirementSpecServer;
import com.validation.manager.core.server.core.StepServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.TestPlanServer;
import com.validation.manager.core.server.core.TestProjectServer;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DemoBuilder {

    private static final Logger LOG
            = Logger.getLogger(DemoBuilder.class.getSimpleName());
    private static int tcCounter = 1, tpCounter, reqCounter;

    public static void buildDemoProject() throws Exception {
        LOG.info("Creating demo projects...");
        ProjectJpaController controller
                = new ProjectJpaController(DataBaseManager
                        .getEntityManagerFactory());
        Project rootProject = new Project("Demo");
        controller.create(rootProject);
        for (int i = 0; i < 2; i++) {
            Project temp = new Project("Sub " + (i + 1));
            temp.setParentProjectId(rootProject);
            controller.create(temp);
            ProjectServer ps = new ProjectServer(temp);
            addDemoProjectRequirements(ps.getEntity());
            addDemoProjectTestProject(ps.getEntity());
            rootProject.getProjectList().add(ps.getEntity());
        }
        addDemoProjectRequirements(rootProject);
        controller.edit(rootProject);
        //Link requirements with steps
        List<Requirement> requirements
                = new RequirementJpaController(DataBaseManager
                        .getEntityManagerFactory())
                        .findRequirementEntities();
        LOG.log(Level.INFO, "Total: {0}", requirements.size());
        List<TestCase> tcs = new TestCaseJpaController(DataBaseManager
                .getEntityManagerFactory()).findTestCaseEntities();
        int amount = tcs.size();
        int size = requirements.size() / amount;
        int i = 0;
        for (TestCase tc : tcs) {
            for (Step step : tc.getStepList()) {
                try {
                    StepServer ss = new StepServer(step);
                    ss.getRequirementList().addAll(requirements
                            .subList(i * size, i * size + size));
                    ss.write2DB();
                }
                catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            i++;
        }
        for (Project p : controller.findProjectEntities()) {
            addDemoExecution(p);
        }
        LOG.info("Done!");
    }

    private static void addDemoProjectRequirements(Project p)
            throws Exception {
        tpCounter = 1;
        reqCounter = 1;
        //Create User Needs spec
        RequirementSpecServer un = createRequirementSpec(p, 1, 1, 5,
                "User Need", "User Need Desc");
        List<RequirementSpecServer> subSpecs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            //Create a spec
            subSpecs.add(createRequirementSpec(p, 5, 1, 5, "Spec " + i,
                    "Desc " + i));
        }
        //Now link UN requirement to lower level requirements
        un.getRequirementSpecNodeList().forEach(rsn -> {
            int count = 0;
            for (Requirement r : rsn.getRequirementList()) {
                subSpecs.get(count).getRequirementSpecNodeList().forEach(nl -> {
                    nl.getRequirementList().forEach(sr -> {
                        try {
                            new RequirementServer(r)
                                    .addChildRequirement(sr);
                        }
                        catch (VMException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                        catch (Exception ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    });
                });
                count++;
            }
        });
        new ProjectJpaController(DataBaseManager
                .getEntityManagerFactory()).edit(p);
    }

    /**
     * Create a requirement spec for the provided project on the specified
     * requirement level.
     *
     * @param p Project to add the spec to.
     * @param level Spec level
     * @param nodeAmount Amount of nodes to create
     * @param reqAmount Amount of requirements to add to each node of this spec.
     * @param specName Spec desired name
     * @param specDesc Spec description
     * @return Created Requirement Spec
     * @throws Exception
     */
    private static RequirementSpecServer createRequirementSpec(Project p,
            int level, int nodeAmount, int reqAmount, String specName,
            String specDesc) throws Exception {
        RequirementSpecServer temp
                = new RequirementSpecServer(specName,
                        specDesc,
                        p.getId(), level);
        temp.write2DB();
        if (nodeAmount > 0) {
            for (int i = 0; i < nodeAmount; i++) {
                RequirementSpecNodeServer node
                        = temp.addSpecNode("Node " + (i + 1),
                                "Description " + (i + 1), "Scope " + (i + 1));
                if (reqAmount > 0) {
                    for (int y = 0; y < reqAmount; y++) {
                        String header = "";
                        switch (level) {
                            case 1:
                                header = "PS";
                                break;
                            case 5:
                                header = "SRS";
                                break;
                            default:
                                header = "SRS";
                                break;
                        }
                        RequirementServer req
                                = new RequirementServer(header
                                        + String.format("%05d", reqCounter),
                                        "Description " + reqCounter,
                                        node.getRequirementSpecNodePK(),
                                        "Notes", 1, 1);
                        req.write2DB();
                        node.getRequirementList().add(req.getEntity());
                        reqCounter++;
                    }
                }
                node.write2DB();
            }
            p.getRequirementSpecList().add(temp.getEntity());
            temp.update();
        }
        return temp;
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
        int amount = 5;
        for (int i = 0; i < 5; i++) {
            //Add steps
            TestCaseServer tcs
                    = new TestCaseServer("Test Case #"
                            + (tcCounter++),
                            new Date());
            tcs.write2DB();
            for (int j = 0; j < amount; j++) {
                tcs.addStep((j + 1), "Step #" + (j + 1), "Note",
                        "Criteria",
                        new ArrayList<>());
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
            ess.setStepHistory(ess.getStep().getHistoryList().get(0));
            ess.assignUser(r.nextBoolean() ? tester1.getEntity()
                    : tester2.getEntity(), assigner.getEntity());
        });
    }
}
