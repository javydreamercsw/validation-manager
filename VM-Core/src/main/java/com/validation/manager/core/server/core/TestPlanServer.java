package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
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
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestPlanServer extends TestPlan implements EntityServer{

    public TestPlanServer(TestPlan plan) {
        TestPlanJpaController controller = new TestPlanJpaController( DataBaseManager.getEntityManagerFactory());
        TestPlan temp = controller.findTestPlan(plan.getTestPlanPK());
        setActive(temp.getActive());
        setIsOpen(temp.getIsOpen());
        setNotes(temp.getNotes());
        setTestPlanHasTestList(temp.getTestPlanHasTestList());
        setTestPlanList(temp.getTestPlanList());
        setUserTestPlanRoleList(temp.getUserTestPlanRoleList());
        setTestPlanPK(temp.getTestPlanPK());
    }

    public TestPlanServer(TestProject testProject, boolean active, boolean isOpen) {
        super(testProject, active, isOpen);
        setTestProject(testProject);
    }

    @Override
    public int write2DB() throws IllegalOrphanException, NonexistentEntityException, Exception {
        TestPlanJpaController controller = new TestPlanJpaController( DataBaseManager.getEntityManagerFactory());
        if (getTestPlanPK().getId() > 0) {
            TestPlan temp = controller.findTestPlan(getTestPlanPK());
            temp.setActive(getActive());
            temp.setIsOpen(getIsOpen());
            temp.setNotes(getNotes());
            temp.setTestPlanHasTestList(getTestPlanHasTestList());
            temp.setTestPlanList(getTestPlanList());
            temp.setUserTestPlanRoleList(getUserTestPlanRoleList());
            controller.edit(temp);
        } else {
            TestPlan temp = new TestPlan(getTestProject(), getActive(), getIsOpen());
            temp.setActive(getActive());
            temp.setIsOpen(getIsOpen());
            temp.setNotes(getNotes());
            temp.setTestPlanHasTestList(getTestPlanHasTestList());
            temp.setTestPlanList(getTestPlanList());
            temp.setUserTestPlanRoleList(getUserTestPlanRoleList());
            controller.create(temp);
            setTestPlanPK(temp.getTestPlanPK());
        }
        return getTestPlanPK().getId();
    }

    public static boolean deleteTestPlan(TestPlan tp) {
        try {
            new TestPlanJpaController( DataBaseManager.getEntityManagerFactory()).destroy(tp.getTestPlanPK());
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(TestPlanServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(TestPlanServer.class.getName()).log(Level.SEVERE, null, ex);
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
            tpht.setTestPlan(new TestPlanJpaController( DataBaseManager.getEntityManagerFactory()).findTestPlan(getTestPlanPK()));
            new TestPlanHasTestJpaController( DataBaseManager.getEntityManagerFactory()).create(tpht);
            getTestPlanHasTestList().add(tpht);
            write2DB();
            return true;
        } catch (PreexistingEntityException ex) {
            Logger.getLogger(TestPlanServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TestPlanServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
