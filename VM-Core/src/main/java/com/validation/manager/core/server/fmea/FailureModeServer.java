package com.validation.manager.core.server.fmea;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.FailureMode;
import com.validation.manager.core.db.controller.FailureModeJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
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
                    getEntityManagerFactory())
                    .findFailureMode(getId());
            update(fm, this);
            new FailureModeJpaController(
                    getEntityManagerFactory()).edit(fm);
        } else {
            FailureMode fm = new FailureMode();
            update(fm, this);
            new FailureModeJpaController(
                    getEntityManagerFactory()).create(fm);
            setId(fm.getId());
        }
        return getId();
    }

    public static boolean deleteFailureMode(FailureMode fm) throws NonexistentEntityException {
        new FailureModeJpaController(
                getEntityManagerFactory()).destroy(fm.getId());
        return true;
    }

    @Override
    public FailureMode getEntity() {
        return new FailureModeJpaController(
                getEntityManagerFactory()).findFailureMode(getId());
    }

    @Override
    public void update(FailureMode target, FailureMode source) {
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        if (source.getRiskItemList() != null) {
            target.setRiskItemList(source.getRiskItemList());
        }
    }
    
    @Override
    public void update() {
        update(this, getEntity());
    }
}
