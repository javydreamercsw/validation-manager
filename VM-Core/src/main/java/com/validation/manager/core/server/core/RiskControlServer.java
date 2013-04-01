package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.controller.RiskControlJpaController;
import com.validation.manager.core.db.controller.RiskControlTypeJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.fmea.RiskControl;
import com.validation.manager.core.db.fmea.RiskControlPK;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RiskControlServer extends RiskControl implements EntityServer {

    public RiskControlServer(RiskControlPK riskControlPK) {
        super(riskControlPK);
        setRiskControlType(new RiskControlTypeJpaController(DataBaseManager.getEntityManagerFactory()).findRiskControlType(riskControlPK.getRiskControlTypeId()));
    }

    public RiskControlServer(int riskControlTypeId) {
        super(new RiskControlPK(riskControlTypeId));
        setRiskControlType(new RiskControlTypeJpaController(DataBaseManager.getEntityManagerFactory()).findRiskControlType(riskControlTypeId));
    }

    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        RiskControlJpaController controller = new RiskControlJpaController(DataBaseManager.getEntityManagerFactory());
        if (getRiskControlPK().getId() > 0) {
            RiskControl temp = controller.findRiskControl(getRiskControlPK());
            temp.setRiskItemList(getRiskItemList());
            temp.setRiskItemList1(getRiskItemList1());
            temp.setTestCaseList(getTestCaseList());
            temp.setRequirementList(getRequirementList());
            temp.setRiskControlType(getRiskControlType());
            controller.edit(temp);
        } else {
            RiskControl temp = new RiskControl(getRiskControlPK());
            temp.setRiskItemList(getRiskItemList());
            temp.setRiskItemList1(getRiskItemList1());
            temp.setTestCaseList(getTestCaseList());
            temp.setRequirementList(getRequirementList());
            temp.setRiskControlType(getRiskControlType());
            controller.create(temp);
            setRiskControlPK(temp.getRiskControlPK());
        }
        return getRiskControlPK().getId();
    }

    public static boolean deleteRiskControl(RiskControl rc) {
        try {
            new RiskControlJpaController(
                    DataBaseManager.getEntityManagerFactory())
                    .destroy(rc.getRiskControlPK());
            return true;
        }catch (NonexistentEntityException ex) {
            Logger.getLogger(RiskControlServer.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        return false;
    }

    public RiskControl getEntity() {
        return new RiskControlJpaController(
                    DataBaseManager.getEntityManagerFactory())
                    .findRiskControl(getRiskControlPK());
    }
}
