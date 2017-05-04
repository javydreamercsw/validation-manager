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
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.Requirement;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.RiskControlHasRequirement;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
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

    public void create(Requirement requirement) {
        if (requirement.getRequirementList() == null) {
            requirement.setRequirementList(new ArrayList<Requirement>());
        }
        if (requirement.getRequirementList1() == null) {
            requirement.setRequirementList1(new ArrayList<Requirement>());
        }
        if (requirement.getStepList() == null) {
            requirement.setStepList(new ArrayList<Step>());
        }
        if (requirement.getRiskControlHasRequirementList() == null) {
            requirement.setRiskControlHasRequirementList(new ArrayList<RiskControlHasRequirement>());
        }
        if (requirement.getHistoryList() == null) {
            requirement.setHistoryList(new ArrayList<History>());
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
            RequirementStatus requirementStatusId = requirement.getRequirementStatusId();
            if (requirementStatusId != null) {
                requirementStatusId = em.getReference(requirementStatusId.getClass(), requirementStatusId.getId());
                requirement.setRequirementStatusId(requirementStatusId);
            }
            RequirementType requirementTypeId = requirement.getRequirementTypeId();
            if (requirementTypeId != null) {
                requirementTypeId = em.getReference(requirementTypeId.getClass(), requirementTypeId.getId());
                requirement.setRequirementTypeId(requirementTypeId);
            }
            List<Requirement> attachedRequirementList = new ArrayList<Requirement>();
            for (Requirement requirementListRequirementToAttach : requirement.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getId());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            requirement.setRequirementList(attachedRequirementList);
            List<Requirement> attachedRequirementList1 = new ArrayList<Requirement>();
            for (Requirement requirementList1RequirementToAttach : requirement.getRequirementList1()) {
                requirementList1RequirementToAttach = em.getReference(requirementList1RequirementToAttach.getClass(), requirementList1RequirementToAttach.getId());
                attachedRequirementList1.add(requirementList1RequirementToAttach);
            }
            requirement.setRequirementList1(attachedRequirementList1);
            List<Step> attachedStepList = new ArrayList<Step>();
            for (Step stepListStepToAttach : requirement.getStepList()) {
                stepListStepToAttach = em.getReference(stepListStepToAttach.getClass(), stepListStepToAttach.getStepPK());
                attachedStepList.add(stepListStepToAttach);
            }
            requirement.setStepList(attachedStepList);
            List<RiskControlHasRequirement> attachedRiskControlHasRequirementList = new ArrayList<RiskControlHasRequirement>();
            for (RiskControlHasRequirement riskControlHasRequirementListRiskControlHasRequirementToAttach : requirement.getRiskControlHasRequirementList()) {
                riskControlHasRequirementListRiskControlHasRequirementToAttach = em.getReference(riskControlHasRequirementListRiskControlHasRequirementToAttach.getClass(), riskControlHasRequirementListRiskControlHasRequirementToAttach.getRiskControlHasRequirementPK());
                attachedRiskControlHasRequirementList.add(riskControlHasRequirementListRiskControlHasRequirementToAttach);
            }
            requirement.setRiskControlHasRequirementList(attachedRiskControlHasRequirementList);
            List<History> attachedHistoryList = new ArrayList<History>();
            for (History historyListHistoryToAttach : requirement.getHistoryList()) {
                historyListHistoryToAttach = em.getReference(historyListHistoryToAttach.getClass(), historyListHistoryToAttach.getId());
                attachedHistoryList.add(historyListHistoryToAttach);
            }
            requirement.setHistoryList(attachedHistoryList);
            em.persist(requirement);
            if (requirementSpecNode != null) {
                requirementSpecNode.getRequirementList().add(requirement);
                requirementSpecNode = em.merge(requirementSpecNode);
            }
            if (requirementStatusId != null) {
                requirementStatusId.getRequirementList().add(requirement);
                requirementStatusId = em.merge(requirementStatusId);
            }
            if (requirementTypeId != null) {
                requirementTypeId.getRequirementList().add(requirement);
                requirementTypeId = em.merge(requirementTypeId);
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
            for (RiskControlHasRequirement riskControlHasRequirementListRiskControlHasRequirement : requirement.getRiskControlHasRequirementList()) {
                Requirement oldRequirementOfRiskControlHasRequirementListRiskControlHasRequirement = riskControlHasRequirementListRiskControlHasRequirement.getRequirement();
                riskControlHasRequirementListRiskControlHasRequirement.setRequirement(requirement);
                riskControlHasRequirementListRiskControlHasRequirement = em.merge(riskControlHasRequirementListRiskControlHasRequirement);
                if (oldRequirementOfRiskControlHasRequirementListRiskControlHasRequirement != null) {
                    oldRequirementOfRiskControlHasRequirementListRiskControlHasRequirement.getRiskControlHasRequirementList().remove(riskControlHasRequirementListRiskControlHasRequirement);
                    oldRequirementOfRiskControlHasRequirementListRiskControlHasRequirement = em.merge(oldRequirementOfRiskControlHasRequirementListRiskControlHasRequirement);
                }
            }
            for (History historyListHistory : requirement.getHistoryList()) {
                historyListHistory.getRequirementList().add(requirement);
                historyListHistory = em.merge(historyListHistory);
            }
            em.getTransaction().commit();
        }
        finally {
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
            Requirement persistentRequirement = em.find(Requirement.class, requirement.getId());
            RequirementSpecNode requirementSpecNodeOld = persistentRequirement.getRequirementSpecNode();
            RequirementSpecNode requirementSpecNodeNew = requirement.getRequirementSpecNode();
            RequirementStatus requirementStatusIdOld = persistentRequirement.getRequirementStatusId();
            RequirementStatus requirementStatusIdNew = requirement.getRequirementStatusId();
            RequirementType requirementTypeIdOld = persistentRequirement.getRequirementTypeId();
            RequirementType requirementTypeIdNew = requirement.getRequirementTypeId();
            List<Requirement> requirementListOld = persistentRequirement.getRequirementList();
            List<Requirement> requirementListNew = requirement.getRequirementList();
            List<Requirement> requirementList1Old = persistentRequirement.getRequirementList1();
            List<Requirement> requirementList1New = requirement.getRequirementList1();
            List<Step> stepListOld = persistentRequirement.getStepList();
            List<Step> stepListNew = requirement.getStepList();
            List<RiskControlHasRequirement> riskControlHasRequirementListOld = persistentRequirement.getRiskControlHasRequirementList();
            List<RiskControlHasRequirement> riskControlHasRequirementListNew = requirement.getRiskControlHasRequirementList();
            List<History> historyListOld = persistentRequirement.getHistoryList();
            List<History> historyListNew = requirement.getHistoryList();
            List<String> illegalOrphanMessages = null;
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
            if (requirementStatusIdNew != null) {
                requirementStatusIdNew = em.getReference(requirementStatusIdNew.getClass(), requirementStatusIdNew.getId());
                requirement.setRequirementStatusId(requirementStatusIdNew);
            }
            if (requirementTypeIdNew != null) {
                requirementTypeIdNew = em.getReference(requirementTypeIdNew.getClass(), requirementTypeIdNew.getId());
                requirement.setRequirementTypeId(requirementTypeIdNew);
            }
            List<Requirement> attachedRequirementListNew = new ArrayList<Requirement>();
            for (Requirement requirementListNewRequirementToAttach : requirementListNew) {
                requirementListNewRequirementToAttach = em.getReference(requirementListNewRequirementToAttach.getClass(), requirementListNewRequirementToAttach.getId());
                attachedRequirementListNew.add(requirementListNewRequirementToAttach);
            }
            requirementListNew = attachedRequirementListNew;
            requirement.setRequirementList(requirementListNew);
            List<Requirement> attachedRequirementList1New = new ArrayList<Requirement>();
            for (Requirement requirementList1NewRequirementToAttach : requirementList1New) {
                requirementList1NewRequirementToAttach = em.getReference(requirementList1NewRequirementToAttach.getClass(), requirementList1NewRequirementToAttach.getId());
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
            List<RiskControlHasRequirement> attachedRiskControlHasRequirementListNew = new ArrayList<RiskControlHasRequirement>();
            for (RiskControlHasRequirement riskControlHasRequirementListNewRiskControlHasRequirementToAttach : riskControlHasRequirementListNew) {
                riskControlHasRequirementListNewRiskControlHasRequirementToAttach = em.getReference(riskControlHasRequirementListNewRiskControlHasRequirementToAttach.getClass(), riskControlHasRequirementListNewRiskControlHasRequirementToAttach.getRiskControlHasRequirementPK());
                attachedRiskControlHasRequirementListNew.add(riskControlHasRequirementListNewRiskControlHasRequirementToAttach);
            }
            riskControlHasRequirementListNew = attachedRiskControlHasRequirementListNew;
            requirement.setRiskControlHasRequirementList(riskControlHasRequirementListNew);
            List<History> attachedHistoryListNew = new ArrayList<History>();
            for (History historyListNewHistoryToAttach : historyListNew) {
                historyListNewHistoryToAttach = em.getReference(historyListNewHistoryToAttach.getClass(), historyListNewHistoryToAttach.getId());
                attachedHistoryListNew.add(historyListNewHistoryToAttach);
            }
            historyListNew = attachedHistoryListNew;
            requirement.setHistoryList(historyListNew);
            requirement = em.merge(requirement);
            if (requirementSpecNodeOld != null && !requirementSpecNodeOld.equals(requirementSpecNodeNew)) {
                requirementSpecNodeOld.getRequirementList().remove(requirement);
                requirementSpecNodeOld = em.merge(requirementSpecNodeOld);
            }
            if (requirementSpecNodeNew != null && !requirementSpecNodeNew.equals(requirementSpecNodeOld)) {
                requirementSpecNodeNew.getRequirementList().add(requirement);
                requirementSpecNodeNew = em.merge(requirementSpecNodeNew);
            }
            if (requirementStatusIdOld != null && !requirementStatusIdOld.equals(requirementStatusIdNew)) {
                requirementStatusIdOld.getRequirementList().remove(requirement);
                requirementStatusIdOld = em.merge(requirementStatusIdOld);
            }
            if (requirementStatusIdNew != null && !requirementStatusIdNew.equals(requirementStatusIdOld)) {
                requirementStatusIdNew.getRequirementList().add(requirement);
                requirementStatusIdNew = em.merge(requirementStatusIdNew);
            }
            if (requirementTypeIdOld != null && !requirementTypeIdOld.equals(requirementTypeIdNew)) {
                requirementTypeIdOld.getRequirementList().remove(requirement);
                requirementTypeIdOld = em.merge(requirementTypeIdOld);
            }
            if (requirementTypeIdNew != null && !requirementTypeIdNew.equals(requirementTypeIdOld)) {
                requirementTypeIdNew.getRequirementList().add(requirement);
                requirementTypeIdNew = em.merge(requirementTypeIdNew);
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
            for (History historyListOldHistory : historyListOld) {
                if (!historyListNew.contains(historyListOldHistory)) {
                    historyListOldHistory.getRequirementList().remove(requirement);
                    historyListOldHistory = em.merge(historyListOldHistory);
                }
            }
            for (History historyListNewHistory : historyListNew) {
                if (!historyListOld.contains(historyListNewHistory)) {
                    historyListNewHistory.getRequirementList().add(requirement);
                    historyListNewHistory = em.merge(historyListNewHistory);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = requirement.getId();
                if (findRequirement(id) == null) {
                    throw new NonexistentEntityException("The requirement with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Requirement requirement;
            try {
                requirement = em.getReference(Requirement.class, id);
                requirement.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The requirement with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
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
            RequirementStatus requirementStatusId = requirement.getRequirementStatusId();
            if (requirementStatusId != null) {
                requirementStatusId.getRequirementList().remove(requirement);
                requirementStatusId = em.merge(requirementStatusId);
            }
            RequirementType requirementTypeId = requirement.getRequirementTypeId();
            if (requirementTypeId != null) {
                requirementTypeId.getRequirementList().remove(requirement);
                requirementTypeId = em.merge(requirementTypeId);
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
            List<History> historyList = requirement.getHistoryList();
            for (History historyListHistory : historyList) {
                historyListHistory.getRequirementList().remove(requirement);
                historyListHistory = em.merge(historyListHistory);
            }
            em.remove(requirement);
            em.getTransaction().commit();
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

    public Requirement findRequirement(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Requirement.class, id);
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

}
