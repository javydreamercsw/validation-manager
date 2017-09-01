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
import com.validation.manager.core.db.StepTransitionsToStep;
import com.validation.manager.core.db.WorkflowInstance;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.WorkflowInstanceHasTransition;
import com.validation.manager.core.db.WorkflowInstanceHasTransitionPK;
import com.validation.manager.core.db.WorkflowInstanceHasTransitionStepField;
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
public class WorkflowInstanceHasTransitionJpaController implements Serializable {

    public WorkflowInstanceHasTransitionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WorkflowInstanceHasTransition workflowInstanceHasTransition) throws PreexistingEntityException, Exception {
        if (workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK() == null) {
            workflowInstanceHasTransition.setWorkflowInstanceHasTransitionPK(new WorkflowInstanceHasTransitionPK());
        }
        if (workflowInstanceHasTransition.getWorkflowInstanceHasTransitionStepFieldList() == null) {
            workflowInstanceHasTransition.setWorkflowInstanceHasTransitionStepFieldList(new ArrayList<>());
        }
        workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK().setStepTransitionsToStepTargetStep(workflowInstanceHasTransition.getStepTransitionsToStep().getStepTransitionsToStepPK().getTargetStep());
        workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK().setWorkflowInstanceId(workflowInstanceHasTransition.getWorkflowInstance().getWorkflowInstancePK().getId());
        workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK().setStepTransitionsToStepSourceStep(workflowInstanceHasTransition.getStepTransitionsToStep().getStepTransitionsToStepPK().getSourceStep());
        workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK().setStepTransitionsToStepTargetStepWorkflow(workflowInstanceHasTransition.getStepTransitionsToStep().getStepTransitionsToStepPK().getTargetStepWorkflow());
        workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK().setWorkflowInstanceWorkflow(workflowInstanceHasTransition.getWorkflowInstance().getWorkflowInstancePK().getWorkflow());
        workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK().setStepTransitionsToStepSourceStepWorkflow(workflowInstanceHasTransition.getStepTransitionsToStep().getStepTransitionsToStepPK().getSourceStepWorkflow());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StepTransitionsToStep stepTransitionsToStep = workflowInstanceHasTransition.getStepTransitionsToStep();
            if (stepTransitionsToStep != null) {
                stepTransitionsToStep = em.getReference(stepTransitionsToStep.getClass(), stepTransitionsToStep.getStepTransitionsToStepPK());
                workflowInstanceHasTransition.setStepTransitionsToStep(stepTransitionsToStep);
            }
            WorkflowInstance workflowInstance = workflowInstanceHasTransition.getWorkflowInstance();
            if (workflowInstance != null) {
                workflowInstance = em.getReference(workflowInstance.getClass(), workflowInstance.getWorkflowInstancePK());
                workflowInstanceHasTransition.setWorkflowInstance(workflowInstance);
            }
            VmUser transitioner = workflowInstanceHasTransition.getTransitioner();
            if (transitioner != null) {
                transitioner = em.getReference(transitioner.getClass(), transitioner.getId());
                workflowInstanceHasTransition.setTransitioner(transitioner);
            }
            List<WorkflowInstanceHasTransitionStepField> attachedWorkflowInstanceHasTransitionStepFieldList = new ArrayList<>();
            for (WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepFieldToAttach : workflowInstanceHasTransition.getWorkflowInstanceHasTransitionStepFieldList()) {
                workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepFieldToAttach = em.getReference(workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepFieldToAttach.getClass(), workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepFieldToAttach.getWorkflowInstanceHasTransitionStepFieldPK());
                attachedWorkflowInstanceHasTransitionStepFieldList.add(workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepFieldToAttach);
            }
            workflowInstanceHasTransition.setWorkflowInstanceHasTransitionStepFieldList(attachedWorkflowInstanceHasTransitionStepFieldList);
            em.persist(workflowInstanceHasTransition);
            if (stepTransitionsToStep != null) {
                stepTransitionsToStep.getWorkflowInstanceHasTransitionList().add(workflowInstanceHasTransition);
                stepTransitionsToStep = em.merge(stepTransitionsToStep);
            }
            if (workflowInstance != null) {
                workflowInstance.getWorkflowInstanceHasTransitionList().add(workflowInstanceHasTransition);
                workflowInstance = em.merge(workflowInstance);
            }
            if (transitioner != null) {
                transitioner.getWorkflowInstanceHasTransitionList().add(workflowInstanceHasTransition);
                transitioner = em.merge(transitioner);
            }
            for (WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField : workflowInstanceHasTransition.getWorkflowInstanceHasTransitionStepFieldList()) {
                WorkflowInstanceHasTransition oldWorkflowInstanceHasTransitionOfWorkflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField = workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition();
                workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField.setWorkflowInstanceHasTransition(workflowInstanceHasTransition);
                workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField = em.merge(workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField);
                if (oldWorkflowInstanceHasTransitionOfWorkflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField != null) {
                    oldWorkflowInstanceHasTransitionOfWorkflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldList().remove(workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField);
                    oldWorkflowInstanceHasTransitionOfWorkflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField = em.merge(oldWorkflowInstanceHasTransitionOfWorkflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findWorkflowInstanceHasTransition(workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK()) != null) {
                throw new PreexistingEntityException("WorkflowInstanceHasTransition " + workflowInstanceHasTransition + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(WorkflowInstanceHasTransition workflowInstanceHasTransition) throws IllegalOrphanException, NonexistentEntityException, Exception {
        workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK().setStepTransitionsToStepTargetStep(workflowInstanceHasTransition.getStepTransitionsToStep().getStepTransitionsToStepPK().getTargetStep());
        workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK().setWorkflowInstanceId(workflowInstanceHasTransition.getWorkflowInstance().getWorkflowInstancePK().getId());
        workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK().setStepTransitionsToStepSourceStep(workflowInstanceHasTransition.getStepTransitionsToStep().getStepTransitionsToStepPK().getSourceStep());
        workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK().setStepTransitionsToStepTargetStepWorkflow(workflowInstanceHasTransition.getStepTransitionsToStep().getStepTransitionsToStepPK().getTargetStepWorkflow());
        workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK().setWorkflowInstanceWorkflow(workflowInstanceHasTransition.getWorkflowInstance().getWorkflowInstancePK().getWorkflow());
        workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK().setStepTransitionsToStepSourceStepWorkflow(workflowInstanceHasTransition.getStepTransitionsToStep().getStepTransitionsToStepPK().getSourceStepWorkflow());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorkflowInstanceHasTransition persistentWorkflowInstanceHasTransition = em.find(WorkflowInstanceHasTransition.class, workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK());
            StepTransitionsToStep stepTransitionsToStepOld = persistentWorkflowInstanceHasTransition.getStepTransitionsToStep();
            StepTransitionsToStep stepTransitionsToStepNew = workflowInstanceHasTransition.getStepTransitionsToStep();
            WorkflowInstance workflowInstanceOld = persistentWorkflowInstanceHasTransition.getWorkflowInstance();
            WorkflowInstance workflowInstanceNew = workflowInstanceHasTransition.getWorkflowInstance();
            VmUser transitionerOld = persistentWorkflowInstanceHasTransition.getTransitioner();
            VmUser transitionerNew = workflowInstanceHasTransition.getTransitioner();
            List<WorkflowInstanceHasTransitionStepField> workflowInstanceHasTransitionStepFieldListOld = persistentWorkflowInstanceHasTransition.getWorkflowInstanceHasTransitionStepFieldList();
            List<WorkflowInstanceHasTransitionStepField> workflowInstanceHasTransitionStepFieldListNew = workflowInstanceHasTransition.getWorkflowInstanceHasTransitionStepFieldList();
            List<String> illegalOrphanMessages = null;
            for (WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepFieldListOldWorkflowInstanceHasTransitionStepField : workflowInstanceHasTransitionStepFieldListOld) {
                if (!workflowInstanceHasTransitionStepFieldListNew.contains(workflowInstanceHasTransitionStepFieldListOldWorkflowInstanceHasTransitionStepField)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain WorkflowInstanceHasTransitionStepField " + workflowInstanceHasTransitionStepFieldListOldWorkflowInstanceHasTransitionStepField + " since its workflowInstanceHasTransition field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (stepTransitionsToStepNew != null) {
                stepTransitionsToStepNew = em.getReference(stepTransitionsToStepNew.getClass(), stepTransitionsToStepNew.getStepTransitionsToStepPK());
                workflowInstanceHasTransition.setStepTransitionsToStep(stepTransitionsToStepNew);
            }
            if (workflowInstanceNew != null) {
                workflowInstanceNew = em.getReference(workflowInstanceNew.getClass(), workflowInstanceNew.getWorkflowInstancePK());
                workflowInstanceHasTransition.setWorkflowInstance(workflowInstanceNew);
            }
            if (transitionerNew != null) {
                transitionerNew = em.getReference(transitionerNew.getClass(), transitionerNew.getId());
                workflowInstanceHasTransition.setTransitioner(transitionerNew);
            }
            List<WorkflowInstanceHasTransitionStepField> attachedWorkflowInstanceHasTransitionStepFieldListNew = new ArrayList<>();
            for (WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepFieldToAttach : workflowInstanceHasTransitionStepFieldListNew) {
                workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepFieldToAttach = em.getReference(workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepFieldToAttach.getClass(), workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepFieldToAttach.getWorkflowInstanceHasTransitionStepFieldPK());
                attachedWorkflowInstanceHasTransitionStepFieldListNew.add(workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepFieldToAttach);
            }
            workflowInstanceHasTransitionStepFieldListNew = attachedWorkflowInstanceHasTransitionStepFieldListNew;
            workflowInstanceHasTransition.setWorkflowInstanceHasTransitionStepFieldList(workflowInstanceHasTransitionStepFieldListNew);
            workflowInstanceHasTransition = em.merge(workflowInstanceHasTransition);
            if (stepTransitionsToStepOld != null && !stepTransitionsToStepOld.equals(stepTransitionsToStepNew)) {
                stepTransitionsToStepOld.getWorkflowInstanceHasTransitionList().remove(workflowInstanceHasTransition);
                stepTransitionsToStepOld = em.merge(stepTransitionsToStepOld);
            }
            if (stepTransitionsToStepNew != null && !stepTransitionsToStepNew.equals(stepTransitionsToStepOld)) {
                stepTransitionsToStepNew.getWorkflowInstanceHasTransitionList().add(workflowInstanceHasTransition);
                stepTransitionsToStepNew = em.merge(stepTransitionsToStepNew);
            }
            if (workflowInstanceOld != null && !workflowInstanceOld.equals(workflowInstanceNew)) {
                workflowInstanceOld.getWorkflowInstanceHasTransitionList().remove(workflowInstanceHasTransition);
                workflowInstanceOld = em.merge(workflowInstanceOld);
            }
            if (workflowInstanceNew != null && !workflowInstanceNew.equals(workflowInstanceOld)) {
                workflowInstanceNew.getWorkflowInstanceHasTransitionList().add(workflowInstanceHasTransition);
                workflowInstanceNew = em.merge(workflowInstanceNew);
            }
            if (transitionerOld != null && !transitionerOld.equals(transitionerNew)) {
                transitionerOld.getWorkflowInstanceHasTransitionList().remove(workflowInstanceHasTransition);
                transitionerOld = em.merge(transitionerOld);
            }
            if (transitionerNew != null && !transitionerNew.equals(transitionerOld)) {
                transitionerNew.getWorkflowInstanceHasTransitionList().add(workflowInstanceHasTransition);
                transitionerNew = em.merge(transitionerNew);
            }
            for (WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField : workflowInstanceHasTransitionStepFieldListNew) {
                if (!workflowInstanceHasTransitionStepFieldListOld.contains(workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField)) {
                    WorkflowInstanceHasTransition oldWorkflowInstanceHasTransitionOfWorkflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField = workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition();
                    workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField.setWorkflowInstanceHasTransition(workflowInstanceHasTransition);
                    workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField = em.merge(workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField);
                    if (oldWorkflowInstanceHasTransitionOfWorkflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField != null && !oldWorkflowInstanceHasTransitionOfWorkflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField.equals(workflowInstanceHasTransition)) {
                        oldWorkflowInstanceHasTransitionOfWorkflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldList().remove(workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField);
                        oldWorkflowInstanceHasTransitionOfWorkflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField = em.merge(oldWorkflowInstanceHasTransitionOfWorkflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                WorkflowInstanceHasTransitionPK id = workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK();
                if (findWorkflowInstanceHasTransition(id) == null) {
                    throw new NonexistentEntityException("The workflowInstanceHasTransition with id " + id + " no longer exists.");
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

    public void destroy(WorkflowInstanceHasTransitionPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorkflowInstanceHasTransition workflowInstanceHasTransition;
            try {
                workflowInstanceHasTransition = em.getReference(WorkflowInstanceHasTransition.class, id);
                workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The workflowInstanceHasTransition with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<WorkflowInstanceHasTransitionStepField> workflowInstanceHasTransitionStepFieldListOrphanCheck = workflowInstanceHasTransition.getWorkflowInstanceHasTransitionStepFieldList();
            for (WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepFieldListOrphanCheckWorkflowInstanceHasTransitionStepField : workflowInstanceHasTransitionStepFieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This WorkflowInstanceHasTransition (" + workflowInstanceHasTransition + ") cannot be destroyed since the WorkflowInstanceHasTransitionStepField " + workflowInstanceHasTransitionStepFieldListOrphanCheckWorkflowInstanceHasTransitionStepField + " in its workflowInstanceHasTransitionStepFieldList field has a non-nullable workflowInstanceHasTransition field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            StepTransitionsToStep stepTransitionsToStep = workflowInstanceHasTransition.getStepTransitionsToStep();
            if (stepTransitionsToStep != null) {
                stepTransitionsToStep.getWorkflowInstanceHasTransitionList().remove(workflowInstanceHasTransition);
                stepTransitionsToStep = em.merge(stepTransitionsToStep);
            }
            WorkflowInstance workflowInstance = workflowInstanceHasTransition.getWorkflowInstance();
            if (workflowInstance != null) {
                workflowInstance.getWorkflowInstanceHasTransitionList().remove(workflowInstanceHasTransition);
                workflowInstance = em.merge(workflowInstance);
            }
            VmUser transitioner = workflowInstanceHasTransition.getTransitioner();
            if (transitioner != null) {
                transitioner.getWorkflowInstanceHasTransitionList().remove(workflowInstanceHasTransition);
                transitioner = em.merge(transitioner);
            }
            em.remove(workflowInstanceHasTransition);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<WorkflowInstanceHasTransition> findWorkflowInstanceHasTransitionEntities() {
        return findWorkflowInstanceHasTransitionEntities(true, -1, -1);
    }

    public List<WorkflowInstanceHasTransition> findWorkflowInstanceHasTransitionEntities(int maxResults, int firstResult) {
        return findWorkflowInstanceHasTransitionEntities(false, maxResults, firstResult);
    }

    private List<WorkflowInstanceHasTransition> findWorkflowInstanceHasTransitionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WorkflowInstanceHasTransition.class));
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

    public WorkflowInstanceHasTransition findWorkflowInstanceHasTransition(WorkflowInstanceHasTransitionPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WorkflowInstanceHasTransition.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getWorkflowInstanceHasTransitionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WorkflowInstanceHasTransition> rt = cq.from(WorkflowInstanceHasTransition.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
