package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.controller.RequirementStatusJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementStatusServer extends RequirementStatus
        implements EntityServer<RequirementStatus> {

    public int write2DB() throws Exception {
        RequirementStatus p;
        if (getId() > 0) {
            p = new RequirementStatusJpaController(
                    DataBaseManager.getEntityManagerFactory())
                    .findRequirementStatus(getId());
            update(p, this);
            new RequirementStatusJpaController(DataBaseManager.getEntityManagerFactory()).edit(p);
        } else {
            p = new RequirementStatus(getStatus());
            update(p, this);
            new RequirementStatusJpaController(DataBaseManager.getEntityManagerFactory()).create(p);
            setId(p.getId());
        }
        return getId();
    }

    public RequirementStatus getEntity() {
        return new RequirementStatusJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findRequirementStatus(getId());
    }

    public void update(RequirementStatus target, RequirementStatus source) {
        target.setId(source.getId());
        target.setStatus(source.getStatus());
        target.setRequirementList(source.getRequirementList());
    }

    public void update() {
        update(this, getEntity());
    }
}
