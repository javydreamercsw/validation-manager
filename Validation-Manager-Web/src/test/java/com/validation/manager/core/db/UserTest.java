package com.validation.manager.core.db;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.controller.UserStatusJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.tool.MD5;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UserTest extends AbstractVMTestCase {

    /**
     * Default constructor.
     */
    public UserTest() {
    }

    @Test
    @SuppressWarnings({"unchecked"})
    public void testCreateAndDeleteUser() {
        try {
            Logger.getLogger(UserTest.class.getSimpleName()).log(Level.INFO,
                    "Available users: {0}",
                    new VmUserJpaController(DataBaseManager.getEntityManagerFactory()).getVmUserCount());
            Logger.getLogger(UserTest.class.getSimpleName()).log(Level.INFO, "Create an user");
            VmUser u = new VmUser("test",
                    MD5.encrypt("password"), "test@test.com",
                    "first", "last", "en", new Date(),
                    new UserStatusJpaController(DataBaseManager.getEntityManagerFactory()).findUserStatus(1), 0);
            u.setUserStatusId(new UserStatusJpaController(DataBaseManager.getEntityManagerFactory()).findUserStatus(1));
            u.setModifierId(1);
            new VmUserJpaController(DataBaseManager.getEntityManagerFactory()).create(u);
            parameters.clear();
            parameters.put("username", "test");
            result = DataBaseManager.namedQuery("VmUser.findByUsername", parameters);
            assertTrue(result.size() > 0);
            u = (VmUser) result.get(0);
            Logger.getLogger(UserTest.class.getSimpleName()).log(Level.INFO, "Delete an user");
            new VmUserJpaController(DataBaseManager.getEntityManagerFactory()).destroy(u.getId());
            result = DataBaseManager.namedQuery("VmUser.findByUsername", parameters);
            assertTrue(result.isEmpty());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
