package com.validation.manager.core.server.core;

import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import static com.validation.manager.core.server.core.ProjectServer.deleteProject;
import static com.validation.manager.core.server.core.ProjectServer.getRequirements;
import static com.validation.manager.core.server.core.RequirementServer.getChildrenRequirement;
import static com.validation.manager.core.server.core.RequirementServer.getParentRequirement;
import static com.validation.manager.core.server.core.RequirementSpecServer.deleteRequirementSpec;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import static com.validation.manager.test.TestHelper.addChildToRequirement;
import static com.validation.manager.test.TestHelper.addProject;
import static com.validation.manager.test.TestHelper.createProject;
import static com.validation.manager.test.TestHelper.createRequirement;
import static com.validation.manager.test.TestHelper.createRequirementSpec;
import static com.validation.manager.test.TestHelper.createRequirementSpecNode;
import org.junit.Test;
import org.openide.util.Exceptions;
import static org.openide.util.Exceptions.printStackTrace;

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
            Project root = createProject("Test", "Notes");
            System.out.println("Add a sub project");
            Project sub = addProject(root, "Sub", "Notes");
            System.out.println("Create Spec for main project.");
            //No errors, nothing dependent
            try {
                deleteProject(sub);
            } catch (Exception ex) {
                printStackTrace(ex);
                fail();
            }
            assertTrue(getRequirements(root).isEmpty());
            RequirementSpec mainSpec
                    = createRequirementSpec("Spec", "Desc", root, 1);
            assertTrue(!root.getRequirementSpecList().isEmpty());
            assertEquals(0, getRequirements(root).size());
            sub = addProject(root, "Sub", "Notes");
            System.out.println("Create Spec for sub project.");
            RequirementSpec spec = createRequirementSpec("Spec 2", "Desc", sub, 1);
            RequirementSpecNode node = createRequirementSpecNode(mainSpec,
                    "Requirement Doc", "Desc", "Scope");
            Requirement req1 = createRequirement("REQ-001", "Desc",
                    node.getRequirementSpecNodePK(), "Notes", 1, 1);
            assertEquals(1, getRequirements(root).size());
            Requirement req2 = createRequirement("REQ-002", "Desc",
                    node.getRequirementSpecNodePK(), "Notes", 1, 1);
            assertEquals(2, getRequirements(root).size());
            req1 = addChildToRequirement(req1, req2);
            assertEquals(1, getChildrenRequirement(req1).size());
            assertEquals(0, getParentRequirement(req1).size());
            assertEquals(1, getParentRequirement(req2).size());
            assertEquals(0, getChildrenRequirement(req2).size());
            try {
                deleteProject(sub);
            } catch (VMException ex) {
                //Expected failure
                System.out.println("Expected failure!");
            }
            deleteRequirementSpec(spec);
            try {
                deleteProject(new ProjectServer(sub).getEntity());
            } catch (VMException ex) {
                printStackTrace(ex);
                fail();
            }
        } catch (Exception ex) {
            printStackTrace(ex);
            fail();
        }
    }
}
