package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNodePK;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import com.validation.manager.core.db.controller.RequirementStatusJpaController;
import com.validation.manager.core.db.controller.RequirementTypeJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementServer extends Requirement implements EntityServer {

    public RequirementServer(String id, String desc, RequirementSpecNodePK rsn,
            String notes, int requirementType, int requirementStatus) {
        setNotes(notes);
        setRequirementSpecNode(
                new RequirementSpecNodeJpaController(DataBaseManager
                .getEntityManagerFactory()).findRequirementSpecNode(rsn));
        setUniqueId(id);
        setDescription(desc);
        setRequirementStatusId(new RequirementStatusJpaController(
                DataBaseManager.getEntityManagerFactory()).findRequirementStatus(requirementStatus));
        setRequirementTypeId(new RequirementTypeJpaController(
                DataBaseManager.getEntityManagerFactory()).findRequirementType(requirementType));
    }

    public static void deleteRequirement(Requirement r)
            throws IllegalOrphanException, NonexistentEntityException {
        RequirementJpaController controller =
                new RequirementJpaController(DataBaseManager.getEntityManagerFactory());
        if (controller.findRequirement(r.getRequirementPK()) != null) {
            controller.destroy(r.getRequirementPK());
        }
    }

    public RequirementServer(Requirement r) {
        RequirementJpaController controller =
                new RequirementJpaController(DataBaseManager.getEntityManagerFactory());
        Requirement requirement = controller.findRequirement(r.getRequirementPK());
        if (requirement != null) {
            setUniqueId(requirement.getUniqueId());
            setNotes(requirement.getNotes());
            setDescription(requirement.getDescription());
            setRequirementSpecNode(requirement.getRequirementSpecNode());
            setRequirementList(requirement.getRequirementList());
            setRequirementList1(requirement.getRequirementList1());
            setRequirementStatusId(requirement.getRequirementStatusId());
            setRequirementTypeId(requirement.getRequirementTypeId());
            setRiskControlList(requirement.getRiskControlList());
            setStepList(requirement.getStepList());
            setRequirementPK(requirement.getRequirementPK());
        } else {
            throw new RuntimeException("Unable to find requirement with id: " + r.getRequirementPK());
        }
    }

    @Override
    public int write2DB() throws Exception {
        if (getRequirementPK() != null && getRequirementPK().getId() > 0) {
            Requirement req = new RequirementJpaController(
                    DataBaseManager.getEntityManagerFactory()).findRequirement(getRequirementPK());
            req.setNotes(getNotes());
            req.setDescription(getDescription());
            req.setRequirementSpecNode(getRequirementSpecNode());
            req.setRequirementList(getRequirementList());
            req.setRequirementList1(getRequirementList1());
            req.setRequirementStatusId(getRequirementStatusId());
            req.setRequirementTypeId(getRequirementTypeId());
            req.setRiskControlList(getRiskControlList());
            req.setStepList(getStepList());
            new RequirementJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(req);
        } else {
            Requirement req = new Requirement(getUniqueId(), getDescription());
            req.setNotes(getNotes());
            req.setDescription(getDescription());
            req.setRequirementSpecNode(getRequirementSpecNode());
            req.setRequirementList(getRequirementList());
            req.setRequirementList1(getRequirementList1());
            req.setRequirementStatusId(getRequirementStatusId());
            req.setRequirementTypeId(getRequirementTypeId());
            req.setRiskControlList(getRiskControlList());
            req.setStepList(getStepList());
            new RequirementJpaController(
                    DataBaseManager.getEntityManagerFactory()).create(req);
            setRequirementPK(req.getRequirementPK());
        }
        return getRequirementPK().getId();
    }

    public Requirement getEntity() {
        return new RequirementJpaController(
                DataBaseManager.getEntityManagerFactory()).findRequirement(
                getRequirementPK());
    }
}
