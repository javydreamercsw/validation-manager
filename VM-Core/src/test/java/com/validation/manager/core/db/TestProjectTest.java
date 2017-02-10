package com.validation.manager.core.db;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.db.controller.RoleJpaController;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.UserTestProjectRoleJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestProjectTest extends AbstractVMTestCase {

    private TestProject tp;

    @Override
    public void setUp() {
        try {
            super.setUp();
            createTestUsers();
        } catch (Exception ex) {
            getLogger(TestProjectTest.class.getSimpleName()).log(Level.SEVERE,
                    null, ex);
            fail();
        }
    }

    @Override
    public void tearDown() {
        try {
            deleteTestUsers();
            if (tp != null && tp.getId() != null
                    && new TestProjectJpaController(getEntityManagerFactory())
                            .findTestProject(tp.getId()) != null) {
                new TestProjectJpaController(getEntityManagerFactory())
                        .destroy(tp.getId());
            }
        } catch (IllegalOrphanException | NonexistentEntityException ex) {
            getLogger(TestProjectTest.class.getSimpleName()).log(Level.SEVERE,
                    null, ex);
        }
        try {
            super.tearDown();
        } catch (Exception ex) {
            getLogger(TestProjectTest.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateTestProject() {
        try {
            getLogger(TestProjectTest.class.getSimpleName()).log(Level.INFO,
                    "Create Test Project");
            parameters.clear();
            parameters.put("name", "Test Project");
            tp = new TestProject("Test Project", true);
            new TestProjectJpaController(getEntityManagerFactory()).create(tp);
            tp = ((TestProject) namedQuery("TestProject.findByName",
                    parameters).get(0));
            namedQuery("TestProject.findByName", parameters);
            assertTrue(tp.getId() >= 0);
            assignRolesForTestProject();
        } catch (Exception ex) {
            getLogger(TestProjectTest.class.getSimpleName()).log(Level.SEVERE,
                    null, ex);
            fail();
        }
    }

    private void assignRolesForTestProject() {
        try {
            getLogger(TestProjectTest.class.getSimpleName()).log(Level.INFO,
                    "Assigning roles");
            ArrayList<UserTestProjectRole> roles = new ArrayList<>();
            //Designer role
            UserTestProjectRole temp = new UserTestProjectRole(tp.getId(),
                    designer.getId(), 4);
            temp.setVmUser(designer);
            temp.setRole(new RoleJpaController(getEntityManagerFactory())
                    .findRole(4));
            temp.setTestProject(tp);
            new UserTestProjectRoleJpaController(getEntityManagerFactory())
                    .create(temp);
            roles.add(temp);
            designer.setUserTestProjectRoleList((List) roles);
            new VmUserJpaController(getEntityManagerFactory()).edit(designer);
            roles.clear();
            //Tester role
            temp = new UserTestProjectRole(tp.getId(), tester.getId(), 7);
            temp.setVmUser(tester);
            temp.setRole(new RoleJpaController(getEntityManagerFactory())
                    .findRole(7));
            temp.setTestProject(tp);
            new UserTestProjectRoleJpaController(getEntityManagerFactory())
                    .create(temp);
            roles.add(temp);
            tester.setUserTestProjectRoleList((List) roles);
            new VmUserJpaController(getEntityManagerFactory()).edit(tester);
            roles.clear();
            //Leader role
            temp = new UserTestProjectRole(tp.getId(), leader.getId(), 9);
            temp.setVmUser(leader);
            temp.setRole(new RoleJpaController(getEntityManagerFactory())
                    .findRole(9));
            temp.setTestProject(tp);
            new UserTestProjectRoleJpaController(getEntityManagerFactory())
                    .create(temp);
            roles.add(temp);
            leader.setUserTestProjectRoleList((List) roles);
            new VmUserJpaController(getEntityManagerFactory()).edit(leader);
        } catch (PreexistingEntityException ex) {
            getLogger(TestProjectTest.class.getSimpleName()).log(Level.SEVERE,
                    null, ex);
            fail();
        } catch (Exception ex) {
            getLogger(TestProjectTest.class.getSimpleName()).log(Level.SEVERE,
                    null, ex);
            fail();
        }
    }
}
