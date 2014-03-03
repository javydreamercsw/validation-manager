package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.controller.RequirementTypeJpaController;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class RequirementTypeServer extends RequirementType
        implements EntityServer<RequirementType>,
        VersionableServer<RequirementType> {

    public RequirementTypeServer(RequirementType rt) {
        RequirementType temp
                = new RequirementTypeJpaController(
                        DataBaseManager.getEntityManagerFactory())
                .findRequirementType(rt.getId());
        update((RequirementType) this, temp);
    }

    public RequirementTypeServer(String name) {
        super(name);
        setId(0);
        setRequirementList(new ArrayList<Requirement>());
    }

    public int write2DB() throws Exception {
        RequirementType rt;
        if (getId() > 0) {
            rt = new RequirementTypeJpaController(DataBaseManager.getEntityManagerFactory()).findRequirementType(getId());
            update(rt, this);
            new RequirementTypeJpaController(DataBaseManager.getEntityManagerFactory()).edit(rt);
        } else {
            rt = new RequirementType(getName());
            update(rt, this);
            new RequirementTypeJpaController(DataBaseManager.getEntityManagerFactory()).create(rt);
            setId(rt.getId());
        }
        return getId();
    }

    public RequirementType getEntity() {
        return new RequirementTypeJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findRequirementType(getId());
    }

    public void update(RequirementType target, RequirementType source) {
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setId(source.getId());
        target.setRequirementList(source.getRequirementList());
    }

    public void update() {
        update(this, getEntity());
    }

    public static Iterable<RequirementType> getRequirementTypes() {
        return new RequirementTypeJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findRequirementTypeEntities();
    }

    public List<RequirementType> getVersions() {
       List<RequirementType> versions = new ArrayList<RequirementType>();
        parameters.clear();
        parameters.put("id", getEntity().getId());
        for (Object obj : DataBaseManager.namedQuery("RequirementType.findById",
                parameters)) {
            versions.add((RequirementType) obj);
        }
        return versions;
    }
}
