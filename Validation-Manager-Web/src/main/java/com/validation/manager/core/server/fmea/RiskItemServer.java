package com.validation.manager.core.server.fmea;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.controller.FMEAJpaController;
import com.validation.manager.core.db.controller.RiskItemJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.fmea.RiskItem;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RiskItemServer extends RiskItem implements EntityServer{

    public RiskItemServer(int fMEAid, int sequence, int version) {
        super(fMEAid);
        setSequence(sequence);
        setVersion(version);
        setFmea(new FMEAJpaController( DataBaseManager.getEntityManagerFactory()).findFMEA(fMEAid));
    }

    @Override
    public int write2DB() throws IllegalOrphanException, NonexistentEntityException, Exception {
        if (getRiskItemPK() != null && getRiskItemPK().getId() > 0) {
            RiskItem ri = new RiskItemJpaController( DataBaseManager.getEntityManagerFactory()).findRiskItem(getRiskItemPK());
            if (getCauseList() != null) {
                ri.setCauseList(getCauseList());
            }
            if (getFailureModeList() != null) {
                ri.setFailureModeList(getFailureModeList());
            }
            ri.setFmea(new FMEAJpaController( DataBaseManager.getEntityManagerFactory()).findFMEA(getRiskItemPK().getFMEAid()));
            if (getHazardList() != null) {
                ri.setHazardList(getHazardList());
            }
            if (getRiskControlList() != null) {
                ri.setRiskControlList(getRiskControlList());
            }
            if (getRiskControlList1() != null) {
                ri.setRiskControlList1(getRiskControlList1());
            }
            ri.setSequence(getSequence());
            ri.setVersion(getVersion());
            new RiskItemJpaController( DataBaseManager.getEntityManagerFactory()).edit(ri);
        } else {
            RiskItem ri = new RiskItem(getFmea().getId());
            ri.setCauseList(getCauseList());
            ri.setFailureModeList(getFailureModeList());
            ri.setFmea(new FMEAJpaController( DataBaseManager.getEntityManagerFactory()).findFMEA(getRiskItemPK().getFMEAid()));
            ri.setHazardList(getHazardList());
            ri.setRiskControlList(getRiskControlList());
            ri.setRiskControlList1(getRiskControlList1());
            ri.setSequence(getSequence());
            ri.setVersion(getVersion());
            new RiskItemJpaController( DataBaseManager.getEntityManagerFactory()).create(ri);
            setRiskItemPK(ri.getRiskItemPK());
        }
        return getRiskItemPK().getId();
    }
}
