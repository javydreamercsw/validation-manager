package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.controller.RoleJpaController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class RoleServer extends Role implements EntityServer<Role> {

    private static final Map<String, Object> PARAMETERS = new HashMap<>();
    private final RoleJpaController c
            = new RoleJpaController(DataBaseManager.getEntityManagerFactory());

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
    public int write2DB() throws Exception {
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
    }
}
