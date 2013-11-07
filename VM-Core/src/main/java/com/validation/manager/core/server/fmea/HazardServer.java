package com.validation.manager.core.server.fmea;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Hazard;
import com.validation.manager.core.db.controller.HazardJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

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
            update(h, this);
            new HazardJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(h);
        } else {
            Hazard h = new Hazard();
            update(h, this);
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

    public void update(Hazard target, Hazard source) {
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setRiskItemList(source.getRiskItemList());
    }
    
    public void update() {
        update(this, getEntity());
    }
}
