package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.controller.RequirementTypeJpaController;
import java.util.ArrayList;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class RequirementTypeServer extends RequirementType
        implements EntityServer<RequirementType>/*,
        VersionableServer<RequirementType>*/ {

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
        setRequirementList(new ArrayList<>());
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

//    @Override
//    public List<RequirementType> getHistoryList() {
//        List<RequirementType> versions = new ArrayList<>();
//        parameters.clear();
//        parameters.put("id", getEntity().getId());
//        namedQuery("RequirementType.findById",
//                parameters).forEach((obj) -> {
//                    versions.add((RequirementType) obj);
//                });
//        return versions;
//    }
}
