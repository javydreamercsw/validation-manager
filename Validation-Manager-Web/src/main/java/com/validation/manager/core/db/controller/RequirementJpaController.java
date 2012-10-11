/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementHasException;
import com.validation.manager.core.db.RequirementPK;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.RiskControlHasRequirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.VmException;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import com.validation.manager.core.db.fmea.RiskControl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementJpaController implements Serializable {

    public RequirementJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Requirement requirement) throws PreexistingEntityException, Exception {
        if (requirement.getRequirementPK() == null) {
            requirement.setRequirementPK(new RequirementPK());
        }
        if (requirement.getVmExceptionList() == null) {
            requirement.setVmExceptionList(new ArrayList<VmException>());
        }
        if (requirement.getRequirementList() == null) {
            requirement.setRequirementList(new ArrayList<Requirement>());
        }
        if (requirement.getRequirementList1() == null) {
            requirement.setRequirementList1(new ArrayList<Requirement>());
        }
        if (requirement.getStepList() == null) {
            requirement.setStepList(new ArrayList<Step>());
        }
        if (requirement.getRiskControlList() == null) {
            requirement.setRiskControlList(new ArrayList<RiskControl>());
        }
        if (requirement.getRequirementHasExceptionList() == null) {
            requirement.setRequirementHasExceptionList(new ArrayList<RequirementHasException>());
        }
        if (requirement.getRiskControlHasRequirementList() == null) {
            requirement.setRiskControlHasRequirementList(new ArrayList<RiskControlHasRequirement>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementSpecNode requirementSpecNode = requirement.getRequirementSpecNode();
            if (requirementSpecNode != null) {
                requirementSpecNode = em.getReference(requirementSpecNode.getClass(), requirementSpecNode.getRequirementSpecNodePK());
                requirement.setRequirementSpecNode(requirementSpecNode);
            }
            Project project = requirement.getProject();
            if (project != null) {
                project = em.getReference(project.getClass(), project.getId());
                requirement.setProject(project);
            }
            RequirementStatus requirementStatus = requirement.getRequirementStatus();
            if (requirementStatus != null) {
                requirementStatus = em.getReference(requirementStatus.getClass(), requirementStatus.getId());
                requirement.setRequirementStatus(requirementStatus);
            }
            RequirementType requirementType = requirement.getRequirementType();
            if (requirementType != null) {
                requirementType = em.getReference(requirementType.getClass(), requirementType.getId());
                requirement.setRequirementType(requirementType);
            }
            List<VmException> attachedVmExceptionList = new ArrayList<VmException>();
            for (VmException vmExceptionListVmExceptionToAttach : requirement.getVmExceptionList()) {
                vmExceptionListVmExceptionToAttach = em.getReference(vmExceptionListVmExceptionToAttach.getClass(), vmExceptionListVmExceptionToAttach.getVmExceptionPK());
                attachedVmExceptionList.add(vmExceptionListVmExceptionToAttach);
            }
            requirement.setVmExceptionList(attachedVmExceptionList);
            List<Requirement> attachedRequirementList = new ArrayList<Requirement>();
            for (Requirement requirementListRequirementToAttach : requirement.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getRequirementPK());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            requirement.setRequirementList(attachedRequirementList);
            List<Requirement> attachedRequirementList1 = new ArrayList<Requirement>();
            for (Requirement requirementList1RequirementToAttach : requirement.getRequirementList1()) {
                requirementList1RequirementToAttach = em.getReference(requirementList1RequirementToAttach.getClass(), requirementList1RequirementToAttach.getRequirementPK());
                attachedRequirementList1.add(requirementList1RequirementToAttach);
            }
            requirement.setRequirementList1(attachedRequirementList1);
            List<Step> attachedStepList = new ArrayList<Step>();
            for (Step stepListStepToAttach : requirement.getStepList()) {
                stepListStepToAttach = em.getReference(stepListStepToAttach.getClass(), stepListStepToAttach.getStepPK());
                attachedStepList.add(stepListStepToAttach);
            }
            requirement.setStepList(attachedStepList);
            List<RiskControl> attachedRiskControlList = new ArrayList<RiskControl>();
            for (RiskControl riskControlListRiskControlToAttach : requirement.getRiskControlList()) {
                riskControlListRiskControlToAttach = em.getReference(riskControlListRiskControlToAttach.getClass(), riskControlListRiskControlToAttach.getRiskControlPK());
                attachedRiskControlList.add(riskControlListRiskControlToAttach);
            }
            requirement.setRiskControlList(attachedRiskControlList);
            List<RequirementHasException> attachedRequirementHasExceptionList = new ArrayList<RequirementHasException>();
            for (RequirementHasException requirementHasExceptionListRequirementHasExceptionToAttach : requirement.getRequirementHasExceptionList()) {
                requirementHasExceptionListRequirementHasExceptionToAttach = em.getReference(requirementHasExceptionListRequirementHasExceptionToAttach.getClass(), requirementHasExceptionListRequirementHasExceptionToAttach.getRequirementHasExceptionPK());
                attachedRequirementHasExceptionList.add(requirementHasExceptionListRequirementHasExceptionToAttach);
            }
            requirement.setRequirementHasExceptionList(attachedRequirementHasExceptionList);
            List<RiskControlHasRequirement> attachedRiskControlHasRequirementList = new ArrayList<RiskControlHasRequirement>();
            for (RiskControlHasRequirement riskControlHasRequirementListRiskControlHasRequirementToAttach : requirement.getRiskControlHasRequirementList()) {
                riskControlHasRequirementListRiskControlHasRequirementToAttach = em.getReference(riskControlHasRequirementListRiskControlHasRequirementToAttach.getClass(), riskControlHasRequirementListRiskControlHasRequirementToAttach.getRiskControlHasRequirementPK());
                attachedRiskControlHasRequirementList.add(riskControlHasRequirementListRiskControlHasRequirementToAttach);
            }
            requirement.setRiskControlHasRequirementList(attachedRiskControlHasRequirementList);
            em.persist(requirement);
            if (requirementSpecNode != null) {
                requirementSpecNode.getRequirementList().add(requirement);
                requirementSpecNode = em.merge(requirementSpecNode);
            }
            if (requirementStatus != null) {
                requirementStatus.getRequirementList().add(requirement);
                requirementStatus = em.merge(requirementStatus);
            }
            if (requirementType != null) {
                requirementType.getRequirementList().add(requirement);
                requirementType = em.merge(requirementType);
            }
            for (VmException vmExceptionListVmException : requirement.getVmExceptionList()) {
                vmExceptionListVmException.getRequirementList().add(requirement);
                vmExceptionListVmException = em.merge(vmExceptionListVmException);
            }
            for (Requirement requirementListRequirement : requirement.getRequirementList()) {
                requirementListRequirement.getRequirementList().add(requirement);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            for (Requirement requirementList1Requirement : requirement.getRequirementList1()) {
                requirementList1Requirement.getRequirementList().add(requirement);
                requirementList1Requirement = em.merge(requirementList1Requirement);
            }
            for (Step stepListStep : requirement.getStepList()) {
                stepListStep.getRequirementList().add(requirement);
                stepListStep = em.merge(stepListStep);
            }
            for (RiskControl riskControlListRiskControl : requirement.getRiskControlList()) {
                riskControlListRiskControl.getRequirementList().add(requirement);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
            }
            for (RequirementHasException requirementHasExceptionListRequirementHasException : requirement.getRequirementHasExceptionList()) {
                Requirement oldRequirementOfRequirementHasExceptionListRequirementHasException = requirementHasExceptionListRequirementHasException.getRequirement();
                requirementHasExceptionListRequirementHasException.setRequirement(requirement);
                requirementHasExceptionListRequirementHasException = em.merge(requirementHasExceptionListRequirementHasException);
                if (oldRequirementOfRequirementHasExceptionListRequirementHasException != null) {
                    oldRequirementOfRequirementHasExceptionListRequirementHasException.getRequirementHasExceptionList().remove(requirementHasExceptionListRequirementHasException);
                    oldRequirementOfRequirementHasExceptionListRequirementHasException = em.merge(oldRequirementOfRequirementHasExceptionListRequirementHasException);
                }
            }
            for (RiskControlHasRequirement riskControlHasRequirementListRiskControlHasRequirement : requirement.getRiskControlHasRequirementList()) {
                Requirement oldRequirementOfRiskControlHasRequirementListRiskControlHasRequirement = riskControlHasRequirementListRiskControlHasRequirement.getRequirement();
                riskControlHasRequirementListRiskControlHasRequirement.setRequirement(requirement);
                riskControlHasRequirementListRiskControlHasRequirement = em.merge(riskControlHasRequirementListRiskControlHasRequirement);
                if (oldRequirementOfRiskControlHasRequirementListRiskControlHasRequirement != null) {
                    oldRequirementOfRiskControlHasRequirementListRiskControlHasRequirement.getRiskControlHasRequirementList().remove(riskControlHasRequirementListRiskControlHasRequirement);
                    oldRequirementOfRiskControlHasRequirementListRiskControlHasRequirement = em.merge(oldRequirementOfRiskControlHasRequirementListRiskControlHasRequirement);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRequirement(requirement.getRequirementPK()) != null) {
                throw new PreexistingEntityException("Requirement " + requirement + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Requirement requirement) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Requirement persistentRequirement = em.find(Requirement.class, requirement.getRequirementPK());
            RequirementSpecNode requirementSpecNodeOld = persistentRequirement.getRequirementSpecNode();
            RequirementSpecNode requirementSpecNodeNew = requirement.getRequirementSpecNode();
            Project projectOld = persistentRequirement.getProject();
            Project projectNew = requirement.getProject();
            RequirementStatus requirementStatusOld = persistentRequirement.getRequirementStatus();
            RequirementStatus requirementStatusNew = requirement.getRequirementStatus();
            RequirementType requirementTypeOld = persistentRequirement.getRequirementType();
            RequirementType requirementTypeNew = requirement.getRequirementType();
            List<VmException> vmExceptionListOld = persistentRequirement.getVmExceptionList();
            List<VmException> vmExceptionListNew = requirement.getVmExceptionList();
            List<Requirement> requirementListOld = persistentRequirement.getRequirementList();
            List<Requirement> requirementListNew = requirement.getRequirementList();
            List<Requirement> requirementList1Old = persistentRequirement.getRequirementList1();
            List<Requirement> requirementList1New = requirement.getRequirementList1();
            List<Step> stepListOld = persistentRequirement.getStepList();
            List<Step> stepListNew = requirement.getStepList();
            List<RiskControl> riskControlListOld = persistentRequirement.getRiskControlList();
            List<RiskControl> riskControlListNew = requirement.getRiskControlList();
            List<RequirementHasException> requirementHasExceptionListOld = persistentRequirement.getRequirementHasExceptionList();
            List<RequirementHasException> requirementHasExceptionListNew = requirement.getRequirementHasExceptionList();
            List<RiskControlHasRequirement> riskControlHasRequirementListOld = persistentRequirement.getRiskControlHasRequirementList();
            List<RiskControlHasRequirement> riskControlHasRequirementListNew = requirement.getRiskControlHasRequirementList();
            List<String> illegalOrphanMessages = null;
            for (RequirementHasException requirementHasExceptionListOldRequirementHasException : requirementHasExceptionListOld) {
                if (!requirementHasExceptionListNew.contains(requirementHasExceptionListOldRequirementHasException)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RequirementHasException " + requirementHasExceptionListOldRequirementHasException + " since its requirement field is not nullable.");
                }
            }
            for (RiskControlHasRequirement riskControlHasRequirementListOldRiskControlHasRequirement : riskControlHasRequirementListOld) {
                if (!riskControlHasRequirementListNew.contains(riskControlHasRequirementListOldRiskControlHasRequirement)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RiskControlHasRequirement " + riskControlHasRequirementListOldRiskControlHasRequirement + " since its requirement field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (requirementSpecNodeNew != null) {
                requirementSpecNodeNew = em.getReference(requirementSpecNodeNew.getClass(), requirementSpecNodeNew.getRequirementSpecNodePK());
                requirement.setRequirementSpecNode(requirementSpecNodeNew);
            }
            if (projectNew != null) {
                projectNew = em.getReference(projectNew.getClass(), projectNew.getId());
                requirement.setProject(projectNew);
            }
            if (requirementStatusNew != null) {
                requirementStatusNew = em.getReference(requirementStatusNew.getClass(), requirementStatusNew.getId());
                requirement.setRequirementStatus(requirementStatusNew);
            }
            if (requirementTypeNew != null) {
                requirementTypeNew = em.getReference(requirementTypeNew.getClass(), requirementTypeNew.getId());
                requirement.setRequirementType(requirementTypeNew);
            }
            List<VmException> attachedVmExceptionListNew = new ArrayList<VmException>();
            for (VmException vmExceptionListNewVmExceptionToAttach : vmExceptionListNew) {
                vmExceptionListNewVmExceptionToAttach = em.getReference(vmExceptionListNewVmExceptionToAttach.getClass(), vmExceptionListNewVmExceptionToAttach.getVmExceptionPK());
                attachedVmExceptionListNew.add(vmExceptionListNewVmExceptionToAttach);
            }
            vmExceptionListNew = attachedVmExceptionListNew;
            requirement.setVmExceptionList(vmExceptionListNew);
            List<Requirement> attachedRequirementListNew = new ArrayList<Requirement>();
            for (Requirement requirementListNewRequirementToAttach : requirementListNew) {
                requirementListNewRequirementToAttach = em.getReference(requirementListNewRequirementToAttach.getClass(), requirementListNewRequirementToAttach.getRequirementPK());
                attachedRequirementListNew.add(requirementListNewRequirementToAttach);
            }
            requirementListNew = attachedRequirementListNew;
            requirement.setRequirementList(requirementListNew);
            List<Requirement> attachedRequirementList1New = new ArrayList<Requirement>();
            for (Requirement requirementList1NewRequirementToAttach : requirementList1New) {
                requirementList1NewRequirementToAttach = em.getReference(requirementList1NewRequirementToAttach.getClass(), requirementList1NewRequirementToAttach.getRequirementPK());
                attachedRequirementList1New.add(requirementList1NewRequirementToAttach);
            }
            requirementList1New = attachedRequirementList1New;
            requirement.setRequirementList1(requirementList1New);
            List<Step> attachedStepListNew = new ArrayList<Step>();
            for (Step stepListNewStepToAttach : stepListNew) {
                stepListNewStepToAttach = em.getReference(stepListNewStepToAttach.getClass(), stepListNewStepToAttach.getStepPK());
                attachedStepListNew.add(stepListNewStepToAttach);
            }
            stepListNew = attachedStepListNew;
            requirement.setStepList(stepListNew);
            List<RiskControl> attachedRiskControlListNew = new ArrayList<RiskControl>();
            for (RiskControl riskControlListNewRiskControlToAttach : riskControlListNew) {
                riskControlListNewRiskControlToAttach = em.getReference(riskControlListNewRiskControlToAttach.getClass(), riskControlListNewRiskControlToAttach.getRiskControlPK());
                attachedRiskControlListNew.add(riskControlListNewRiskControlToAttach);
            }
            riskControlListNew = attachedRiskControlListNew;
            requirement.setRiskControlList(riskControlListNew);
            List<RequirementHasException> attachedRequirementHasExceptionListNew = new ArrayList<RequirementHasException>();
            for (RequirementHasException requirementHasExceptionListNewRequirementHasExceptionToAttach : requirementHasExceptionListNew) {
                requirementHasExceptionListNewRequirementHasExceptionToAttach = em.getReference(requirementHasExceptionListNewRequirementHasExceptionToAttach.getClass(), requirementHasExceptionListNewRequirementHasExceptionToAttach.getRequirementHasExceptionPK());
                attachedRequirementHasExceptionListNew.add(requirementHasExceptionListNewRequirementHasExceptionToAttach);
            }
            requirementHasExceptionListNew = attachedRequirementHasExceptionListNew;
            requirement.setRequirementHasExceptionList(requirementHasExceptionListNew);
            List<RiskControlHasRequirement> attachedRiskControlHasRequirementListNew = new ArrayList<RiskControlHasRequirement>();
            for (RiskControlHasRequirement riskControlHasRequirementListNewRiskControlHasRequirementToAttach : riskControlHasRequirementListNew) {
                riskControlHasRequirementListNewRiskControlHasRequirementToAttach = em.getReference(riskControlHasRequirementListNewRiskControlHasRequirementToAttach.getClass(), riskControlHasRequirementListNewRiskControlHasRequirementToAttach.getRiskControlHasRequirementPK());
                attachedRiskControlHasRequirementListNew.add(riskControlHasRequirementListNewRiskControlHasRequirementToAttach);
            }
            riskControlHasRequirementListNew = attachedRiskControlHasRequirementListNew;
            requirement.setRiskControlHasRequirementList(riskControlHasRequirementListNew);
            requirement = em.merge(requirement);
            if (requirementSpecNodeOld != null && !requirementSpecNodeOld.equals(requirementSpecNodeNew)) {
                requirementSpecNodeOld.getRequirementList().remove(requirement);
                requirementSpecNodeOld = em.merge(requirementSpecNodeOld);
            }
            if (requirementSpecNodeNew != null && !requirementSpecNodeNew.equals(requirementSpecNodeOld)) {
                requirementSpecNodeNew.getRequirementList().add(requirement);
                requirementSpecNodeNew = em.merge(requirementSpecNodeNew);
            }
            if (requirementStatusOld != null && !requirementStatusOld.equals(requirementStatusNew)) {
                requirementStatusOld.getRequirementList().remove(requirement);
                requirementStatusOld = em.merge(requirementStatusOld);
            }
            if (requirementStatusNew != null && !requirementStatusNew.equals(requirementStatusOld)) {
                requirementStatusNew.getRequirementList().add(requirement);
                requirementStatusNew = em.merge(requirementStatusNew);
            }
            if (requirementTypeOld != null && !requirementTypeOld.equals(requirementTypeNew)) {
                requirementTypeOld.getRequirementList().remove(requirement);
                requirementTypeOld = em.merge(requirementTypeOld);
            }
            if (requirementTypeNew != null && !requirementTypeNew.equals(requirementTypeOld)) {
                requirementTypeNew.getRequirementList().add(requirement);
                requirementTypeNew = em.merge(requirementTypeNew);
            }
            for (VmException vmExceptionListOldVmException : vmExceptionListOld) {
                if (!vmExceptionListNew.contains(vmExceptionListOldVmException)) {
                    vmExceptionListOldVmException.getRequirementList().remove(requirement);
                    vmExceptionListOldVmException = em.merge(vmExceptionListOldVmException);
                }
            }
            for (VmException vmExceptionListNewVmException : vmExceptionListNew) {
                if (!vmExceptionListOld.contains(vmExceptionListNewVmException)) {
                    vmExceptionListNewVmException.getRequirementList().add(requirement);
                    vmExceptionListNewVmException = em.merge(vmExceptionListNewVmException);
                }
            }
            for (Requirement requirementListOldRequirement : requirementListOld) {
                if (!requirementListNew.contains(requirementListOldRequirement)) {
                    requirementListOldRequirement.getRequirementList().remove(requirement);
                    requirementListOldRequirement = em.merge(requirementListOldRequirement);
                }
            }
            for (Requirement requirementListNewRequirement : requirementListNew) {
                if (!requirementListOld.contains(requirementListNewRequirement)) {
                    requirementListNewRequirement.getRequirementList().add(requirement);
                    requirementListNewRequirement = em.merge(requirementListNewRequirement);
                }
            }
            for (Requirement requirementList1OldRequirement : requirementList1Old) {
                if (!requirementList1New.contains(requirementList1OldRequirement)) {
                    requirementList1OldRequirement.getRequirementList().remove(requirement);
                    requirementList1OldRequirement = em.merge(requirementList1OldRequirement);
                }
            }
            for (Requirement requirementList1NewRequirement : requirementList1New) {
                if (!requirementList1Old.contains(requirementList1NewRequirement)) {
                    requirementList1NewRequirement.getRequirementList().add(requirement);
                    requirementList1NewRequirement = em.merge(requirementList1NewRequirement);
                }
            }
            for (Step stepListOldStep : stepListOld) {
                if (!stepListNew.contains(stepListOldStep)) {
                    stepListOldStep.getRequirementList().remove(requirement);
                    stepListOldStep = em.merge(stepListOldStep);
                }
            }
            for (Step stepListNewStep : stepListNew) {
                if (!stepListOld.contains(stepListNewStep)) {
                    stepListNewStep.getRequirementList().add(requirement);
                    stepListNewStep = em.merge(stepListNewStep);
                }
            }
            for (RiskControl riskControlListOldRiskControl : riskControlListOld) {
                if (!riskControlListNew.contains(riskControlListOldRiskControl)) {
                    riskControlListOldRiskControl.getRequirementList().remove(requirement);
                    riskControlListOldRiskControl = em.merge(riskControlListOldRiskControl);
                }
            }
            for (RiskControl riskControlListNewRiskControl : riskControlListNew) {
                if (!riskControlListOld.contains(riskControlListNewRiskControl)) {
                    riskControlListNewRiskControl.getRequirementList().add(requirement);
                    riskControlListNewRiskControl = em.merge(riskControlListNewRiskControl);
                }
            }
            for (RequirementHasException requirementHasExceptionListNewRequirementHasException : requirementHasExceptionListNew) {
                if (!requirementHasExceptionListOld.contains(requirementHasExceptionListNewRequirementHasException)) {
                    Requirement oldRequirementOfRequirementHasExceptionListNewRequirementHasException = requirementHasExceptionListNewRequirementHasException.getRequirement();
                    requirementHasExceptionListNewRequirementHasException.setRequirement(requirement);
                    requirementHasExceptionListNewRequirementHasException = em.merge(requirementHasExceptionListNewRequirementHasException);
                    if (oldRequirementOfRequirementHasExceptionListNewRequirementHasException != null && !oldRequirementOfRequirementHasExceptionListNewRequirementHasException.equals(requirement)) {
                        oldRequirementOfRequirementHasExceptionListNewRequirementHasException.getRequirementHasExceptionList().remove(requirementHasExceptionListNewRequirementHasException);
                        oldRequirementOfRequirementHasExceptionListNewRequirementHasException = em.merge(oldRequirementOfRequirementHasExceptionListNewRequirementHasException);
                    }
                }
            }
            for (RiskControlHasRequirement riskControlHasRequirementListNewRiskControlHasRequirement : riskControlHasRequirementListNew) {
                if (!riskControlHasRequirementListOld.contains(riskControlHasRequirementListNewRiskControlHasRequirement)) {
                    Requirement oldRequirementOfRiskControlHasRequirementListNewRiskControlHasRequirement = riskControlHasRequirementListNewRiskControlHasRequirement.getRequirement();
                    riskControlHasRequirementListNewRiskControlHasRequirement.setRequirement(requirement);
                    riskControlHasRequirementListNewRiskControlHasRequirement = em.merge(riskControlHasRequirementListNewRiskControlHasRequirement);
                    if (oldRequirementOfRiskControlHasRequirementListNewRiskControlHasRequirement != null && !oldRequirementOfRiskControlHasRequirementListNewRiskControlHasRequirement.equals(requirement)) {
                        oldRequirementOfRiskControlHasRequirementListNewRiskControlHasRequirement.getRiskControlHasRequirementList().remove(riskControlHasRequirementListNewRiskControlHasRequirement);
                        oldRequirementOfRiskControlHasRequirementListNewRiskControlHasRequirement = em.merge(oldRequirementOfRiskControlHasRequirementListNewRiskControlHasRequirement);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RequirementPK id = requirement.getRequirementPK();
                if (findRequirement(id) == null) {
                    throw new NonexistentEntityException("The requirement with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RequirementPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Requirement requirement;
            try {
                requirement = em.getReference(Requirement.class, id);
                requirement.getRequirementPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The requirement with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RequirementHasException> requirementHasExceptionListOrphanCheck = requirement.getRequirementHasExceptionList();
            for (RequirementHasException requirementHasExceptionListOrphanCheckRequirementHasException : requirementHasExceptionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Requirement (" + requirement + ") cannot be destroyed since the RequirementHasException " + requirementHasExceptionListOrphanCheckRequirementHasException + " in its requirementHasExceptionList field has a non-nullable requirement field.");
            }
            List<RiskControlHasRequirement> riskControlHasRequirementListOrphanCheck = requirement.getRiskControlHasRequirementList();
            for (RiskControlHasRequirement riskControlHasRequirementListOrphanCheckRiskControlHasRequirement : riskControlHasRequirementListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Requirement (" + requirement + ") cannot be destroyed since the RiskControlHasRequirement " + riskControlHasRequirementListOrphanCheckRiskControlHasRequirement + " in its riskControlHasRequirementList field has a non-nullable requirement field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            RequirementSpecNode requirementSpecNode = requirement.getRequirementSpecNode();
            if (requirementSpecNode != null) {
                requirementSpecNode.getRequirementList().remove(requirement);
                requirementSpecNode = em.merge(requirementSpecNode);
            }
            RequirementStatus requirementStatus = requirement.getRequirementStatus();
            if (requirementStatus != null) {
                requirementStatus.getRequirementList().remove(requirement);
                requirementStatus = em.merge(requirementStatus);
            }
            RequirementType requirementType = requirement.getRequirementType();
            if (requirementType != null) {
                requirementType.getRequirementList().remove(requirement);
                requirementType = em.merge(requirementType);
            }
            List<VmException> vmExceptionList = requirement.getVmExceptionList();
            for (VmException vmExceptionListVmException : vmExceptionList) {
                vmExceptionListVmException.getRequirementList().remove(requirement);
                vmExceptionListVmException = em.merge(vmExceptionListVmException);
            }
            List<Requirement> requirementList = requirement.getRequirementList();
            for (Requirement requirementListRequirement : requirementList) {
                requirementListRequirement.getRequirementList().remove(requirement);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            List<Requirement> requirementList1 = requirement.getRequirementList1();
            for (Requirement requirementList1Requirement : requirementList1) {
                requirementList1Requirement.getRequirementList().remove(requirement);
                requirementList1Requirement = em.merge(requirementList1Requirement);
            }
            List<Step> stepList = requirement.getStepList();
            for (Step stepListStep : stepList) {
                stepListStep.getRequirementList().remove(requirement);
                stepListStep = em.merge(stepListStep);
            }
            List<RiskControl> riskControlList = requirement.getRiskControlList();
            for (RiskControl riskControlListRiskControl : riskControlList) {
                riskControlListRiskControl.getRequirementList().remove(requirement);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
            }
            em.remove(requirement);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Requirement> findRequirementEntities() {
        return findRequirementEntities(true, -1, -1);
    }

    public List<Requirement> findRequirementEntities(int maxResults, int firstResult) {
        return findRequirementEntities(false, maxResults, firstResult);
    }

    private List<Requirement> findRequirementEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Requirement.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Requirement findRequirement(RequirementPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Requirement.class, id);
        } finally {
            em.close();
        }
    }

    public int getRequirementCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Requirement> rt = cq.from(Requirement.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
