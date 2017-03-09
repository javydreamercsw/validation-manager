package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.isVersioningEnabled;
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
        implements EntityServer<RequirementStatus>,
        VersionableServer<RequirementStatus> {

    public RequirementStatusServer(Integer id) {
        RequirementStatusJpaController controller
                = new RequirementStatusJpaController(
                        getEntityManagerFactory());
        RequirementStatus rs = controller.findRequirementStatus(id);
        if (rs != null) {
            update(this, rs);
        }
    }

    @Override
    public int write2DB() throws Exception {
        RequirementStatus p;
        RequirementStatusJpaController controller
                = new RequirementStatusJpaController(
                        getEntityManagerFactory());
        if (getId() > 0) {
            if (isVersioningEnabled() && isChangeVersionable()) {
                p = new RequirementStatus();
                if (p.getClass().isInstance(Versionable.class)) {
                    Versionable vTarget = Versionable.class.cast(p);
                    Versionable vSource = Versionable.class.cast(this);
                    vTarget.setMajorVersion(vSource.getMajorVersion());
                    vTarget.setMidVersion(vSource.getMidVersion());
                    vTarget.setMinorVersion(vSource.getMinorVersion() + 1);
                }
                update(this, p);
            } else {
                p = controller.findRequirementStatus(getId());
                update(p, this);
                controller.edit(p);
            }
        } else {
            p = new RequirementStatus(getStatus());
            update(p, this);
            controller.create(p);
            setId(p.getId());
        }
        return getId();
    }

    @Override
    public RequirementStatus getEntity() {
        return new RequirementStatusJpaController(
                getEntityManagerFactory())
                .findRequirementStatus(getId());
    }

    @Override
    public void update(RequirementStatus target, RequirementStatus source) {
        target.setId(source.getId());
        target.setStatus(source.getStatus());
        target.setRequirementList(source.getRequirementList());
        if (target.getClass().isInstance(Versionable.class)) {
            Versionable vTarget = Versionable.class.cast(target);
            Versionable vSource = Versionable.class.cast(source);
            vTarget.setMajorVersion(vSource.getMajorVersion());
            vTarget.setMidVersion(vSource.getMidVersion());
            vTarget.setMinorVersion(vSource.getMinorVersion());
        }
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    @Override
    public List<RequirementStatus> getVersions() {
        List<RequirementStatus> versions = new ArrayList<>();
        parameters.clear();
        parameters.put("id", getEntity().getId());
        namedQuery("RequirementStatus.findById",
                parameters).forEach((obj) -> {
                    versions.add((RequirementStatus) obj);
                });
        return versions;
    }
}
