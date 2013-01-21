package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Test;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestPlanHasTest;
import com.validation.manager.core.db.controller.TestJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestServer extends Test implements EntityServer{

    public TestServer(String name, String purpose, String scope) {
        super(name, purpose, scope);
    }

    public TestServer(Integer id) {
        setId(id);
        TestJpaController controller = new TestJpaController( DataBaseManager.getEntityManagerFactory());
        Test temp = controller.findTest(getId());
        setName(temp.getName());
        setNotes(temp.getNotes());
        setPurpose(temp.getPurpose());
        setScope(temp.getScope());
        setTestCaseList(temp.getTestCaseList());
        setTestPlanHasTestList(temp.getTestPlanHasTestList());
    }

    @Override
    public int write2DB() throws IllegalOrphanException, NonexistentEntityException, Exception {
        if (getTestCaseList() == null) {
            setTestCaseList(new ArrayList<TestCase>());
        }
        if (getTestPlanHasTestList() == null) {
            setTestPlanHasTestList(new ArrayList<TestPlanHasTest>());
        }
        if (getId() != null && getId() > 0) {
            Test temp = new TestJpaController( DataBaseManager.getEntityManagerFactory()).findTest(getId());
            temp.setName(getName());
            temp.setNotes(getNotes());
            temp.setPurpose(getPurpose());
            temp.setScope(getScope());
            if (getTestCaseList() != null) {
                temp.setTestCaseList(getTestCaseList());
            }
            if (getTestPlanHasTestList() != null) {
                temp.setTestPlanHasTestList(getTestPlanHasTestList());
            }
            new TestJpaController( DataBaseManager.getEntityManagerFactory()).edit(temp);
        } else {
            Test temp = new Test(getName(), getPurpose(), getScope());
            temp.setNotes(getNotes());
            temp.setTestCaseList(getTestCaseList());
            temp.setTestPlanHasTestList(getTestPlanHasTestList());
            new TestJpaController( DataBaseManager.getEntityManagerFactory()).create(temp);
            setId(temp.getId());
        }
        return getId();
    }

    public static boolean deleteTest(Test t) {
        try {
            new TestJpaController( DataBaseManager.getEntityManagerFactory()).destroy(t.getId());
            return true;
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(TestServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(TestServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
