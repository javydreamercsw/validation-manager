package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.UserTestPlanRole;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.TestPlanJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class TestPlanServer extends TestPlan
        implements EntityServer<TestPlan> {

    private final static Logger LOG
            = Logger.getLogger(TestPlanServer.class.getName());

    public TestPlanServer(TestPlan plan) {
        TestPlanJpaController controller
                = new TestPlanJpaController(getEntityManagerFactory());
        TestPlan temp = controller.findTestPlan(plan.getTestPlanPK());
        update(TestPlanServer.this, temp);
    }

    public TestPlanServer(TestProject testProject, boolean active,
            boolean isOpen) {
        super(testProject, active, isOpen);
        setTestProject(testProject);
        setTestPlanList(new ArrayList<>());
        setUserTestPlanRoleList(new ArrayList<>());
        setTestCaseList(new ArrayList<>());
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        TestPlanJpaController controller
                = new TestPlanJpaController(getEntityManagerFactory());
        if (getTestPlanPK().getId() > 0) {
            TestPlan temp = controller.findTestPlan(getTestPlanPK());
            update(temp, this);
            controller.edit(temp);
        } else {
            TestPlan temp = new TestPlan(getTestProject(), getActive(),
                    getIsOpen());
            update(temp, this);
            controller.create(temp);
            setTestPlanPK(temp.getTestPlanPK());
        }
        return getTestPlanPK().getId();
    }

    public static boolean deleteTestPlan(TestPlan tp) {
        try {
            new TestPlanJpaController(getEntityManagerFactory())
                    .destroy(tp.getTestPlanPK());
        }
        catch (IllegalOrphanException | NonexistentEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public boolean addTestCase(TestCase test) {
        try {
            getTestCaseList().add(test);
            write2DB();
            return true;
        }
        catch (PreexistingEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public TestPlan getEntity() {
        return new TestPlanJpaController(getEntityManagerFactory())
                .findTestPlan(getTestPlanPK());
    }

    @Override
    public void update(TestPlan target, TestPlan source) {
        target.setActive(source.getActive());
        target.setIsOpen(source.getIsOpen());
        target.setNotes(source.getNotes());
        target.setTestCaseList(source.getTestCaseList());
        target.setTestPlanList(source.getTestPlanList());
        target.setUserTestPlanRoleList(source.getUserTestPlanRoleList());
        target.setTestPlanPK(source.getTestPlanPK());
        target.setTestPlan(source.getTestPlan());
        target.setName(source.getName());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public void addUserTestPlanRole(VmUser user, Role role) throws Exception {
        for (UserTestPlanRole utpr : getUserTestPlanRoleList()) {
            if (utpr.getTestPlan().getTestPlanPK().equals(getTestPlanPK())
                    && Objects.equals(utpr.getVmUser().getId(), user.getId())
                    && Objects.equals(utpr.getRole().getId(), role.getId())) {
                //We have already this role.
                return;
            }
        }
        UserTestPlanRoleServer temp = new UserTestPlanRoleServer(getEntity(),
                user, role);
        if (temp.getEntity() == null) {
            temp.write2DB();
        }
        getUserTestPlanRoleList().add(temp.getEntity());
        write2DB();
    }
}
