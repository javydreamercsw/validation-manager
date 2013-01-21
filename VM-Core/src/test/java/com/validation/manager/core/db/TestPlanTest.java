package com.validation.manager.core.db;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.controller.RoleJpaController;
import com.validation.manager.core.db.controller.TestPlanJpaController;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import com.validation.manager.test.TestHelper;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestPlanTest extends AbstractVMTestCase {

    private TestProject tp;
    private TestPlan tpl;

    public TestPlanTest() {
    }

    @Override
    public void setUp() {
        try {
            super.setUp();
            Logger.getLogger(TestProjectTest.class.getSimpleName()).log(Level.INFO, "Create test project");
            tp = new TestProject("Test Project", true);
            new TestProjectJpaController( DataBaseManager.getEntityManagerFactory()).create(tp);
            Logger.getLogger(TestProjectTest.class.getSimpleName()).log(Level.INFO, "Done!");
        } catch (Exception ex) {
            Logger.getLogger(TestPlanTest.class.getSimpleName()).log(Level.SEVERE, null, ex);
        }
    }

    @After
    public void clean() {
        try {
            deleteTestUsers();
            if (tpl != null && tpl.getTestPlanPK() != null && new TestPlanJpaController( DataBaseManager.getEntityManagerFactory()).findTestPlan(tpl.getTestPlanPK()) != null) {
                new TestPlanJpaController( DataBaseManager.getEntityManagerFactory()).destroy(tpl.getTestPlanPK());
            }
            if (tp != null && tp.getId() != null && new TestProjectJpaController( DataBaseManager.getEntityManagerFactory()).findTestProject(tp.getId()) != null) {
                new TestProjectJpaController( DataBaseManager.getEntityManagerFactory()).destroy(tp.getId());
            }
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(TestProjectTest.class.getSimpleName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(TestProjectTest.class.getSimpleName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of toString method, of class TestPlan.
     */
    @Test
    public void testCreateAndDestroyTestPlan() {
        try {
            createTestUsers();
            System.out.println("Create Test Plan");
            tpl = new TestPlan(tp, true, true);
            new TestPlanJpaController( DataBaseManager.getEntityManagerFactory()).create(tpl);
            assertTrue(tpl.getTestPlanPK().getId() > 0);
            //Assign roles
            TestHelper.addUserTestPlanRole(tpl, designer,
                    new RoleJpaController( DataBaseManager.getEntityManagerFactory()).findRole(4));
            TestHelper.addUserTestPlanRole(tpl, leader,
                    new RoleJpaController( DataBaseManager.getEntityManagerFactory()).findRole(9));
            TestHelper.addUserTestPlanRole(tpl, tester,
                    new RoleJpaController( DataBaseManager.getEntityManagerFactory()).findRole(7));
        } catch (PreexistingEntityException ex) {
            Logger.getLogger(TestPlanTest.class.getSimpleName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (Exception ex) {
            Logger.getLogger(TestPlanTest.class.getSimpleName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
