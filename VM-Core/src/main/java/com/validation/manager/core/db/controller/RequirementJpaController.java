/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.Requirement;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.Step;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.RequirementHasException;
import com.validation.manager.core.db.fmea.RiskControl;
import com.validation.manager.core.db.StepHasRequirement;
import com.validation.manager.core.db.RequirementHasRequirement;
import com.validation.manager.core.db.RequirementPK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
        if (requirement.getStepList() == null) {
            requirement.setStepList(new ArrayList<Step>());
        }
        if (requirement.getRequirementHasExceptionList() == null) {
            requirement.setRequirementHasExceptionList(new ArrayList<RequirementHasException>());
        }
        if (requirement.getRiskControlList() == null) {
            requirement.setRiskControlList(new ArrayList<RiskControl>());
        }
        if (requirement.getStepHasRequirementList() == null) {
            requirement.setStepHasRequirementList(new ArrayList<StepHasRequirement>());
        }
        if (requirement.getRequirementHasRequirementList() == null) {
            requirement.setRequirementHasRequirementList(new ArrayList<RequirementHasRequirement>());
        }
        if (requirement.getRequirementHasRequirementList1() == null) {
            requirement.setRequirementHasRequirementList1(new ArrayList<RequirementHasRequirement>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementType requirementTypeId = requirement.getRequirementTypeId();
            if (requirementTypeId != null) {
                requirementTypeId = em.getReference(requirementTypeId.getClass(), requirementTypeId.getId());
                requirement.setRequirementTypeId(requirementTypeId);
            }
            RequirementStatus requirementStatusId = requirement.getRequirementStatusId();
            if (requirementStatusId != null) {
                requirementStatusId = em.getReference(requirementStatusId.getClass(), requirementStatusId.getId());
                requirement.setRequirementStatusId(requirementStatusId);
            }
            RequirementSpecNode requirementSpecNode = requirement.getRequirementSpecNode();
            if (requirementSpecNode != null) {
                requirementSpecNode = em.getReference(requirementSpecNode.getClass(), requirementSpecNode.getRequirementSpecNodePK());
                requirement.setRequirementSpecNode(requirementSpecNode);
            }
            List<Step> attachedStepList = new ArrayList<Step>();
            for (Step stepListStepToAttach : requirement.getStepList()) {
                stepListStepToAttach = em.getReference(stepListStepToAttach.getClass(), stepListStepToAttach.getStepPK());
                attachedStepList.add(stepListStepToAttach);
            }
            requirement.setStepList(attachedStepList);
            List<RequirementHasException> attachedRequirementHasExceptionList = new ArrayList<RequirementHasException>();
            for (RequirementHasException requirementHasExceptionListRequirementHasExceptionToAttach : requirement.getRequirementHasExceptionList()) {
                requirementHasExceptionListRequirementHasExceptionToAttach = em.getReference(requirementHasExceptionListRequirementHasExceptionToAttach.getClass(), requirementHasExceptionListRequirementHasExceptionToAttach.getRequirementHasExceptionPK());
                attachedRequirementHasExceptionList.add(requirementHasExceptionListRequirementHasExceptionToAttach);
            }
            requirement.setRequirementHasExceptionList(attachedRequirementHasExceptionList);
            List<RiskControl> attachedRiskControlList = new ArrayList<RiskControl>();
            for (RiskControl riskControlListRiskControlToAttach : requirement.getRiskControlList()) {
                riskControlListRiskControlToAttach = em.getReference(riskControlListRiskControlToAttach.getClass(), riskControlListRiskControlToAttach.getRiskControlPK());
                attachedRiskControlList.add(riskControlListRiskControlToAttach);
            }
            requirement.setRiskControlList(attachedRiskControlList);
            List<StepHasRequirement> attachedStepHasRequirementList = new ArrayList<StepHasRequirement>();
            for (StepHasRequirement stepHasRequirementListStepHasRequirementToAttach : requirement.getStepHasRequirementList()) {
                stepHasRequirementListStepHasRequirementToAttach = em.getReference(stepHasRequirementListStepHasRequirementToAttach.getClass(), stepHasRequirementListStepHasRequirementToAttach.getStepHasRequirementPK());
                attachedStepHasRequirementList.add(stepHasRequirementListStepHasRequirementToAttach);
            }
            requirement.setStepHasRequirementList(attachedStepHasRequirementList);
            List<RequirementHasRequirement> attachedRequirementHasRequirementList = new ArrayList<RequirementHasRequirement>();
            for (RequirementHasRequirement requirementHasRequirementListRequirementHasRequirementToAttach : requirement.getRequirementHasRequirementList()) {
                requirementHasRequirementListRequirementHasRequirementToAttach = em.getReference(requirementHasRequirementListRequirementHasRequirementToAttach.getClass(), requirementHasRequirementListRequirementHasRequirementToAttach.getRequirementHasRequirementPK());
                attachedRequirementHasRequirementList.add(requirementHasRequirementListRequirementHasRequirementToAttach);
            }
            requirement.setRequirementHasRequirementList(attachedRequirementHasRequirementList);
            List<RequirementHasRequirement> attachedRequirementHasRequirementList1 = new ArrayList<RequirementHasRequirement>();
            for (RequirementHasRequirement requirementHasRequirementList1RequirementHasRequirementToAttach : requirement.getRequirementHasRequirementList1()) {
                requirementHasRequirementList1RequirementHasRequirementToAttach = em.getReference(requirementHasRequirementList1RequirementHasRequirementToAttach.getClass(), requirementHasRequirementList1RequirementHasRequirementToAttach.getRequirementHasRequirementPK());
                attachedRequirementHasRequirementList1.add(requirementHasRequirementList1RequirementHasRequirementToAttach);
            }
            requirement.setRequirementHasRequirementList1(attachedRequirementHasRequirementList1);
            em.persist(requirement);
            if (requirementTypeId != null) {
                requirementTypeId.getRequirementList().add(requirement);
                requirementTypeId = em.merge(requirementTypeId);
            }
            if (requirementStatusId != null) {
                requirementStatusId.getRequirementList().add(requirement);
                requirementStatusId = em.merge(requirementStatusId);
            }
            if (requirementSpecNode != null) {
                requirementSpecNode.getRequirementList().add(requirement);
                requirementSpecNode = em.merge(requirementSpecNode);
            }
            for (Step stepListStep : requirement.getStepList()) {
                stepListStep.getRequirementList().add(requirement);
                stepListStep = em.merge(stepListStep);
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
            for (RiskControl riskControlListRiskControl : requirement.getRiskControlList()) {
                riskControlListRiskControl.getRequirementList().add(requirement);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
            }
            for (StepHasRequirement stepHasRequirementListStepHasRequirement : requirement.getStepHasRequirementList()) {
                Requirement oldRequirementOfStepHasRequirementListStepHasRequirement = stepHasRequirementListStepHasRequirement.getRequirement();
                stepHasRequirementListStepHasRequirement.setRequirement(requirement);
                stepHasRequirementListStepHasRequirement = em.merge(stepHasRequirementListStepHasRequirement);
                if (oldRequirementOfStepHasRequirementListStepHasRequirement != null) {
                    oldRequirementOfStepHasRequirementListStepHasRequirement.getStepHasRequirementList().remove(stepHasRequirementListStepHasRequirement);
                    oldRequirementOfStepHasRequirementListStepHasRequirement = em.merge(oldRequirementOfStepHasRequirementListStepHasRequirement);
                }
            }
            for (RequirementHasRequirement requirementHasRequirementListRequirementHasRequirement : requirement.getRequirementHasRequirementList()) {
                Requirement oldRequirementOfRequirementHasRequirementListRequirementHasRequirement = requirementHasRequirementListRequirementHasRequirement.getParentRequirement();
                requirementHasRequirementListRequirementHasRequirement.setParentRequirement(requirement);
                requirementHasRequirementListRequirementHasRequirement = em.merge(requirementHasRequirementListRequirementHasRequirement);
                if (oldRequirementOfRequirementHasRequirementListRequirementHasRequirement != null) {
                    oldRequirementOfRequirementHasRequirementListRequirementHasRequirement.getRequirementHasRequirementList().remove(requirementHasRequirementListRequirementHasRequirement);
                    oldRequirementOfRequirementHasRequirementListRequirementHasRequirement = em.merge(oldRequirementOfRequirementHasRequirementListRequirementHasRequirement);
                }
            }
            for (RequirementHasRequirement requirementHasRequirementList1RequirementHasRequirement : requirement.getRequirementHasRequirementList1()) {
                Requirement oldRequirement1OfRequirementHasRequirementList1RequirementHasRequirement = requirementHasRequirementList1RequirementHasRequirement.getChildRequirement();
                requirementHasRequirementList1RequirementHasRequirement.setChildRequirement(requirement);
                requirementHasRequirementList1RequirementHasRequirement = em.merge(requirementHasRequirementList1RequirementHasRequirement);
                if (oldRequirement1OfRequirementHasRequirementList1RequirementHasRequirement != null) {
                    oldRequirement1OfRequirementHasRequirementList1RequirementHasRequirement.getRequirementHasRequirementList1().remove(requirementHasRequirementList1RequirementHasRequirement);
                    oldRequirement1OfRequirementHasRequirementList1RequirementHasRequirement = em.merge(oldRequirement1OfRequirementHasRequirementList1RequirementHasRequirement);
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
            RequirementType requirementTypeIdOld = persistentRequirement.getRequirementTypeId();
            RequirementType requirementTypeIdNew = requirement.getRequirementTypeId();
            RequirementStatus requirementStatusIdOld = persistentRequirement.getRequirementStatusId();
            RequirementStatus requirementStatusIdNew = requirement.getRequirementStatusId();
            RequirementSpecNode requirementSpecNodeOld = persistentRequirement.getRequirementSpecNode();
            RequirementSpecNode requirementSpecNodeNew = requirement.getRequirementSpecNode();
            List<Step> stepListOld = persistentRequirement.getStepList();
            List<Step> stepListNew = requirement.getStepList();
            List<RequirementHasException> requirementHasExceptionListOld = persistentRequirement.getRequirementHasExceptionList();
            List<RequirementHasException> requirementHasExceptionListNew = requirement.getRequirementHasExceptionList();
            List<RiskControl> riskControlListOld = persistentRequirement.getRiskControlList();
            List<RiskControl> riskControlListNew = requirement.getRiskControlList();
            List<StepHasRequirement> stepHasRequirementListOld = persistentRequirement.getStepHasRequirementList();
            List<StepHasRequirement> stepHasRequirementListNew = requirement.getStepHasRequirementList();
            List<RequirementHasRequirement> requirementHasRequirementListOld = persistentRequirement.getRequirementHasRequirementList();
            List<RequirementHasRequirement> requirementHasRequirementListNew = requirement.getRequirementHasRequirementList();
            List<RequirementHasRequirement> requirementHasRequirementList1Old = persistentRequirement.getRequirementHasRequirementList1();
            List<RequirementHasRequirement> requirementHasRequirementList1New = requirement.getRequirementHasRequirementList1();
            List<String> illegalOrphanMessages = null;
            for (RequirementHasException requirementHasExceptionListOldRequirementHasException : requirementHasExceptionListOld) {
                if (!requirementHasExceptionListNew.contains(requirementHasExceptionListOldRequirementHasException)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RequirementHasException " + requirementHasExceptionListOldRequirementHasException + " since its requirement field is not nullable.");
                }
            }
            for (StepHasRequirement stepHasRequirementListOldStepHasRequirement : stepHasRequirementListOld) {
                if (!stepHasRequirementListNew.contains(stepHasRequirementListOldStepHasRequirement)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain StepHasRequirement " + stepHasRequirementListOldStepHasRequirement + " since its requirement field is not nullable.");
                }
            }
            for (RequirementHasRequirement requirementHasRequirementListOldRequirementHasRequirement : requirementHasRequirementListOld) {
                if (!requirementHasRequirementListNew.contains(requirementHasRequirementListOldRequirementHasRequirement)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RequirementHasRequirement " + requirementHasRequirementListOldRequirementHasRequirement + " since its requirement field is not nullable.");
                }
            }
            for (RequirementHasRequirement requirementHasRequirementList1OldRequirementHasRequirement : requirementHasRequirementList1Old) {
                if (!requirementHasRequirementList1New.contains(requirementHasRequirementList1OldRequirementHasRequirement)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RequirementHasRequirement " + requirementHasRequirementList1OldRequirementHasRequirement + " since its requirement1 field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (requirementTypeIdNew != null) {
                requirementTypeIdNew = em.getReference(requirementTypeIdNew.getClass(), requirementTypeIdNew.getId());
                requirement.setRequirementTypeId(requirementTypeIdNew);
            }
            if (requirementStatusIdNew != null) {
                requirementStatusIdNew = em.getReference(requirementStatusIdNew.getClass(), requirementStatusIdNew.getId());
                requirement.setRequirementStatusId(requirementStatusIdNew);
            }
            if (requirementSpecNodeNew != null) {
                requirementSpecNodeNew = em.getReference(requirementSpecNodeNew.getClass(), requirementSpecNodeNew.getRequirementSpecNodePK());
                requirement.setRequirementSpecNode(requirementSpecNodeNew);
            }
            List<Step> attachedStepListNew = new ArrayList<Step>();
            for (Step stepListNewStepToAttach : stepListNew) {
                stepListNewStepToAttach = em.getReference(stepListNewStepToAttach.getClass(), stepListNewStepToAttach.getStepPK());
                attachedStepListNew.add(stepListNewStepToAttach);
            }
            stepListNew = attachedStepListNew;
            requirement.setStepList(stepListNew);
            List<RequirementHasException> attachedRequirementHasExceptionListNew = new ArrayList<RequirementHasException>();
            for (RequirementHasException requirementHasExceptionListNewRequirementHasExceptionToAttach : requirementHasExceptionListNew) {
                requirementHasExceptionListNewRequirementHasExceptionToAttach = em.getReference(requirementHasExceptionListNewRequirementHasExceptionToAttach.getClass(), requirementHasExceptionListNewRequirementHasExceptionToAttach.getRequirementHasExceptionPK());
                attachedRequirementHasExceptionListNew.add(requirementHasExceptionListNewRequirementHasExceptionToAttach);
            }
            requirementHasExceptionListNew = attachedRequirementHasExceptionListNew;
            requirement.setRequirementHasExceptionList(requirementHasExceptionListNew);
            List<RiskControl> attachedRiskControlListNew = new ArrayList<RiskControl>();
            for (RiskControl riskControlListNewRiskControlToAttach : riskControlListNew) {
                riskControlListNewRiskControlToAttach = em.getReference(riskControlListNewRiskControlToAttach.getClass(), riskControlListNewRiskControlToAttach.getRiskControlPK());
                attachedRiskControlListNew.add(riskControlListNewRiskControlToAttach);
            }
            riskControlListNew = attachedRiskControlListNew;
            requirement.setRiskControlList(riskControlListNew);
            List<StepHasRequirement> attachedStepHasRequirementListNew = new ArrayList<StepHasRequirement>();
            for (StepHasRequirement stepHasRequirementListNewStepHasRequirementToAttach : stepHasRequirementListNew) {
                stepHasRequirementListNewStepHasRequirementToAttach = em.getReference(stepHasRequirementListNewStepHasRequirementToAttach.getClass(), stepHasRequirementListNewStepHasRequirementToAttach.getStepHasRequirementPK());
                attachedStepHasRequirementListNew.add(stepHasRequirementListNewStepHasRequirementToAttach);
            }
            stepHasRequirementListNew = attachedStepHasRequirementListNew;
            requirement.setStepHasRequirementList(stepHasRequirementListNew);
            List<RequirementHasRequirement> attachedRequirementHasRequirementListNew = new ArrayList<RequirementHasRequirement>();
            for (RequirementHasRequirement requirementHasRequirementListNewRequirementHasRequirementToAttach : requirementHasRequirementListNew) {
                requirementHasRequirementListNewRequirementHasRequirementToAttach = em.getReference(requirementHasRequirementListNewRequirementHasRequirementToAttach.getClass(), requirementHasRequirementListNewRequirementHasRequirementToAttach.getRequirementHasRequirementPK());
                attachedRequirementHasRequirementListNew.add(requirementHasRequirementListNewRequirementHasRequirementToAttach);
            }
            requirementHasRequirementListNew = attachedRequirementHasRequirementListNew;
            requirement.setRequirementHasRequirementList(requirementHasRequirementListNew);
            List<RequirementHasRequirement> attachedRequirementHasRequirementList1New = new ArrayList<RequirementHasRequirement>();
            for (RequirementHasRequirement requirementHasRequirementList1NewRequirementHasRequirementToAttach : requirementHasRequirementList1New) {
                requirementHasRequirementList1NewRequirementHasRequirementToAttach = em.getReference(requirementHasRequirementList1NewRequirementHasRequirementToAttach.getClass(), requirementHasRequirementList1NewRequirementHasRequirementToAttach.getRequirementHasRequirementPK());
                attachedRequirementHasRequirementList1New.add(requirementHasRequirementList1NewRequirementHasRequirementToAttach);
            }
            requirementHasRequirementList1New = attachedRequirementHasRequirementList1New;
            requirement.setRequirementHasRequirementList1(requirementHasRequirementList1New);
            requirement = em.merge(requirement);
            if (requirementTypeIdOld != null && !requirementTypeIdOld.equals(requirementTypeIdNew)) {
                requirementTypeIdOld.getRequirementList().remove(requirement);
                requirementTypeIdOld = em.merge(requirementTypeIdOld);
            }
            if (requirementTypeIdNew != null && !requirementTypeIdNew.equals(requirementTypeIdOld)) {
                requirementTypeIdNew.getRequirementList().add(requirement);
                requirementTypeIdNew = em.merge(requirementTypeIdNew);
            }
            if (requirementStatusIdOld != null && !requirementStatusIdOld.equals(requirementStatusIdNew)) {
                requirementStatusIdOld.getRequirementList().remove(requirement);
                requirementStatusIdOld = em.merge(requirementStatusIdOld);
            }
            if (requirementStatusIdNew != null && !requirementStatusIdNew.equals(requirementStatusIdOld)) {
                requirementStatusIdNew.getRequirementList().add(requirement);
                requirementStatusIdNew = em.merge(requirementStatusIdNew);
            }
            if (requirementSpecNodeOld != null && !requirementSpecNodeOld.equals(requirementSpecNodeNew)) {
                requirementSpecNodeOld.getRequirementList().remove(requirement);
                requirementSpecNodeOld = em.merge(requirementSpecNodeOld);
            }
            if (requirementSpecNodeNew != null && !requirementSpecNodeNew.equals(requirementSpecNodeOld)) {
                requirementSpecNodeNew.getRequirementList().add(requirement);
                requirementSpecNodeNew = em.merge(requirementSpecNodeNew);
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
            for (StepHasRequirement stepHasRequirementListNewStepHasRequirement : stepHasRequirementListNew) {
                if (!stepHasRequirementListOld.contains(stepHasRequirementListNewStepHasRequirement)) {
                    Requirement oldRequirementOfStepHasRequirementListNewStepHasRequirement = stepHasRequirementListNewStepHasRequirement.getRequirement();
                    stepHasRequirementListNewStepHasRequirement.setRequirement(requirement);
                    stepHasRequirementListNewStepHasRequirement = em.merge(stepHasRequirementListNewStepHasRequirement);
                    if (oldRequirementOfStepHasRequirementListNewStepHasRequirement != null && !oldRequirementOfStepHasRequirementListNewStepHasRequirement.equals(requirement)) {
                        oldRequirementOfStepHasRequirementListNewStepHasRequirement.getStepHasRequirementList().remove(stepHasRequirementListNewStepHasRequirement);
                        oldRequirementOfStepHasRequirementListNewStepHasRequirement = em.merge(oldRequirementOfStepHasRequirementListNewStepHasRequirement);
                    }
                }
            }
            for (RequirementHasRequirement requirementHasRequirementListNewRequirementHasRequirement : requirementHasRequirementListNew) {
                if (!requirementHasRequirementListOld.contains(requirementHasRequirementListNewRequirementHasRequirement)) {
                    Requirement oldRequirementOfRequirementHasRequirementListNewRequirementHasRequirement = requirementHasRequirementListNewRequirementHasRequirement.getParentRequirement();
                    requirementHasRequirementListNewRequirementHasRequirement.setParentRequirement(requirement);
                    requirementHasRequirementListNewRequirementHasRequirement = em.merge(requirementHasRequirementListNewRequirementHasRequirement);
                    if (oldRequirementOfRequirementHasRequirementListNewRequirementHasRequirement != null && !oldRequirementOfRequirementHasRequirementListNewRequirementHasRequirement.equals(requirement)) {
                        oldRequirementOfRequirementHasRequirementListNewRequirementHasRequirement.getRequirementHasRequirementList().remove(requirementHasRequirementListNewRequirementHasRequirement);
                        oldRequirementOfRequirementHasRequirementListNewRequirementHasRequirement = em.merge(oldRequirementOfRequirementHasRequirementListNewRequirementHasRequirement);
                    }
                }
            }
            for (RequirementHasRequirement requirementHasRequirementList1NewRequirementHasRequirement : requirementHasRequirementList1New) {
                if (!requirementHasRequirementList1Old.contains(requirementHasRequirementList1NewRequirementHasRequirement)) {
                    Requirement oldRequirement1OfRequirementHasRequirementList1NewRequirementHasRequirement = requirementHasRequirementList1NewRequirementHasRequirement.getChildRequirement();
                    requirementHasRequirementList1NewRequirementHasRequirement.setChildRequirement(requirement);
                    requirementHasRequirementList1NewRequirementHasRequirement = em.merge(requirementHasRequirementList1NewRequirementHasRequirement);
                    if (oldRequirement1OfRequirementHasRequirementList1NewRequirementHasRequirement != null && !oldRequirement1OfRequirementHasRequirementList1NewRequirementHasRequirement.equals(requirement)) {
                        oldRequirement1OfRequirementHasRequirementList1NewRequirementHasRequirement.getRequirementHasRequirementList1().remove(requirementHasRequirementList1NewRequirementHasRequirement);
                        oldRequirement1OfRequirementHasRequirementList1NewRequirementHasRequirement = em.merge(oldRequirement1OfRequirementHasRequirementList1NewRequirementHasRequirement);
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
            List<StepHasRequirement> stepHasRequirementListOrphanCheck = requirement.getStepHasRequirementList();
            for (StepHasRequirement stepHasRequirementListOrphanCheckStepHasRequirement : stepHasRequirementListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Requirement (" + requirement + ") cannot be destroyed since the StepHasRequirement " + stepHasRequirementListOrphanCheckStepHasRequirement + " in its stepHasRequirementList field has a non-nullable requirement field.");
            }
            List<RequirementHasRequirement> requirementHasRequirementListOrphanCheck = requirement.getRequirementHasRequirementList();
            for (RequirementHasRequirement requirementHasRequirementListOrphanCheckRequirementHasRequirement : requirementHasRequirementListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Requirement (" + requirement + ") cannot be destroyed since the RequirementHasRequirement " + requirementHasRequirementListOrphanCheckRequirementHasRequirement + " in its requirementHasRequirementList field has a non-nullable requirement field.");
            }
            List<RequirementHasRequirement> requirementHasRequirementList1OrphanCheck = requirement.getRequirementHasRequirementList1();
            for (RequirementHasRequirement requirementHasRequirementList1OrphanCheckRequirementHasRequirement : requirementHasRequirementList1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Requirement (" + requirement + ") cannot be destroyed since the RequirementHasRequirement " + requirementHasRequirementList1OrphanCheckRequirementHasRequirement + " in its requirementHasRequirementList1 field has a non-nullable requirement1 field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            RequirementType requirementTypeId = requirement.getRequirementTypeId();
            if (requirementTypeId != null) {
                requirementTypeId.getRequirementList().remove(requirement);
                requirementTypeId = em.merge(requirementTypeId);
            }
            RequirementStatus requirementStatusId = requirement.getRequirementStatusId();
            if (requirementStatusId != null) {
                requirementStatusId.getRequirementList().remove(requirement);
                requirementStatusId = em.merge(requirementStatusId);
            }
            RequirementSpecNode requirementSpecNode = requirement.getRequirementSpecNode();
            if (requirementSpecNode != null) {
                requirementSpecNode.getRequirementList().remove(requirement);
                requirementSpecNode = em.merge(requirementSpecNode);
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
