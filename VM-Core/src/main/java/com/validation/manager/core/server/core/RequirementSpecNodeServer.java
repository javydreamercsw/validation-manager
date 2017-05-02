package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementSpecJpaController;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class RequirementSpecNodeServer extends RequirementSpecNode
        implements EntityServer<RequirementSpecNode> {

    public RequirementSpecNodeServer(RequirementSpec rs, String name,
            String description, String scope) {
        super(rs.getRequirementSpecPK().getId(),
                rs.getRequirementSpecPK().getProjectId(),
                rs.getRequirementSpecPK().getSpecLevelId());
        setDescription(description);
        setScope(scope);
        setName(name);
        setRequirementSpec(rs);
        setRequirementSpecNode(null);
        setRequirementSpecNodeList(new ArrayList<>());
        setRequirementSpec(
                new RequirementSpecJpaController(
                        getEntityManagerFactory())
                        .findRequirementSpec(rs.getRequirementSpecPK()));
    }

    public RequirementSpecNodeServer(RequirementSpecNode rsn) {
        super(rsn.getRequirementSpecNodePK());
        update();
    }

    public RequirementSpecNodeServer(int requirementSpecId,
            int requirementSpecProjectId, int requirementSpecSpecLevelId) {
        super(requirementSpecId, requirementSpecProjectId,
                requirementSpecSpecLevelId);
        update();
    }

    @Override
    public int write2DB() throws Exception {
        RequirementSpecNode rsn;
        if (getRequirementSpecNodePK() == null || getRequirementSpecNodePK().getId() == 0) {
            rsn = new RequirementSpecNode();
            update(rsn, this);
            new RequirementSpecNodeJpaController(getEntityManagerFactory()).create(rsn);
            setRequirementSpecNodePK(rsn.getRequirementSpecNodePK());
        } else {
            rsn = new RequirementSpecNodeJpaController(
                    getEntityManagerFactory()).findRequirementSpecNode(
                    getRequirementSpecNodePK());
            update(rsn, this);
            new RequirementSpecNodeJpaController(getEntityManagerFactory()).edit(rsn);
        }
        update();
        return getRequirementSpecNodePK().getId();
    }

    @Override
    public RequirementSpecNode getEntity() {
        return new RequirementSpecNodeJpaController(
                getEntityManagerFactory())
                .findRequirementSpecNode(getRequirementSpecNodePK());
    }

    @Override
    public void update(RequirementSpecNode target, RequirementSpecNode source) {
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setRequirementSpec(source.getRequirementSpec());
        target.setScope(source.getScope());
        target.setRequirementSpec(source.getRequirementSpec());
        target.setRequirementSpecNode(source.getRequirementSpecNode());
        target.setRequirementSpecNodeList(source.getRequirementSpecNodeList());
        target.setRequirementSpecNodePK(source.getRequirementSpecNodePK());
        target.setRequirementList(source.getRequirementList());
    }

    public static Collection<? extends Requirement> getRequirements(RequirementSpecNode rsn) {
        List<Requirement> requirements = new ArrayList<>();
        RequirementSpecNodeServer rsns = new RequirementSpecNodeServer(rsn);
        rsns.getRequirementList().forEach((rs) -> {
            requirements.add(rs);
        });
        rsns.getRequirementSpecNodeList().forEach((sub) -> {
            requirements.addAll(getRequirements(sub));
        });
        return requirements;
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
