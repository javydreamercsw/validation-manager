package com.validation.manager.core.server.fmea;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.controller.FMEAJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.fmea.FMEA;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class FMEAServer extends FMEA implements EntityServer{

    public FMEAServer(String name) {
        super(name);
        setId(0);
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        if (getId() > 0) {
            FMEA fmea = new FMEAJpaController( DataBaseManager.getEntityManagerFactory()).findFMEA(getId());
            if (getFMEAList() != null) {
                fmea.setFMEAList(getFMEAList());
            }
            fmea.setParent(getParent());
            if (getRiskItemList() != null) {
                fmea.setRiskItemList(getRiskItemList());
            }
            new FMEAJpaController( DataBaseManager.getEntityManagerFactory()).edit(fmea);
        } else {
            FMEA fmea = new FMEA(getName());
            fmea.setFMEAList(getFMEAList());
            fmea.setParent(getParent());
            fmea.setRiskItemList(getRiskItemList());
            new FMEAJpaController( DataBaseManager.getEntityManagerFactory()).create(fmea);
            setId(fmea.getId());
        }
        return getId();
    }

    public static boolean deleteFMEA(int id) {
        try {
            new FMEAJpaController( DataBaseManager.getEntityManagerFactory()).destroy(id);
            return true;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(FMEAServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}