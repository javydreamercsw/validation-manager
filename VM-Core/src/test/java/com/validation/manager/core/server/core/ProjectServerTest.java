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
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import org.junit.Test;

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
        catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
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
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
