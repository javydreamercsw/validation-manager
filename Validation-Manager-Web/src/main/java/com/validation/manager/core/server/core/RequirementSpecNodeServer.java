/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementSpecJpaController;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import java.util.ArrayList;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementSpecNodeServer extends RequirementSpecNode implements EntityServer {

    public RequirementSpecNodeServer(RequirementSpec rs, String name, String description, String scope) {
        super(rs.getRequirementSpecPK().getId());
        setDescription(description);
        setScope(scope);
        setName(name);
        setRequirementList(new ArrayList<Requirement>());
        setRequirementSpec(new RequirementSpecJpaController( DataBaseManager.getEntityManagerFactory()).findRequirementSpec(rs.getRequirementSpecPK()));
    }

    @Override
    public int write2DB() throws Exception {
        if (getRequirementSpecNodePK() != null && getRequirementSpecNodePK().getId() > 0) {
            RequirementSpecNode rsn = new RequirementSpecNodeJpaController(
                    DataBaseManager.getEntityManagerFactory()).findRequirementSpecNode(
                    getRequirementSpecNodePK());
            rsn.setDescription(getDescription());
            rsn.setName(getName());
            rsn.setRequirementSpec(getRequirementSpec());
            rsn.setScope(getScope());
            rsn.setRequirementList(getRequirementList());
            new RequirementSpecNodeJpaController( DataBaseManager.getEntityManagerFactory()).edit(rsn);
        } else {
            RequirementSpecNode rsn = new RequirementSpecNode();
            rsn.setDescription(getDescription());
            rsn.setName(getName());
            rsn.setRequirementSpec(getRequirementSpec());
            rsn.setScope(getScope());
            rsn.setRequirementList(getRequirementList());
            new RequirementSpecNodeJpaController( DataBaseManager.getEntityManagerFactory()).create(rsn);
            setRequirementSpecNodePK(rsn.getRequirementSpecNodePK());
        }
        return getRequirementSpecNodePK().getId();
    }
}
