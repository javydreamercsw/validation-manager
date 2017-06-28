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

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.controller.RoleJpaController;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RoleServer extends Role implements EntityServer<Role> {

    private final RoleJpaController c
            = new RoleJpaController(DataBaseManager.getEntityManagerFactory());

    public RoleServer(int id) {
        setId(id);
        update();
    }

    public RoleServer(String description) {
        super(description);
        update(RoleServer.this, getRole(description));
    }

    public static Role getRole(String role) {
        Role r = null;
        PARAMETERS.clear();
        PARAMETERS.put("name", role);
        List<Object> result = DataBaseManager.namedQuery("Role.findByName",
                PARAMETERS);
        if (!result.isEmpty()) {
            r = (Role) result.get(0);
        }
        return r;
    }

    @Override
    public int write2DB() throws VMException {
        try {
            if (getId() == null) {
                Role r = new Role();
                update(r, this);
                c.create(r);
                update(this, r);
            } else {
                Role r = getEntity();
                update(r, this);
                c.edit(r);
                update(this, r);
            }
        }
        catch (Exception ex) {
            throw new VMException(ex);
        }
        return getId();
    }

    @Override
    public void update() {
        update(RoleServer.this, getEntity());
    }

    @Override
    public Role getEntity() {
        return getId() == null ? null : c.findRole(getId());
    }

    @Override
    public void update(Role target, Role source) {
        target.setDescription(source.getDescription());
        target.setId(source.getId());
        target.setRoleName(source.getRoleName());
        target.setUserRightList(source.getUserRightList());
        target.setUserTestPlanRoleList(source.getUserTestPlanRoleList());
        target.setVmUserList(source.getVmUserList());
        target.setUserHasRoleList(source.getUserHasRoleList());
    }
}
