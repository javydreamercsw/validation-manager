package com.validation.manager.core.server.fmea;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Fmea;
import com.validation.manager.core.db.controller.FMEAJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class FMEAServer extends Fmea implements EntityServer<Fmea> {

    public FMEAServer(String name) {
        super(name);
        setId(0);
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        if (getId() > 0) {
            Fmea fmea = new FMEAJpaController(
                    DataBaseManager.getEntityManagerFactory()).findFmea(getId());
            update(fmea, this);
            new FMEAJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(fmea);
        } else {
            Fmea fmea = new Fmea(getName());
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
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(FMEAServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Fmea getEntity() {
        return new FMEAJpaController(
                DataBaseManager.getEntityManagerFactory()).findFmea(getId());
    }

    public void update(Fmea target, Fmea source) {
        target.setFmeaList(source.getFmeaList());
        target.setParent(source.getParent());
        target.setRiskItemList(source.getRiskItemList());
    }
    
    public void update() {
        update(this, getEntity());
    }
}
