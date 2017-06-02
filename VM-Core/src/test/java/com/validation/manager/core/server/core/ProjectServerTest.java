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
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.tool.Tool;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import org.junit.Test;
import org.openide.util.Exceptions;
import static org.openide.util.Exceptions.printStackTrace;

/**
 *
 * @author jortiz00
 */
public class ProjectServerTest extends AbstractVMTestCase {

    private Project p;
    private static final Logger LOG
            = Logger.getLogger(ProjectServerTest.class.getSimpleName());

    @Override
    protected void postSetUp() {
        try {
            RequirementSpec rss = null;
            p = TestHelper.createProject("New Project", "Notes");
            ProjectServer project = new ProjectServer(p);
            project.setNotes("Notes 2");
            project.write2DB();
            assertTrue(new ProjectJpaController(
                    DataBaseManager.getEntityManagerFactory())
                    .findProject(project.getId()).getNotes().equals(project.getNotes()));
            //Create requirements
            System.out.println("Create Requirement Spec");
            try {
                rss = TestHelper.createRequirementSpec("Test", "Test",
                        project, 1);
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            System.out.println("Create Requirement Spec Node");
            try {
                TestHelper.createRequirementSpecNode(
                        rss, "Test", "Test", "Test");
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        catch (NonexistentEntityException ex) {
            Exceptions.printStackTrace(ex);
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Test of create and deleteProject method, of class ProjectServer.
     */
    @Test
    public void testCreateAndDeleteProject() {
        try {
            System.out.println("Create a project");
            Project root = TestHelper.createProject("Test", "Notes");
            System.out.println("Add a sub project");
            Project sub = TestHelper.addProject(root, "Sub", "Notes");
            System.out.println("Create Spec for main project.");
            //No errors, nothing dependent
            try {
                ProjectServer.deleteProject(sub);
            }
            catch (VMException ex) {
                printStackTrace(ex);
                fail();
            }
            assertTrue(Tool.extractRequirements(root).isEmpty());
            RequirementSpec mainSpec
                    = TestHelper.createRequirementSpec("Spec", "Desc", root, 1);
            assertTrue(!root.getRequirementSpecList().isEmpty());
            assertEquals(0, Tool.extractRequirements(root).size());
            sub = TestHelper.addProject(root, "Sub", "Notes");
            System.out.println("Create Spec for sub project.");
            RequirementSpec spec = TestHelper.createRequirementSpec("Spec 2",
                    "Desc", sub, 1);
            RequirementSpecNode node = TestHelper.createRequirementSpecNode(mainSpec,
                    "Requirement Doc", "Desc", "Scope");
            Requirement req1 = TestHelper.createRequirement("REQ-001", "Desc",
                    node.getRequirementSpecNodePK(), "Notes", 1, 1);
            assertEquals(1, Tool.extractRequirements(root).size());
            Requirement req2 = TestHelper.createRequirement("REQ-002", "Desc",
                    node.getRequirementSpecNodePK(), "Notes", 1, 1);
            assertEquals(2, Tool.extractRequirements(root).size());
            TestHelper.addChildToRequirement(req1, req2);
            assertEquals(1, new ProjectServer(root).getChildren().size());
            try {
                ProjectServer.deleteProject(sub);
            }
            catch (VMException ex) {
                //Expected failure
                System.out.println("Expected failure!");
            }
            RequirementSpecServer.deleteRequirementSpec(spec);
            try {
                ProjectServer.deleteProject(new ProjectServer(sub).getEntity());
            }
            catch (VMException ex) {
                printStackTrace(ex);
                fail();
            }
        }
        catch (Exception ex) {
            printStackTrace(ex);
            fail();
        }
    }

    @Test
    public void testExtractTestProjects() {
        try {
            System.out.println("Add Test Project");
            TestHelper.addTestProjectToProject(TestHelper
                    .createTestProject("Test"), p);
            for (int i = 0; i < 5; i++) {
                Project sub = TestHelper.addProject(p, "Sub #" + i,
                        "Notes #" + i);
                TestHelper.addTestProjectToProject(TestHelper
                        .createTestProject("Test"), sub);
            }
            ProjectServer ps = new ProjectServer(p);
            assertEquals(1, ps.getTestProjects(false).size());
            assertEquals(1 + ps.getProjectList().size(),
                    ps.getTestProjects(true).size());
        }
        catch (NonexistentEntityException ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
