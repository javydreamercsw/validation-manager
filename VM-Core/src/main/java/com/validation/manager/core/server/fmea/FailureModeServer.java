package com.validation.manager.core.server.fmea;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.controller.FailureModeJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.fmea.FailureMode;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class FailureModeServer extends FailureMode
        implements EntityServer<FailureMode> {

    public FailureModeServer(String name, String description) {
        super(name, description);
        setId(0);
    }

    @Override
    public int write2DB() throws NonexistentEntityException, Exception {
        if (getId() > 0) {
            FailureMode fm = new FailureModeJpaController(
                    DataBaseManager.getEntityManagerFactory())
                    .findFailureMode(getId());
            update(fm, this);
            new FailureModeJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(fm);
        } else {
            FailureMode fm = new FailureMode();
            update(fm, this);
            new FailureModeJpaController(
                    DataBaseManager.getEntityManagerFactory()).create(fm);
            setId(fm.getId());
        }
        return getId();
    }

    public static boolean deleteFailureMode(FailureMode fm) throws NonexistentEntityException {
        new FailureModeJpaController(
                DataBaseManager.getEntityManagerFactory()).destroy(fm.getId());
        return true;
    }

    public FailureMode getEntity() {
        return new FailureModeJpaController(
                DataBaseManager.getEntityManagerFactory()).findFailureMode(getId());
    }

    public void update(FailureMode target, FailureMode source) {
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        if (source.getRiskItemList() != null) {
            target.setRiskItemList(source.getRiskItemList());
        }
    }
}
