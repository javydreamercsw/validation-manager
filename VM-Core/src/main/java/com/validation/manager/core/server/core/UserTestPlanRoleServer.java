package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.UserTestPlanRole;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.UserTestPlanRoleJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UserTestPlanRoleServer extends UserTestPlanRole implements EntityServer {
    
    public UserTestPlanRoleServer(TestPlan tpl, VmUser user, Role role) {
        super(tpl, user, role);
        setTestPlan(tpl);
        setVmUser(user);
        setRole(role);
    }
    
    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        UserTestPlanRoleJpaController controller = new UserTestPlanRoleJpaController(DataBaseManager.getEntityManagerFactory());
        UserTestPlanRole temp = new UserTestPlanRole(getTestPlan(), getVmUser(), getRole());
        temp.setRole(getRole());
        temp.setTestPlan(getTestPlan());
        temp.setVmUser(getVmUser());
        controller.create(temp);
        return getUserTestPlanRolePK().getTestPlanTestProjectId();
    }
    
    public static boolean deleteUserTestPlanRole(UserTestPlanRole utpr) {
        try {
            new UserTestPlanRoleJpaController(DataBaseManager.getEntityManagerFactory()).destroy(utpr.getUserTestPlanRolePK());
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(UserTestPlanRoleServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    public UserTestPlanRole getEntity() {
        return new UserTestPlanRoleJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findUserTestPlanRole(getUserTestPlanRolePK());
    }
}
