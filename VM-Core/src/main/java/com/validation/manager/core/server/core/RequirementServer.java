package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNodePK;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import com.validation.manager.core.db.controller.RequirementStatusJpaController;
import com.validation.manager.core.db.controller.RequirementTypeJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementServer extends Requirement implements EntityServer<Requirement> {

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
            update(this, requirement);
        } else {
            throw new RuntimeException("Unable to find requirement with id: " + r.getRequirementPK());
        }
    }

    @Override
    public int write2DB() throws Exception {
        if (getRequirementPK() != null && getRequirementPK().getId() > 0) {
            Requirement req = new RequirementJpaController(
                    DataBaseManager.getEntityManagerFactory()).findRequirement(getRequirementPK());
            update(req, this);
            new RequirementJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(req);
        } else {
            Requirement req = new Requirement(getUniqueId(), getDescription());
            update(req, this);
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

    public void update(Requirement target, Requirement source) {
        target.setNotes(source.getNotes());
        target.setDescription(source.getDescription());
        target.setRequirementSpecNode(source.getRequirementSpecNode());
        target.setRequirementList(source.getRequirementList());
        target.setRequirementList1(source.getRequirementList1());
        target.setRequirementStatusId(source.getRequirementStatusId());
        target.setRequirementTypeId(source.getRequirementTypeId());
        target.setRiskControlList(source.getRiskControlList());
        target.setStepList(source.getStepList());
        target.setUniqueId(source.getUniqueId());
        target.setRequirementPK(source.getRequirementPK());
    }

    public static boolean isDuplicate(Requirement req) {
        //Must be unique within a project.
        boolean result = false;
        Project project =
                req.getRequirementSpecNode().getRequirementSpec().getProject();
        List<Requirement> requirements = ProjectServer.getRequirements(project);
        for (Requirement r : requirements) {
            if (r.getUniqueId().equals(req.getUniqueId())) {
                result = true;
                break;
            }
        }
        return result;
    }
}
