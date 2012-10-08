package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.controller.RequirementJpaController;
import com.validation.manager.core.db.controller.RequirementStatusJpaController;
import com.validation.manager.core.db.controller.RequirementTypeJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementServer extends Requirement implements EntityServer {

    public RequirementServer(String id, String desc, Project p, String notes, int requirementType, int requirementStatus) {
        setNotes(notes);
        setProject(p);
        setUniqueId(id);
        setDescription(desc);
        setRequirementStatus(new RequirementStatusJpaController(
                DataBaseManager.getEntityManagerFactory()).findRequirementStatus(requirementStatus));
        setRequirementType(new RequirementTypeJpaController(
                DataBaseManager.getEntityManagerFactory()).findRequirementType(requirementType));
    }

    public static void deleteRequirement(Requirement r) {
        RequirementJpaController controller = 
                new RequirementJpaController(DataBaseManager.getEntityManagerFactory());
        if (controller.findRequirement(r.getRequirementPK()) != null) {
            try {
                controller.destroy(r.getRequirementPK());
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(RequirementServer.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            setProject(requirement.getProject());
            setRequirementList(requirement.getRequirementList());
            setRequirementList1(requirement.getRequirementList1());
            setRequirementStatus(requirement.getRequirementStatus());
            setRequirementType(requirement.getRequirementType());
            setRiskControlList(requirement.getRiskControlList());
            setStepList(requirement.getStepList());
            setVmExceptionList(requirement.getVmExceptionList());
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
            req.setProject(getProject());
            req.setRequirementList(getRequirementList());
            req.setRequirementList1(getRequirementList1());
            req.setRequirementStatus(getRequirementStatus());
            req.setRequirementType(getRequirementType());
            req.setRiskControlList(getRiskControlList());
            req.setStepList(getStepList());
            req.setVmExceptionList(getVmExceptionList());
            new RequirementJpaController(
                    DataBaseManager.getEntityManagerFactory()).edit(req);
        } else {
            Requirement req = new Requirement(getUniqueId(), getDescription());
            req.setNotes(getNotes());
            req.setDescription(getDescription());
            req.setProject(getProject());
            req.setRequirementList(getRequirementList());
            req.setRequirementList1(getRequirementList1());
            req.setRequirementStatus(getRequirementStatus());
            req.setRequirementType(getRequirementType());
            req.setRiskControlList(getRiskControlList());
            req.setStepList(getStepList());
            req.setVmExceptionList(getVmExceptionList());
            new RequirementJpaController(
                    DataBaseManager.getEntityManagerFactory()).create(req);
            setRequirementPK(req.getRequirementPK());
        }
        return getRequirementPK().getId();
    }
}
