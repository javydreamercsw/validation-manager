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
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Cause;
import com.validation.manager.core.db.controller.CauseJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class CauseServer extends Cause implements EntityServer<Cause> {

    public CauseServer(String name, String description) {
        super(name, description);
        setId(0);
    }

    @Override
    public int write2DB() throws VMException {
        try {
            if (getId() > 0) {
                Cause c = new CauseJpaController(
                        getEntityManagerFactory())
                        .findCause(getId());
                update(c, this);
                new CauseJpaController(
                        getEntityManagerFactory()).edit(c);
            } else {
                Cause c = new Cause();
                update(c, this);
                new CauseJpaController(
                        getEntityManagerFactory()).create(c);
                setId(c.getId());
            }
        }
        catch (Exception ex) {
            throw new VMException(ex);
        }
        return getId();
    }

    public static boolean deleteCause(Cause c)
            throws NonexistentEntityException, IllegalOrphanException {
        new CauseJpaController(
                getEntityManagerFactory()).destroy(c.getId());
        return true;
    }

    @Override
    public Cause getEntity() {
        return new CauseJpaController(
                getEntityManagerFactory()).findCause(getId());
    }

    @Override
    public void update(Cause target, Cause source) {
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setId(source.getId());
        target.setFailureModeHasCauseList(source.getFailureModeHasCauseList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
