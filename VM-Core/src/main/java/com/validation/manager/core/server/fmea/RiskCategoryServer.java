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
import com.validation.manager.core.db.RiskCategory;
import com.validation.manager.core.db.controller.RiskCategoryJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RiskCategoryServer extends RiskCategory
        implements EntityServer<RiskCategory> {

    public RiskCategoryServer(String name, int min, int max) {
        super(name, min, max);
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        if (getId() == null) {
            RiskCategory rc = new RiskCategory(getName(), getMinimum(),
                    getMaximum());
            update(rc, this);
            new RiskCategoryJpaController(
                    getEntityManagerFactory()).create(rc);
            setId(rc.getId());
        } else {
            RiskCategory rc = new RiskCategoryJpaController(
                    getEntityManagerFactory())
                    .findRiskCategory(getId());
            update(rc, this);
            new RiskCategoryJpaController(
                    getEntityManagerFactory()).edit(rc);
        }
        update();
        return getId();
    }

    public static boolean deleteRiskCategory(RiskCategory rc)
            throws IllegalOrphanException, NonexistentEntityException {
        new RiskCategoryJpaController(
                getEntityManagerFactory()).destroy(rc.getId());
        return true;
    }

    @Override
    public RiskCategory getEntity() {
        return new RiskCategoryJpaController(
                getEntityManagerFactory()).findRiskCategory(getId());
    }

    @Override
    public void update(RiskCategory target, RiskCategory source) {
        target.setMaximum(source.getMaximum());
        target.setMinimum(source.getMinimum());
        target.setFailureModeHasCauseHasRiskCategoryList(source
                .getFailureModeHasCauseHasRiskCategoryList());
        target.setFailureModeHasCauseList(source.getFailureModeHasCauseList());
        target.setFmeaList(source.getFmeaList());
        target.setCategoryEquation(source.getCategoryEquation());
        target.setId(source.getId());
    }
    
    @Override
    public void update() {
        update(this, getEntity());
    }
}
