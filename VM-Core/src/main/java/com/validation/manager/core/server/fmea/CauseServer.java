package com.validation.manager.core.server.fmea;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.controller.CauseJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.fmea.Cause;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class CauseServer extends Cause implements EntityServer {
    
    public CauseServer(String name, String description) {
        super(name, description);
        setId(0);
    }
    
    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        if (getId() > 0) {
            Cause c = new CauseJpaController(
                    DataBaseManager.getEntityManagerFactory())
                    .findCause(getId());
            c.setDescription(getDescription());
            c.setName(getName());
            if (getRiskItemList() != null) {
                c.setRiskItemList(getRiskItemList());
            }
            new CauseJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(c);
        } else {
            Cause c = new Cause();
            c.setDescription(getDescription());
            c.setName(getName());
            c.setRiskItemList(getRiskItemList());
            new CauseJpaController(
                    DataBaseManager.getEntityManagerFactory()).create(c);
            setId(c.getId());
        }
        return getId();
    }
    
    public static boolean deleteCause(Cause c) throws NonexistentEntityException {
        new CauseJpaController(
                DataBaseManager.getEntityManagerFactory()).destroy(c.getId());
        return true;
    }
    
    public Cause getEntity() {
        return new CauseJpaController(
                DataBaseManager.getEntityManagerFactory()).findCause(getId());
    }
}
