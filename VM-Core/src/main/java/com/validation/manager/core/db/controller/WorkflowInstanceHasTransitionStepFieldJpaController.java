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
import com.validation.manager.core.db.WorkflowStepField;
import com.validation.manager.core.db.WorkflowInstanceHasTransition;
import com.validation.manager.core.db.WorkflowInstanceHasTransitionStepField;
import com.validation.manager.core.db.WorkflowInstanceHasTransitionStepFieldPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class WorkflowInstanceHasTransitionStepFieldJpaController implements Serializable {

    public WorkflowInstanceHasTransitionStepFieldJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepField) throws PreexistingEntityException, Exception {
        if (workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK() == null) {
            workflowInstanceHasTransitionStepField.setWorkflowInstanceHasTransitionStepFieldPK(new WorkflowInstanceHasTransitionStepFieldPK());
        }
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setTargetStep(workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition().getWorkflowInstanceHasTransitionPK().getStepTransitionsToStepTargetStep());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setSourceStepWorkflow(workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition().getWorkflowInstanceHasTransitionPK().getStepTransitionsToStepSourceStepWorkflow());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setInstance(workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition().getWorkflowInstanceHasTransitionPK().getWorkflowInstanceId());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setTargetStepWorkflow(workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition().getWorkflowInstanceHasTransitionPK().getStepTransitionsToStepTargetStepWorkflow());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setWorkflow(workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition().getWorkflowInstanceHasTransitionPK().getWorkflowInstanceWorkflow());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setSourceStep(workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition().getWorkflowInstanceHasTransitionPK().getStepTransitionsToStepSourceStep());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setStepFieldId(workflowInstanceHasTransitionStepField.getWorkflowStepField().getWorkflowStepFieldPK().getId());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setFieldType(workflowInstanceHasTransitionStepField.getWorkflowStepField().getWorkflowStepFieldPK().getFieldType());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorkflowStepField workflowStepField = workflowInstanceHasTransitionStepField.getWorkflowStepField();
            if (workflowStepField != null) {
                workflowStepField = em.getReference(workflowStepField.getClass(), workflowStepField.getWorkflowStepFieldPK());
                workflowInstanceHasTransitionStepField.setWorkflowStepField(workflowStepField);
            }
            WorkflowInstanceHasTransition workflowInstanceHasTransition = workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition();
            if (workflowInstanceHasTransition != null) {
                workflowInstanceHasTransition = em.getReference(workflowInstanceHasTransition.getClass(), workflowInstanceHasTransition.getWorkflowInstanceHasTransitionPK());
                workflowInstanceHasTransitionStepField.setWorkflowInstanceHasTransition(workflowInstanceHasTransition);
            }
            em.persist(workflowInstanceHasTransitionStepField);
            if (workflowStepField != null) {
                workflowStepField.getWorkflowInstanceHasTransitionStepFieldList().add(workflowInstanceHasTransitionStepField);
                workflowStepField = em.merge(workflowStepField);
            }
            if (workflowInstanceHasTransition != null) {
                workflowInstanceHasTransition.getWorkflowInstanceHasTransitionStepFieldList().add(workflowInstanceHasTransitionStepField);
                workflowInstanceHasTransition = em.merge(workflowInstanceHasTransition);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findWorkflowInstanceHasTransitionStepField(workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK()) != null) {
                throw new PreexistingEntityException("WorkflowInstanceHasTransitionStepField " + workflowInstanceHasTransitionStepField + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepField) throws NonexistentEntityException, Exception {
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setTargetStep(workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition().getWorkflowInstanceHasTransitionPK().getStepTransitionsToStepTargetStep());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setSourceStepWorkflow(workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition().getWorkflowInstanceHasTransitionPK().getStepTransitionsToStepSourceStepWorkflow());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setInstance(workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition().getWorkflowInstanceHasTransitionPK().getWorkflowInstanceId());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setTargetStepWorkflow(workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition().getWorkflowInstanceHasTransitionPK().getStepTransitionsToStepTargetStepWorkflow());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setWorkflow(workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition().getWorkflowInstanceHasTransitionPK().getWorkflowInstanceWorkflow());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setSourceStep(workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition().getWorkflowInstanceHasTransitionPK().getStepTransitionsToStepSourceStep());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setStepFieldId(workflowInstanceHasTransitionStepField.getWorkflowStepField().getWorkflowStepFieldPK().getId());
        workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK().setFieldType(workflowInstanceHasTransitionStepField.getWorkflowStepField().getWorkflowStepFieldPK().getFieldType());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorkflowInstanceHasTransitionStepField persistentWorkflowInstanceHasTransitionStepField = em.find(WorkflowInstanceHasTransitionStepField.class, workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK());
            WorkflowStepField workflowStepFieldOld = persistentWorkflowInstanceHasTransitionStepField.getWorkflowStepField();
            WorkflowStepField workflowStepFieldNew = workflowInstanceHasTransitionStepField.getWorkflowStepField();
            WorkflowInstanceHasTransition workflowInstanceHasTransitionOld = persistentWorkflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition();
            WorkflowInstanceHasTransition workflowInstanceHasTransitionNew = workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition();
            if (workflowStepFieldNew != null) {
                workflowStepFieldNew = em.getReference(workflowStepFieldNew.getClass(), workflowStepFieldNew.getWorkflowStepFieldPK());
                workflowInstanceHasTransitionStepField.setWorkflowStepField(workflowStepFieldNew);
            }
            if (workflowInstanceHasTransitionNew != null) {
                workflowInstanceHasTransitionNew = em.getReference(workflowInstanceHasTransitionNew.getClass(), workflowInstanceHasTransitionNew.getWorkflowInstanceHasTransitionPK());
                workflowInstanceHasTransitionStepField.setWorkflowInstanceHasTransition(workflowInstanceHasTransitionNew);
            }
            workflowInstanceHasTransitionStepField = em.merge(workflowInstanceHasTransitionStepField);
            if (workflowStepFieldOld != null && !workflowStepFieldOld.equals(workflowStepFieldNew)) {
                workflowStepFieldOld.getWorkflowInstanceHasTransitionStepFieldList().remove(workflowInstanceHasTransitionStepField);
                workflowStepFieldOld = em.merge(workflowStepFieldOld);
            }
            if (workflowStepFieldNew != null && !workflowStepFieldNew.equals(workflowStepFieldOld)) {
                workflowStepFieldNew.getWorkflowInstanceHasTransitionStepFieldList().add(workflowInstanceHasTransitionStepField);
                workflowStepFieldNew = em.merge(workflowStepFieldNew);
            }
            if (workflowInstanceHasTransitionOld != null && !workflowInstanceHasTransitionOld.equals(workflowInstanceHasTransitionNew)) {
                workflowInstanceHasTransitionOld.getWorkflowInstanceHasTransitionStepFieldList().remove(workflowInstanceHasTransitionStepField);
                workflowInstanceHasTransitionOld = em.merge(workflowInstanceHasTransitionOld);
            }
            if (workflowInstanceHasTransitionNew != null && !workflowInstanceHasTransitionNew.equals(workflowInstanceHasTransitionOld)) {
                workflowInstanceHasTransitionNew.getWorkflowInstanceHasTransitionStepFieldList().add(workflowInstanceHasTransitionStepField);
                workflowInstanceHasTransitionNew = em.merge(workflowInstanceHasTransitionNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                WorkflowInstanceHasTransitionStepFieldPK id = workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK();
                if (findWorkflowInstanceHasTransitionStepField(id) == null) {
                    throw new NonexistentEntityException("The workflowInstanceHasTransitionStepField with id " + id + " no longer exists.");
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

    public void destroy(WorkflowInstanceHasTransitionStepFieldPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepField;
            try {
                workflowInstanceHasTransitionStepField = em.getReference(WorkflowInstanceHasTransitionStepField.class, id);
                workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The workflowInstanceHasTransitionStepField with id " + id + " no longer exists.", enfe);
            }
            WorkflowStepField workflowStepField = workflowInstanceHasTransitionStepField.getWorkflowStepField();
            if (workflowStepField != null) {
                workflowStepField.getWorkflowInstanceHasTransitionStepFieldList().remove(workflowInstanceHasTransitionStepField);
                workflowStepField = em.merge(workflowStepField);
            }
            WorkflowInstanceHasTransition workflowInstanceHasTransition = workflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransition();
            if (workflowInstanceHasTransition != null) {
                workflowInstanceHasTransition.getWorkflowInstanceHasTransitionStepFieldList().remove(workflowInstanceHasTransitionStepField);
                workflowInstanceHasTransition = em.merge(workflowInstanceHasTransition);
            }
            em.remove(workflowInstanceHasTransitionStepField);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<WorkflowInstanceHasTransitionStepField> findWorkflowInstanceHasTransitionStepFieldEntities() {
        return findWorkflowInstanceHasTransitionStepFieldEntities(true, -1, -1);
    }

    public List<WorkflowInstanceHasTransitionStepField> findWorkflowInstanceHasTransitionStepFieldEntities(int maxResults, int firstResult) {
        return findWorkflowInstanceHasTransitionStepFieldEntities(false, maxResults, firstResult);
    }

    private List<WorkflowInstanceHasTransitionStepField> findWorkflowInstanceHasTransitionStepFieldEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WorkflowInstanceHasTransitionStepField.class));
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

    public WorkflowInstanceHasTransitionStepField findWorkflowInstanceHasTransitionStepField(WorkflowInstanceHasTransitionStepFieldPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WorkflowInstanceHasTransitionStepField.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getWorkflowInstanceHasTransitionStepFieldCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WorkflowInstanceHasTransitionStepField> rt = cq.from(WorkflowInstanceHasTransitionStepField.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
