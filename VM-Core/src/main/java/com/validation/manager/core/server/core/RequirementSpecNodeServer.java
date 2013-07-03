package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
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
public class RequirementSpecNodeServer extends RequirementSpecNode
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
        setRequirementSpecNodeList(new ArrayList<RequirementSpecNode>());
        setRequirementSpec(
                new RequirementSpecJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findRequirementSpec(rs.getRequirementSpecPK()));
    }

    public RequirementSpecNodeServer(RequirementSpecNode rsn) {
        super(rsn.getRequirementSpecNodePK());
        update(this, rsn);
    }

    @Override
    public int write2DB() throws Exception {
        RequirementSpecNode rsn;
        if (getRequirementSpecNodePK() != null && getRequirementSpecNodePK().getId() > 0) {
            rsn = new RequirementSpecNodeJpaController(
                    DataBaseManager.getEntityManagerFactory()).findRequirementSpecNode(
                    getRequirementSpecNodePK());
            update(rsn, this);
            new RequirementSpecNodeJpaController(DataBaseManager.getEntityManagerFactory()).edit(rsn);
        } else {
            rsn = new RequirementSpecNode();
            update(rsn, this);
            new RequirementSpecNodeJpaController(DataBaseManager.getEntityManagerFactory()).create(rsn);
        }
        update(this, rsn);
        return getRequirementSpecNodePK().getId();
    }

    public RequirementSpecNode getEntity() {
        return new RequirementSpecNodeJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findRequirementSpecNode(getRequirementSpecNodePK());
    }

    public void update(RequirementSpecNode target, RequirementSpecNode source) {
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setRequirementSpec(source.getRequirementSpec());
        target.setScope(source.getScope());
        target.setRequirementSpec(source.getRequirementSpec());
        target.setRequirementSpecNode(source.getRequirementSpecNode());
        target.setRequirementSpecNodeList(source.getRequirementSpecNodeList());
        target.setRequirementSpecNodePK(source.getRequirementSpecNodePK());
    }

    public static Collection<? extends Requirement> getRequirements(RequirementSpecNode rsn) {
        List<Requirement> requirements = new ArrayList<Requirement>();
        RequirementSpecNodeServer rsns = new RequirementSpecNodeServer(rsn);
        for (Requirement rs : rsns.getRequirementList()) {
            requirements.add(rs);
        }
        for (RequirementSpecNode sub : rsns.getRequirementSpecNodeList()) {
            requirements.addAll(getRequirements(sub));
        }
        return requirements;
    }
}
