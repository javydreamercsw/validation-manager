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
import com.validation.manager.core.db.RiskItem;
import com.validation.manager.core.db.controller.FmeaJpaController;
import com.validation.manager.core.db.controller.RiskItemJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RiskItemServer extends RiskItem implements EntityServer<RiskItem> {

    public RiskItemServer(int fMEAid, int sequence, int version) {
        super(fMEAid);
        setSequence(sequence);
        setVersion(version);
        setFmea(new FmeaJpaController(
                getEntityManagerFactory()).findFmea(fMEAid));
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        if (getRiskItemPK() != null && getRiskItemPK().getId() > 0) {
            RiskItem ri = getEntity();
            update(ri, this);
            new RiskItemJpaController(
                    getEntityManagerFactory()).edit(ri);
        } else {
            RiskItem ri = new RiskItem(getFmea().getId());
            update(ri, this);
            new RiskItemJpaController(
                    getEntityManagerFactory()).create(ri);
            setRiskItemPK(ri.getRiskItemPK());
        }
        return getRiskItemPK().getId();
    }

    @Override
    public RiskItem getEntity() {
        return new RiskItemJpaController(
                getEntityManagerFactory())
                .findRiskItem(getRiskItemPK());
    }

    @Override
    public void update(RiskItem target, RiskItem source) {
        if (source.getCauseList() != null) {
            target.setCauseList(source.getCauseList());
        }
        if (source.getFailureModeList() != null) {
            target.setFailureModeList(source.getFailureModeList());
        }
        target.setFmea(new FmeaJpaController(
                getEntityManagerFactory())
                .findFmea(source.getRiskItemPK().getFMEAid()));
        if (source.getHazardList() != null) {
            target.setHazardList(source.getHazardList());
        }
        if (source.getRiskControlList() != null) {
            target.setRiskControlList(source.getRiskControlList());
        }
        if (source.getRiskControlList1() != null) {
            target.setRiskControlList1(source.getRiskControlList1());
        }
        target.setSequence(source.getSequence());
        target.setVersion(source.getVersion());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
