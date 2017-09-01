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
import com.validation.manager.core.db.StepTransitionsToStepPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.WorkflowStep;
import com.validation.manager.core.db.WorkflowInstanceHasTransition;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class StepTransitionsToStepJpaController implements Serializable {

    public StepTransitionsToStepJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(StepTransitionsToStep stepTransitionsToStep) throws PreexistingEntityException, Exception {
        if (stepTransitionsToStep.getStepTransitionsToStepPK() == null) {
            stepTransitionsToStep.setStepTransitionsToStepPK(new StepTransitionsToStepPK());
        }
        if (stepTransitionsToStep.getWorkflowInstanceHasTransitionList() == null) {
            stepTransitionsToStep.setWorkflowInstanceHasTransitionList(new ArrayList<>());
        }
        stepTransitionsToStep.getStepTransitionsToStepPK().setSourceStep(stepTransitionsToStep.getWorkflowStepSource().getWorkflowStepPK().getId());
        stepTransitionsToStep.getStepTransitionsToStepPK().setTargetStepWorkflow(stepTransitionsToStep.getWorkflowStepTarget().getWorkflowStepPK().getWorkflow());
        stepTransitionsToStep.getStepTransitionsToStepPK().setTargetStep(stepTransitionsToStep.getWorkflowStepTarget().getWorkflowStepPK().getId());
        stepTransitionsToStep.getStepTransitionsToStepPK().setSourceStepWorkflow(stepTransitionsToStep.getWorkflowStepSource().getWorkflowStepPK().getWorkflow());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorkflowStep workflowStepSource = stepTransitionsToStep.getWorkflowStepSource();
            if (workflowStepSource != null) {
                workflowStepSource = em.getReference(workflowStepSource.getClass(), workflowStepSource.getWorkflowStepPK());
                stepTransitionsToStep.setWorkflowStepSource(workflowStepSource);
            }
            WorkflowStep workflowStepTarget = stepTransitionsToStep.getWorkflowStepTarget();
            if (workflowStepTarget != null) {
                workflowStepTarget = em.getReference(workflowStepTarget.getClass(), workflowStepTarget.getWorkflowStepPK());
                stepTransitionsToStep.setWorkflowStepTarget(workflowStepTarget);
            }
            List<WorkflowInstanceHasTransition> attachedWorkflowInstanceHasTransitionList = new ArrayList<>();
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach : stepTransitionsToStep.getWorkflowInstanceHasTransitionList()) {
                workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach = em.getReference(workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach.getClass(), workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach.getWorkflowInstanceHasTransitionPK());
                attachedWorkflowInstanceHasTransitionList.add(workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach);
            }
            stepTransitionsToStep.setWorkflowInstanceHasTransitionList(attachedWorkflowInstanceHasTransitionList);
            em.persist(stepTransitionsToStep);
            if (workflowStepSource != null) {
                workflowStepSource.getSourceTransitions().add(stepTransitionsToStep);
                workflowStepSource = em.merge(workflowStepSource);
            }
            if (workflowStepTarget != null) {
                workflowStepTarget.getSourceTransitions().add(stepTransitionsToStep);
                workflowStepTarget = em.merge(workflowStepTarget);
            }
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListWorkflowInstanceHasTransition : stepTransitionsToStep.getWorkflowInstanceHasTransitionList()) {
                StepTransitionsToStep oldStepTransitionsToStepOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition = workflowInstanceHasTransitionListWorkflowInstanceHasTransition.getStepTransitionsToStep();
                workflowInstanceHasTransitionListWorkflowInstanceHasTransition.setStepTransitionsToStep(stepTransitionsToStep);
                workflowInstanceHasTransitionListWorkflowInstanceHasTransition = em.merge(workflowInstanceHasTransitionListWorkflowInstanceHasTransition);
                if (oldStepTransitionsToStepOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition != null) {
                    oldStepTransitionsToStepOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition.getWorkflowInstanceHasTransitionList().remove(workflowInstanceHasTransitionListWorkflowInstanceHasTransition);
                    oldStepTransitionsToStepOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition = em.merge(oldStepTransitionsToStepOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findStepTransitionsToStep(stepTransitionsToStep.getStepTransitionsToStepPK()) != null) {
                throw new PreexistingEntityException("StepTransitionsToStep " + stepTransitionsToStep + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(StepTransitionsToStep stepTransitionsToStep) throws IllegalOrphanException, NonexistentEntityException, Exception {
        stepTransitionsToStep.getStepTransitionsToStepPK().setSourceStep(stepTransitionsToStep.getWorkflowStepSource().getWorkflowStepPK().getId());
        stepTransitionsToStep.getStepTransitionsToStepPK().setTargetStepWorkflow(stepTransitionsToStep.getWorkflowStepTarget().getWorkflowStepPK().getWorkflow());
        stepTransitionsToStep.getStepTransitionsToStepPK().setTargetStep(stepTransitionsToStep.getWorkflowStepTarget().getWorkflowStepPK().getId());
        stepTransitionsToStep.getStepTransitionsToStepPK().setSourceStepWorkflow(stepTransitionsToStep.getWorkflowStepSource().getWorkflowStepPK().getWorkflow());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StepTransitionsToStep persistentStepTransitionsToStep = em.find(StepTransitionsToStep.class, stepTransitionsToStep.getStepTransitionsToStepPK());
            WorkflowStep workflowStepSourceOld = persistentStepTransitionsToStep.getWorkflowStepSource();
            WorkflowStep workflowStepSourceNew = stepTransitionsToStep.getWorkflowStepSource();
            WorkflowStep workflowStepTargetOld = persistentStepTransitionsToStep.getWorkflowStepTarget();
            WorkflowStep workflowStepTargetNew = stepTransitionsToStep.getWorkflowStepTarget();
            List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionListOld = persistentStepTransitionsToStep.getWorkflowInstanceHasTransitionList();
            List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionListNew = stepTransitionsToStep.getWorkflowInstanceHasTransitionList();
            List<String> illegalOrphanMessages = null;
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListOldWorkflowInstanceHasTransition : workflowInstanceHasTransitionListOld) {
                if (!workflowInstanceHasTransitionListNew.contains(workflowInstanceHasTransitionListOldWorkflowInstanceHasTransition)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain WorkflowInstanceHasTransition " + workflowInstanceHasTransitionListOldWorkflowInstanceHasTransition + " since its stepTransitionsToStep field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (workflowStepSourceNew != null) {
                workflowStepSourceNew = em.getReference(workflowStepSourceNew.getClass(), workflowStepSourceNew.getWorkflowStepPK());
                stepTransitionsToStep.setWorkflowStepSource(workflowStepSourceNew);
            }
            if (workflowStepTargetNew != null) {
                workflowStepTargetNew = em.getReference(workflowStepTargetNew.getClass(), workflowStepTargetNew.getWorkflowStepPK());
                stepTransitionsToStep.setWorkflowStepTarget(workflowStepTargetNew);
            }
            List<WorkflowInstanceHasTransition> attachedWorkflowInstanceHasTransitionListNew = new ArrayList<>();
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach : workflowInstanceHasTransitionListNew) {
                workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach = em.getReference(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach.getClass(), workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach.getWorkflowInstanceHasTransitionPK());
                attachedWorkflowInstanceHasTransitionListNew.add(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach);
            }
            workflowInstanceHasTransitionListNew = attachedWorkflowInstanceHasTransitionListNew;
            stepTransitionsToStep.setWorkflowInstanceHasTransitionList(workflowInstanceHasTransitionListNew);
            stepTransitionsToStep = em.merge(stepTransitionsToStep);
            if (workflowStepSourceOld != null && !workflowStepSourceOld.equals(workflowStepSourceNew)) {
                workflowStepSourceOld.getSourceTransitions().remove(stepTransitionsToStep);
                workflowStepSourceOld = em.merge(workflowStepSourceOld);
            }
            if (workflowStepSourceNew != null && !workflowStepSourceNew.equals(workflowStepSourceOld)) {
                workflowStepSourceNew.getSourceTransitions().add(stepTransitionsToStep);
                workflowStepSourceNew = em.merge(workflowStepSourceNew);
            }
            if (workflowStepTargetOld != null && !workflowStepTargetOld.equals(workflowStepTargetNew)) {
                workflowStepTargetOld.getSourceTransitions().remove(stepTransitionsToStep);
                workflowStepTargetOld = em.merge(workflowStepTargetOld);
            }
            if (workflowStepTargetNew != null && !workflowStepTargetNew.equals(workflowStepTargetOld)) {
                workflowStepTargetNew.getSourceTransitions().add(stepTransitionsToStep);
                workflowStepTargetNew = em.merge(workflowStepTargetNew);
            }
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition : workflowInstanceHasTransitionListNew) {
                if (!workflowInstanceHasTransitionListOld.contains(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition)) {
                    StepTransitionsToStep oldStepTransitionsToStepOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition = workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition.getStepTransitionsToStep();
                    workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition.setStepTransitionsToStep(stepTransitionsToStep);
                    workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition = em.merge(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition);
                    if (oldStepTransitionsToStepOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition != null && !oldStepTransitionsToStepOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition.equals(stepTransitionsToStep)) {
                        oldStepTransitionsToStepOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition.getWorkflowInstanceHasTransitionList().remove(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition);
                        oldStepTransitionsToStepOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition = em.merge(oldStepTransitionsToStepOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                StepTransitionsToStepPK id = stepTransitionsToStep.getStepTransitionsToStepPK();
                if (findStepTransitionsToStep(id) == null) {
                    throw new NonexistentEntityException("The stepTransitionsToStep with id " + id + " no longer exists.");
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

    public void destroy(StepTransitionsToStepPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StepTransitionsToStep stepTransitionsToStep;
            try {
                stepTransitionsToStep = em.getReference(StepTransitionsToStep.class, id);
                stepTransitionsToStep.getStepTransitionsToStepPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The stepTransitionsToStep with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionListOrphanCheck = stepTransitionsToStep.getWorkflowInstanceHasTransitionList();
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListOrphanCheckWorkflowInstanceHasTransition : workflowInstanceHasTransitionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This StepTransitionsToStep (" + stepTransitionsToStep + ") cannot be destroyed since the WorkflowInstanceHasTransition " + workflowInstanceHasTransitionListOrphanCheckWorkflowInstanceHasTransition + " in its workflowInstanceHasTransitionList field has a non-nullable stepTransitionsToStep field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            WorkflowStep workflowStepSource = stepTransitionsToStep.getWorkflowStepSource();
            if (workflowStepSource != null) {
                workflowStepSource.getSourceTransitions().remove(stepTransitionsToStep);
                workflowStepSource = em.merge(workflowStepSource);
            }
            WorkflowStep workflowStepTarget = stepTransitionsToStep.getWorkflowStepTarget();
            if (workflowStepTarget != null) {
                workflowStepTarget.getSourceTransitions().remove(stepTransitionsToStep);
                workflowStepTarget = em.merge(workflowStepTarget);
            }
            em.remove(stepTransitionsToStep);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<StepTransitionsToStep> findStepTransitionsToStepEntities() {
        return findStepTransitionsToStepEntities(true, -1, -1);
    }

    public List<StepTransitionsToStep> findStepTransitionsToStepEntities(int maxResults, int firstResult) {
        return findStepTransitionsToStepEntities(false, maxResults, firstResult);
    }

    private List<StepTransitionsToStep> findStepTransitionsToStepEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(StepTransitionsToStep.class));
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

    public StepTransitionsToStep findStepTransitionsToStep(StepTransitionsToStepPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(StepTransitionsToStep.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getStepTransitionsToStepCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<StepTransitionsToStep> rt = cq.from(StepTransitionsToStep.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
