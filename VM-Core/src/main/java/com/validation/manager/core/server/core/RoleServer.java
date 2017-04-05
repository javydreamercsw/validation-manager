package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Role;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RoleServer extends Role {

    private static final Map<String, Object> PARAMETERS = new HashMap<>();

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
}
