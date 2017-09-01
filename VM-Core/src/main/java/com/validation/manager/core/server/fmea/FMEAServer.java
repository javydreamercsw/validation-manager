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
import com.validation.manager.core.db.Fmea;
import com.validation.manager.core.db.FmeaPK;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.controller.FmeaJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class FMEAServer extends Fmea implements EntityServer<Fmea> {

    public FMEAServer(String name, Project p) {
        super(name);
        setFmeaPK(new FmeaPK(0, p.getId()));
        setProject(p);
        setFmeaList(new ArrayList<>());
        setRiskItemList(new ArrayList<>());
        setRiskCategoryList(new ArrayList<>());
        setFmeaList(new ArrayList<>());
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        if (getFmeaPK().getId() > 0) {
            Fmea fmea = new FmeaJpaController(
                    getEntityManagerFactory()).findFmea(getFmeaPK());
            update(fmea, this);
            new FmeaJpaController(
                    getEntityManagerFactory()).edit(fmea);
        } else {
            Fmea fmea = new Fmea(getName());
            update(fmea, this);
            new FmeaJpaController(
                    getEntityManagerFactory()).create(fmea);
            setFmeaPK(fmea.getFmeaPK());
        }
        return getFmeaPK().getId();
    }

    public static boolean deleteFMEA(FmeaPK id) {
        boolean result = false;
        try {
            new FmeaJpaController(
                    getEntityManagerFactory()).destroy(id);
            result = true;
        }
        catch (NonexistentEntityException | IllegalOrphanException ex) {
            getLogger(FMEAServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public Fmea getEntity() {
        return new FmeaJpaController(
                getEntityManagerFactory()).findFmea(getFmeaPK());
    }

    @Override
    public void update(Fmea target, Fmea source) {
        target.setFmeaList(source.getFmeaList());
        target.setParent(source.getParent());
        target.setRiskItemList(source.getRiskItemList());
        target.setProject(source.getProject());
        target.setFmeaPK(source.getFmeaPK());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setRiskCategoryList(source.getRiskCategoryList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
