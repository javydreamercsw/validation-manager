package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementServerTest extends AbstractVMTestCase {

    private static final Logger LOG
            = Logger.getLogger(RequirementServerTest.class.getName());
    private Project p;
    private RequirementSpec rss;
    private RequirementSpecNode rsns;

    private void prepare() throws Exception {
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
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Create Requirement Spec Node");
        try {
            rsns = TestHelper.createRequirementSpecNode(
                    rss, "Test", "Test", "Test");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of deleteRequirement method, of class RequirementServer.
     */
    @Test
    public void testDeleteRequirement() {
        try {
            System.out.println("deleteRequirement");
            prepare();
            Requirement r = TestHelper.createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            RequirementServer.deleteRequirement(r);
            parameters.clear();
            parameters.put("id", r.getRequirementPK().getId());
            assertTrue(DataBaseManager.namedQuery("Requirement.findById", parameters).isEmpty());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of update method, of class RequirementServer.
     */
    @Test
    public void testUpdate() {
        try {
            System.out.println("update");
            prepare();
            Requirement source = TestHelper.createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            RequirementServer instance = new RequirementServer(source);
            Requirement target = new Requirement();
            instance.update(target, source);
            assertEquals(instance.getUniqueId(), target.getUniqueId());
            assertEquals(instance.getDescription(), target.getDescription());
            assertEquals(instance.getNotes(), target.getNotes());
            assertEquals(instance.getRequirementTypeId().getId(), target.getRequirementTypeId().getId());
            assertEquals(instance.getRequirementStatusId().getId(), target.getRequirementStatusId().getId());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of isDuplicate method, of class RequirementServer.
     */
    @Test
    public void testIsDuplicate() {
        try {
            System.out.println("isDuplicate");
            prepare();
            Requirement source = TestHelper.createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            Requirement source2 = TestHelper.createRequirement("SRS-SW-0002",
                    "Sample requirement", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            assertFalse(RequirementServer.isDuplicate(source));
            assertFalse(RequirementServer.isDuplicate(source2));
            Requirement source3 = TestHelper.createRequirement("SRS-SW-0002",
                    "Sample requirement", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            assertEquals(true, RequirementServer.isDuplicate(source3));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of getTestCoverage method, of class RequirementServer.
     */
    @Test
    public void testGetTestCoverage() {
        try {
            System.out.println("getTestCoverage");
            prepare();
            Requirement req = TestHelper.createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            Requirement req2 = TestHelper.createRequirement("SRS-SW-0002",
                    "Sample requirement", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            Requirement req3 = TestHelper.createRequirement("SRS-SW-0003",
                    "Sample requirement", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            Requirement req4 = TestHelper.createRequirement("SRS-SW-0004",
                    "Sample requirement", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            RequirementServer rs = new RequirementServer(req);
            assertEquals(0, rs.getTestCoverage());
            //Add a test case
            com.validation.manager.core.db.Test test
                    = TestHelper.createTest("Test", "Purpose", "Scope");
            //Create Test Case
            TestCase tc = TestHelper.createTestCase("Dummy", new Short("1"),
                    "Expected Results", test, /*user,*/ "Summary");
            //Add steps
            int i = 1;
            for (; i < 6; i++) {
                LOG.log(Level.INFO, "Adding step: {0}", i);
                tc = TestHelper.addStep(tc, i, "Step " + i, "Note " + i);
                Step step = tc.getStepList().get(i - 1);
                TestHelper.addRequirementToStep(step, req);
                new TestCaseServer(tc).write2DB();
                assertEquals(1, new StepServer(step).getRequirementList().size());
            }
            LOG.log(Level.INFO, "Adding step: {0}", i);
            tc = TestHelper.addStep(tc, i, "Step " + i, "Note " + i);
            Step step = tc.getStepList().get(i - 1);
            TestHelper.addRequirementToStep(step, req3);
            new TestCaseServer(tc).write2DB();
            //Create test Project
            TestProject tp = TestHelper.createTestProject("Test Project");
            //Create test plan
            TestPlan plan = TestHelper.createTestPlan(tp, "Notes", true, true);
            //Add test case to test
            TestHelper.addTestCaseToTest(test, tc);
            //Add test to plan
            TestHelper.addTestToPlan(plan, test);
            rs.update();
            assertEquals(100, rs.getTestCoverage());
            //Add a related requirement
            rs.getRequirementList().add(req3);
            rs.write2DB();
            assertEquals(100, rs.getTestCoverage());
            rs.getRequirementList().add(req2);
            rs.write2DB();
            assertEquals(50, rs.getTestCoverage());
            rs.getRequirementList().add(req4);
            rs.write2DB();
            assertEquals(33, rs.getTestCoverage());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of getChildrenRequirement method, of class RequirementServer.
     */
    @Test
    public void testChildAndParentRequirements() {
        try {
            System.out.println("Child And Parent Requirements");
            prepare();
            List<Requirement> children;
            Requirement req = TestHelper.createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            Requirement req2 = TestHelper.createRequirement("SRS-SW-0002",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            children = RequirementServer.getChildrenRequirement(req);
            assertTrue(children.isEmpty());
            //Add a child
            RequirementServer rs = new RequirementServer(req);
            rs.addChildRequirement(req2);
            for (Requirement r : rs.getRequirementList()) {
                LOG.info(r.getUniqueId());
            }
            assertEquals(1, rs.getRequirementList().size());
            for (Requirement r : rs.getRequirementList1()) {
                LOG.info(r.getUniqueId());
            }
            assertEquals(0, rs.getRequirementList1().size());
            RequirementServer rs2 = new RequirementServer(req2);
            for (Requirement r : rs2.getRequirementList()) {
                LOG.info(r.getUniqueId());
            }
            assertEquals(0, rs2.getRequirementList().size());
            for (Requirement r : rs2.getRequirementList1()) {
                LOG.info(r.getUniqueId());
            }
            assertEquals(1, rs2.getRequirementList1().size());
            assertEquals(1, RequirementServer.getChildrenRequirement(req).size());
            assertEquals(0, RequirementServer.getParentRequirement(req).size());
            assertEquals(0, RequirementServer.getChildrenRequirement(req2).size());
            assertEquals(1, RequirementServer.getParentRequirement(req2).size());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of getChildrenRequirement method, of class RequirementServer.
     */
    @Test
    public void testRequirementVersioning() {
        try {
            System.out.println("getTestCoverage");
            prepare();
            String first = "Sample requirement", second = "Updated";
            Requirement req = TestHelper.createRequirement("SRS-SW-0001",
                    first, rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            RequirementServer rs = new RequirementServer(req);
            assertEquals(0, rs.getRequirementPK().getMajorVersion());
            assertEquals(0, rs.getRequirementPK().getMidVersion());
            assertEquals(0, rs.getRequirementPK().getMinorVersion());
            assertEquals(1, rs.getRequirementVersions().size());
            assertEquals(first, rs.getDescription());
            rs.setDescription(second);
            rs.updateMajorVersion(rs.getRequirementPK().getMinorVersion() + 1);
            rs.updateMidVersion(rs.getRequirementPK().getMinorVersion() + 2);
            rs.updateMinorVersion(rs.getRequirementPK().getMinorVersion() + 3);
            rs.write2DB();
            assertEquals(1, rs.getRequirementPK().getMajorVersion());
            assertEquals(2, rs.getRequirementPK().getMidVersion());
            assertEquals(3, rs.getRequirementPK().getMinorVersion());
            assertEquals(second, rs.getDescription());
            assertEquals(2, rs.getRequirementVersions().size());
            for (Requirement r : rs.getRequirementVersions()) {
                System.out.println(r);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
