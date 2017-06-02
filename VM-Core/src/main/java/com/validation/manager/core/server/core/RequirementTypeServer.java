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
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.controller.RequirementTypeJpaController;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RequirementTypeServer extends RequirementType
        implements EntityServer<RequirementType> {

    public RequirementTypeServer(RequirementType rt) {
        RequirementType temp
                = new RequirementTypeJpaController(
                        getEntityManagerFactory())
                        .findRequirementType(rt.getId());
        update((RequirementType) this, temp);
    }

    public RequirementTypeServer(String name) {
        super(name);
        setId(0);
        setRequirementList(new ArrayList<>());
    }

    @Override
    public int write2DB() throws Exception {
        RequirementType rt;
        if (getId() > 0) {
            rt = new RequirementTypeJpaController(getEntityManagerFactory()).findRequirementType(getId());
            update(rt, this);
            new RequirementTypeJpaController(getEntityManagerFactory()).edit(rt);
        } else {
            rt = new RequirementType(getName());
            update(rt, this);
            new RequirementTypeJpaController(getEntityManagerFactory()).create(rt);
            setId(rt.getId());
        }
        update();
        return getId();
    }

    @Override
    public RequirementType getEntity() {
        return new RequirementTypeJpaController(
                getEntityManagerFactory())
                .findRequirementType(getId());
    }

    @Override
    public void update(RequirementType target, RequirementType source) {
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setId(source.getId());
        target.setRequirementList(source.getRequirementList());
        target.setLevel(source.getLevel());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public static List<RequirementType> getRequirementTypes() {
        return new RequirementTypeJpaController(
                getEntityManagerFactory())
                .findRequirementTypeEntities();
    }
}
