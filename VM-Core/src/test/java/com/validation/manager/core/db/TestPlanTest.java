package com.validation.manager.core.db;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.db.controller.RoleJpaController;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import com.validation.manager.core.server.core.TestPlanServer;
import com.validation.manager.core.server.core.TestProjectServer;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TestPlanTest extends AbstractVMTestCase {

    private static final Logger LOG
            = Logger.getLogger(TestProjectTest.class.getSimpleName());

    /**
     * Test of toString method, of class TestPlan.
     */
    @Test
    @Ignore
    public void testCreateAndDestroyTestPlan() {
        try {
            createTestUsers();
            LOG.log(Level.INFO, "Create test project");
            TestProjectServer tp = new TestProjectServer("Test Project", true);
            tp.write2DB();
            LOG.log(Level.INFO, "Done!");
            System.out.println("Create Test Plan");
            TestPlanServer tps = new TestPlanServer(tp.getEntity(), true, true);
            tps.write2DB();
            assertTrue(tps.getTestPlanPK().getId() > 0);
            //Assign roles
            tps.addUserTestPlanRole(designer,
                    new RoleJpaController(getEntityManagerFactory())
                            .findRole(4));
            tps.addUserTestPlanRole(leader,
                    new RoleJpaController(getEntityManagerFactory())
                            .findRole(9));
            tps.addUserTestPlanRole(tester,
                    new RoleJpaController(getEntityManagerFactory())
                            .findRole(7));
            assertEquals(3, tps.getUserTestPlanRoleList().size());
        } catch (PreexistingEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
