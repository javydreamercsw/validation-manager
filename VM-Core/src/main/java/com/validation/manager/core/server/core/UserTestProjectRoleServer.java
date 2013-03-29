package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.UserTestProjectRole;
import com.validation.manager.core.db.controller.RoleJpaController;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.UserTestProjectRoleJpaController;
import com.validation.manager.core.db.controller.VmUserJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UserTestProjectRoleServer extends UserTestProjectRole implements EntityServer {

    public UserTestProjectRoleServer(int testProjectId, int userId, int roleId) {
        super(testProjectId, userId, roleId);
        setRole(new RoleJpaController( DataBaseManager.getEntityManagerFactory()).findRole(roleId));
        setTestProject(new TestProjectJpaController( DataBaseManager.getEntityManagerFactory()).findTestProject(testProjectId));
        setVmUser(new VmUserJpaController( DataBaseManager.getEntityManagerFactory()).findVmUser(userId));
    }

    /**
     * Persist to database
     *
     * @throws PreexistingEntityException
     * @throws Exception
     */
    @Override
    public int write2DB() throws PreexistingEntityException, Exception {
        UserTestProjectRoleJpaController controller = new UserTestProjectRoleJpaController( DataBaseManager.getEntityManagerFactory());
        if (getUserTestProjectRolePK().getRoleId() > 0
                && getUserTestProjectRolePK().getTestProjectId() > 0
                && getUserTestProjectRolePK().getUserId() > 0) {
            UserTestProjectRole temp = controller.findUserTestProjectRole(getUserTestProjectRolePK());
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
            new UserTestProjectRoleJpaController(DataBaseManager.getEntityManagerFactory()).destroy(role.getUserTestProjectRolePK());
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(UserTestProjectRoleServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
}
