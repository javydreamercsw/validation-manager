package com.validation.manager.core.server.core;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author jortiz00
 */
public class ProjectServerTest extends AbstractVMTestCase {

    public ProjectServerTest() {
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
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                fail();
            }
            assertTrue(ProjectServer.getRequirements(root).isEmpty());
            RequirementSpec mainSpec
                    = TestHelper.createRequirementSpec("Spec", "Desc", root, 1);
            assertTrue(!root.getRequirementSpecList().isEmpty());
            assertEquals(0, ProjectServer.getRequirements(root).size());
            sub = TestHelper.addProject(root, "Sub", "Notes");
            System.out.println("Create Spec for sub project.");
            TestHelper.createRequirementSpec("Spec 2", "Desc", sub, 1);
            RequirementSpecNode node = TestHelper.createRequirementSpecNode(mainSpec,
                    "Requirement Doc", "Desc", "Scope");
            Requirement req1 = TestHelper.createRequirement("REQ-001", "Desc", node.getRequirementSpecNodePK(), "Notes", 1, 1);
            assertEquals(1, ProjectServer.getRequirements(root).size());
            Requirement req2 = TestHelper.createRequirement("REQ-002", "Desc", node.getRequirementSpecNodePK(), "Notes", 1, 1);
            assertEquals(2, ProjectServer.getRequirements(root).size());
            req1 = TestHelper.addChildToRequirement(req1, req2);
            assertEquals(1, RequirementServer.getChildrenRequirement(req1).size());
            assertEquals(0, RequirementServer.getParentRequirement(req1).size());
            assertEquals(1, RequirementServer.getParentRequirement(req2).size());
            assertEquals(0, RequirementServer.getChildrenRequirement(req2).size());
            try {
                ProjectServer.deleteProject(sub);
            } catch (Exception ex) {
                //Expected failure
                System.out.println("Expected failure!");
                Exceptions.printStackTrace(ex);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
