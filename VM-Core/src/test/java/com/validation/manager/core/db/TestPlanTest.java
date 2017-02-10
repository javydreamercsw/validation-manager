package com.validation.manager.core.db;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.db.controller.RoleJpaController;
import com.validation.manager.core.db.controller.TestPlanJpaController;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import com.validation.manager.test.AbstractVMTestCase;
import static com.validation.manager.test.TestHelper.addUserTestPlanRole;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestPlanTest extends AbstractVMTestCase {

    private TestProject tp;
    private TestPlan tpl;

    @Override
    public void setUp() {
        try {
            super.setUp();
            getLogger(TestProjectTest.class.getSimpleName())
                    .log(Level.INFO, "Create test project");
            tp = new TestProject("Test Project", true);
            new TestProjectJpaController(getEntityManagerFactory()).create(tp);
            getLogger(TestProjectTest.class.getSimpleName()).log(Level.INFO,
                    "Done!");
        } catch (Exception ex) {
            getLogger(TestPlanTest.class.getSimpleName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    @After
    public void clean() {
        try {
            deleteTestUsers();
            if (tpl != null && tpl.getTestPlanPK() != null
                    && new TestPlanJpaController(getEntityManagerFactory())
                            .findTestPlan(tpl.getTestPlanPK()) != null) {
                new TestPlanJpaController(getEntityManagerFactory())
                        .destroy(tpl.getTestPlanPK());
            }
            if (tp != null && tp.getId() != null
                    && new TestProjectJpaController(getEntityManagerFactory())
                            .findTestProject(tp.getId()) != null) {
                new TestProjectJpaController(getEntityManagerFactory())
                        .destroy(tp.getId());
            }
        } catch (IllegalOrphanException | NonexistentEntityException ex) {
            getLogger(TestProjectTest.class.getSimpleName()).log(Level.SEVERE,
                    null, ex);
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
            new TestPlanJpaController(getEntityManagerFactory()).create(tpl);
            assertTrue(tpl.getTestPlanPK().getId() > 0);
            //Assign roles
            addUserTestPlanRole(tpl, designer,
                    new RoleJpaController(getEntityManagerFactory())
                            .findRole(4));
            addUserTestPlanRole(tpl, leader,
                    new RoleJpaController(getEntityManagerFactory())
                            .findRole(9));
            addUserTestPlanRole(tpl, tester,
                    new RoleJpaController(getEntityManagerFactory())
                            .findRole(7));
        } catch (PreexistingEntityException ex) {
            getLogger(TestPlanTest.class.getSimpleName()).log(Level.SEVERE,
                    null, ex);
            fail();
        } catch (Exception ex) {
            getLogger(TestPlanTest.class.getSimpleName()).log(Level.SEVERE,
                    null, ex);
            fail();
        }
    }
}
