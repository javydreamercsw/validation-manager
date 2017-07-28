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
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.UserTestProjectRole;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.TestProjectJpaController;
import com.validation.manager.core.db.controller.UserTestProjectRoleJpaController;
import java.util.ArrayList;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TestProjectServer extends TestProject
        implements EntityServer<TestProject> {

    public TestProjectServer(String name, boolean active) {
        super(name, active);
        setProjectList(new ArrayList<>());
        setTestPlanList(new ArrayList<>());
        setUserTestProjectRoleList(new ArrayList<>());
    }

    public TestProjectServer(int id) {
        super.setId(id);
        update();
    }

    public TestProjectServer(TestProject tp) {
        super.setId(tp.getId());
        update();
    }

    @Override
    public int write2DB() throws VMException {
        try {
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
        }
        catch (Exception ex) {
            throw new VMException(ex);
        }
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
