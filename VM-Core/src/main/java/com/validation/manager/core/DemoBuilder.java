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
package com.validation.manager.core;

import com.validation.manager.core.db.FailureMode;
import com.validation.manager.core.db.Hazard;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RiskCategory;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.controller.FailureModeJpaController;
import com.validation.manager.core.db.controller.HazardJpaController;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.RiskCategoryJpaController;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.DataEntryServer;
import com.validation.manager.core.server.core.ExecutionStepServer;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.ProjectTypeServer;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.server.core.RequirementSpecNodeServer;
import com.validation.manager.core.server.core.RequirementSpecServer;
import com.validation.manager.core.server.core.StepServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.TestCaseTypeServer;
import com.validation.manager.core.server.core.TestPlanServer;
import com.validation.manager.core.server.core.TestProjectServer;
import com.validation.manager.core.server.core.VMUserServer;
import com.validation.manager.core.server.fmea.FMEAServer;
import com.validation.manager.core.server.fmea.FailureModeServer;
import com.validation.manager.core.server.fmea.HazardServer;
import com.validation.manager.core.server.fmea.RiskCategoryServer;
import com.validation.manager.core.server.fmea.RiskItemServer;
import com.validation.manager.core.tool.Tool;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DemoBuilder {

    private static final Logger LOG
            = Logger.getLogger(DemoBuilder.class.getSimpleName());
    private static int tcCounter = 1, tpCounter, reqCounter;
    private static final List<RiskCategory> RISK_CATEGORIES = new ArrayList<>();
    private static final List<Hazard> HAZARDS = new ArrayList<>();
    private static final List<FailureMode> FAILURE_MODES = new ArrayList<>();

    public static void buildDemoProject() throws Exception {
        LOG.info("Creating demo projects...");
        RiskCategoryServer rcs;
        rcs = new RiskCategoryServer("Severity", 1, 5);
        rcs.write2DB();
        RISK_CATEGORIES.add(new RiskCategoryJpaController(DataBaseManager
                .getEntityManagerFactory()).findRiskCategory(rcs.getId()));
        rcs = new RiskCategoryServer("Occurence", 1, 5);
        rcs.write2DB();
        RISK_CATEGORIES.add(new RiskCategoryJpaController(DataBaseManager
                .getEntityManagerFactory()).findRiskCategory(rcs.getId()));
        rcs = new RiskCategoryServer("Detectability", 1, 5);
        rcs.write2DB();
        RISK_CATEGORIES.add(new RiskCategoryJpaController(DataBaseManager
                .getEntityManagerFactory()).findRiskCategory(rcs.getId()));

        for (int i = 0; i < 5; i++) {
            HazardServer hs = new HazardServer("Hazard " + i,
                    "Hazard Description " + i);
            hs.write2DB();
            HAZARDS.add(new HazardJpaController(DataBaseManager
                    .getEntityManagerFactory()).findHazard(hs.getId()));
        }

        System.out.println("Create Failure Modes");
        for (int i = 0; i < 5; i++) {
            FailureModeServer fm = new FailureModeServer("Failure Mode "
                    + i, "Failure Mode Desc " + i);
            fm.write2DB();
            FAILURE_MODES.add(new FailureModeJpaController(DataBaseManager
                    .getEntityManagerFactory()).findFailureMode(fm.getId()));
        }

        ProjectServer rootProject = new ProjectServer("Demo", "Demo project",
                new ProjectTypeServer(1).getEntity());
        rootProject.write2DB();
        for (int i = 0; i < 2; i++) {
            ProjectServer ps = new ProjectServer("Sub " + (i + 1),
                    "Demo project",
                    new ProjectTypeServer(1).getEntity());
            ps.setParentProjectId(rootProject.getEntity());
            ps.write2DB();
            addDemoProjectRequirements(ps.getEntity());
            addDemoProjectTestProject(ps.getEntity());
            addRiskManagement(ps.getEntity());
        }
        rootProject.update();
        //Link requirements with steps
        List<Requirement> requirements
                = new RequirementJpaController(DataBaseManager
                        .getEntityManagerFactory())
                        .findRequirementEntities();
        LOG.log(Level.FINE, "Total Requirements: {0}", requirements.size());
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
        for (Project p : ProjectServer.getProjects()) {
            if (p.getParentProjectId() != null) {
                addDemoExecution(p);
            }
        }
        addRiskManagement(rootProject.getEntity());
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
                        String header;
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
                    = new TestCaseServer("Test Case #" + (tcCounter++),
                            new Date(),
                            new TestCaseTypeServer(5).getEntity());
            tcs.write2DB();
            List<Requirement> reqs = Tool.extractRequirements(p);
            Random r = new Random();
            for (int j = 0; j < amount; j++) {
                int min = r.nextInt(reqs.size());
                int max = min == reqs.size() - 1 ? min
                        : min + r.nextInt(reqs.size() - min);
                tcs.addStep((j + 1), "Step #" + (j + 1), "Note",
                        "Criteria",
                        reqs.subList(min, max),
                        Arrays.asList(DataEntryServer
                                .getStringField("Text",
                                        r.nextBoolean() ? "Hello" : null,
                                        r.nextBoolean()),
                                DataEntryServer.getBooleanField("Boolean",
                                        r.nextBoolean()),
                                DataEntryServer.getNumericField("Numeric",
                                        10f, 20f),
                                DataEntryServer.getAttachmentField("Attachment")));
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

    /**
     * Create demo Risk management
     *
     * @param p Project to add the risk management memo data.
     */
    private static void addRiskManagement(Project p) {
        try {
            FMEAServer fmea = new FMEAServer("Demo FMEA", p);
            fmea.write2DB();
            for (int i = 0; i < 5; i++) {
                RiskItemServer ri
                        = new RiskItemServer(fmea.getFmeaPK(),
                                "Desc " + (i + 1));
                ri.write2DB();
                ri.setRiskCategoryList(new ArrayList<>());
                RISK_CATEGORIES.forEach(rc -> {
                    ri.getRiskCategoryList().add(rc);
                });
                ri.setHazardList(new ArrayList<>());
                for (int j = 0; j < (i + 1); j++) {
                    Hazard hazard = HAZARDS.get(j);
                    ri.getHazardList().add(hazard);
                }
                ri.setFailureModeList(new ArrayList<>());
                for (int j = 0; j < (i + 1); j++) {
                    FailureMode fm = FAILURE_MODES.get(j);
                    ri.getFailureModeList().add(fm);
                }
                ri.write2DB();
            }
        }
        catch (NonexistentEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
