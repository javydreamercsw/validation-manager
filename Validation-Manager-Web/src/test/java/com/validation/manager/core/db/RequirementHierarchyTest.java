/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.server.core.RequirementSpecServer;
import com.validation.manager.core.server.core.SpecLevelServer;
import com.validation.manager.test.AbstractVMTestCase;
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

    private static final Logger LOG = Logger.getLogger(RequirementHierarchyTest.class.getName());
    private ProjectServer product;

    @Test
    public void testHierarchy() {
        System.out.println("Test Requirement Hierarchy");
        System.out.println("Create Project");
        product = new ProjectServer("Test product", "Project Notes");
        try {
            product.write2DB();
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
        System.out.println("Create Requirements");
        List<Requirement> requirements = new ArrayList<Requirement>();
        int requirementAmount = 15;
        for (int i = 0; i < requirementAmount; i++) {
            RequirementServer reqs = new RequirementServer("SRS-SW-00" + i, "Description " + i,
                    new ProjectJpaController( DataBaseManager.getEntityManagerFactory()).findProject(product.getId()),
                    "Notes " + i, 2, 1);
            try {
                reqs.write2DB();
                requirements.add(new RequirementJpaController( DataBaseManager.getEntityManagerFactory()).findRequirement(reqs.getRequirementPK()));
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        System.out.println("Done!");
        System.out.println("Create Requirement Spec Levels");
        SpecLevelServer userSpecLevel = createSpecLevel("User Requirement");
        SpecLevelServer functionalSpecLevel = createSpecLevel("Functional Requirement");
        SpecLevelServer unitSpecLevel = createSpecLevel("Unit Requirement");
        System.out.println("Done!");
        System.out.println("Create Requirement Specs");
        RequirementSpecServer productSpec = createRequirementSpec("Project Spec", userSpecLevel);
        RequirementSpecServer archSpec = createRequirementSpec("Architectural Spec", functionalSpecLevel);
        RequirementSpecServer swSpec = createRequirementSpec("Software Spec", unitSpecLevel);
        System.out.println("Done!");
        System.out.println("Adding requirements to spec nodes");
        try {
            productSpec.addSpecNode("Node 1", "description", "scope", requirements.subList(0, 5));
            assertEquals(1, productSpec.getRequirementSpecNodeList().size());
            assertEquals(5, productSpec.getRequirementSpecNodeList().get(0).getRequirementList().size());
            archSpec.addSpecNode("Node 2", "description", "scope", requirements.subList(5, 10));
            assertEquals(1, archSpec.getRequirementSpecNodeList().size());
            assertEquals(5, archSpec.getRequirementSpecNodeList().get(0).getRequirementList().size());
            swSpec.addSpecNode("Node 3", "description", "scope", requirements.subList(10, 15));
            assertEquals(1, swSpec.getRequirementSpecNodeList().size());
            assertEquals(5, swSpec.getRequirementSpecNodeList().get(0).getRequirementList().size());
        } catch (Exception ex) {
            Logger.getLogger(RequirementHierarchyTest.class.getName()).log(Level.SEVERE, null, ex);
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
        RequirementSpecServer rss = new RequirementSpecServer(name, "description", product.getId(), sl.getId());
        try {
            rss.write2DB();
        } catch (Exception ex) {
            Logger.getLogger(RequirementHierarchyTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        return rss;
    }
}
