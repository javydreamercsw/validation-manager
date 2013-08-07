package com.validation.manager.core.server.fmea;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.controller.RiskCategoryJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.fmea.RiskCategory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RiskCategoryServer extends RiskCategory
        implements EntityServer<RiskCategory> {

    public RiskCategoryServer(String name, int min, int max) {
        super(name, min, max);
        setId(0);
    }

    @Override
    public int write2DB() throws IllegalOrphanException,
            NonexistentEntityException, Exception {
        if (getId() > 0) {
            RiskCategory rc = new RiskCategoryJpaController(
                    DataBaseManager.getEntityManagerFactory())
                    .findRiskCategory(getId());
            update(rc, this);
            new RiskCategoryJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(rc);
        } else {
            RiskCategory rc = new RiskCategory(getName(), getMinimum(),
                    getMaximum());
            new RiskCategoryJpaController(
                    DataBaseManager.getEntityManagerFactory()).create(rc);
            setId(rc.getId());
        }
        return getId();
    }

    public static boolean deleteRiskCategory(RiskCategory rc)
            throws IllegalOrphanException, NonexistentEntityException {
        new RiskCategoryJpaController(
                DataBaseManager.getEntityManagerFactory()).destroy(rc.getId());
        return true;
    }

    public RiskCategory getEntity() {
        return new RiskCategoryJpaController(
                DataBaseManager.getEntityManagerFactory()).findRiskCategory(getId());
    }

    public void update(RiskCategory target, RiskCategory source) {
        target.setMaximum(source.getMaximum());
        target.setMinimum(source.getMinimum());
    }
    
    public void update() {
        update(this, getEntity());
    }
}
