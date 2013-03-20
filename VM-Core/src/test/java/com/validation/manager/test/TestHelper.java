package com.validation.manager.test;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementSpecNodePK;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.Test;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestPlanHasTest;
import com.validation.manager.core.db.TestPlanHasTestPK;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.UserTestPlanRole;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.db.controller.TestJpaController;
import com.validation.manager.core.db.controller.TestPlanHasTestJpaController;
import com.validation.manager.core.db.controller.TestPlanJpaController;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.UserStatusJpaController;
import com.validation.manager.core.db.controller.UserTestPlanRoleJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.server.core.RequirementSpecNodeServer;
import com.validation.manager.core.server.core.RequirementSpecServer;
import com.validation.manager.core.server.core.StepServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.TestPlanServer;
import com.validation.manager.core.server.core.TestProjectServer;
import com.validation.manager.core.server.core.TestServer;
import com.validation.manager.core.server.core.UserTestPlanRoleServer;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestHelper {

    public static VmUser createUser(String name, String pass, String email,
            String first, String last) throws Exception {
        VMUserServer temp = new VMUserServer(name,
                pass, first, email, last);
        temp.setUserStatusId(new UserStatusJpaController(DataBaseManager.getEntityManagerFactory()).findUserStatus(1));
        temp.write2DB();
        return new VmUserJpaController(DataBaseManager.getEntityManagerFactory()).findVmUser(temp.getId());
    }

    public static void deleteUser(VmUser user) throws NonexistentEntityException, IllegalOrphanException {
        if (user != null) {
            VMUserServer.deleteUser(user);
        }
    }

    public static void addUserTestPlanRole(TestPlan tpl, VmUser user, Role role) throws PreexistingEntityException, Exception {
        UserTestPlanRoleServer temp = new UserTestPlanRoleServer(tpl, user, role);
        temp.write2DB();
        UserTestPlanRole utpr = new UserTestPlanRoleJpaController(DataBaseManager.getEntityManagerFactory()).findUserTestPlanRole(temp.getUserTestPlanRolePK());
        assertTrue(utpr.getUserTestPlanRolePK().getTestPlanTestProjectId() == temp.getUserTestPlanRolePK().getTestPlanTestProjectId());
        assertTrue(utpr.getUserTestPlanRolePK().getRoleId() == temp.getUserTestPlanRolePK().getRoleId());
        assertTrue(utpr.getUserTestPlanRolePK().getTestPlanId() == temp.getUserTestPlanRolePK().getTestPlanId());
        assertTrue(utpr.getUserTestPlanRolePK().getUserId() == temp.getUserTestPlanRolePK().getUserId());
    }

    public static Project createProject(String name, String notes) {
        ProjectServer ps = new ProjectServer(name, notes);
        try {
            ps.write2DB();
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(TestHelper.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(TestHelper.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (Exception ex) {
            Logger.getLogger(TestHelper.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        Project p = new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).findProject(ps.getId());
        assertTrue(new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).findProject(p.getId()) != null);
        assertTrue(new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).findProject(p.getId()).getNotes().equals(p.getNotes()));
        return p;
    }

    public static void destroyProject(Project p) throws IllegalOrphanException, NonexistentEntityException {
        ProjectServer.deleteProject(p);
        assertTrue(new ProjectJpaController(DataBaseManager.getEntityManagerFactory()).findProject(p.getId()) == null);
    }

    public static Test createTest(String name, String purpose, String scope) throws PreexistingEntityException, Exception {
        TestServer t = new TestServer(name, purpose, scope);
        t.setNotes("Notes");
        t.write2DB();
        assertTrue(new TestJpaController(DataBaseManager.getEntityManagerFactory()).findTest(t.getId()) != null);
        return new TestJpaController(DataBaseManager.getEntityManagerFactory()).findTest(t.getId());
    }

    public static TestCase createTestCase(int testId, short version,
            String expectedResults, Test test, /*VmUser user,*/ String summary) throws PreexistingEntityException, Exception {
        TestCaseServer tc = new TestCaseServer(testId, version, new Date());
        tc.setExpectedResults(expectedResults);
        tc.setTest(test);
//        tc.setVmUserId(user);
        tc.setActive(true);
        tc.setExpectedResults(expectedResults);
        tc.setIsOpen(true);
        tc.setSummary(summary);
        TestCaseJpaController controller = new TestCaseJpaController(
                DataBaseManager.getEntityManagerFactory());
        tc.write2DB();
        return controller.findTestCase(tc.getTestCasePK());
    }

    public static Requirement createRequirement(String id, String desc, 
            RequirementSpecNodePK p, String notes, int requirementType, 
            int requirementStatus) throws Exception {
        RequirementServer req = new RequirementServer(id, desc, p, notes, 
                requirementType, requirementStatus);
        req.write2DB();
        return new RequirementJpaController(DataBaseManager.getEntityManagerFactory()).findRequirement(req.getRequirementPK());
    }

    public static void destroyRequirement(Requirement r) throws NonexistentEntityException {
        try {
            RequirementServer.deleteRequirement(r);
            assertTrue(new RequirementJpaController(DataBaseManager.getEntityManagerFactory()).findRequirement(r.getRequirementPK()) == null);
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(TestHelper.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }

    public static TestCase addStep(TestCase tc, int sequence, String text, String note) throws PreexistingEntityException, Exception {
        StepServer s = new StepServer(tc, sequence, text);
        int amount = tc.getStepList().size();
        s.setNotes(note);
        s.write2DB();
        TestCaseServer tcs = new TestCaseServer(tc.getTestCasePK());
        tcs.getStepList().add(new StepJpaController(DataBaseManager.getEntityManagerFactory()).findStep(s.getStepPK()));
        tcs.write2DB();
        assertTrue(tcs.getStepList().size() > amount);
        return new TestCaseJpaController(DataBaseManager.getEntityManagerFactory()).findTestCase(tc.getTestCasePK());
    }

    public static TestProject createTestProject(String name) throws IllegalOrphanException, NonexistentEntityException, Exception {
        TestProjectServer tps = new TestProjectServer("Test Project", true);
        tps.write2DB();
        return new TestProjectJpaController(DataBaseManager.getEntityManagerFactory()).findTestProject(tps.getId());
    }

    public static TestPlan createTestPlan(TestProject tp, String notes, boolean active, boolean open) throws PreexistingEntityException, Exception {
        TestPlanServer plan = new TestPlanServer(tp, active, open);
        plan.setNotes(notes);
        plan.setTestProject(tp);
        plan.write2DB();
        return new TestPlanJpaController(DataBaseManager.getEntityManagerFactory()).findTestPlan(plan.getTestPlanPK());
    }

    public static void addTestCaseToTest(Test test, TestCase tc) throws IllegalOrphanException, NonexistentEntityException, Exception {
        TestCaseServer tcs = new TestCaseServer(tc.getTestCasePK());
        tcs.setTest(test);
        tcs.write2DB();
        TestServer t = new TestServer(test.getId());
        t.getTestCaseList().add(tc);
        t.write2DB();
    }

    public static void addTestToPlan(TestPlan plan, Test test) throws PreexistingEntityException, Exception {
        int testInPlan = plan.getTestPlanHasTestList().size();
        TestPlanServer tps = new TestPlanServer(plan);
        TestPlanHasTest tpht = new TestPlanHasTest(
                new TestPlanHasTestPK(plan.getTestPlanPK().getId(), plan.getTestPlanPK().getTestProjectId(), test.getId()),
                new Date(), 1);
        tpht.setTest(test);
        tpht.setTestPlan(plan);
        new TestPlanHasTestJpaController(DataBaseManager.getEntityManagerFactory()).create(tpht);
        tps.getTestPlanHasTestList().add(tpht);
        tps.write2DB();
        assertTrue(tps.getTestPlanHasTestList().size() > testInPlan);
    }

    public static RequirementSpec createRequirementSpec(String name, String description, Project project, int specLevelId) throws Exception {
        RequirementSpecServer rss = new RequirementSpecServer(name, description,
                project.getId(), specLevelId);
        rss.write2DB();
        return rss;
    }

    public static RequirementSpecNode createRequirementSpecNode(RequirementSpec rss, String name, String description, String scope) throws Exception {
        RequirementSpecNodeServer rsns = new RequirementSpecNodeServer(rss,
                name, description, scope);
        rsns.write2DB();
        return rsns;
    }
}