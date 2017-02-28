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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
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
