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
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.UserTestPlanRole;
import com.validation.manager.core.db.UserTestPlanRolePK;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.UserTestPlanRoleJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class UserTestPlanRoleServer extends UserTestPlanRole
        implements EntityServer<UserTestPlanRole> {

    public UserTestPlanRoleServer(TestPlan tpl, VmUser user, Role role) {
        super(tpl, user, role);
    }

    public UserTestPlanRoleServer(UserTestPlanRolePK pk) {
        super.setUserTestPlanRolePK(pk);
        update();
    }

    @Override
    public int write2DB() throws VMException {
        try {
            UserTestPlanRoleJpaController controller
                    = new UserTestPlanRoleJpaController(getEntityManagerFactory());
            if (controller.findUserTestPlanRole(getUserTestPlanRolePK()) == null) {
                UserTestPlanRole temp = new UserTestPlanRole(getTestPlan(), getVmUser(),
                        getRole());
                update(temp, this);
                controller.create(temp);
                update(this, temp);
            }
        }
        catch (Exception ex) {
            throw new VMException(ex);
        }
        return getUserTestPlanRolePK().getTestPlanTestProjectId();
    }

    public static boolean deleteUserTestPlanRole(UserTestPlanRole utpr) {
        try {
            new UserTestPlanRoleJpaController(
                    getEntityManagerFactory()).destroy(
                    utpr.getUserTestPlanRolePK());
        }
        catch (NonexistentEntityException ex) {
            getLogger(UserTestPlanRoleServer.class.getName())
                    .log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public UserTestPlanRole getEntity() {
        return new UserTestPlanRoleJpaController(
                getEntityManagerFactory())
                .findUserTestPlanRole(getUserTestPlanRolePK());
    }

    @Override
    public void update(UserTestPlanRole target, UserTestPlanRole source) {
        target.setRole(source.getRole());
        target.setTestPlan(source.getTestPlan());
        target.setVmUser(source.getVmUser());
        target.setUserTestPlanRolePK(source.getUserTestPlanRolePK());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
