package com.validation.manager.core.server.fmea;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.controller.HazardJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.fmea.Hazard;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class HazardServer extends Hazard implements EntityServer<Hazard> {

    public HazardServer(String name, String description) {
        super(name, description);
        setId(0);
    }

    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        if (getId() > 0) {
            Hazard h = new HazardJpaController(
                    DataBaseManager.getEntityManagerFactory())
                    .findHazard(getId());
            h.setDescription(getDescription());
            h.setName(getName());
            h.setRiskItemList(getRiskItemList());
            new HazardJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(h);
        } else {
            Hazard h = new Hazard();
            h.setDescription(getDescription());
            h.setName(getName());
            h.setRiskItemList(getRiskItemList());
            new HazardJpaController(
                    DataBaseManager.getEntityManagerFactory()).create(h);
            setId(h.getId());
        }
        return getId();
    }

    public static boolean deleteHazard(Hazard h) 
            throws NonexistentEntityException {
        new HazardJpaController(
                DataBaseManager.getEntityManagerFactory()).destroy(h.getId());
        return true;
    }

    public Hazard getEntity() {
        return new HazardJpaController(
                DataBaseManager.getEntityManagerFactory()).findHazard(getId());
    }
}
