package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementServerTest extends AbstractVMTestCase {

    private static final Logger LOG
            = getLogger(RequirementServerTest.class.getName());
    private Project p;
    private RequirementSpecNode rsns;

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
                rsns = TestHelper.createRequirementSpecNode(
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
     * Test of update method, of class RequirementServer.
     */
    @Test
    public void testUpdate() {
        try {
            System.out.println("update");
            Requirement source = TestHelper.createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            RequirementServer instance = new RequirementServer(source);
            assertEquals(1, instance.getHistoryList().size());
            int count = 1;
            for (History h : instance.getHistoryList()) {
                assertEquals(0, h.getMajorVersion());
                assertEquals(0, h.getMidVersion());
                assertEquals(count, h.getMinorVersion());
                count++;
            }
            Requirement target = new Requirement();
            instance.update(target, source);
            assertEquals(instance.getUniqueId(), target.getUniqueId());
            assertEquals(instance.getDescription(), target.getDescription());
            assertEquals(instance.getNotes(), target.getNotes());
            assertEquals(instance.getRequirementTypeId().getId(),
                    target.getRequirementTypeId().getId());
            assertEquals(instance.getRequirementStatusId().getId(),
                    target.getRequirementStatusId().getId());
            assertEquals(instance.getHistoryList().size(),
                    target.getHistoryList().size());
        }
        catch (Exception ex) {
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
            Requirement source = TestHelper.createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            Requirement source2 = TestHelper.createRequirement("SRS-SW-0002",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            assertEquals(false, RequirementServer.isDuplicate(source));
            assertEquals(false, RequirementServer.isDuplicate(source2));
            Requirement source3 = TestHelper.createRequirement("SRS-SW-0002",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            assertEquals(true, RequirementServer.isDuplicate(source3));
        }
        catch (Exception ex) {
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
            Requirement req = TestHelper.createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            Requirement req2 = TestHelper.createRequirement("SRS-SW-0002",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            Requirement req3 = TestHelper.createRequirement("SRS-SW-0003",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            Requirement req4 = TestHelper.createRequirement("SRS-SW-0004",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            Requirement userReq = TestHelper.createRequirement("PS-SW-0001",
                    "Sample User requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            RequirementServer rs = new RequirementServer(req);
            assertEquals(0, rs.getTestCoverage());
            //Create Test Case
            TestCase tc = TestHelper.createTestCase("Dummy", "Summary");
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
            //Add test to plan
            TestHelper.addTestCaseToPlan(plan, tc);
            rs.update();
            assertEquals(100, rs.getTestCoverage());
            //Add a related requirement
            rs.addChildRequirement(req3);
            assertEquals(100, rs.getTestCoverage());
            rs.addChildRequirement(req2);
            assertEquals(50, rs.getTestCoverage());
            rs.addChildRequirement(req4);
            assertEquals(33, rs.getTestCoverage());
            //Add related requirements to test coverage
            RequirementServer ur = new RequirementServer(userReq);
            assertEquals(0, ur.getTestCoverage());
            ur.addChildRequirement(req);
            assertEquals(33, ur.getTestCoverage());
        }
        catch (Exception ex) {
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
            Requirement req = TestHelper.createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            Requirement req2 = TestHelper.createRequirement("SRS-SW-0002",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            assertTrue(req.getRequirementList1().isEmpty());
            //Add a child
            RequirementServer rs = new RequirementServer(req);
            //Add a version to the requirement
            DataBaseManager.setVersioningEnabled(true);
            rs.setDescription("Modified requirement");
            rs.write2DB();
            rs.addChildRequirement(req2);
            //Should have one children now
            rs.getRequirementList1().forEach((r) -> {
                LOG.info(r.getUniqueId());
            });
            assertEquals(1, rs.getRequirementList1().size());
            rs.getRequirementList().forEach((r) -> {
                LOG.info(r.getUniqueId());
            });
            //No parents
            assertEquals(0, rs.getRequirementList().size());
            RequirementServer rs2 = new RequirementServer(req2);
            rs2.getRequirementList().forEach((r) -> {
                LOG.info(r.getUniqueId());
            });
            //One parent
            assertEquals(1, rs2.getRequirementList().size());
            rs2.getRequirementList1().forEach((r) -> {
                LOG.info(r.getUniqueId());
            });
            //No children
            assertEquals(0, rs2.getRequirementList1().size());
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of requirement coverage with versioning method, of class
     * RequirementServer.
     */
    @Test
    public void testRequirementCoverage() {
        try {
            System.out.println("Requirement Coverage and Versioning");
            //Enable versioning
            DataBaseManager.setVersioningEnabled(true);
            Requirement req = TestHelper.createRequirement("SRS-SW-0001",
                    "Description", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            RequirementServer rs = new RequirementServer(req);
            //Add a test case covering this test case
            TestProject tp = TestHelper.createTestProject("Test Project");
            TestHelper.addTestProjectToProject(tp, p);
            TestPlan plan = TestHelper.createTestPlan(tp, "Plan", true, true);
            TestCase tc = TestHelper.createTestCase("TC #1",
                    "Summary");
            TestHelper.addTestCaseToPlan(plan, tc);
            TestCase step = TestHelper.addStep(tc, 1, "Test", "Test");
            rs.getStepList().add(step.getStepList().get(0));
            rs.write2DB();
            assertEquals(1, rs.getStepList().size());
            assertEquals(100, rs.getTestCoverage());
            //Update version and remove test coverage
            rs.setDescription("New version");
            rs.getStepList().clear();
            rs.write2DB();
            assertEquals(0, rs.getStepList().size());
            rs.update();
            assertEquals(0, rs.getTestCoverage());
            rs.setDescription("New version 2");
            StepServer ss = new StepServer(step.getStepList().get(0));
            ss.removeRequirement(req);
            ss.write2DB();
            ss.addRequirement(rs.getEntity());
            rs.update();
            rs.write2DB();
            assertEquals(1, rs.getStepList().size());
            assertEquals(100, rs.getTestCoverage());
            //Add a parent requirement
            Requirement r = TestHelper.createRequirement("PS-0001",
                    "Description", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            RequirementServer parent = new RequirementServer(r);
            parent.addChildRequirement(req);
            parent.write2DB();
            assertEquals(100, parent.getTestCoverage());
            assertEquals(100, rs.getTestCoverage());
            //Version parent
            parent.setDescription("Version 2");
            parent.write2DB();
            assertEquals(100, parent.getTestCoverage());
            assertEquals(100, rs.getTestCoverage());
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }

    /**
     * Test of Circular Dependencies detection method, of class
     * RequirementServer.
     */
    @Test
    public void testCircularDependencies() {
        try {
            Requirement req = TestHelper.createRequirement("SRS-SW-0001",
                    "Description", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            Requirement req2 = TestHelper.createRequirement("SRS-SW-0002",
                    "Description", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            RequirementServer r1 = new RequirementServer(req);
            RequirementServer r2 = new RequirementServer(req2);
            r1.addChildRequirement(req2);
            assertEquals(1, r1.getRequirementList1().size());
            assertEquals(0, r1.getRequirementList().size());
            //Get the change above from db.
            r2.update();
            r2.addChildRequirement(req);
            assertEquals(0, r2.getRequirementList1().size());
            assertEquals(1, r2.getRequirementList().size());
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }

    @Test
    public void testVersioning() {
        try {
            Requirement req = TestHelper.createRequirement("SRS-SW-0001",
                    "Description", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            RequirementServer rs = new RequirementServer(req);
            int historyCount = 1;
            assertEquals(historyCount++, rs.getHistoryList().size());
            History history = rs.getHistoryList().get(rs
                    .getHistoryList().size() - 1);
            assertEquals(0, history.getMajorVersion());
            assertEquals(0, history.getMidVersion());
            assertEquals(1, history.getMinorVersion());
            assertTrue(checkHistory(rs));
            rs.setDescription("desc 2");
            rs.write2DB();
            assertEquals(historyCount++, rs.getHistoryList().size());
            history = rs.getHistoryList().get(rs.getHistoryList().size() - 1);
            assertEquals(0, history.getMajorVersion());
            assertEquals(0, history.getMidVersion());
            assertEquals(2, history.getMinorVersion());
            assertTrue(checkHistory(rs));
            rs.setDescription("desc 3");
            rs.write2DB();
            assertEquals(historyCount++, rs.getHistoryList().size());
            history = rs.getHistoryList().get(rs.getHistoryList().size() - 1);
            assertEquals(0, history.getMajorVersion());
            assertEquals(0, history.getMidVersion());
            assertEquals(3, history.getMinorVersion());
            assertTrue(checkHistory(rs));
            rs.increaseMidVersion();
            assertEquals(historyCount++, rs.getHistoryList().size());
            history = rs.getHistoryList().get(rs.getHistoryList().size() - 1);
            assertEquals(0, history.getMajorVersion());
            assertEquals(1, history.getMidVersion());
            assertEquals(0, history.getMinorVersion());
            assertTrue(checkHistory(rs));
            rs.increaseMajorVersion();
            assertEquals(historyCount++, rs.getHistoryList().size());
            history = rs.getHistoryList().get(rs.getHistoryList().size() - 1);
            assertEquals(1, history.getMajorVersion());
            assertEquals(0, history.getMidVersion());
            assertEquals(0, history.getMinorVersion());
            assertTrue(checkHistory(rs));
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
