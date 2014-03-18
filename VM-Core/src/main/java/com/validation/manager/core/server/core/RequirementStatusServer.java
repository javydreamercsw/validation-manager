package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.controller.RequirementStatusJpaController;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementStatusServer extends RequirementStatus
        implements EntityServer<RequirementStatus> , 
        VersionableServer<RequirementStatus>{

    public int write2DB() throws Exception {
        RequirementStatus p;
        if (getId() > 0) {
            p = new RequirementStatusJpaController(
                    getEntityManagerFactory())
                    .findRequirementStatus(getId());
            update(p, this);
            new RequirementStatusJpaController(getEntityManagerFactory()).edit(p);
        } else {
            p = new RequirementStatus(getStatus());
            update(p, this);
            new RequirementStatusJpaController(getEntityManagerFactory()).create(p);
            setId(p.getId());
        }
        return getId();
    }

    public RequirementStatus getEntity() {
        return new RequirementStatusJpaController(
                getEntityManagerFactory())
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

    public List<RequirementStatus> getVersions() {
         List<RequirementStatus> versions = new ArrayList<RequirementStatus>();
        parameters.clear();
        parameters.put("id", getEntity().getId());
        for (Object obj : namedQuery("RequirementStatus.findById",
                parameters)) {
            versions.add((RequirementStatus) obj);
        }
        return versions;
    }
}
