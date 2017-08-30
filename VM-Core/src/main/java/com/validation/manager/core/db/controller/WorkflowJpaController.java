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

import com.validation.manager.core.db.Workflow;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.WorkflowInstance;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.WorkflowStep;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class WorkflowJpaController implements Serializable {

    public WorkflowJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Workflow workflow) {
        if (workflow.getWorkflowInstanceList() == null) {
            workflow.setWorkflowInstanceList(new ArrayList<>());
        }
        if (workflow.getWorkflowStepList() == null) {
            workflow.setWorkflowStepList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<WorkflowInstance> attachedWorkflowInstanceList = new ArrayList<>();
            for (WorkflowInstance workflowInstanceListWorkflowInstanceToAttach : workflow.getWorkflowInstanceList()) {
                workflowInstanceListWorkflowInstanceToAttach = em.getReference(workflowInstanceListWorkflowInstanceToAttach.getClass(), workflowInstanceListWorkflowInstanceToAttach.getWorkflowInstancePK());
                attachedWorkflowInstanceList.add(workflowInstanceListWorkflowInstanceToAttach);
            }
            workflow.setWorkflowInstanceList(attachedWorkflowInstanceList);
            List<WorkflowStep> attachedWorkflowStepList = new ArrayList<>();
            for (WorkflowStep workflowStepListWorkflowStepToAttach : workflow.getWorkflowStepList()) {
                workflowStepListWorkflowStepToAttach = em.getReference(workflowStepListWorkflowStepToAttach.getClass(), workflowStepListWorkflowStepToAttach.getWorkflowStepPK());
                attachedWorkflowStepList.add(workflowStepListWorkflowStepToAttach);
            }
            workflow.setWorkflowStepList(attachedWorkflowStepList);
            em.persist(workflow);
            for (WorkflowInstance workflowInstanceListWorkflowInstance : workflow.getWorkflowInstanceList()) {
                Workflow oldWorkflowOfWorkflowInstanceListWorkflowInstance = workflowInstanceListWorkflowInstance.getWorkflow();
                workflowInstanceListWorkflowInstance.setWorkflow(workflow);
                workflowInstanceListWorkflowInstance = em.merge(workflowInstanceListWorkflowInstance);
                if (oldWorkflowOfWorkflowInstanceListWorkflowInstance != null) {
                    oldWorkflowOfWorkflowInstanceListWorkflowInstance.getWorkflowInstanceList().remove(workflowInstanceListWorkflowInstance);
                    oldWorkflowOfWorkflowInstanceListWorkflowInstance = em.merge(oldWorkflowOfWorkflowInstanceListWorkflowInstance);
                }
            }
            for (WorkflowStep workflowStepListWorkflowStep : workflow.getWorkflowStepList()) {
                Workflow oldWorkflowOfWorkflowStepListWorkflowStep = workflowStepListWorkflowStep.getWorkflow();
                workflowStepListWorkflowStep.setWorkflow(workflow);
                workflowStepListWorkflowStep = em.merge(workflowStepListWorkflowStep);
                if (oldWorkflowOfWorkflowStepListWorkflowStep != null) {
                    oldWorkflowOfWorkflowStepListWorkflowStep.getWorkflowStepList().remove(workflowStepListWorkflowStep);
                    oldWorkflowOfWorkflowStepListWorkflowStep = em.merge(oldWorkflowOfWorkflowStepListWorkflowStep);
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

    public void edit(Workflow workflow) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Workflow persistentWorkflow = em.find(Workflow.class, workflow.getId());
            List<WorkflowInstance> workflowInstanceListOld = persistentWorkflow.getWorkflowInstanceList();
            List<WorkflowInstance> workflowInstanceListNew = workflow.getWorkflowInstanceList();
            List<WorkflowStep> workflowStepListOld = persistentWorkflow.getWorkflowStepList();
            List<WorkflowStep> workflowStepListNew = workflow.getWorkflowStepList();
            List<String> illegalOrphanMessages = null;
            for (WorkflowInstance workflowInstanceListOldWorkflowInstance : workflowInstanceListOld) {
                if (!workflowInstanceListNew.contains(workflowInstanceListOldWorkflowInstance)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain WorkflowInstance " + workflowInstanceListOldWorkflowInstance + " since its workflow field is not nullable.");
                }
            }
            for (WorkflowStep workflowStepListOldWorkflowStep : workflowStepListOld) {
                if (!workflowStepListNew.contains(workflowStepListOldWorkflowStep)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain WorkflowStep " + workflowStepListOldWorkflowStep + " since its workflow field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<WorkflowInstance> attachedWorkflowInstanceListNew = new ArrayList<>();
            for (WorkflowInstance workflowInstanceListNewWorkflowInstanceToAttach : workflowInstanceListNew) {
                workflowInstanceListNewWorkflowInstanceToAttach = em.getReference(workflowInstanceListNewWorkflowInstanceToAttach.getClass(), workflowInstanceListNewWorkflowInstanceToAttach.getWorkflowInstancePK());
                attachedWorkflowInstanceListNew.add(workflowInstanceListNewWorkflowInstanceToAttach);
            }
            workflowInstanceListNew = attachedWorkflowInstanceListNew;
            workflow.setWorkflowInstanceList(workflowInstanceListNew);
            List<WorkflowStep> attachedWorkflowStepListNew = new ArrayList<>();
            for (WorkflowStep workflowStepListNewWorkflowStepToAttach : workflowStepListNew) {
                workflowStepListNewWorkflowStepToAttach = em.getReference(workflowStepListNewWorkflowStepToAttach.getClass(), workflowStepListNewWorkflowStepToAttach.getWorkflowStepPK());
                attachedWorkflowStepListNew.add(workflowStepListNewWorkflowStepToAttach);
            }
            workflowStepListNew = attachedWorkflowStepListNew;
            workflow.setWorkflowStepList(workflowStepListNew);
            workflow = em.merge(workflow);
            for (WorkflowInstance workflowInstanceListNewWorkflowInstance : workflowInstanceListNew) {
                if (!workflowInstanceListOld.contains(workflowInstanceListNewWorkflowInstance)) {
                    Workflow oldWorkflowOfWorkflowInstanceListNewWorkflowInstance = workflowInstanceListNewWorkflowInstance.getWorkflow();
                    workflowInstanceListNewWorkflowInstance.setWorkflow(workflow);
                    workflowInstanceListNewWorkflowInstance = em.merge(workflowInstanceListNewWorkflowInstance);
                    if (oldWorkflowOfWorkflowInstanceListNewWorkflowInstance != null && !oldWorkflowOfWorkflowInstanceListNewWorkflowInstance.equals(workflow)) {
                        oldWorkflowOfWorkflowInstanceListNewWorkflowInstance.getWorkflowInstanceList().remove(workflowInstanceListNewWorkflowInstance);
                        oldWorkflowOfWorkflowInstanceListNewWorkflowInstance = em.merge(oldWorkflowOfWorkflowInstanceListNewWorkflowInstance);
                    }
                }
            }
            for (WorkflowStep workflowStepListNewWorkflowStep : workflowStepListNew) {
                if (!workflowStepListOld.contains(workflowStepListNewWorkflowStep)) {
                    Workflow oldWorkflowOfWorkflowStepListNewWorkflowStep = workflowStepListNewWorkflowStep.getWorkflow();
                    workflowStepListNewWorkflowStep.setWorkflow(workflow);
                    workflowStepListNewWorkflowStep = em.merge(workflowStepListNewWorkflowStep);
                    if (oldWorkflowOfWorkflowStepListNewWorkflowStep != null && !oldWorkflowOfWorkflowStepListNewWorkflowStep.equals(workflow)) {
                        oldWorkflowOfWorkflowStepListNewWorkflowStep.getWorkflowStepList().remove(workflowStepListNewWorkflowStep);
                        oldWorkflowOfWorkflowStepListNewWorkflowStep = em.merge(oldWorkflowOfWorkflowStepListNewWorkflowStep);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = workflow.getId();
                if (findWorkflow(id) == null) {
                    throw new NonexistentEntityException("The workflow with id " + id + " no longer exists.");
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
            Workflow workflow;
            try {
                workflow = em.getReference(Workflow.class, id);
                workflow.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The workflow with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<WorkflowInstance> workflowInstanceListOrphanCheck = workflow.getWorkflowInstanceList();
            for (WorkflowInstance workflowInstanceListOrphanCheckWorkflowInstance : workflowInstanceListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Workflow (" + workflow + ") cannot be destroyed since the WorkflowInstance " + workflowInstanceListOrphanCheckWorkflowInstance + " in its workflowInstanceList field has a non-nullable workflow field.");
            }
            List<WorkflowStep> workflowStepListOrphanCheck = workflow.getWorkflowStepList();
            for (WorkflowStep workflowStepListOrphanCheckWorkflowStep : workflowStepListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Workflow (" + workflow + ") cannot be destroyed since the WorkflowStep " + workflowStepListOrphanCheckWorkflowStep + " in its workflowStepList field has a non-nullable workflow field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(workflow);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Workflow> findWorkflowEntities() {
        return findWorkflowEntities(true, -1, -1);
    }

    public List<Workflow> findWorkflowEntities(int maxResults, int firstResult) {
        return findWorkflowEntities(false, maxResults, firstResult);
    }

    private List<Workflow> findWorkflowEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Workflow.class));
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

    public Workflow findWorkflow(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Workflow.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getWorkflowCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Workflow> rt = cq.from(Workflow.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
