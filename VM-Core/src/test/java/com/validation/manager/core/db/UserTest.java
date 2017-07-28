/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core.db;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.controller.UserStatusJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import static com.validation.manager.core.tool.MD5.encrypt;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
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
                    "first", "last", "en",
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
        catch (VMException | IllegalOrphanException | NonexistentEntityException e) {
            fail(e.getMessage());
        }
    }
}
