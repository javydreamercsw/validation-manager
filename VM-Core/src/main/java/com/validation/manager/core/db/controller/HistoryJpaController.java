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
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.VmSetting;
import com.validation.manager.core.db.HistoryField;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.Baseline;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class HistoryJpaController implements Serializable {

    public HistoryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(History history) {
        if (history.getHistoryFieldList() == null) {
            history.setHistoryFieldList(new ArrayList<>());
        }
        if (history.getBaselineList() == null) {
            history.setBaselineList(new ArrayList<>());
        }
        if (history.getExecutionStepList() == null) {
            history.setExecutionStepList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmUser modifierId = history.getModifierId();
            if (modifierId != null) {
                modifierId = em.getReference(modifierId.getClass(), modifierId.getId());
                history.setModifierId(modifierId);
            }
            Requirement requirementId = history.getRequirementId();
            if (requirementId != null) {
                requirementId = em.getReference(requirementId.getClass(), requirementId.getId());
                history.setRequirementId(requirementId);
            }
            Project projectId = history.getProjectId();
            if (projectId != null) {
                projectId = em.getReference(projectId.getClass(), projectId.getId());
                history.setProjectId(projectId);
            }
            Step step = history.getStep();
            if (step != null) {
                step = em.getReference(step.getClass(), step.getStepPK());
                history.setStep(step);
            }
            VmSetting vmSettingId = history.getVmSettingId();
            if (vmSettingId != null) {
                vmSettingId = em.getReference(vmSettingId.getClass(), vmSettingId.getId());
                history.setVmSettingId(vmSettingId);
            }
            VmUser vmUserId = history.getVmUserId();
            if (vmUserId != null) {
                vmUserId = em.getReference(vmUserId.getClass(), vmUserId.getId());
                history.setVmUserId(vmUserId);
            }
            List<HistoryField> attachedHistoryFieldList = new ArrayList<>();
            for (HistoryField historyFieldListHistoryFieldToAttach : history.getHistoryFieldList()) {
                historyFieldListHistoryFieldToAttach = em.getReference(historyFieldListHistoryFieldToAttach.getClass(), historyFieldListHistoryFieldToAttach.getHistoryFieldPK());
                attachedHistoryFieldList.add(historyFieldListHistoryFieldToAttach);
            }
            history.setHistoryFieldList(attachedHistoryFieldList);
            List<Baseline> attachedBaselineList = new ArrayList<>();
            for (Baseline baselineListBaselineToAttach : history.getBaselineList()) {
                baselineListBaselineToAttach = em.getReference(baselineListBaselineToAttach.getClass(), baselineListBaselineToAttach.getId());
                attachedBaselineList.add(baselineListBaselineToAttach);
            }
            history.setBaselineList(attachedBaselineList);
            List<ExecutionStep> attachedExecutionStepList = new ArrayList<>();
            for (ExecutionStep executionStepListExecutionStepToAttach : history.getExecutionStepList()) {
                executionStepListExecutionStepToAttach = em.getReference(executionStepListExecutionStepToAttach.getClass(), executionStepListExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepList.add(executionStepListExecutionStepToAttach);
            }
            history.setExecutionStepList(attachedExecutionStepList);
            em.persist(history);
            if (modifierId != null) {
                modifierId.getHistoryModificationList().add(history);
                modifierId = em.merge(modifierId);
            }
            if (requirementId != null) {
                requirementId.getHistoryList().add(history);
                requirementId = em.merge(requirementId);
            }
            if (projectId != null) {
                projectId.getHistoryList().add(history);
                projectId = em.merge(projectId);
            }
            if (step != null) {
                step.getHistoryList().add(history);
                step = em.merge(step);
            }
            if (vmSettingId != null) {
                vmSettingId.getHistoryList().add(history);
                vmSettingId = em.merge(vmSettingId);
            }
            if (vmUserId != null) {
                vmUserId.getHistoryModificationList().add(history);
                vmUserId = em.merge(vmUserId);
            }
            for (HistoryField historyFieldListHistoryField : history.getHistoryFieldList()) {
                History oldHistoryOfHistoryFieldListHistoryField = historyFieldListHistoryField.getHistory();
                historyFieldListHistoryField.setHistory(history);
                historyFieldListHistoryField = em.merge(historyFieldListHistoryField);
                if (oldHistoryOfHistoryFieldListHistoryField != null) {
                    oldHistoryOfHistoryFieldListHistoryField.getHistoryFieldList().remove(historyFieldListHistoryField);
                    oldHistoryOfHistoryFieldListHistoryField = em.merge(oldHistoryOfHistoryFieldListHistoryField);
                }
            }
            for (Baseline baselineListBaseline : history.getBaselineList()) {
                baselineListBaseline.getHistoryList().add(history);
                baselineListBaseline = em.merge(baselineListBaseline);
            }
            for (ExecutionStep executionStepListExecutionStep : history.getExecutionStepList()) {
                executionStepListExecutionStep.getHistoryList().add(history);
                executionStepListExecutionStep = em.merge(executionStepListExecutionStep);
            }
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(History history) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            History persistentHistory = em.find(History.class, history.getId());
            VmUser modifierIdOld = persistentHistory.getModifierId();
            VmUser modifierIdNew = history.getModifierId();
            Requirement requirementIdOld = persistentHistory.getRequirementId();
            Requirement requirementIdNew = history.getRequirementId();
            Project projectIdOld = persistentHistory.getProjectId();
            Project projectIdNew = history.getProjectId();
            Step stepOld = persistentHistory.getStep();
            Step stepNew = history.getStep();
            VmSetting vmSettingIdOld = persistentHistory.getVmSettingId();
            VmSetting vmSettingIdNew = history.getVmSettingId();
            VmUser vmUserIdOld = persistentHistory.getVmUserId();
            VmUser vmUserIdNew = history.getVmUserId();
            List<HistoryField> historyFieldListOld = persistentHistory.getHistoryFieldList();
            List<HistoryField> historyFieldListNew = history.getHistoryFieldList();
            List<Baseline> baselineListOld = persistentHistory.getBaselineList();
            List<Baseline> baselineListNew = history.getBaselineList();
            List<ExecutionStep> executionStepListOld = persistentHistory.getExecutionStepList();
            List<ExecutionStep> executionStepListNew = history.getExecutionStepList();
            List<String> illegalOrphanMessages = null;
            for (HistoryField historyFieldListOldHistoryField : historyFieldListOld) {
                if (!historyFieldListNew.contains(historyFieldListOldHistoryField)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain HistoryField " + historyFieldListOldHistoryField + " since its history field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (modifierIdNew != null) {
                modifierIdNew = em.getReference(modifierIdNew.getClass(), modifierIdNew.getId());
                history.setModifierId(modifierIdNew);
            }
            if (requirementIdNew != null) {
                requirementIdNew = em.getReference(requirementIdNew.getClass(), requirementIdNew.getId());
                history.setRequirementId(requirementIdNew);
            }
            if (projectIdNew != null) {
                projectIdNew = em.getReference(projectIdNew.getClass(), projectIdNew.getId());
                history.setProjectId(projectIdNew);
            }
            if (stepNew != null) {
                stepNew = em.getReference(stepNew.getClass(), stepNew.getStepPK());
                history.setStep(stepNew);
            }
            if (vmSettingIdNew != null) {
                vmSettingIdNew = em.getReference(vmSettingIdNew.getClass(), vmSettingIdNew.getId());
                history.setVmSettingId(vmSettingIdNew);
            }
            if (vmUserIdNew != null) {
                vmUserIdNew = em.getReference(vmUserIdNew.getClass(), vmUserIdNew.getId());
                history.setVmUserId(vmUserIdNew);
            }
            List<HistoryField> attachedHistoryFieldListNew = new ArrayList<>();
            for (HistoryField historyFieldListNewHistoryFieldToAttach : historyFieldListNew) {
                historyFieldListNewHistoryFieldToAttach = em.getReference(historyFieldListNewHistoryFieldToAttach.getClass(), historyFieldListNewHistoryFieldToAttach.getHistoryFieldPK());
                attachedHistoryFieldListNew.add(historyFieldListNewHistoryFieldToAttach);
            }
            historyFieldListNew = attachedHistoryFieldListNew;
            history.setHistoryFieldList(historyFieldListNew);
            List<Baseline> attachedBaselineListNew = new ArrayList<>();
            for (Baseline baselineListNewBaselineToAttach : baselineListNew) {
                baselineListNewBaselineToAttach = em.getReference(baselineListNewBaselineToAttach.getClass(), baselineListNewBaselineToAttach.getId());
                attachedBaselineListNew.add(baselineListNewBaselineToAttach);
            }
            baselineListNew = attachedBaselineListNew;
            history.setBaselineList(baselineListNew);
            List<ExecutionStep> attachedExecutionStepListNew = new ArrayList<>();
            for (ExecutionStep executionStepListNewExecutionStepToAttach : executionStepListNew) {
                executionStepListNewExecutionStepToAttach = em.getReference(executionStepListNewExecutionStepToAttach.getClass(), executionStepListNewExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepListNew.add(executionStepListNewExecutionStepToAttach);
            }
            executionStepListNew = attachedExecutionStepListNew;
            history.setExecutionStepList(executionStepListNew);
            history = em.merge(history);
            if (modifierIdOld != null && !modifierIdOld.equals(modifierIdNew)) {
                modifierIdOld.getHistoryModificationList().remove(history);
                modifierIdOld = em.merge(modifierIdOld);
            }
            if (modifierIdNew != null && !modifierIdNew.equals(modifierIdOld)) {
                modifierIdNew.getHistoryModificationList().add(history);
                modifierIdNew = em.merge(modifierIdNew);
            }
            if (requirementIdOld != null && !requirementIdOld.equals(requirementIdNew)) {
                requirementIdOld.getHistoryList().remove(history);
                requirementIdOld = em.merge(requirementIdOld);
            }
            if (requirementIdNew != null && !requirementIdNew.equals(requirementIdOld)) {
                requirementIdNew.getHistoryList().add(history);
                requirementIdNew = em.merge(requirementIdNew);
            }
            if (projectIdOld != null && !projectIdOld.equals(projectIdNew)) {
                projectIdOld.getHistoryList().remove(history);
                projectIdOld = em.merge(projectIdOld);
            }
            if (projectIdNew != null && !projectIdNew.equals(projectIdOld)) {
                projectIdNew.getHistoryList().add(history);
                projectIdNew = em.merge(projectIdNew);
            }
            if (stepOld != null && !stepOld.equals(stepNew)) {
                stepOld.getHistoryList().remove(history);
                stepOld = em.merge(stepOld);
            }
            if (stepNew != null && !stepNew.equals(stepOld)) {
                stepNew.getHistoryList().add(history);
                stepNew = em.merge(stepNew);
            }
            if (vmSettingIdOld != null && !vmSettingIdOld.equals(vmSettingIdNew)) {
                vmSettingIdOld.getHistoryList().remove(history);
                vmSettingIdOld = em.merge(vmSettingIdOld);
            }
            if (vmSettingIdNew != null && !vmSettingIdNew.equals(vmSettingIdOld)) {
                vmSettingIdNew.getHistoryList().add(history);
                vmSettingIdNew = em.merge(vmSettingIdNew);
            }
            if (vmUserIdOld != null && !vmUserIdOld.equals(vmUserIdNew)) {
                vmUserIdOld.getHistoryModificationList().remove(history);
                vmUserIdOld = em.merge(vmUserIdOld);
            }
            if (vmUserIdNew != null && !vmUserIdNew.equals(vmUserIdOld)) {
                vmUserIdNew.getHistoryModificationList().add(history);
                vmUserIdNew = em.merge(vmUserIdNew);
            }
            for (HistoryField historyFieldListNewHistoryField : historyFieldListNew) {
                if (!historyFieldListOld.contains(historyFieldListNewHistoryField)) {
                    History oldHistoryOfHistoryFieldListNewHistoryField = historyFieldListNewHistoryField.getHistory();
                    historyFieldListNewHistoryField.setHistory(history);
                    historyFieldListNewHistoryField = em.merge(historyFieldListNewHistoryField);
                    if (oldHistoryOfHistoryFieldListNewHistoryField != null && !oldHistoryOfHistoryFieldListNewHistoryField.equals(history)) {
                        oldHistoryOfHistoryFieldListNewHistoryField.getHistoryFieldList().remove(historyFieldListNewHistoryField);
                        oldHistoryOfHistoryFieldListNewHistoryField = em.merge(oldHistoryOfHistoryFieldListNewHistoryField);
                    }
                }
            }
            for (Baseline baselineListOldBaseline : baselineListOld) {
                if (!baselineListNew.contains(baselineListOldBaseline)) {
                    baselineListOldBaseline.getHistoryList().remove(history);
                    baselineListOldBaseline = em.merge(baselineListOldBaseline);
                }
            }
            for (Baseline baselineListNewBaseline : baselineListNew) {
                if (!baselineListOld.contains(baselineListNewBaseline)) {
                    baselineListNewBaseline.getHistoryList().add(history);
                    baselineListNewBaseline = em.merge(baselineListNewBaseline);
                }
            }
            for (ExecutionStep executionStepListOldExecutionStep : executionStepListOld) {
                if (!executionStepListNew.contains(executionStepListOldExecutionStep)) {
                    executionStepListOldExecutionStep.getHistoryList().remove(history);
                    executionStepListOldExecutionStep = em.merge(executionStepListOldExecutionStep);
                }
            }
            for (ExecutionStep executionStepListNewExecutionStep : executionStepListNew) {
                if (!executionStepListOld.contains(executionStepListNewExecutionStep)) {
                    executionStepListNewExecutionStep.getHistoryList().add(history);
                    executionStepListNewExecutionStep = em.merge(executionStepListNewExecutionStep);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = history.getId();
                if (findHistory(id) == null) {
                    throw new NonexistentEntityException("The history with id " + id + " no longer exists.");
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
            History history;
            try {
                history = em.getReference(History.class, id);
                history.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The history with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<HistoryField> historyFieldListOrphanCheck = history.getHistoryFieldList();
            for (HistoryField historyFieldListOrphanCheckHistoryField : historyFieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This History (" + history + ") cannot be destroyed since the HistoryField " + historyFieldListOrphanCheckHistoryField + " in its historyFieldList field has a non-nullable history field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            VmUser modifierId = history.getModifierId();
            if (modifierId != null) {
                modifierId.getHistoryModificationList().remove(history);
                modifierId = em.merge(modifierId);
            }
            Requirement requirementId = history.getRequirementId();
            if (requirementId != null) {
                requirementId.getHistoryList().remove(history);
                requirementId = em.merge(requirementId);
            }
            Project projectId = history.getProjectId();
            if (projectId != null) {
                projectId.getHistoryList().remove(history);
                projectId = em.merge(projectId);
            }
            Step step = history.getStep();
            if (step != null) {
                step.getHistoryList().remove(history);
                step = em.merge(step);
            }
            VmSetting vmSettingId = history.getVmSettingId();
            if (vmSettingId != null) {
                vmSettingId.getHistoryList().remove(history);
                vmSettingId = em.merge(vmSettingId);
            }
            VmUser vmUserId = history.getVmUserId();
            if (vmUserId != null) {
                vmUserId.getHistoryModificationList().remove(history);
                vmUserId = em.merge(vmUserId);
            }
            List<Baseline> baselineList = history.getBaselineList();
            for (Baseline baselineListBaseline : baselineList) {
                baselineListBaseline.getHistoryList().remove(history);
                baselineListBaseline = em.merge(baselineListBaseline);
            }
            List<ExecutionStep> executionStepList = history.getExecutionStepList();
            for (ExecutionStep executionStepListExecutionStep : executionStepList) {
                executionStepListExecutionStep.getHistoryList().remove(history);
                executionStepListExecutionStep = em.merge(executionStepListExecutionStep);
            }
            em.remove(history);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<History> findHistoryEntities() {
        return findHistoryEntities(true, -1, -1);
    }

    public List<History> findHistoryEntities(int maxResults, int firstResult) {
        return findHistoryEntities(false, maxResults, firstResult);
    }

    private List<History> findHistoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(History.class));
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

    public History findHistory(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(History.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getHistoryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<History> rt = cq.from(History.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
