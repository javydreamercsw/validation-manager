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
import com.validation.manager.core.db.RiskControl;
import com.validation.manager.core.db.RiskControlPK;
import com.validation.manager.core.db.controller.RiskControlJpaController;
import com.validation.manager.core.db.controller.RiskControlTypeJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.logging.Level;
import static java.util.logging.Logger.getLogger;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RiskControlServer extends RiskControl
        implements EntityServer<RiskControl> {

    public RiskControlServer(RiskControlPK riskControlPK) {
        super(riskControlPK);
        setRiskControlType(new RiskControlTypeJpaController(
                getEntityManagerFactory()).findRiskControlType(
                riskControlPK.getRiskControlTypeId()));
        update();
    }

    public RiskControlServer(RiskControl riskControl) {
        super(riskControl.getRiskControlPK());
        setRiskControlType(new RiskControlTypeJpaController(
                getEntityManagerFactory()).findRiskControlType(
                riskControlPK.getRiskControlTypeId()));
    }

    public RiskControlServer(int riskControlTypeId) {
        super(new RiskControlPK(riskControlTypeId));
        setRiskControlType(new RiskControlTypeJpaController(
                getEntityManagerFactory()).findRiskControlType(riskControlTypeId));
    }

    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        RiskControlJpaController controller
                = new RiskControlJpaController(getEntityManagerFactory());
        if (getRiskControlPK().getId() > 0) {
            RiskControl target = controller.findRiskControl(getRiskControlPK());
            update(target, this);
            controller.edit(target);
        } else {
            RiskControl target = new RiskControl(getRiskControlPK());
            update(target, this);
            controller.create(target);
            setRiskControlPK(target.getRiskControlPK());
        }
        return getRiskControlPK().getId();
    }

    public static boolean deleteRiskControl(RiskControl rc) {
        try {
            new RiskControlJpaController(
                    getEntityManagerFactory())
                    .destroy(rc.getRiskControlPK());
            return true;
        }
        catch (NonexistentEntityException | IllegalOrphanException ex) {
            getLogger(RiskControlServer.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        return false;
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
        target.setRiskControlHasTestCaseList(source
                .getRiskControlHasTestCaseList());
        target.setRiskItemList(source.getRiskItemList());
        target.setRiskItemList1(source.getRiskItemList1());
        target.setRiskControlType(source.getRiskControlType());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
