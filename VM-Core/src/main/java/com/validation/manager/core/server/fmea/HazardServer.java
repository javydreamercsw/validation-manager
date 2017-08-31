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
import com.validation.manager.core.db.Hazard;
import com.validation.manager.core.db.controller.HazardJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class HazardServer extends Hazard implements EntityServer<Hazard> {

    public HazardServer(String name, String description) {
        super(name, description);
    }

    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        if (getId() == null) {
            Hazard h = new Hazard();
            update(h, this);
            new HazardJpaController(
                    getEntityManagerFactory()).create(h);
            setId(h.getId());
        } else {
            Hazard h = new HazardJpaController(
                    getEntityManagerFactory())
                    .findHazard(getId());
            update(h, this);
            new HazardJpaController(
                    getEntityManagerFactory()).edit(h);
        }
        return getId();
    }

    public static boolean deleteHazard(Hazard h)
            throws NonexistentEntityException, IllegalOrphanException {
        new HazardJpaController(
                getEntityManagerFactory()).destroy(h.getId());
        return true;
    }

    @Override
    public Hazard getEntity() {
        return new HazardJpaController(
                getEntityManagerFactory()).findHazard(getId());
    }

    @Override
    public void update(Hazard target, Hazard source) {
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setId(source.getId());
        target.setRiskItemHasHazardList(source.getRiskItemHasHazardList());
    }
    
    @Override
    public void update() {
        update(this, getEntity());
    }
}
