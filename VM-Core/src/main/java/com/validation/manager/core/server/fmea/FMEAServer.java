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
public class FMEAServer extends FMEA implements EntityServer<FMEA> {

    public FMEAServer(String name) {
        super(name);
        setId(0);
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        if (getId() > 0) {
            FMEA fmea = new FMEAJpaController(
                    DataBaseManager.getEntityManagerFactory()).findFMEA(getId());
            update(fmea, this);
            new FMEAJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(fmea);
        } else {
            FMEA fmea = new FMEA(getName());
            update(fmea, this);
            new FMEAJpaController(
                    DataBaseManager.getEntityManagerFactory()).create(fmea);
            setId(fmea.getId());
        }
        return getId();
    }

    public static boolean deleteFMEA(int id) {
        try {
            new FMEAJpaController(
                    DataBaseManager.getEntityManagerFactory()).destroy(id);
            return true;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(FMEAServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public FMEA getEntity() {
        return new FMEAJpaController(
                DataBaseManager.getEntityManagerFactory()).findFMEA(getId());
    }

    public void update(FMEA target, FMEA source) {
        target.setFMEAList(source.getFMEAList());
        target.setParent(source.getParent());
        target.setRiskItemList(source.getRiskItemList());
    }
    
    public void update() {
        update(this, getEntity());
    }
}
