package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
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
                        getEntityManagerFactory())
                .findRequirementType(rt.getId());
        update((RequirementType) this, temp);
    }

    public RequirementTypeServer(String name) {
        super(name);
        setId(0);
        setRequirementList(new ArrayList<Requirement>());
    }

    @Override
    public int write2DB() throws Exception {
        RequirementType rt;
        if (getId() > 0) {
            rt = new RequirementTypeJpaController(getEntityManagerFactory()).findRequirementType(getId());
            update(rt, this);
            new RequirementTypeJpaController(getEntityManagerFactory()).edit(rt);
        } else {
            rt = new RequirementType(getName());
            update(rt, this);
            new RequirementTypeJpaController(getEntityManagerFactory()).create(rt);
            setId(rt.getId());
        }
        return getId();
    }

    @Override
    public RequirementType getEntity() {
        return new RequirementTypeJpaController(
                getEntityManagerFactory())
                .findRequirementType(getId());
    }

    @Override
    public void update(RequirementType target, RequirementType source) {
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setId(source.getId());
        target.setRequirementList(source.getRequirementList());
        target.setMajorVersion(source.getMajorVersion());
        target.setMidVersion(source.getMidVersion());
        target.setMinorVersion(source.getMinorVersion());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    public static Iterable<RequirementType> getRequirementTypes() {
        return new RequirementTypeJpaController(
                getEntityManagerFactory())
                .findRequirementTypeEntities();
    }

    @Override
    public List<RequirementType> getVersions() {
       List<RequirementType> versions = new ArrayList<RequirementType>();
        parameters.clear();
        parameters.put("id", getEntity().getId());
        for (Object obj : namedQuery("RequirementType.findById",
                parameters)) {
            versions.add((RequirementType) obj);
        }
        return versions;
    }

    @Override
    public boolean isChangeVersionable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
