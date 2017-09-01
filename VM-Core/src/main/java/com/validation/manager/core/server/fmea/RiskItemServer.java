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

import com.validation.manager.core.DataBaseManager;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Cause;
import com.validation.manager.core.db.FailureMode;
import com.validation.manager.core.db.FailureModeHasCause;
import com.validation.manager.core.db.FailureModeHasCauseHasRiskCategory;
import com.validation.manager.core.db.Fmea;
import com.validation.manager.core.db.FmeaPK;
import com.validation.manager.core.db.Hazard;
import com.validation.manager.core.db.HazardHasFailureMode;
import com.validation.manager.core.db.RiskItem;
import com.validation.manager.core.db.RiskItemHasHazard;
import com.validation.manager.core.db.controller.FailureModeHasCauseHasRiskCategoryJpaController;
import com.validation.manager.core.db.controller.FailureModeHasCauseJpaController;
import com.validation.manager.core.db.controller.FmeaJpaController;
import com.validation.manager.core.db.controller.HazardHasFailureModeJpaController;
import com.validation.manager.core.db.controller.RiskItemHasHazardJpaController;
import com.validation.manager.core.db.controller.RiskItemJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RiskItemServer extends RiskItem implements EntityServer<RiskItem> {

    public RiskItemServer(FmeaPK fMEAid, String desc) {
        super(fMEAid);
        setDescription(desc);
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
            RiskItem ri = new RiskItem(getFmea().getFmeaPK());
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
        target.setFmea(new FmeaJpaController(
                getEntityManagerFactory())
                .findFmea(new FmeaPK(source.getRiskItemPK().getFMEAid(),
                        source.getRiskItemPK().getFMEAprojectid())));
        target.setDescription(source.getDescription());
        target.setRiskItemHasHazardList(source.getRiskItemHasHazardList());
        target.setRiskItemPK(source.getRiskItemPK());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public void addHazard(Hazard hazard, List<FailureMode> fms,
            List<Cause> causes) throws Exception {
        RiskItemHasHazard rihh = new RiskItemHasHazard(getRiskItemPK().getId(),
                getRiskItemPK().getFMEAid(),
                getRiskItemPK().getFMEAprojectid(),
                hazard.getId());
        rihh.setHazard(hazard);
        rihh.setRiskItem(getEntity());
        new RiskItemHasHazardJpaController(DataBaseManager
                .getEntityManagerFactory()).create(rihh);
        if (fms != null) {
            for (FailureMode fm : fms) {
                HazardHasFailureMode hhfm
                        = new HazardHasFailureMode(getRiskItemPK().getId(),
                                getRiskItemPK().getFMEAid(),
                                getRiskItemPK().getFMEAprojectid(),
                                hazard.getId(),
                                fm.getId());
                hhfm.setFailureMode(fm);
                hhfm.setRiskItemHasHazard(rihh);
                new HazardHasFailureModeJpaController(DataBaseManager
                        .getEntityManagerFactory()).create(hhfm);
                if (causes != null) {
                    Fmea fmea = new FmeaJpaController(DataBaseManager
                            .getEntityManagerFactory())
                            .findFmea(new FmeaPK(getRiskItemPK().getFMEAid(),
                                    getRiskItemPK().getFMEAprojectid()));
                    causes.forEach(cause -> {
                        try {
                            FailureModeHasCause fmhc
                                    = new FailureModeHasCause(getRiskItemPK().getId(),
                                            getRiskItemPK().getFMEAid(),
                                            getRiskItemPK().getFMEAprojectid(),
                                            hazard.getId(),
                                            fm.getId(),
                                            cause.getId());
                            fmhc.setCause(cause);
                            fmhc.setHazardHasFailureMode(hhfm);
                            new FailureModeHasCauseJpaController(DataBaseManager
                                    .getEntityManagerFactory()).create(fmhc);
                            FailureModeHasCauseHasRiskCategoryJpaController c
                                    = new FailureModeHasCauseHasRiskCategoryJpaController(
                                            DataBaseManager.getEntityManagerFactory());
                            fmea.getRiskCategoryList().forEach(rc -> {
                                try {
                                    FailureModeHasCauseHasRiskCategory fmhchrc
                                            = new FailureModeHasCauseHasRiskCategory(
                                                    getRiskItemPK().getId(),
                                                    getRiskItemPK().getFMEAid(),
                                                    getRiskItemPK().getFMEAprojectid(),
                                                    hazard.getId(),
                                                    fm.getId(),
                                                    cause.getId(),
                                                    rc.getId());
                                    fmhchrc.setFailureModeHasCause(fmhc);
                                    fmhchrc.setRiskCategory(rc);
                                    c.create(fmhchrc);
                                }
                                catch (Exception ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            });
                        }
                        catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    });
                }
            }
        }
        update();
    }
}
