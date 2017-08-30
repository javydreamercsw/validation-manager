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
import com.validation.manager.core.db.WorkflowStep;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.Workflow;
import com.validation.manager.core.db.WorkflowInstance;
import com.validation.manager.core.db.WorkflowInstanceHasTransition;
import com.validation.manager.core.db.WorkflowInstancePK;
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
public class WorkflowInstanceJpaController implements Serializable {

    public WorkflowInstanceJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WorkflowInstance workflowInstance) throws PreexistingEntityException, Exception {
        if (workflowInstance.getWorkflowInstancePK() == null) {
            workflowInstance.setWorkflowInstancePK(new WorkflowInstancePK());
        }
        if (workflowInstance.getWorkflowInstanceHasTransitionList() == null) {
            workflowInstance.setWorkflowInstanceHasTransitionList(new ArrayList<>());
        }
        workflowInstance.getWorkflowInstancePK().setWorkflow(workflowInstance.getWorkflow().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorkflowStep workflowStep = workflowInstance.getWorkflowStep();
            if (workflowStep != null) {
                workflowStep = em.getReference(workflowStep.getClass(), workflowStep.getWorkflowStepPK());
                workflowInstance.setWorkflowStep(workflowStep);
            }
            VmUser assignedUser = workflowInstance.getAssignedUser();
            if (assignedUser != null) {
                assignedUser = em.getReference(assignedUser.getClass(), assignedUser.getId());
                workflowInstance.setAssignedUser(assignedUser);
            }
            Workflow workflow = workflowInstance.getWorkflow();
            if (workflow != null) {
                workflow = em.getReference(workflow.getClass(), workflow.getId());
                workflowInstance.setWorkflow(workflow);
            }
            List<WorkflowInstanceHasTransition> attachedWorkflowInstanceHasTransitionList = new ArrayList<>();
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach : workflowInstance.getWorkflowInstanceHasTransitionList()) {
                workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach = em.getReference(workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach.getClass(), workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach.getWorkflowInstanceHasTransitionPK());
                attachedWorkflowInstanceHasTransitionList.add(workflowInstanceHasTransitionListWorkflowInstanceHasTransitionToAttach);
            }
            workflowInstance.setWorkflowInstanceHasTransitionList(attachedWorkflowInstanceHasTransitionList);
            em.persist(workflowInstance);
            if (workflowStep != null) {
                workflowStep.getWorkflowInstanceList().add(workflowInstance);
                workflowStep = em.merge(workflowStep);
            }
            if (assignedUser != null) {
                assignedUser.getWorkflowInstanceList().add(workflowInstance);
                assignedUser = em.merge(assignedUser);
            }
            if (workflow != null) {
                workflow.getWorkflowInstanceList().add(workflowInstance);
                workflow = em.merge(workflow);
            }
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListWorkflowInstanceHasTransition : workflowInstance.getWorkflowInstanceHasTransitionList()) {
                WorkflowInstance oldWorkflowInstanceOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition = workflowInstanceHasTransitionListWorkflowInstanceHasTransition.getWorkflowInstance();
                workflowInstanceHasTransitionListWorkflowInstanceHasTransition.setWorkflowInstance(workflowInstance);
                workflowInstanceHasTransitionListWorkflowInstanceHasTransition = em.merge(workflowInstanceHasTransitionListWorkflowInstanceHasTransition);
                if (oldWorkflowInstanceOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition != null) {
                    oldWorkflowInstanceOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition.getWorkflowInstanceHasTransitionList().remove(workflowInstanceHasTransitionListWorkflowInstanceHasTransition);
                    oldWorkflowInstanceOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition = em.merge(oldWorkflowInstanceOfWorkflowInstanceHasTransitionListWorkflowInstanceHasTransition);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findWorkflowInstance(workflowInstance.getWorkflowInstancePK()) != null) {
                throw new PreexistingEntityException("WorkflowInstance " + workflowInstance + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(WorkflowInstance workflowInstance) throws IllegalOrphanException, NonexistentEntityException, Exception {
        workflowInstance.getWorkflowInstancePK().setWorkflow(workflowInstance.getWorkflow().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorkflowInstance persistentWorkflowInstance = em.find(WorkflowInstance.class, workflowInstance.getWorkflowInstancePK());
            WorkflowStep workflowStepOld = persistentWorkflowInstance.getWorkflowStep();
            WorkflowStep workflowStepNew = workflowInstance.getWorkflowStep();
            VmUser assignedUserOld = persistentWorkflowInstance.getAssignedUser();
            VmUser assignedUserNew = workflowInstance.getAssignedUser();
            Workflow workflowOld = persistentWorkflowInstance.getWorkflow();
            Workflow workflowNew = workflowInstance.getWorkflow();
            List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionListOld = persistentWorkflowInstance.getWorkflowInstanceHasTransitionList();
            List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionListNew = workflowInstance.getWorkflowInstanceHasTransitionList();
            List<String> illegalOrphanMessages = null;
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListOldWorkflowInstanceHasTransition : workflowInstanceHasTransitionListOld) {
                if (!workflowInstanceHasTransitionListNew.contains(workflowInstanceHasTransitionListOldWorkflowInstanceHasTransition)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain WorkflowInstanceHasTransition " + workflowInstanceHasTransitionListOldWorkflowInstanceHasTransition + " since its workflowInstance field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (workflowStepNew != null) {
                workflowStepNew = em.getReference(workflowStepNew.getClass(), workflowStepNew.getWorkflowStepPK());
                workflowInstance.setWorkflowStep(workflowStepNew);
            }
            if (assignedUserNew != null) {
                assignedUserNew = em.getReference(assignedUserNew.getClass(), assignedUserNew.getId());
                workflowInstance.setAssignedUser(assignedUserNew);
            }
            if (workflowNew != null) {
                workflowNew = em.getReference(workflowNew.getClass(), workflowNew.getId());
                workflowInstance.setWorkflow(workflowNew);
            }
            List<WorkflowInstanceHasTransition> attachedWorkflowInstanceHasTransitionListNew = new ArrayList<>();
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach : workflowInstanceHasTransitionListNew) {
                workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach = em.getReference(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach.getClass(), workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach.getWorkflowInstanceHasTransitionPK());
                attachedWorkflowInstanceHasTransitionListNew.add(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransitionToAttach);
            }
            workflowInstanceHasTransitionListNew = attachedWorkflowInstanceHasTransitionListNew;
            workflowInstance.setWorkflowInstanceHasTransitionList(workflowInstanceHasTransitionListNew);
            workflowInstance = em.merge(workflowInstance);
            if (workflowStepOld != null && !workflowStepOld.equals(workflowStepNew)) {
                workflowStepOld.getWorkflowInstanceList().remove(workflowInstance);
                workflowStepOld = em.merge(workflowStepOld);
            }
            if (workflowStepNew != null && !workflowStepNew.equals(workflowStepOld)) {
                workflowStepNew.getWorkflowInstanceList().add(workflowInstance);
                workflowStepNew = em.merge(workflowStepNew);
            }
            if (assignedUserOld != null && !assignedUserOld.equals(assignedUserNew)) {
                assignedUserOld.getWorkflowInstanceList().remove(workflowInstance);
                assignedUserOld = em.merge(assignedUserOld);
            }
            if (assignedUserNew != null && !assignedUserNew.equals(assignedUserOld)) {
                assignedUserNew.getWorkflowInstanceList().add(workflowInstance);
                assignedUserNew = em.merge(assignedUserNew);
            }
            if (workflowOld != null && !workflowOld.equals(workflowNew)) {
                workflowOld.getWorkflowInstanceList().remove(workflowInstance);
                workflowOld = em.merge(workflowOld);
            }
            if (workflowNew != null && !workflowNew.equals(workflowOld)) {
                workflowNew.getWorkflowInstanceList().add(workflowInstance);
                workflowNew = em.merge(workflowNew);
            }
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition : workflowInstanceHasTransitionListNew) {
                if (!workflowInstanceHasTransitionListOld.contains(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition)) {
                    WorkflowInstance oldWorkflowInstanceOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition = workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition.getWorkflowInstance();
                    workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition.setWorkflowInstance(workflowInstance);
                    workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition = em.merge(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition);
                    if (oldWorkflowInstanceOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition != null && !oldWorkflowInstanceOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition.equals(workflowInstance)) {
                        oldWorkflowInstanceOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition.getWorkflowInstanceHasTransitionList().remove(workflowInstanceHasTransitionListNewWorkflowInstanceHasTransition);
                        oldWorkflowInstanceOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition = em.merge(oldWorkflowInstanceOfWorkflowInstanceHasTransitionListNewWorkflowInstanceHasTransition);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                WorkflowInstancePK id = workflowInstance.getWorkflowInstancePK();
                if (findWorkflowInstance(id) == null) {
                    throw new NonexistentEntityException("The workflowInstance with id " + id + " no longer exists.");
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

    public void destroy(WorkflowInstancePK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorkflowInstance workflowInstance;
            try {
                workflowInstance = em.getReference(WorkflowInstance.class, id);
                workflowInstance.getWorkflowInstancePK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The workflowInstance with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionListOrphanCheck = workflowInstance.getWorkflowInstanceHasTransitionList();
            for (WorkflowInstanceHasTransition workflowInstanceHasTransitionListOrphanCheckWorkflowInstanceHasTransition : workflowInstanceHasTransitionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This WorkflowInstance (" + workflowInstance + ") cannot be destroyed since the WorkflowInstanceHasTransition " + workflowInstanceHasTransitionListOrphanCheckWorkflowInstanceHasTransition + " in its workflowInstanceHasTransitionList field has a non-nullable workflowInstance field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            WorkflowStep workflowStep = workflowInstance.getWorkflowStep();
            if (workflowStep != null) {
                workflowStep.getWorkflowInstanceList().remove(workflowInstance);
                workflowStep = em.merge(workflowStep);
            }
            VmUser assignedUser = workflowInstance.getAssignedUser();
            if (assignedUser != null) {
                assignedUser.getWorkflowInstanceList().remove(workflowInstance);
                assignedUser = em.merge(assignedUser);
            }
            Workflow workflow = workflowInstance.getWorkflow();
            if (workflow != null) {
                workflow.getWorkflowInstanceList().remove(workflowInstance);
                workflow = em.merge(workflow);
            }
            em.remove(workflowInstance);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<WorkflowInstance> findWorkflowInstanceEntities() {
        return findWorkflowInstanceEntities(true, -1, -1);
    }

    public List<WorkflowInstance> findWorkflowInstanceEntities(int maxResults, int firstResult) {
        return findWorkflowInstanceEntities(false, maxResults, firstResult);
    }

    private List<WorkflowInstance> findWorkflowInstanceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WorkflowInstance.class));
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

    public WorkflowInstance findWorkflowInstance(WorkflowInstancePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WorkflowInstance.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getWorkflowInstanceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WorkflowInstance> rt = cq.from(WorkflowInstance.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
