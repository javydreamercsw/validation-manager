package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
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
    public int write2DB() throws NonexistentEntityException, Exception {
        UserTestPlanRoleJpaController controller
                = new UserTestPlanRoleJpaController(getEntityManagerFactory());
        if (controller.findUserTestPlanRole(getUserTestPlanRolePK()) == null) {
            UserTestPlanRole temp = new UserTestPlanRole(getTestPlan(), getVmUser(),
                    getRole());
            update(temp, this);
            controller.create(temp);
            update(this, temp);
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
