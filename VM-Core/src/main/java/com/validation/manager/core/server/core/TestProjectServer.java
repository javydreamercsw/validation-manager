package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.UserTestProjectRole;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.UserTestProjectRoleJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TestProjectServer extends TestProject
        implements EntityServer<TestProject> {

    public TestProjectServer(String name, boolean active) {
        super(name, active);
    }

    public TestProjectServer(TestProject tp) {
        super.setId(tp.getId());
        update();
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        TestProject tp;
        if (getId() == null) {
            tp = new TestProject();
            update(tp, this);
            new TestProjectJpaController(getEntityManagerFactory()).create(tp);
            setId(tp.getId());
        } else {
            tp = getEntity();
            update(tp, this);
            new TestProjectJpaController(getEntityManagerFactory()).edit(tp);
        }
        update();
        return getId();
    }

    @Override
    public TestProject getEntity() {
        return new TestProjectJpaController(
                getEntityManagerFactory())
                .findTestProject(getId());
    }

    @Override
    public void update(TestProject target, TestProject source) {
        target.setActive(source.getActive());
        target.setName(source.getName());
        target.setNotes(source.getNotes());
        target.setId(source.getId());
        target.setProjectList(source.getProjectList());
        target.setTestPlanList(source.getTestPlanList());
        target.setUserTestProjectRoleList(source.getUserTestProjectRoleList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public UserTestProjectRole addUserTestProjectRole(TestProject tp, VmUser user, Role role)
            throws Exception {
        UserTestProjectRole temp = new UserTestProjectRole(tp.getId(),
                user.getId(), role.getId());
        temp.setVmUser(user);
        temp.setRole(role);
        temp.setTestProject(tp);
        UserTestProjectRoleJpaController controller
                = new UserTestProjectRoleJpaController(DataBaseManager
                        .getEntityManagerFactory());
        if (controller.findUserTestProjectRole(temp
                .getUserTestProjectRolePK()) == null) {
            controller.create(temp);
            update();
        }
        return temp;
    }
}
