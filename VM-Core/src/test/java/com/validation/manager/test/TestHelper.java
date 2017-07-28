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
package com.validation.manager.test;

import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementSpecNodePK;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.ProjectTypeServer;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.server.core.RequirementSpecNodeServer;
import com.validation.manager.core.server.core.RequirementSpecServer;
import com.validation.manager.core.server.core.StepServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.TestCaseTypeServer;
import com.validation.manager.core.server.core.TestPlanServer;
import com.validation.manager.core.server.core.TestProjectServer;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TestHelper {

    private static final Logger LOG
            = Logger.getLogger(TestHelper.class.getName());

    public static Project createProject(String name, String notes) {
        ProjectServer ps = new ProjectServer(name, notes,
                new ProjectTypeServer(1).getEntity());
        try {
            ps.write2DB();
        }
        catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        assertTrue(ps.getEntity() != null);
        return ps.getEntity();
    }

    public static TestCase createTestCase(String name, String summary)
            throws PreexistingEntityException, Exception {
        TestCaseServer tc = new TestCaseServer(name, new Date(),
                new TestCaseTypeServer(5).getEntity());
        tc.setRiskControlHasTestCaseList(new ArrayList<>());
        tc.setActive(true);
        tc.setIsOpen(true);
        tc.setSummary(summary.getBytes());
        tc.setTestCaseType(new TestCaseTypeServer(5).getEntity());
        tc.write2DB();
        return tc.getEntity();
    }

    public static Requirement createRequirement(String id, String desc,
            RequirementSpecNodePK p, String notes, int requirementType,
            int requirementStatus) throws Exception {
        RequirementServer req = new RequirementServer(id, desc, p, notes,
                requirementType, requirementStatus);
        req.write2DB();
        assert req.getEntity().getRequirementSpecNode() != null;
        return req.getEntity();
    }

    public static TestCase addStep(TestCase tc, int sequence,
            String text, String note, String result) throws PreexistingEntityException,
            Exception {
        TestCaseServer tcs = new TestCaseServer(tc.getTestCasePK());
        int amount = tcs.getStepList().size();
        tcs.addStep(sequence, text, note, note, null);
        assertEquals(amount + 1, tcs.getStepList().size());
        return tcs.getEntity();
    }

    public static TestProject createTestProject(String name)
            throws IllegalOrphanException, NonexistentEntityException,
            Exception {
        TestProjectServer tps = new TestProjectServer("Test Project", true);
        tps.write2DB();
        return tps.getEntity();
    }

    public static TestPlan createTestPlan(TestProject tp, String notes,
            boolean active, boolean open) throws PreexistingEntityException,
            Exception {
        TestPlanServer plan = new TestPlanServer(tp, active, open);
        plan.setNotes(notes);
        plan.setTestProject(tp);
        plan.write2DB();
        tp.getTestPlanList().add(plan.getEntity());
        return plan.getEntity();
    }

    public static void addTestCaseToPlan(TestPlan plan, TestCase testCase)
            throws PreexistingEntityException, Exception {
        int testInPlan = plan.getTestCaseList().size();
        TestPlanServer tps = new TestPlanServer(plan);
        tps.getTestCaseList().add(testCase);
        tps.write2DB();
        tps.update(plan, tps);
        testCase.getTestPlanList().add(plan);
        assertTrue(plan.getTestCaseList().size() > testInPlan);
    }

    public static RequirementSpec createRequirementSpec(String name,
            String description, Project project, int specLevelId)
            throws Exception {
        RequirementSpecServer rss = new RequirementSpecServer(name, description,
                project.getId(), specLevelId);
        rss.write2DB();
        project.getRequirementSpecList().add(rss.getEntity());
        new ProjectServer(project).write2DB();
        return rss.getEntity();
    }

    public static RequirementSpecNode createRequirementSpecNode(
            RequirementSpec rss, String name, String description, String scope)
            throws Exception {
        RequirementSpecNodeServer rsns = new RequirementSpecNodeServer(rss,
                name, description, scope);
        rsns.write2DB();
        return rsns.getEntity();
    }

    public static void addTestProjectToProject(TestProject tp, Project project)
            throws IllegalOrphanException, NonexistentEntityException,
            Exception {
        ProjectServer ps = new ProjectServer(project);
        int current = ps.getTestProjectList().size();
        ps.getTestProjectList().add(tp);
        ps.write2DB();
        assertTrue(ps.getEntity().getTestProjectList().size() > current);
        tp.getProjectList().add(project);
    }

    public static void addRequirementToStep(Step step, Requirement req)
            throws Exception {
        StepServer ss = new StepServer(step);
        ss.addRequirement(req);
        ss.write2DB();
    }

    public static Project addProject(Project root, String name, String notes)
            throws NonexistentEntityException, Exception {
        Project sub = createProject(name, notes);
        ProjectServer ps = new ProjectServer(root);
        ps.getProjectList().add(sub);
        ps.write2DB();
        return new ProjectServer(sub).getEntity();
    }
}
