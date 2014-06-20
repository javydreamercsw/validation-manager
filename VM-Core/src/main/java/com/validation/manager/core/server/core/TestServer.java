package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
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
import static java.util.logging.Logger.getLogger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestServer extends Test implements EntityServer<Test> {

    public TestServer(String name, String purpose, String scope) {
        super(name, purpose, scope);
    }

    public TestServer(Integer id) {
        setId(id);
        TestJpaController controller = new TestJpaController(getEntityManagerFactory());
        Test temp = controller.findTest(getId());
        update(this,temp);
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
            Test temp = new TestJpaController(getEntityManagerFactory()).findTest(getId());
            update(this,temp);
            new TestJpaController(getEntityManagerFactory()).edit(temp);
        } else {
            Test temp = new Test(getName(), getPurpose(), getScope());
            update(this,temp);
            new TestJpaController(getEntityManagerFactory()).create(temp);
            setId(temp.getId());
        }
        return getId();
    }

    public static boolean deleteTest(Test t) {
        try {
            new TestJpaController(getEntityManagerFactory()).destroy(t.getId());
            return true;
        } catch (IllegalOrphanException ex) {
            getLogger(TestServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NonexistentEntityException ex) {
            getLogger(TestServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public Test getEntity() {
        return new TestJpaController(getEntityManagerFactory()).findTest(getId());
    }

    @Override
    public void update(Test target, Test source) {
        target.setName(source.getName());
        target.setNotes(source.getNotes());
        target.setPurpose(source.getPurpose());
        target.setScope(source.getScope());
        if (source.getTestCaseList() != null) {
            target.setTestCaseList(source.getTestCaseList());
        }
        if (source.getTestPlanHasTestList() != null) {
            target.setTestPlanHasTestList(source.getTestPlanHasTestList());
        }
    }
    
    @Override
    public void update() {
        update(this, getEntity());
    }
}
