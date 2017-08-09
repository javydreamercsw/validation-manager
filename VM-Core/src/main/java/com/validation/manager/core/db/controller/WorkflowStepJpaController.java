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

import com.validation.manager.core.db.StepTransitionsToStep;
import com.validation.manager.core.db.Workflow;
import com.validation.manager.core.db.WorkflowInstance;
import com.validation.manager.core.db.WorkflowStep;
import com.validation.manager.core.db.WorkflowStepField;
import com.validation.manager.core.db.WorkflowStepPK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
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
            Workflow workflow1 = workflowStep.getWorkflow();
            if (workflow1 != null) {
                workflow1 = em.getReference(workflow1.getClass(), workflow1.getId());
                workflowStep.setWorkflow(workflow1);
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
            List<StepTransitionsToStep> attachedStepTransitionsToStepList = new ArrayList<>();
            for (StepTransitionsToStep stepTransitionsToStepListStepTransitionsToStepToAttach : workflowStep.getSourceTransitions()) {
                stepTransitionsToStepListStepTransitionsToStepToAttach = em.getReference(stepTransitionsToStepListStepTransitionsToStepToAttach.getClass(), stepTransitionsToStepListStepTransitionsToStepToAttach.getStepTransitionsToStepPK());
                attachedStepTransitionsToStepList.add(stepTransitionsToStepListStepTransitionsToStepToAttach);
            }
            workflowStep.setSourceTransitions(attachedStepTransitionsToStepList);
            List<StepTransitionsToStep> attachedStepTransitionsToStepList1 = new ArrayList<>();
            for (StepTransitionsToStep stepTransitionsToStepList1StepTransitionsToStepToAttach : workflowStep.getTargetTransitions()) {
                stepTransitionsToStepList1StepTransitionsToStepToAttach = em.getReference(stepTransitionsToStepList1StepTransitionsToStepToAttach.getClass(), stepTransitionsToStepList1StepTransitionsToStepToAttach.getStepTransitionsToStepPK());
                attachedStepTransitionsToStepList1.add(stepTransitionsToStepList1StepTransitionsToStepToAttach);
            }
            workflowStep.setTargetTransitions(attachedStepTransitionsToStepList1);
            em.persist(workflowStep);
            if (workflow1 != null) {
                workflow1.getWorkflowStepList().add(workflowStep);
                workflow1 = em.merge(workflow1);
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
            for (StepTransitionsToStep stepTransitionsToStepListStepTransitionsToStep : workflowStep.getSourceTransitions()) {
                WorkflowStep oldWorkflowStepSourceOfStepTransitionsToStepListStepTransitionsToStep = stepTransitionsToStepListStepTransitionsToStep.getWorkflowStepSource();
                stepTransitionsToStepListStepTransitionsToStep.setWorkflowStepSource(workflowStep);
                stepTransitionsToStepListStepTransitionsToStep = em.merge(stepTransitionsToStepListStepTransitionsToStep);
                if (oldWorkflowStepSourceOfStepTransitionsToStepListStepTransitionsToStep != null) {
                    oldWorkflowStepSourceOfStepTransitionsToStepListStepTransitionsToStep.getSourceTransitions().remove(stepTransitionsToStepListStepTransitionsToStep);
                    oldWorkflowStepSourceOfStepTransitionsToStepListStepTransitionsToStep = em.merge(oldWorkflowStepSourceOfStepTransitionsToStepListStepTransitionsToStep);
                }
            }
            for (StepTransitionsToStep stepTransitionsToStepList1StepTransitionsToStep : workflowStep.getTargetTransitions()) {
                WorkflowStep oldWorkflowStepTargetOfStepTransitionsToStepList1StepTransitionsToStep = stepTransitionsToStepList1StepTransitionsToStep.getWorkflowStepTarget();
                stepTransitionsToStepList1StepTransitionsToStep.setWorkflowStepTarget(workflowStep);
                stepTransitionsToStepList1StepTransitionsToStep = em.merge(stepTransitionsToStepList1StepTransitionsToStep);
                if (oldWorkflowStepTargetOfStepTransitionsToStepList1StepTransitionsToStep != null) {
                    oldWorkflowStepTargetOfStepTransitionsToStepList1StepTransitionsToStep.getTargetTransitions().remove(stepTransitionsToStepList1StepTransitionsToStep);
                    oldWorkflowStepTargetOfStepTransitionsToStepList1StepTransitionsToStep = em.merge(oldWorkflowStepTargetOfStepTransitionsToStepList1StepTransitionsToStep);
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
            Workflow workflow1Old = persistentWorkflowStep.getWorkflow();
            Workflow workflow1New = workflowStep.getWorkflow();
            List<WorkflowStepField> workflowStepFieldListOld = persistentWorkflowStep.getWorkflowStepFieldList();
            List<WorkflowStepField> workflowStepFieldListNew = workflowStep.getWorkflowStepFieldList();
            List<WorkflowInstance> workflowInstanceListOld = persistentWorkflowStep.getWorkflowInstanceList();
            List<WorkflowInstance> workflowInstanceListNew = workflowStep.getWorkflowInstanceList();
            List<StepTransitionsToStep> stepTransitionsToStepListOld = persistentWorkflowStep.getSourceTransitions();
            List<StepTransitionsToStep> stepTransitionsToStepListNew = workflowStep.getSourceTransitions();
            List<StepTransitionsToStep> stepTransitionsToStepList1Old = persistentWorkflowStep.getTargetTransitions();
            List<StepTransitionsToStep> stepTransitionsToStepList1New = workflowStep.getTargetTransitions();
            List<String> illegalOrphanMessages = null;
            for (WorkflowInstance workflowInstanceListOldWorkflowInstance : workflowInstanceListOld) {
                if (!workflowInstanceListNew.contains(workflowInstanceListOldWorkflowInstance)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain WorkflowInstance " + workflowInstanceListOldWorkflowInstance + " since its workflowStep field is not nullable.");
                }
            }
            for (StepTransitionsToStep stepTransitionsToStepListOldStepTransitionsToStep : stepTransitionsToStepListOld) {
                if (!stepTransitionsToStepListNew.contains(stepTransitionsToStepListOldStepTransitionsToStep)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain StepTransitionsToStep " + stepTransitionsToStepListOldStepTransitionsToStep + " since its workflowStepSource field is not nullable.");
                }
            }
            for (StepTransitionsToStep stepTransitionsToStepList1OldStepTransitionsToStep : stepTransitionsToStepList1Old) {
                if (!stepTransitionsToStepList1New.contains(stepTransitionsToStepList1OldStepTransitionsToStep)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain StepTransitionsToStep " + stepTransitionsToStepList1OldStepTransitionsToStep + " since its workflowStepTarget field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (workflow1New != null) {
                workflow1New = em.getReference(workflow1New.getClass(), workflow1New.getId());
                workflowStep.setWorkflow(workflow1New);
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
            List<StepTransitionsToStep> attachedStepTransitionsToStepListNew = new ArrayList<>();
            for (StepTransitionsToStep stepTransitionsToStepListNewStepTransitionsToStepToAttach : stepTransitionsToStepListNew) {
                stepTransitionsToStepListNewStepTransitionsToStepToAttach = em.getReference(stepTransitionsToStepListNewStepTransitionsToStepToAttach.getClass(), stepTransitionsToStepListNewStepTransitionsToStepToAttach.getStepTransitionsToStepPK());
                attachedStepTransitionsToStepListNew.add(stepTransitionsToStepListNewStepTransitionsToStepToAttach);
            }
            stepTransitionsToStepListNew = attachedStepTransitionsToStepListNew;
            workflowStep.setSourceTransitions(stepTransitionsToStepListNew);
            List<StepTransitionsToStep> attachedStepTransitionsToStepList1New = new ArrayList<>();
            for (StepTransitionsToStep stepTransitionsToStepList1NewStepTransitionsToStepToAttach : stepTransitionsToStepList1New) {
                stepTransitionsToStepList1NewStepTransitionsToStepToAttach = em.getReference(stepTransitionsToStepList1NewStepTransitionsToStepToAttach.getClass(), stepTransitionsToStepList1NewStepTransitionsToStepToAttach.getStepTransitionsToStepPK());
                attachedStepTransitionsToStepList1New.add(stepTransitionsToStepList1NewStepTransitionsToStepToAttach);
            }
            stepTransitionsToStepList1New = attachedStepTransitionsToStepList1New;
            workflowStep.setTargetTransitions(stepTransitionsToStepList1New);
            workflowStep = em.merge(workflowStep);
            if (workflow1Old != null && !workflow1Old.equals(workflow1New)) {
                workflow1Old.getWorkflowStepList().remove(workflowStep);
                workflow1Old = em.merge(workflow1Old);
            }
            if (workflow1New != null && !workflow1New.equals(workflow1Old)) {
                workflow1New.getWorkflowStepList().add(workflowStep);
                workflow1New = em.merge(workflow1New);
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
            for (StepTransitionsToStep stepTransitionsToStepListNewStepTransitionsToStep : stepTransitionsToStepListNew) {
                if (!stepTransitionsToStepListOld.contains(stepTransitionsToStepListNewStepTransitionsToStep)) {
                    WorkflowStep oldWorkflowStepSourceOfStepTransitionsToStepListNewStepTransitionsToStep = stepTransitionsToStepListNewStepTransitionsToStep.getWorkflowStepSource();
                    stepTransitionsToStepListNewStepTransitionsToStep.setWorkflowStepSource(workflowStep);
                    stepTransitionsToStepListNewStepTransitionsToStep = em.merge(stepTransitionsToStepListNewStepTransitionsToStep);
                    if (oldWorkflowStepSourceOfStepTransitionsToStepListNewStepTransitionsToStep != null && !oldWorkflowStepSourceOfStepTransitionsToStepListNewStepTransitionsToStep.equals(workflowStep)) {
                        oldWorkflowStepSourceOfStepTransitionsToStepListNewStepTransitionsToStep.getSourceTransitions().remove(stepTransitionsToStepListNewStepTransitionsToStep);
                        oldWorkflowStepSourceOfStepTransitionsToStepListNewStepTransitionsToStep = em.merge(oldWorkflowStepSourceOfStepTransitionsToStepListNewStepTransitionsToStep);
                    }
                }
            }
            for (StepTransitionsToStep stepTransitionsToStepList1NewStepTransitionsToStep : stepTransitionsToStepList1New) {
                if (!stepTransitionsToStepList1Old.contains(stepTransitionsToStepList1NewStepTransitionsToStep)) {
                    WorkflowStep oldWorkflowStepTargetOfStepTransitionsToStepList1NewStepTransitionsToStep = stepTransitionsToStepList1NewStepTransitionsToStep.getWorkflowStepTarget();
                    stepTransitionsToStepList1NewStepTransitionsToStep.setWorkflowStepTarget(workflowStep);
                    stepTransitionsToStepList1NewStepTransitionsToStep = em.merge(stepTransitionsToStepList1NewStepTransitionsToStep);
                    if (oldWorkflowStepTargetOfStepTransitionsToStepList1NewStepTransitionsToStep != null && !oldWorkflowStepTargetOfStepTransitionsToStepList1NewStepTransitionsToStep.equals(workflowStep)) {
                        oldWorkflowStepTargetOfStepTransitionsToStepList1NewStepTransitionsToStep.getTargetTransitions().remove(stepTransitionsToStepList1NewStepTransitionsToStep);
                        oldWorkflowStepTargetOfStepTransitionsToStepList1NewStepTransitionsToStep = em.merge(oldWorkflowStepTargetOfStepTransitionsToStepList1NewStepTransitionsToStep);
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
            List<StepTransitionsToStep> stepTransitionsToStepListOrphanCheck = workflowStep.getSourceTransitions();
            for (StepTransitionsToStep stepTransitionsToStepListOrphanCheckStepTransitionsToStep : stepTransitionsToStepListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This WorkflowStep (" + workflowStep + ") cannot be destroyed since the StepTransitionsToStep " + stepTransitionsToStepListOrphanCheckStepTransitionsToStep + " in its stepTransitionsToStepList field has a non-nullable workflowStepSource field.");
            }
            List<StepTransitionsToStep> stepTransitionsToStepList1OrphanCheck = workflowStep.getTargetTransitions();
            for (StepTransitionsToStep stepTransitionsToStepList1OrphanCheckStepTransitionsToStep : stepTransitionsToStepList1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This WorkflowStep (" + workflowStep + ") cannot be destroyed since the StepTransitionsToStep " + stepTransitionsToStepList1OrphanCheckStepTransitionsToStep + " in its stepTransitionsToStepList1 field has a non-nullable workflowStepTarget field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Workflow workflow1 = workflowStep.getWorkflow();
            if (workflow1 != null) {
                workflow1.getWorkflowStepList().remove(workflowStep);
                workflow1 = em.merge(workflow1);
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
