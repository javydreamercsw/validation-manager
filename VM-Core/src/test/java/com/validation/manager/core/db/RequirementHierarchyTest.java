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

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.ProjectTypeServer;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.server.core.RequirementSpecNodeServer;
import com.validation.manager.core.server.core.RequirementSpecServer;
import com.validation.manager.core.server.core.SpecLevelServer;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import static com.validation.manager.test.TestHelper.createRequirement;
import static com.validation.manager.test.TestHelper.createRequirementSpecNode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RequirementHierarchyTest extends AbstractVMTestCase {

    private static final Logger LOG
            = getLogger(RequirementHierarchyTest.class.getName());
    private ProjectServer project;

    @Test
    public void testHierarchy() {
        System.out.println("Test Requirement Hierarchy");
        System.out.println("Create Project");
        project = new ProjectServer("Test product", "Project Notes",
                new ProjectTypeServer(1).getEntity());
        try {
            project.write2DB();
        }
        catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Create Requirement Spec");
        RequirementSpec rss = null;
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
            RequirementSpecNode rsns = createRequirementSpecNode(
                    rss, "Test", "Test", "Test");
            System.out.println("Create Requirements");
            List<Requirement> requirements = new ArrayList<>();
            int requirementAmount = 15;
            for (int i = 0; i < requirementAmount; i++) {
                try {
                    Requirement reqs
                            = createRequirement("SRS-SW-00" + i,
                                    "Description " + i,
                                    rsns.getRequirementSpecNodePK(),
                                    "Notes " + i, 2, 1);
                    requirements.add(new RequirementJpaController(
                            getEntityManagerFactory())
                            .findRequirement(reqs.getId()));
                }
                catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    fail();
                }
            }
            System.out.println("Done!");
            System.out.println("Create Requirement Spec Levels");
            SpecLevelServer userSpecLevel = createSpecLevel("User Requirement");
            SpecLevelServer functionalSpecLevel
                    = createSpecLevel("Functional Requirement");
            SpecLevelServer unitSpecLevel = createSpecLevel("Unit Requirement");
            System.out.println("Done!");
            System.out.println("Create Requirement Specs");
            RequirementSpecServer productSpec = createRequirementSpec(
                    "Project Spec", userSpecLevel);
            RequirementSpecServer archSpec = createRequirementSpec(
                    "Architectural Spec", functionalSpecLevel);
            RequirementSpecServer swSpec = createRequirementSpec(
                    "Software Spec", unitSpecLevel);
            System.out.println("Done!");
            System.out.println("Adding requirements to spec nodes");
            try {
                RequirementSpecNodeServer n1
                        = productSpec.addSpecNode("Node 1", "description", "scope");
                requirements.subList(0, 5).forEach((r) -> {
                    n1.getRequirementList().add(r);
                });
                n1.write2DB();
                assertEquals(1, productSpec.getRequirementSpecNodeList().size());
                RequirementSpecNodeServer n2
                        = archSpec.addSpecNode("Node 2", "description", "scope");
                requirements.subList(5, 10).forEach((r) -> {
                    n2.getRequirementList().add(r);
                });
                n2.write2DB();
                assertEquals(1, archSpec.getRequirementSpecNodeList().size());
                RequirementSpecNodeServer n3
                        = swSpec.addSpecNode("Node 3", "description", "scope");
                requirements.subList(10, 15).forEach((r) -> {
                    n3.getRequirementList().add(r);
                });
                n3.write2DB();
                assertEquals(1, swSpec.getRequirementSpecNodeList().size());
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            System.out.println("Done!");
            System.out.println("Adding requirements to requirements");
            int count = 0;
            for (Requirement req : requirements) {
                try {
                    req.getRequirementList().add(requirements
                            .get(requirementAmount - count - 1));
                    new RequirementServer(req).write2DB();
                    count++;
                }
                catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    fail();
                }
            }
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Done!");
    }

    private SpecLevelServer createSpecLevel(String name) {
        SpecLevelServer sl = new SpecLevelServer(name, name + " Level");
        try {
            assertTrue(sl.write2DB() > 0);
        }
        catch (IllegalOrphanException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        catch (NonexistentEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        return sl;
    }

    private RequirementSpecServer createRequirementSpec(String name,
            SpecLevelServer sl) {
        RequirementSpecServer rss = new RequirementSpecServer(name,
                "description", project.getId(), sl.getId());
        try {
            rss.write2DB();
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        return rss;
    }
}
