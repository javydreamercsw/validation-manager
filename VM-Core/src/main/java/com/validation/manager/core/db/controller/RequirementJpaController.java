/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.RequirementType;
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
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
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
            requirement.setRequirementList(new ArrayList<>());
        }
        if (requirement.getStepList() == null) {
            requirement.setStepList(new ArrayList<>());
        }
        if (requirement.getRiskControlHasRequirementList() == null) {
            requirement.setRiskControlHasRequirementList(new ArrayList<>());
        }
        if (requirement.getHistoryList() == null) {
            requirement.setHistoryList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Requirement parentRequirementId = requirement.getParentRequirementId();
            if (parentRequirementId != null) {
                parentRequirementId = em.getReference(parentRequirementId.getClass(), parentRequirementId.getId());
                requirement.setParentRequirementId(parentRequirementId);
            }
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
            List<Requirement> attachedRequirementList = new ArrayList<>();
            for (Requirement requirementListRequirementToAttach : requirement.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getId());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            requirement.setRequirementList(attachedRequirementList);
            List<Step> attachedStepList = new ArrayList<>();
            for (Step stepListStepToAttach : requirement.getStepList()) {
                stepListStepToAttach = em.getReference(stepListStepToAttach.getClass(), stepListStepToAttach.getStepPK());
                attachedStepList.add(stepListStepToAttach);
            }
            requirement.setStepList(attachedStepList);
            List<RiskControlHasRequirement> attachedRiskControlHasRequirementList = new ArrayList<>();
            for (RiskControlHasRequirement riskControlHasRequirementListRiskControlHasRequirementToAttach : requirement.getRiskControlHasRequirementList()) {
                riskControlHasRequirementListRiskControlHasRequirementToAttach = em.getReference(riskControlHasRequirementListRiskControlHasRequirementToAttach.getClass(), riskControlHasRequirementListRiskControlHasRequirementToAttach.getRiskControlHasRequirementPK());
                attachedRiskControlHasRequirementList.add(riskControlHasRequirementListRiskControlHasRequirementToAttach);
            }
            requirement.setRiskControlHasRequirementList(attachedRiskControlHasRequirementList);
            List<History> attachedHistoryList = new ArrayList<>();
            for (History historyListHistoryToAttach : requirement.getHistoryList()) {
                historyListHistoryToAttach = em.getReference(historyListHistoryToAttach.getClass(), historyListHistoryToAttach.getId());
                attachedHistoryList.add(historyListHistoryToAttach);
            }
            requirement.setHistoryList(attachedHistoryList);
            em.persist(requirement);
            if (parentRequirementId != null) {
                parentRequirementId.getRequirementList().add(requirement);
                parentRequirementId = em.merge(parentRequirementId);
            }
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
                Requirement oldParentRequirementIdOfRequirementListRequirement = requirementListRequirement.getParentRequirementId();
                requirementListRequirement.setParentRequirementId(requirement);
                requirementListRequirement = em.merge(requirementListRequirement);
                if (oldParentRequirementIdOfRequirementListRequirement != null) {
                    oldParentRequirementIdOfRequirementListRequirement.getRequirementList().remove(requirementListRequirement);
                    oldParentRequirementIdOfRequirementListRequirement = em.merge(oldParentRequirementIdOfRequirementListRequirement);
                }
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
                Requirement oldRequirementIdOfHistoryListHistory = historyListHistory.getRequirementId();
                historyListHistory.setRequirementId(requirement);
                historyListHistory = em.merge(historyListHistory);
                if (oldRequirementIdOfHistoryListHistory != null) {
                    oldRequirementIdOfHistoryListHistory.getHistoryList().remove(historyListHistory);
                    oldRequirementIdOfHistoryListHistory = em.merge(oldRequirementIdOfHistoryListHistory);
                }
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
            Requirement parentRequirementIdOld = persistentRequirement.getParentRequirementId();
            Requirement parentRequirementIdNew = requirement.getParentRequirementId();
            RequirementSpecNode requirementSpecNodeOld = persistentRequirement.getRequirementSpecNode();
            RequirementSpecNode requirementSpecNodeNew = requirement.getRequirementSpecNode();
            RequirementStatus requirementStatusIdOld = persistentRequirement.getRequirementStatusId();
            RequirementStatus requirementStatusIdNew = requirement.getRequirementStatusId();
            RequirementType requirementTypeIdOld = persistentRequirement.getRequirementTypeId();
            RequirementType requirementTypeIdNew = requirement.getRequirementTypeId();
            List<Requirement> requirementListOld = persistentRequirement.getRequirementList();
            List<Requirement> requirementListNew = requirement.getRequirementList();
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
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RiskControlHasRequirement " + riskControlHasRequirementListOldRiskControlHasRequirement + " since its requirement field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (parentRequirementIdNew != null) {
                parentRequirementIdNew = em.getReference(parentRequirementIdNew.getClass(), parentRequirementIdNew.getId());
                requirement.setParentRequirementId(parentRequirementIdNew);
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
            List<Requirement> attachedRequirementListNew = new ArrayList<>();
            for (Requirement requirementListNewRequirementToAttach : requirementListNew) {
                requirementListNewRequirementToAttach = em.getReference(requirementListNewRequirementToAttach.getClass(), requirementListNewRequirementToAttach.getId());
                attachedRequirementListNew.add(requirementListNewRequirementToAttach);
            }
            requirementListNew = attachedRequirementListNew;
            requirement.setRequirementList(requirementListNew);
            List<Step> attachedStepListNew = new ArrayList<>();
            for (Step stepListNewStepToAttach : stepListNew) {
                stepListNewStepToAttach = em.getReference(stepListNewStepToAttach.getClass(), stepListNewStepToAttach.getStepPK());
                attachedStepListNew.add(stepListNewStepToAttach);
            }
            stepListNew = attachedStepListNew;
            requirement.setStepList(stepListNew);
            List<RiskControlHasRequirement> attachedRiskControlHasRequirementListNew = new ArrayList<>();
            for (RiskControlHasRequirement riskControlHasRequirementListNewRiskControlHasRequirementToAttach : riskControlHasRequirementListNew) {
                riskControlHasRequirementListNewRiskControlHasRequirementToAttach = em.getReference(riskControlHasRequirementListNewRiskControlHasRequirementToAttach.getClass(), riskControlHasRequirementListNewRiskControlHasRequirementToAttach.getRiskControlHasRequirementPK());
                attachedRiskControlHasRequirementListNew.add(riskControlHasRequirementListNewRiskControlHasRequirementToAttach);
            }
            riskControlHasRequirementListNew = attachedRiskControlHasRequirementListNew;
            requirement.setRiskControlHasRequirementList(riskControlHasRequirementListNew);
            List<History> attachedHistoryListNew = new ArrayList<>();
            for (History historyListNewHistoryToAttach : historyListNew) {
                historyListNewHistoryToAttach = em.getReference(historyListNewHistoryToAttach.getClass(), historyListNewHistoryToAttach.getId());
                attachedHistoryListNew.add(historyListNewHistoryToAttach);
            }
            historyListNew = attachedHistoryListNew;
            requirement.setHistoryList(historyListNew);
            requirement = em.merge(requirement);
            if (parentRequirementIdOld != null && !parentRequirementIdOld.equals(parentRequirementIdNew)) {
                parentRequirementIdOld.getRequirementList().remove(requirement);
                parentRequirementIdOld = em.merge(parentRequirementIdOld);
            }
            if (parentRequirementIdNew != null && !parentRequirementIdNew.equals(parentRequirementIdOld)) {
                parentRequirementIdNew.getRequirementList().add(requirement);
                parentRequirementIdNew = em.merge(parentRequirementIdNew);
            }
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
                    requirementListOldRequirement.setParentRequirementId(null);
                    requirementListOldRequirement = em.merge(requirementListOldRequirement);
                }
            }
            for (Requirement requirementListNewRequirement : requirementListNew) {
                if (!requirementListOld.contains(requirementListNewRequirement)) {
                    Requirement oldParentRequirementIdOfRequirementListNewRequirement = requirementListNewRequirement.getParentRequirementId();
                    requirementListNewRequirement.setParentRequirementId(requirement);
                    requirementListNewRequirement = em.merge(requirementListNewRequirement);
                    if (oldParentRequirementIdOfRequirementListNewRequirement != null && !oldParentRequirementIdOfRequirementListNewRequirement.equals(requirement)) {
                        oldParentRequirementIdOfRequirementListNewRequirement.getRequirementList().remove(requirementListNewRequirement);
                        oldParentRequirementIdOfRequirementListNewRequirement = em.merge(oldParentRequirementIdOfRequirementListNewRequirement);
                    }
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
                    historyListOldHistory.setRequirementId(null);
                    historyListOldHistory = em.merge(historyListOldHistory);
                }
            }
            for (History historyListNewHistory : historyListNew) {
                if (!historyListOld.contains(historyListNewHistory)) {
                    Requirement oldRequirementIdOfHistoryListNewHistory = historyListNewHistory.getRequirementId();
                    historyListNewHistory.setRequirementId(requirement);
                    historyListNewHistory = em.merge(historyListNewHistory);
                    if (oldRequirementIdOfHistoryListNewHistory != null && !oldRequirementIdOfHistoryListNewHistory.equals(requirement)) {
                        oldRequirementIdOfHistoryListNewHistory.getHistoryList().remove(historyListNewHistory);
                        oldRequirementIdOfHistoryListNewHistory = em.merge(oldRequirementIdOfHistoryListNewHistory);
                    }
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
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Requirement (" + requirement + ") cannot be destroyed since the RiskControlHasRequirement " + riskControlHasRequirementListOrphanCheckRiskControlHasRequirement + " in its riskControlHasRequirementList field has a non-nullable requirement field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Requirement parentRequirementId = requirement.getParentRequirementId();
            if (parentRequirementId != null) {
                parentRequirementId.getRequirementList().remove(requirement);
                parentRequirementId = em.merge(parentRequirementId);
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
                requirementListRequirement.setParentRequirementId(null);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            List<Step> stepList = requirement.getStepList();
            for (Step stepListStep : stepList) {
                stepListStep.getRequirementList().remove(requirement);
                stepListStep = em.merge(stepListStep);
            }
            List<History> historyList = requirement.getHistoryList();
            for (History historyListHistory : historyList) {
                historyListHistory.setRequirementId(null);
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
