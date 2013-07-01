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
        if (getRequirementSpecNodePK() != null && getRequirementSpecNodePK().getId() > 0) {
            RequirementSpecNode rsn = new RequirementSpecNodeJpaController(
                    DataBaseManager.getEntityManagerFactory()).findRequirementSpecNode(
                    getRequirementSpecNodePK());
            update(rsn, this);
            new RequirementSpecNodeJpaController(DataBaseManager.getEntityManagerFactory()).edit(rsn);
        } else {
            RequirementSpecNode rsn = new RequirementSpecNode();
            update(rsn, this);
            new RequirementSpecNodeJpaController(DataBaseManager.getEntityManagerFactory()).create(rsn);
            setRequirementSpecNodePK(rsn.getRequirementSpecNodePK());
        }
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
    }

    public static List<Requirement> getRequirements(RequirementSpecNode rsn) {
        List<Requirement> result = new ArrayList<Requirement>();
        //Add the ones on this node
        result.addAll(rsn.getRequirementList());
        //And all the ones in the sub nodes
        for (RequirementSpecNode srsn : rsn.getRequirementSpecNodeList()) {
            result.addAll(RequirementSpecNodeServer.getRequirements(srsn));
        }
        return result;
    }
}
