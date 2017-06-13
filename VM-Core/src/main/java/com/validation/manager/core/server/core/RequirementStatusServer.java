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
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.controller.RequirementStatusJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RequirementStatusServer extends RequirementStatus
        implements EntityServer<RequirementStatus> {

    public RequirementStatusServer(Integer id) {
        RequirementStatusJpaController controller
                = new RequirementStatusJpaController(
                        getEntityManagerFactory());
        RequirementStatus rs = controller.findRequirementStatus(id);
        if (rs != null) {
            update(RequirementStatusServer.this, rs);
        }
    }

    public RequirementStatusServer(String status) {
        super(status);
    }

    @Override
    public int write2DB() throws Exception {
        RequirementStatus p;
        RequirementStatusJpaController controller
                = new RequirementStatusJpaController(
                        getEntityManagerFactory());
        if (getId() == null) {
            p = new RequirementStatus(getStatus());
            update(p, this);
            controller.create(p);
            setId(p.getId());
        } else {
            p = controller.findRequirementStatus(getId());
            update(p, this);
            controller.edit(p);
        }
        return getId();
    }

    @Override
    public RequirementStatus getEntity() {
        return new RequirementStatusJpaController(
                getEntityManagerFactory())
                .findRequirementStatus(getId());
    }

    @Override
    public void update(RequirementStatus target, RequirementStatus source) {
        target.setId(source.getId());
        target.setStatus(source.getStatus());
        target.setRequirementList(source.getRequirementList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
