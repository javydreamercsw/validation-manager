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
package com.validation.manager.core.server.fmea;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.FailureMode;
import com.validation.manager.core.db.controller.FailureModeJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class FailureModeServer extends FailureMode
        implements EntityServer<FailureMode> {

    public FailureModeServer(String name, String description) {
        super(name, description);
    }

    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        if (getId() == null) {
            FailureMode fm = new FailureMode();
            update(fm, this);
            new FailureModeJpaController(
                    getEntityManagerFactory()).create(fm);
            setId(fm.getId());
        } else {
            FailureMode fm = new FailureModeJpaController(
                    getEntityManagerFactory())
                    .findFailureMode(getId());
            update(fm, this);
            new FailureModeJpaController(
                    getEntityManagerFactory()).edit(fm);
        }
        return getId();
    }

    public static boolean deleteFailureMode(FailureMode fm)
            throws NonexistentEntityException, IllegalOrphanException {
        new FailureModeJpaController(
                getEntityManagerFactory()).destroy(fm.getId());
        return true;
    }

    @Override
    public FailureMode getEntity() {
        return new FailureModeJpaController(
                getEntityManagerFactory()).findFailureMode(getId());
    }

    @Override
    public void update(FailureMode target, FailureMode source) {
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setId(source.getId());
        target.setHazardHasFailureModeList(source.getHazardHasFailureModeList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
