package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Test;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestPlanHasTest;
import com.validation.manager.core.db.TestPlanHasTestPK;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.TestPlanHasTestJpaController;
import com.validation.manager.core.db.controller.TestPlanJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.Date;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class TestPlanServer extends TestPlan implements EntityServer<TestPlan> {

    public TestPlanServer(TestPlan plan) {
        TestPlanJpaController controller = new TestPlanJpaController(getEntityManagerFactory());
        TestPlan temp = controller.findTestPlan(plan.getTestPlanPK());
        update(TestPlanServer.this, temp);
    }

    public TestPlanServer(TestProject testProject, boolean active, boolean isOpen) {
        super(testProject, active, isOpen);
        setTestProject(testProject);
    }

    @Override
    public int write2DB() throws IllegalOrphanException, NonexistentEntityException, Exception {
        TestPlanJpaController controller = new TestPlanJpaController(getEntityManagerFactory());
        if (getTestPlanPK().getId() > 0) {
            TestPlan temp = controller.findTestPlan(getTestPlanPK());
            update(temp, this);
            controller.edit(temp);
        } else {
            TestPlan temp = new TestPlan(getTestProject(), getActive(), getIsOpen());
            update(temp, this);
            controller.create(temp);
            setTestPlanPK(temp.getTestPlanPK());
        }
        return getTestPlanPK().getId();
    }

    public static boolean deleteTestPlan(TestPlan tp) {
        try {
            new TestPlanJpaController(getEntityManagerFactory()).destroy(tp.getTestPlanPK());
        } catch (IllegalOrphanException ex) {
            getLogger(TestPlanServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (NonexistentEntityException ex) {
            getLogger(TestPlanServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public boolean addTest(Test test) {
        try {
            TestPlanHasTest tpht = new TestPlanHasTest(
                    new TestPlanHasTestPK(getTestPlanPK().getId(), getTestPlanPK().getTestProjectId(), test.getId()),
                    new Date(), 1);
            tpht.setTest(test);
            tpht.setTestPlan(new TestPlanJpaController(getEntityManagerFactory()).findTestPlan(getTestPlanPK()));
            new TestPlanHasTestJpaController(getEntityManagerFactory()).create(tpht);
            getTestPlanHasTestList().add(tpht);
            write2DB();
            return true;
        } catch (PreexistingEntityException ex) {
            getLogger(TestPlanServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            getLogger(TestPlanServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public TestPlan getEntity() {
        return new TestPlanJpaController(
                getEntityManagerFactory())
                .findTestPlan(getTestPlanPK());
    }

    @Override
    public void update(TestPlan target, TestPlan source) {
        target.setActive(source.getActive());
        target.setIsOpen(source.getIsOpen());
        target.setNotes(source.getNotes());
        target.setTestPlanHasTestList(source.getTestPlanHasTestList());
        target.setTestPlanList(source.getTestPlanList());
        target.setUserTestPlanRoleList(source.getUserTestPlanRoleList());
        target.setTestPlanPK(source.getTestPlanPK());
    }
    
    @Override
    public void update() {
        update(this, getEntity());
    }
}
