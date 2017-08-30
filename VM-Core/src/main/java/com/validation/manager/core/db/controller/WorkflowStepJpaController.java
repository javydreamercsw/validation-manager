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
import com.validation.manager.core.db.Workflow;
import com.validation.manager.core.db.WorkflowStepField;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.WorkflowInstance;
import com.validation.manager.core.db.StepTransitionsToStep;
import com.validation.manager.core.db.WorkflowStep;
import com.validation.manager.core.db.WorkflowStepPK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class WorkflowStepJpaController implements Serializable {

    public WorkflowStepJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WorkflowStep workflowStep) throws PreexistingEntityException, Exception {
        if (workflowStep.getWorkflowStepPK() == null) {
            workflowStep.setWorkflowStepPK(new WorkflowStepPK());
        }
        if (workflowStep.getWorkflowStepFieldList() == null) {
            workflowStep.setWorkflowStepFieldList(new ArrayList<>());
        }
        if (workflowStep.getWorkflowInstanceList() == null) {
            workflowStep.setWorkflowInstanceList(new ArrayList<>());
        }
        if (workflowStep.getSourceTransitions() == null) {
            workflowStep.setSourceTransitions(new ArrayList<>());
        }
        if (workflowStep.getTargetTransitions() == null) {
            workflowStep.setTargetTransitions(new ArrayList<>());
        }
        workflowStep.getWorkflowStepPK().setWorkflow(workflowStep.getWorkflow().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Workflow workflow = workflowStep.getWorkflow();
            if (workflow != null) {
                workflow = em.getReference(workflow.getClass(), workflow.getId());
                workflowStep.setWorkflow(workflow);
            }
            List<WorkflowStepField> attachedWorkflowStepFieldList = new ArrayList<>();
            for (WorkflowStepField workflowStepFieldListWorkflowStepFieldToAttach : workflowStep.getWorkflowStepFieldList()) {
                workflowStepFieldListWorkflowStepFieldToAttach = em.getReference(workflowStepFieldListWorkflowStepFieldToAttach.getClass(), workflowStepFieldListWorkflowStepFieldToAttach.getWorkflowStepFieldPK());
                attachedWorkflowStepFieldList.add(workflowStepFieldListWorkflowStepFieldToAttach);
            }
            workflowStep.setWorkflowStepFieldList(attachedWorkflowStepFieldList);
            List<WorkflowInstance> attachedWorkflowInstanceList = new ArrayList<>();
            for (WorkflowInstance workflowInstanceListWorkflowInstanceToAttach : workflowStep.getWorkflowInstanceList()) {
                workflowInstanceListWorkflowInstanceToAttach = em.getReference(workflowInstanceListWorkflowInstanceToAttach.getClass(), workflowInstanceListWorkflowInstanceToAttach.getWorkflowInstancePK());
                attachedWorkflowInstanceList.add(workflowInstanceListWorkflowInstanceToAttach);
            }
            workflowStep.setWorkflowInstanceList(attachedWorkflowInstanceList);
            List<StepTransitionsToStep> attachedSourceTransitions = new ArrayList<>();
            for (StepTransitionsToStep sourceTransitionsStepTransitionsToStepToAttach : workflowStep.getSourceTransitions()) {
                sourceTransitionsStepTransitionsToStepToAttach = em.getReference(sourceTransitionsStepTransitionsToStepToAttach.getClass(), sourceTransitionsStepTransitionsToStepToAttach.getStepTransitionsToStepPK());
                attachedSourceTransitions.add(sourceTransitionsStepTransitionsToStepToAttach);
            }
            workflowStep.setSourceTransitions(attachedSourceTransitions);
            List<StepTransitionsToStep> attachedTargetTransitions = new ArrayList<>();
            for (StepTransitionsToStep targetTransitionsStepTransitionsToStepToAttach : workflowStep.getTargetTransitions()) {
                targetTransitionsStepTransitionsToStepToAttach = em.getReference(targetTransitionsStepTransitionsToStepToAttach.getClass(), targetTransitionsStepTransitionsToStepToAttach.getStepTransitionsToStepPK());
                attachedTargetTransitions.add(targetTransitionsStepTransitionsToStepToAttach);
            }
            workflowStep.setTargetTransitions(attachedTargetTransitions);
            em.persist(workflowStep);
            if (workflow != null) {
                workflow.getWorkflowStepList().add(workflowStep);
                workflow = em.merge(workflow);
            }
            for (WorkflowStepField workflowStepFieldListWorkflowStepField : workflowStep.getWorkflowStepFieldList()) {
                workflowStepFieldListWorkflowStepField.getWorkflowStepList().add(workflowStep);
                workflowStepFieldListWorkflowStepField = em.merge(workflowStepFieldListWorkflowStepField);
            }
            for (WorkflowInstance workflowInstanceListWorkflowInstance : workflowStep.getWorkflowInstanceList()) {
                WorkflowStep oldWorkflowStepOfWorkflowInstanceListWorkflowInstance = workflowInstanceListWorkflowInstance.getWorkflowStep();
                workflowInstanceListWorkflowInstance.setWorkflowStep(workflowStep);
                workflowInstanceListWorkflowInstance = em.merge(workflowInstanceListWorkflowInstance);
                if (oldWorkflowStepOfWorkflowInstanceListWorkflowInstance != null) {
                    oldWorkflowStepOfWorkflowInstanceListWorkflowInstance.getWorkflowInstanceList().remove(workflowInstanceListWorkflowInstance);
                    oldWorkflowStepOfWorkflowInstanceListWorkflowInstance = em.merge(oldWorkflowStepOfWorkflowInstanceListWorkflowInstance);
                }
            }
            for (StepTransitionsToStep sourceTransitionsStepTransitionsToStep : workflowStep.getSourceTransitions()) {
                WorkflowStep oldWorkflowStepSourceOfSourceTransitionsStepTransitionsToStep = sourceTransitionsStepTransitionsToStep.getWorkflowStepSource();
                sourceTransitionsStepTransitionsToStep.setWorkflowStepSource(workflowStep);
                sourceTransitionsStepTransitionsToStep = em.merge(sourceTransitionsStepTransitionsToStep);
                if (oldWorkflowStepSourceOfSourceTransitionsStepTransitionsToStep != null) {
                    oldWorkflowStepSourceOfSourceTransitionsStepTransitionsToStep.getSourceTransitions().remove(sourceTransitionsStepTransitionsToStep);
                    oldWorkflowStepSourceOfSourceTransitionsStepTransitionsToStep = em.merge(oldWorkflowStepSourceOfSourceTransitionsStepTransitionsToStep);
                }
            }
            for (StepTransitionsToStep targetTransitionsStepTransitionsToStep : workflowStep.getTargetTransitions()) {
                WorkflowStep oldWorkflowStepTargetOfTargetTransitionsStepTransitionsToStep = targetTransitionsStepTransitionsToStep.getWorkflowStepTarget();
                targetTransitionsStepTransitionsToStep.setWorkflowStepTarget(workflowStep);
                targetTransitionsStepTransitionsToStep = em.merge(targetTransitionsStepTransitionsToStep);
                if (oldWorkflowStepTargetOfTargetTransitionsStepTransitionsToStep != null) {
                    oldWorkflowStepTargetOfTargetTransitionsStepTransitionsToStep.getTargetTransitions().remove(targetTransitionsStepTransitionsToStep);
                    oldWorkflowStepTargetOfTargetTransitionsStepTransitionsToStep = em.merge(oldWorkflowStepTargetOfTargetTransitionsStepTransitionsToStep);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findWorkflowStep(workflowStep.getWorkflowStepPK()) != null) {
                throw new PreexistingEntityException("WorkflowStep " + workflowStep + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(WorkflowStep workflowStep) throws IllegalOrphanException, NonexistentEntityException, Exception {
        workflowStep.getWorkflowStepPK().setWorkflow(workflowStep.getWorkflow().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorkflowStep persistentWorkflowStep = em.find(WorkflowStep.class, workflowStep.getWorkflowStepPK());
            Workflow workflowOld = persistentWorkflowStep.getWorkflow();
            Workflow workflowNew = workflowStep.getWorkflow();
            List<WorkflowStepField> workflowStepFieldListOld = persistentWorkflowStep.getWorkflowStepFieldList();
            List<WorkflowStepField> workflowStepFieldListNew = workflowStep.getWorkflowStepFieldList();
            List<WorkflowInstance> workflowInstanceListOld = persistentWorkflowStep.getWorkflowInstanceList();
            List<WorkflowInstance> workflowInstanceListNew = workflowStep.getWorkflowInstanceList();
            List<StepTransitionsToStep> sourceTransitionsOld = persistentWorkflowStep.getSourceTransitions();
            List<StepTransitionsToStep> sourceTransitionsNew = workflowStep.getSourceTransitions();
            List<StepTransitionsToStep> targetTransitionsOld = persistentWorkflowStep.getTargetTransitions();
            List<StepTransitionsToStep> targetTransitionsNew = workflowStep.getTargetTransitions();
            List<String> illegalOrphanMessages = null;
            for (WorkflowInstance workflowInstanceListOldWorkflowInstance : workflowInstanceListOld) {
                if (!workflowInstanceListNew.contains(workflowInstanceListOldWorkflowInstance)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain WorkflowInstance " + workflowInstanceListOldWorkflowInstance + " since its workflowStep field is not nullable.");
                }
            }
            for (StepTransitionsToStep sourceTransitionsOldStepTransitionsToStep : sourceTransitionsOld) {
                if (!sourceTransitionsNew.contains(sourceTransitionsOldStepTransitionsToStep)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain StepTransitionsToStep " + sourceTransitionsOldStepTransitionsToStep + " since its workflowStepSource field is not nullable.");
                }
            }
            for (StepTransitionsToStep targetTransitionsOldStepTransitionsToStep : targetTransitionsOld) {
                if (!targetTransitionsNew.contains(targetTransitionsOldStepTransitionsToStep)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain StepTransitionsToStep " + targetTransitionsOldStepTransitionsToStep + " since its workflowStepTarget field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (workflowNew != null) {
                workflowNew = em.getReference(workflowNew.getClass(), workflowNew.getId());
                workflowStep.setWorkflow(workflowNew);
            }
            List<WorkflowStepField> attachedWorkflowStepFieldListNew = new ArrayList<>();
            for (WorkflowStepField workflowStepFieldListNewWorkflowStepFieldToAttach : workflowStepFieldListNew) {
                workflowStepFieldListNewWorkflowStepFieldToAttach = em.getReference(workflowStepFieldListNewWorkflowStepFieldToAttach.getClass(), workflowStepFieldListNewWorkflowStepFieldToAttach.getWorkflowStepFieldPK());
                attachedWorkflowStepFieldListNew.add(workflowStepFieldListNewWorkflowStepFieldToAttach);
            }
            workflowStepFieldListNew = attachedWorkflowStepFieldListNew;
            workflowStep.setWorkflowStepFieldList(workflowStepFieldListNew);
            List<WorkflowInstance> attachedWorkflowInstanceListNew = new ArrayList<>();
            for (WorkflowInstance workflowInstanceListNewWorkflowInstanceToAttach : workflowInstanceListNew) {
                workflowInstanceListNewWorkflowInstanceToAttach = em.getReference(workflowInstanceListNewWorkflowInstanceToAttach.getClass(), workflowInstanceListNewWorkflowInstanceToAttach.getWorkflowInstancePK());
                attachedWorkflowInstanceListNew.add(workflowInstanceListNewWorkflowInstanceToAttach);
            }
            workflowInstanceListNew = attachedWorkflowInstanceListNew;
            workflowStep.setWorkflowInstanceList(workflowInstanceListNew);
            List<StepTransitionsToStep> attachedSourceTransitionsNew = new ArrayList<>();
            for (StepTransitionsToStep sourceTransitionsNewStepTransitionsToStepToAttach : sourceTransitionsNew) {
                sourceTransitionsNewStepTransitionsToStepToAttach = em.getReference(sourceTransitionsNewStepTransitionsToStepToAttach.getClass(), sourceTransitionsNewStepTransitionsToStepToAttach.getStepTransitionsToStepPK());
                attachedSourceTransitionsNew.add(sourceTransitionsNewStepTransitionsToStepToAttach);
            }
            sourceTransitionsNew = attachedSourceTransitionsNew;
            workflowStep.setSourceTransitions(sourceTransitionsNew);
            List<StepTransitionsToStep> attachedTargetTransitionsNew = new ArrayList<>();
            for (StepTransitionsToStep targetTransitionsNewStepTransitionsToStepToAttach : targetTransitionsNew) {
                targetTransitionsNewStepTransitionsToStepToAttach = em.getReference(targetTransitionsNewStepTransitionsToStepToAttach.getClass(), targetTransitionsNewStepTransitionsToStepToAttach.getStepTransitionsToStepPK());
                attachedTargetTransitionsNew.add(targetTransitionsNewStepTransitionsToStepToAttach);
            }
            targetTransitionsNew = attachedTargetTransitionsNew;
            workflowStep.setTargetTransitions(targetTransitionsNew);
            workflowStep = em.merge(workflowStep);
            if (workflowOld != null && !workflowOld.equals(workflowNew)) {
                workflowOld.getWorkflowStepList().remove(workflowStep);
                workflowOld = em.merge(workflowOld);
            }
            if (workflowNew != null && !workflowNew.equals(workflowOld)) {
                workflowNew.getWorkflowStepList().add(workflowStep);
                workflowNew = em.merge(workflowNew);
            }
            for (WorkflowStepField workflowStepFieldListOldWorkflowStepField : workflowStepFieldListOld) {
                if (!workflowStepFieldListNew.contains(workflowStepFieldListOldWorkflowStepField)) {
                    workflowStepFieldListOldWorkflowStepField.getWorkflowStepList().remove(workflowStep);
                    workflowStepFieldListOldWorkflowStepField = em.merge(workflowStepFieldListOldWorkflowStepField);
                }
            }
            for (WorkflowStepField workflowStepFieldListNewWorkflowStepField : workflowStepFieldListNew) {
                if (!workflowStepFieldListOld.contains(workflowStepFieldListNewWorkflowStepField)) {
                    workflowStepFieldListNewWorkflowStepField.getWorkflowStepList().add(workflowStep);
                    workflowStepFieldListNewWorkflowStepField = em.merge(workflowStepFieldListNewWorkflowStepField);
                }
            }
            for (WorkflowInstance workflowInstanceListNewWorkflowInstance : workflowInstanceListNew) {
                if (!workflowInstanceListOld.contains(workflowInstanceListNewWorkflowInstance)) {
                    WorkflowStep oldWorkflowStepOfWorkflowInstanceListNewWorkflowInstance = workflowInstanceListNewWorkflowInstance.getWorkflowStep();
                    workflowInstanceListNewWorkflowInstance.setWorkflowStep(workflowStep);
                    workflowInstanceListNewWorkflowInstance = em.merge(workflowInstanceListNewWorkflowInstance);
                    if (oldWorkflowStepOfWorkflowInstanceListNewWorkflowInstance != null && !oldWorkflowStepOfWorkflowInstanceListNewWorkflowInstance.equals(workflowStep)) {
                        oldWorkflowStepOfWorkflowInstanceListNewWorkflowInstance.getWorkflowInstanceList().remove(workflowInstanceListNewWorkflowInstance);
                        oldWorkflowStepOfWorkflowInstanceListNewWorkflowInstance = em.merge(oldWorkflowStepOfWorkflowInstanceListNewWorkflowInstance);
                    }
                }
            }
            for (StepTransitionsToStep sourceTransitionsNewStepTransitionsToStep : sourceTransitionsNew) {
                if (!sourceTransitionsOld.contains(sourceTransitionsNewStepTransitionsToStep)) {
                    WorkflowStep oldWorkflowStepSourceOfSourceTransitionsNewStepTransitionsToStep = sourceTransitionsNewStepTransitionsToStep.getWorkflowStepSource();
                    sourceTransitionsNewStepTransitionsToStep.setWorkflowStepSource(workflowStep);
                    sourceTransitionsNewStepTransitionsToStep = em.merge(sourceTransitionsNewStepTransitionsToStep);
                    if (oldWorkflowStepSourceOfSourceTransitionsNewStepTransitionsToStep != null && !oldWorkflowStepSourceOfSourceTransitionsNewStepTransitionsToStep.equals(workflowStep)) {
                        oldWorkflowStepSourceOfSourceTransitionsNewStepTransitionsToStep.getSourceTransitions().remove(sourceTransitionsNewStepTransitionsToStep);
                        oldWorkflowStepSourceOfSourceTransitionsNewStepTransitionsToStep = em.merge(oldWorkflowStepSourceOfSourceTransitionsNewStepTransitionsToStep);
                    }
                }
            }
            for (StepTransitionsToStep targetTransitionsNewStepTransitionsToStep : targetTransitionsNew) {
                if (!targetTransitionsOld.contains(targetTransitionsNewStepTransitionsToStep)) {
                    WorkflowStep oldWorkflowStepTargetOfTargetTransitionsNewStepTransitionsToStep = targetTransitionsNewStepTransitionsToStep.getWorkflowStepTarget();
                    targetTransitionsNewStepTransitionsToStep.setWorkflowStepTarget(workflowStep);
                    targetTransitionsNewStepTransitionsToStep = em.merge(targetTransitionsNewStepTransitionsToStep);
                    if (oldWorkflowStepTargetOfTargetTransitionsNewStepTransitionsToStep != null && !oldWorkflowStepTargetOfTargetTransitionsNewStepTransitionsToStep.equals(workflowStep)) {
                        oldWorkflowStepTargetOfTargetTransitionsNewStepTransitionsToStep.getTargetTransitions().remove(targetTransitionsNewStepTransitionsToStep);
                        oldWorkflowStepTargetOfTargetTransitionsNewStepTransitionsToStep = em.merge(oldWorkflowStepTargetOfTargetTransitionsNewStepTransitionsToStep);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                WorkflowStepPK id = workflowStep.getWorkflowStepPK();
                if (findWorkflowStep(id) == null) {
                    throw new NonexistentEntityException("The workflowStep with id " + id + " no longer exists.");
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

    public void destroy(WorkflowStepPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorkflowStep workflowStep;
            try {
                workflowStep = em.getReference(WorkflowStep.class, id);
                workflowStep.getWorkflowStepPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The workflowStep with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<WorkflowInstance> workflowInstanceListOrphanCheck = workflowStep.getWorkflowInstanceList();
            for (WorkflowInstance workflowInstanceListOrphanCheckWorkflowInstance : workflowInstanceListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This WorkflowStep (" + workflowStep + ") cannot be destroyed since the WorkflowInstance " + workflowInstanceListOrphanCheckWorkflowInstance + " in its workflowInstanceList field has a non-nullable workflowStep field.");
            }
            List<StepTransitionsToStep> sourceTransitionsOrphanCheck = workflowStep.getSourceTransitions();
            for (StepTransitionsToStep sourceTransitionsOrphanCheckStepTransitionsToStep : sourceTransitionsOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This WorkflowStep (" + workflowStep + ") cannot be destroyed since the StepTransitionsToStep " + sourceTransitionsOrphanCheckStepTransitionsToStep + " in its sourceTransitions field has a non-nullable workflowStepSource field.");
            }
            List<StepTransitionsToStep> targetTransitionsOrphanCheck = workflowStep.getTargetTransitions();
            for (StepTransitionsToStep targetTransitionsOrphanCheckStepTransitionsToStep : targetTransitionsOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This WorkflowStep (" + workflowStep + ") cannot be destroyed since the StepTransitionsToStep " + targetTransitionsOrphanCheckStepTransitionsToStep + " in its targetTransitions field has a non-nullable workflowStepTarget field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Workflow workflow = workflowStep.getWorkflow();
            if (workflow != null) {
                workflow.getWorkflowStepList().remove(workflowStep);
                workflow = em.merge(workflow);
            }
            List<WorkflowStepField> workflowStepFieldList = workflowStep.getWorkflowStepFieldList();
            for (WorkflowStepField workflowStepFieldListWorkflowStepField : workflowStepFieldList) {
                workflowStepFieldListWorkflowStepField.getWorkflowStepList().remove(workflowStep);
                workflowStepFieldListWorkflowStepField = em.merge(workflowStepFieldListWorkflowStepField);
            }
            em.remove(workflowStep);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<WorkflowStep> findWorkflowStepEntities() {
        return findWorkflowStepEntities(true, -1, -1);
    }

    public List<WorkflowStep> findWorkflowStepEntities(int maxResults, int firstResult) {
        return findWorkflowStepEntities(false, maxResults, firstResult);
    }

    private List<WorkflowStep> findWorkflowStepEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WorkflowStep.class));
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

    public WorkflowStep findWorkflowStep(WorkflowStepPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WorkflowStep.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getWorkflowStepCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WorkflowStep> rt = cq.from(WorkflowStep.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
