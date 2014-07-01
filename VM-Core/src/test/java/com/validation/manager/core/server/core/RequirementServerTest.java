package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.isVersioningEnabled;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import static com.validation.manager.core.DataBaseManager.setVersioningEnabled;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import static java.lang.Short.valueOf;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import org.junit.Test;
import org.openide.util.Exceptions;
import static org.openide.util.Exceptions.printStackTrace;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementServerTest extends AbstractVMTestCase {

    private static final Logger LOG
            = getLogger(RequirementServerTest.class.getName());
    private Project p;
    private RequirementSpec rss;
    private RequirementSpecNode rsns;

    private void prepare() throws Exception {
        p = TestHelper.createProject("New Project", "Notes");
        ProjectServer project = new ProjectServer(p);
        project.setNotes("Notes 2");
        project.write2DB();
        assertTrue(new ProjectJpaController(
                getEntityManagerFactory())
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
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            RequirementServer.deleteRequirement(r);
            parameters.clear();
            parameters.put("id", r.getId());
            assertTrue(namedQuery("Requirement.findById", parameters).isEmpty());
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
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            RequirementServer instance = new RequirementServer(source);
            Requirement target = new Requirement();
            instance.update(target, source);
            assertEquals(instance.getUniqueId(), target.getUniqueId());
            assertEquals(instance.getDescription(), target.getDescription());
            assertEquals(instance.getNotes(), target.getNotes());
            assertEquals(instance.getRequirementTypeId().getId(),
                    target.getRequirementTypeId().getId());
            assertEquals(instance.getRequirementStatusId().getId(),
                    target.getRequirementStatusId().getId());
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
            setVersioningEnabled(true);
            rs.setDescription("Modified requirement");
            rs.write2DB();
            rs.addChildRequirement(req2);
            //Should have one children now
            for (Requirement r : rs.getRequirementList1()) {
                LOG.info(r.getUniqueId());
            }
            assertEquals(1, rs.getRequirementList1().size());
            for (Requirement r : rs.getRequirementList()) {
                LOG.info(r.getUniqueId());
            }
            //No parents
            assertEquals(0, rs.getRequirementList().size());
            RequirementServer rs2 = new RequirementServer(req2);
            for (Requirement r : rs2.getRequirementList()) {
                LOG.info(r.getUniqueId());
            }
            //One parent
            assertEquals(1, rs2.getRequirementList().size());
            for (Requirement r : rs2.getRequirementList1()) {
                LOG.info(r.getUniqueId());
            }
            //No children
            assertEquals(0, rs2.getRequirementList1().size());
            checkCircularDependency(req);
            checkCircularDependency(req2);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    private void checkCircularDependency(Requirement r) {
        RequirementServer rs = new RequirementServer(r);
        for (Requirement t : rs.getVersions()) {
            RequirementServer temp = new RequirementServer(t);
            //Detect circular relationships
            if (temp.getRequirementList().size() > 0
                    && temp.getRequirementList1().size() > 0) {
                //Has both children and parents
                LOG.log(Level.INFO,
                        "Inspecting {0} for circular dependencies.",
                        temp.getUniqueId());
                for (Requirement parent : temp.getRequirementList1()) {
                    //Check all parents of this requirement
                    for (Requirement child : parent.getRequirementList()) {
                        //Check if the parent has this requirement as a child
                        if (child.getUniqueId().equals(temp.getUniqueId())) {
                            LOG.log(Level.SEVERE,
                                    "Circular dependency "
                                    + "detected between {0} and {1}",
                                    new Object[]{temp.getUniqueId(),
                                        parent.getUniqueId()});
                            fail();
                        }
                    }
                }
            }
        }
    }

    /**
     * Test of requirement versioning method, of class RequirementServer.
     */
    @Test
    public void testRequirementVersioningEnabled() {
        try {
            System.out.println("Requirement Versioning (Enabled)");
            setVersioningEnabled(true);
            runVersioningTest();
        } catch (Exception ex) {
            printStackTrace(ex);
            fail();
        }
    }

    /**
     * Test of requirement versioning method, of class RequirementServer.
     */
    @Test
    public void testRequirementVersioningDisabled() {
        try {
            System.out.println("Requirement Versioning (Disabled)");
            setVersioningEnabled(false);
            runVersioningTest();
        } catch (Exception ex) {
            printStackTrace(ex);
            fail();
        }
    }

    private void runVersioningTest() throws Exception {
        prepare();
        int version = 0;
        String first = "Sample requirement", second = "Updated";
        Requirement req = TestHelper.createRequirement("SRS-SW-0001",
                first, rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
        RequirementServer rs = new RequirementServer(req);
        assertEquals(0, rs.getMajorVersion());
        assertEquals(0, rs.getMidVersion());
        assertEquals(version, rs.getMinorVersion());
        assertEquals(1, rs.getVersions().size());
        assertEquals(first, rs.getDescription());
        TestProject tp = TestHelper.createTestProject("Test Project");
        TestHelper.addTestProjectToProject(tp, p);
        TestPlan plan = TestHelper.createTestPlan(tp, "Plan", true, true);
        com.validation.manager.core.db.Test test
                = TestHelper.createTest("Test", "Test", "Test");
        TestHelper.addTestToPlan(plan, test);
        TestCase tc = TestHelper.createTestCase("TC #1", valueOf("1"),
                "Results",
                test, "Summary");
        TestCase step = TestHelper.addStep(tc, 1, "Test", "Test");
        rs.getStepList().add(step.getStepList().get(0));
        rs.setDescription(second);
        RequirementType originalRequirementType = rs.getRequirementTypeId();
        RequirementSpecNode originalRequirementSpecNode
                = rs.getRequirementSpecNode();
        RequirementStatus originalRequirementStatusId
                = rs.getRequirementStatusId();
        if (isVersioningEnabled()) {
            rs.write2DB();
            version++;
            assertEquals(0, rs.getMajorVersion());
            assertEquals(0, rs.getMidVersion());
            assertEquals(version, rs.getMinorVersion());
            assertEquals(1, rs.getStepList().size());
            assertEquals(originalRequirementType, rs.getRequirementTypeId());
            assertEquals(originalRequirementSpecNode,
                    rs.getRequirementSpecNode());
            assertEquals(originalRequirementStatusId,
                    rs.getRequirementStatusId());
        } else {
            rs.write2DB();
            version++;
            assertEquals(0, rs.getMajorVersion());
            assertEquals(0, rs.getMidVersion());
            assertEquals(0, rs.getMinorVersion());
        }
        assertEquals(second, rs.getDescription());
        assertEquals(isVersioningEnabled() ? version + 1 : 1,
                rs.getVersions().size());
        LOG.info("Versions:");
        for (Requirement r : rs.getVersions()) {
            LOG.info(r.toString());
        }
        checkCircularDependency(req);
    }

    /**
     * Test of requirement coverage with versioning method, of class
     * RequirementServer.
     */
    @Test
    public void testRequirementCoverageWithVersions() {
        try {
            System.out.println("Requirement Coverage and Versioning");
            prepare();
            int version = 0;
            //Enable versioning
            setVersioningEnabled(true);
            Requirement req = TestHelper.createRequirement("SRS-SW-0001",
                    "Description", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
            RequirementServer rs = new RequirementServer(req);
            //Add a test case covering this test case
            TestProject tp = TestHelper.createTestProject("Test Project");
            TestHelper.addTestProjectToProject(tp, p);
            TestPlan plan = TestHelper.createTestPlan(tp, "Plan", true, true);
            com.validation.manager.core.db.Test test
                    = TestHelper.createTest("Test", "Test", "Test");
            TestHelper.addTestToPlan(plan, test);
            TestCase tc = TestHelper.createTestCase("TC #1", valueOf("1"),
                    "Results",
                    test, "Summary");
            TestCase step = TestHelper.addStep(tc, 1, "Test", "Test");
            rs.getStepList().add(step.getStepList().get(0));
            rs.write2DB();
            assertEquals(0, rs.getMajorVersion());
            assertEquals(0, rs.getMidVersion());
            assertEquals(version, rs.getMinorVersion());
            assertEquals(1, rs.getStepList().size());
            assertEquals(100, rs.getTestCoverage());
            version++;
            //Update version and remove test coverage
            rs.setDescription("New version");
            rs.getStepList().clear();
            rs.write2DB();
            assertEquals(0, rs.getMajorVersion());
            assertEquals(0, rs.getMidVersion());
            assertEquals(version, rs.getMinorVersion());
            assertEquals(0, rs.getStepList().size());
            assertEquals(0, rs.getTestCoverage());
            version++;
            rs.setDescription("New version 2");
            rs.getStepList().add(step.getStepList().get(0));
            rs.write2DB();
            assertEquals(0, rs.getMajorVersion());
            assertEquals(0, rs.getMidVersion());
            assertEquals(version, rs.getMinorVersion());
            assertEquals(1, rs.getStepList().size());
            assertEquals(100, rs.getTestCoverage());
            version++;
            //Add a parent requirement
            Requirement r = TestHelper.createRequirement("PS-0001",
                    "Description", rsns.getRequirementSpecNodePK(), "Notes", 1, 1);
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
        } catch (Exception ex) {
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
            prepare();
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
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
