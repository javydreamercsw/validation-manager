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
import com.validation.manager.core.db.RiskControl;
import com.validation.manager.core.db.controller.RiskControlJpaController;
import com.validation.manager.core.db.controller.RiskControlTypeJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RiskControlServer extends RiskControl
        implements EntityServer<RiskControl> {

    public RiskControlServer(int riskControlTypeId) {
        super(riskControlTypeId);
        setRiskControlType(new RiskControlTypeJpaController(
                getEntityManagerFactory()).
                findRiskControlType(riskControlTypeId));
    }

    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        if (getRiskControlPK() != null && getRiskControlPK().getId() > 0) {
            RiskControl rc = new RiskControlJpaController(
                    getEntityManagerFactory())
                    .findRiskControl(getRiskControlPK());
            update(rc, this);
            new RiskControlJpaController(
                    getEntityManagerFactory()).edit(rc);
        } else {
            RiskControl rc = new RiskControl();
            update(rc, this);
            new RiskControlJpaController(
                    getEntityManagerFactory()).create(rc);
            setRiskControlPK(rc.getRiskControlPK());
        }
        return getRiskControlPK().getId();
    }

    public static boolean deleteRiskControl(RiskControl rc)
            throws NonexistentEntityException, IllegalOrphanException {
        new RiskControlJpaController(
                getEntityManagerFactory())
                .destroy(rc.getRiskControlPK());
        return true;
    }

    @Override
    public RiskControl getEntity() {
        return new RiskControlJpaController(
                getEntityManagerFactory())
                .findRiskControl(getRiskControlPK());
    }

    @Override
    public void update(RiskControl target, RiskControl source) {
        target.setRiskControlHasRequirementList(source
                .getRiskControlHasRequirementList());
        target.setRiskControlType(source.getRiskControlType());
        target.setRiskItemList(source.getRiskItemList());
        target.setRiskItemList1(source.getRiskItemList1());
        target.setRiskControlHasTestCaseList(source
                .getRiskControlHasTestCaseList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
