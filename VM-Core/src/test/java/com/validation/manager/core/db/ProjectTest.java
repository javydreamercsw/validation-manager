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
package com.validation.manager.core.db;

import com.validation.manager.core.DataBaseManager;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.TestPlanJpaController;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.StepServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.TestPlanServer;
import com.validation.manager.core.tool.Tool;
import com.validation.manager.test.AbstractVMTestCase;
import static com.validation.manager.test.TestHelper.addRequirementToStep;
import static com.validation.manager.test.TestHelper.addStep;
import static com.validation.manager.test.TestHelper.addTestCaseToPlan;
import static com.validation.manager.test.TestHelper.createProject;
import static com.validation.manager.test.TestHelper.createRequirement;
import static com.validation.manager.test.TestHelper.createRequirementSpec;
import static com.validation.manager.test.TestHelper.createRequirementSpecNode;
import static com.validation.manager.test.TestHelper.createTestCase;
import static com.validation.manager.test.TestHelper.createTestPlan;
import static com.validation.manager.test.TestHelper.createTestProject;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ProjectTest extends AbstractVMTestCase {

    private Project p;
    private static final Logger LOG
            = getLogger(ProjectTest.class.getName());

    /**
     * Test of toString method, of class Project.
     */
    @Test
    public void testCreateAndDestroy() {
        try {
            p = createProject("New Project", "Notes");
            assertEquals(0, Tool.extractRequirements(p).size());
            ProjectServer project = new ProjectServer(p);
            project.setNotes("Notes 2");
            project.write2DB();
            Project temp = new ProjectJpaController(
                    getEntityManagerFactory())
                    .findProject(project.getId());
            assertTrue(temp.getNotes().equals(project.getNotes()));
            //Create requirements
            LOG.info("Create Requirement Spec");
            RequirementSpec rss = null;
            try {
                rss = createRequirementSpec("Test", "Test",
                        project, 1);
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            LOG.info("Create Requirement Spec Node");
            RequirementSpecNode rsns;
            try {
                rsns = createRequirementSpecNode(
                        rss, "Test", "Test", "Test");
                Requirement r = createRequirement("SRS-SW-0001",
                        "Sample requirement", rsns.getRequirementSpecNodePK(),
                        "Notes", 1, 1);
                //Create Test Case
                TestCase tc = createTestCase("Dummy", "Summary");
                //Add steps
                for (int i = 1; i < 6; i++) {
                    LOG.info(MessageFormat.format("Adding step: {0}", i));
                    tc = addStep(tc, i, "Step " + i, "Note " + i, "Result " + i);
                    Step step = tc.getStepList().get(i - 1);
                    addRequirementToStep(step, r);
                    new TestCaseServer(tc).write2DB();
                    assertEquals(1, new StepServer(step).getRequirementList().size());
                }
                //Create test Project
                TestProject tp = createTestProject("Test Project");
                //Create test plan
                TestPlan plan = createTestPlan(tp, "Notes", true, true);
                //Add test to plan
                addTestCaseToPlan(plan, tc);
                project.write2DB();
                assertEquals(1, Tool.extractRequirements(project.getEntity()).size());
                TestPlanServer.deleteTestPlan(plan);
                assertNull(new TestPlanJpaController(DataBaseManager
                        .getEntityManagerFactory()).findTestPlan(plan.getTestPlanPK()));
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
