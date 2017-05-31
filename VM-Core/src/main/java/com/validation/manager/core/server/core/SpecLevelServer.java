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
import com.validation.manager.core.db.SpecLevel;
import com.validation.manager.core.db.controller.SpecLevelJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class SpecLevelServer extends SpecLevel implements EntityServer<SpecLevel> {

    public SpecLevelServer(int id) {
        super.setId(id);
        update();
    }

    public SpecLevelServer(String name, String description) {
        super(name, description);
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        if (getId() == null) {
            SpecLevel sl = new SpecLevel();
            update(sl, this);
            new SpecLevelJpaController(
                    getEntityManagerFactory()).create(sl);
            setId(sl.getId());
        } else {
            SpecLevel sl = new SpecLevelJpaController(
                    getEntityManagerFactory()).findSpecLevel(getId());
            update(sl, this);
            new SpecLevelJpaController(
                    getEntityManagerFactory()).edit(sl);
        }
        update();
        return getId();
    }

    public static void deleteSpecLevel(SpecLevel sl)
            throws IllegalOrphanException, NonexistentEntityException {
        new SpecLevelJpaController(
                getEntityManagerFactory()).destroy(sl.getId());
    }

    @Override
    public SpecLevel getEntity() {
        return new SpecLevelJpaController(
                getEntityManagerFactory()).findSpecLevel(getId());
    }

    @Override
    public void update(SpecLevel target, SpecLevel source) {
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        if (source.getRequirementSpecList() != null) {
            target.setRequirementSpecList(source.getRequirementSpecList());
        }
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public static List<SpecLevel> getLevels() {
        return new SpecLevelJpaController(getEntityManagerFactory()).findSpecLevelEntities();
    }
}
