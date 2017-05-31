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
package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.UserTestProjectRole;
import com.validation.manager.core.db.controller.UserTestProjectRoleJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class UserTestProjectRoleServer extends UserTestProjectRole
        implements EntityServer<UserTestProjectRole> {

    public UserTestProjectRoleServer(int testProjectId, int userId, int roleId) {
        super(testProjectId, userId, roleId);
        update();
    }

    /**
     * Persist to database
     *
     * @throws PreexistingEntityException If entity already exists and tried to
     * be re-created.
     * @throws Exception If something goes wrong writing to the database.
     */
    @Override
    public int write2DB() throws PreexistingEntityException, Exception {
        UserTestProjectRoleJpaController controller
                = new UserTestProjectRoleJpaController(getEntityManagerFactory());
        if (getUserTestProjectRolePK().getRoleId() > 0
                && getUserTestProjectRolePK().getTestProjectId() > 0
                && getUserTestProjectRolePK().getUserId() > 0) {
            UserTestProjectRole temp
                    = controller.findUserTestProjectRole(getUserTestProjectRolePK());
            temp.setRole(getRole());
            temp.setTestProject(getTestProject());
            temp.setVmUser(getVmUser());
            controller.edit(temp);
        } else {
            UserTestProjectRole temp = new UserTestProjectRole(
                    getUserTestProjectRolePK().getTestProjectId(),
                    getUserTestProjectRolePK().getUserId(),
                    getUserTestProjectRolePK().getRoleId());
            temp.setRole(getRole());
            temp.setTestProject(getTestProject());
            temp.setVmUser(getVmUser());
            controller.create(temp);
            setUserTestProjectRolePK(temp.getUserTestProjectRolePK());
        }
        return getUserTestProjectRolePK().getRoleId();
    }

    /**
     * Delete UserTestProjectRole
     *
     * @param role UserTestProjectRole to delete
     * @return true if successful
     */
    public static boolean deleteUserTestProjectRole(UserTestProjectRole role) {
        try {
            new UserTestProjectRoleJpaController(
                    getEntityManagerFactory()).destroy(
                    role.getUserTestProjectRolePK());
        }
        catch (NonexistentEntityException ex) {
            getLogger(UserTestProjectRoleServer.class.getName())
                    .log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public UserTestProjectRole getEntity() {
        return new UserTestProjectRoleJpaController(
                getEntityManagerFactory())
                .findUserTestProjectRole(getUserTestProjectRolePK());
    }

    @Override
    public void update(UserTestProjectRole target, UserTestProjectRole source) {
        target.setRole(source.getRole());
        target.setTestProject(source.getTestProject());
        target.setVmUser(source.getVmUser());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
