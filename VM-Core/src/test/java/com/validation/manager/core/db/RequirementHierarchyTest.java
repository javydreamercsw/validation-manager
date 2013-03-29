package com.validation.manager.core.db;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementSpecServer;
import com.validation.manager.core.server.core.SpecLevelServer;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementHierarchyTest extends AbstractVMTestCase {

    private static final Logger LOG =
            Logger.getLogger(RequirementHierarchyTest.class.getName());
    private ProjectServer project;

    @Test
    public void testHierarchy() {
        System.out.println("Test Requirement Hierarchy");
        System.out.println("Create Project");
        project = new ProjectServer("Test product", "Project Notes");
        try {
            project.write2DB();
        } catch (IllegalOrphanException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        } catch (NonexistentEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Create Requirement Spec");
        RequirementSpec rss = null;
        try {
            rss = TestHelper.createRequirementSpec("Test", "Test",
                    project, 1);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Create Requirement Spec Node");
        RequirementSpecNode rsns = null;
        try {
            rsns = TestHelper.createRequirementSpecNode(
                    rss, "Test", "Test", "Test");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Create Requirements");
        List<Requirement> requirements = new ArrayList<Requirement>();
        int requirementAmount = 15;
        for (int i = 0; i < requirementAmount; i++) {
            try {
                Requirement reqs =
                        TestHelper.createRequirement("SRS-SW-00" + i,
                        "Description " + i,
                        rsns.getRequirementSpecNodePK(),
                        "Notes " + i, 2, 1);
                requirements.add(new RequirementJpaController(
                        DataBaseManager.getEntityManagerFactory())
                        .findRequirement(reqs.getRequirementPK()));
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        System.out.println("Done!");
        System.out.println("Create Requirement Spec Levels");
        SpecLevelServer userSpecLevel = createSpecLevel("User Requirement");
        SpecLevelServer functionalSpecLevel =
                createSpecLevel("Functional Requirement");
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
            productSpec.addSpecNode("Node 1", "description", "scope",
                    requirements.subList(0, 5));
            assertEquals(1, productSpec.getRequirementSpecNodeList().size());
            archSpec.addSpecNode("Node 2", "description", "scope",
                    requirements.subList(5, 10));
            assertEquals(1, archSpec.getRequirementSpecNodeList().size());
            swSpec.addSpecNode("Node 3", "description", "scope",
                    requirements.subList(10, 15));
            assertEquals(1, swSpec.getRequirementSpecNodeList().size());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Done!");
    }

    private SpecLevelServer createSpecLevel(String name) {
        SpecLevelServer sl = new SpecLevelServer(name, name + " Level");
        try {
            assertTrue(sl.write2DB() > 0);
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(RequirementHierarchyTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(RequirementHierarchyTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (Exception ex) {
            Logger.getLogger(RequirementHierarchyTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        return sl;
    }

    private RequirementSpecServer createRequirementSpec(String name, SpecLevelServer sl) {
        RequirementSpecServer rss = new RequirementSpecServer(name, "description", project.getId(), sl.getId());
        try {
            rss.write2DB();
        } catch (Exception ex) {
            Logger.getLogger(RequirementHierarchyTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        return rss;
    }
}
