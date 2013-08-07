package com.validation.manager.core.server.fmea;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.controller.RiskControlJpaController;
import com.validation.manager.core.db.controller.RiskControlTypeJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.fmea.RiskControl;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RiskControlServer extends RiskControl
        implements EntityServer<RiskControl> {

    public RiskControlServer(int riskControlTypeId) {
        super(riskControlTypeId);
        setRiskControlType(new RiskControlTypeJpaController(
                DataBaseManager.getEntityManagerFactory()).
                findRiskControlType(riskControlTypeId));
    }

    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        if (getRiskControlPK() != null && getRiskControlPK().getId() > 0) {
            RiskControl rc = new RiskControlJpaController(
                    DataBaseManager.getEntityManagerFactory())
                    .findRiskControl(getRiskControlPK());
            update(rc, this);
            new RiskControlJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(rc);
        } else {
            RiskControl rc = new RiskControl();
            update(rc, this);
            new RiskControlJpaController(
                    DataBaseManager.getEntityManagerFactory()).create(rc);
            setRiskControlPK(rc.getRiskControlPK());
        }
        return getRiskControlPK().getId();
    }

    public static boolean deleteRiskControl(RiskControl rc)
            throws NonexistentEntityException, IllegalOrphanException {
        new RiskControlJpaController(
                DataBaseManager.getEntityManagerFactory())
                .destroy(rc.getRiskControlPK());
        return true;
    }

    public RiskControl getEntity() {
        return new RiskControlJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findRiskControl(getRiskControlPK());
    }

    public void update(RiskControl target, RiskControl source) {
        target.setRequirementList(source.getRequirementList());
        target.setRiskControlType(source.getRiskControlType());
        target.setRiskItemList(source.getRiskItemList());
        target.setRiskItemList1(source.getRiskItemList1());
        target.setTestCaseList(source.getTestCaseList());
    }
    
    public void update() {
        update(this, getEntity());
    }
}
