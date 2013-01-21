package com.validation.manager.core.server.fmea;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.controller.RiskControlTypeJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.fmea.RiskControlType;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RiskControlTypeServer extends RiskControlType implements EntityServer{

    public RiskControlTypeServer(String name, String description) {
        super(name, description);
        setId(0);
    }

    @Override
    public int write2DB() throws IllegalOrphanException, NonexistentEntityException, Exception {
        if (getId() > 0) {
            RiskControlType rct = new RiskControlTypeJpaController( DataBaseManager.getEntityManagerFactory()).findRiskControlType(getId());
            rct.setName(getName());
            rct.setDescription(getDescription());
            if (getRiskControlList() != null) {
                rct.setRiskControlList(getRiskControlList());
            }
            new RiskControlTypeJpaController( DataBaseManager.getEntityManagerFactory()).edit(rct);
        } else {
            RiskControlType rct = new RiskControlType();
            rct.setName(getName());
            rct.setDescription(getDescription());
            rct.setRiskControlList(getRiskControlList());
            new RiskControlTypeJpaController( DataBaseManager.getEntityManagerFactory()).create(rct);
            setId(rct.getId());
        }
        return getId();
    }

    public static boolean deleteRiskControlType(RiskControlType rct) throws IllegalOrphanException, NonexistentEntityException {
        new RiskControlTypeJpaController( DataBaseManager.getEntityManagerFactory()).destroy(rct.getId());
        return true;
    }
}
