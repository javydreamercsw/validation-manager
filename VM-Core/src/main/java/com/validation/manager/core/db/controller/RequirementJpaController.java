/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.Requirement;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.RiskControl;
import com.validation.manager.core.db.RequirementHasException;
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
            List<String> illegalOrphanMessages = null;
            for (RequirementHasException requirementHasExceptionListOldRequirementHasException : requirementHasExceptionListOld) {
                if (!requirementHasExceptionListNew.contains(requirementHasExceptionListOldRequirementHasException)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RequirementHasException " + requirementHasExceptionListOldRequirementHasException + " since its requirement field is not nullable.");
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
