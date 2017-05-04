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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class RiskControlServer extends RiskControl
        implements EntityServer<RiskControl> {

    public RiskControlServer(RiskControlPK riskControlPK) {
        super(riskControlPK);
        setRiskControlType(new RiskControlTypeJpaController(
                getEntityManagerFactory()).findRiskControlType(
                riskControlPK.getRiskControlTypeId()));
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
