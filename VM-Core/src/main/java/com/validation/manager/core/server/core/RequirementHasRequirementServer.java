/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementHasRequirement;
import com.validation.manager.core.db.controller.RequirementHasRequirementJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementHasRequirementServer extends RequirementHasRequirement
        implements EntityServer<RequirementHasRequirement> {

    public RequirementHasRequirementServer(Requirement parent, Requirement child) {
        setChildRequirement(child);
        setParentRequirement(parent);
    }

    public int write2DB() throws Exception {
        if (getRequirementHasRequirementPK() == null) {
            RequirementHasRequirement rhr = new RequirementHasRequirement();
            update(rhr, this);
            new RequirementHasRequirementJpaController(
                    DataBaseManager.getEntityManagerFactory()).create(rhr);
        } else {
            RequirementHasRequirement rhr = new RequirementHasRequirement();
            update(rhr, this);
            new RequirementHasRequirementJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(rhr);
        }
        return 1;
    }

    public RequirementHasRequirement getEntity() {
        return new RequirementHasRequirementJpaController(
                DataBaseManager.getEntityManagerFactory())
                .findRequirementHasRequirement(
                getRequirementHasRequirementPK());
    }

    public void update(RequirementHasRequirement target, RequirementHasRequirement source) {
        target.setChildRequirement(source.getChildRequirement());
        target.setParentRequirement(source.getParentRequirement());
        target.setRequirementHasRequirementPK(source.getRequirementHasRequirementPK());
    }

    public void update() {
        update(this, getEntity());
    }
}
