package com.validation.manager.core.db;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.db.controller.UserStatusJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
import static com.validation.manager.core.tool.MD5.encrypt;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.Date;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UserTest extends AbstractVMTestCase {

    @Test
    @SuppressWarnings({"unchecked"})
    public void testCreateAndDeleteUser() {
        try {
            getLogger(UserTest.class.getSimpleName()).log(Level.INFO,
                    "Available users: {0}",
                    new VmUserJpaController(getEntityManagerFactory())
                            .getVmUserCount());
            getLogger(UserTest.class.getSimpleName()).log(Level.INFO,
                    "Create an user");
            VmUser u = new VmUser("test",
                    encrypt("password"), "test@test.com",
                    "first", "last", "en", new Date(),
                    new UserStatusJpaController(getEntityManagerFactory())
                            .findUserStatus(1), 0);
            u.setUserStatusId(new UserStatusJpaController(
                    getEntityManagerFactory()).findUserStatus(1));
            new VmUserJpaController(getEntityManagerFactory()).create(u);
            parameters.clear();
            parameters.put("username", "test");
            result = namedQuery("VmUser.findByUsername", parameters);
            assertTrue(result.size() > 0);
            u = (VmUser) result.get(0);
            getLogger(UserTest.class.getSimpleName()).log(Level.INFO,
                    "Delete an user");
            new VmUserJpaController(getEntityManagerFactory())
                    .destroy(u.getId());
            result = namedQuery("VmUser.findByUsername", parameters);
            assertTrue(result.isEmpty());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
