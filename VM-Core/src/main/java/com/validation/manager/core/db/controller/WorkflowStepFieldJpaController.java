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
import com.validation.manager.core.db.FieldType;
import com.validation.manager.core.db.WorkflowStep;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.WorkflowInstanceHasTransitionStepField;
import com.validation.manager.core.db.WorkflowStepField;
import com.validation.manager.core.db.WorkflowStepFieldPK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class WorkflowStepFieldJpaController implements Serializable {

    public WorkflowStepFieldJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WorkflowStepField workflowStepField) throws PreexistingEntityException, Exception {
        if (workflowStepField.getWorkflowStepFieldPK() == null) {
            workflowStepField.setWorkflowStepFieldPK(new WorkflowStepFieldPK());
        }
        if (workflowStepField.getWorkflowStepList() == null) {
            workflowStepField.setWorkflowStepList(new ArrayList<>());
        }
        if (workflowStepField.getWorkflowInstanceHasTransitionStepFieldList() == null) {
            workflowStepField.setWorkflowInstanceHasTransitionStepFieldList(new ArrayList<>());
        }
        workflowStepField.getWorkflowStepFieldPK().setFieldType(workflowStepField.getFieldType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FieldType fieldType = workflowStepField.getFieldType();
            if (fieldType != null) {
                fieldType = em.getReference(fieldType.getClass(), fieldType.getId());
                workflowStepField.setFieldType(fieldType);
            }
            List<WorkflowStep> attachedWorkflowStepList = new ArrayList<>();
            for (WorkflowStep workflowStepListWorkflowStepToAttach : workflowStepField.getWorkflowStepList()) {
                workflowStepListWorkflowStepToAttach = em.getReference(workflowStepListWorkflowStepToAttach.getClass(), workflowStepListWorkflowStepToAttach.getWorkflowStepPK());
                attachedWorkflowStepList.add(workflowStepListWorkflowStepToAttach);
            }
            workflowStepField.setWorkflowStepList(attachedWorkflowStepList);
            List<WorkflowInstanceHasTransitionStepField> attachedWorkflowInstanceHasTransitionStepFieldList = new ArrayList<>();
            for (WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepFieldToAttach : workflowStepField.getWorkflowInstanceHasTransitionStepFieldList()) {
                workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepFieldToAttach = em.getReference(workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepFieldToAttach.getClass(), workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepFieldToAttach.getWorkflowInstanceHasTransitionStepFieldPK());
                attachedWorkflowInstanceHasTransitionStepFieldList.add(workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepFieldToAttach);
            }
            workflowStepField.setWorkflowInstanceHasTransitionStepFieldList(attachedWorkflowInstanceHasTransitionStepFieldList);
            em.persist(workflowStepField);
            if (fieldType != null) {
                fieldType.getWorkflowStepFieldList().add(workflowStepField);
                fieldType = em.merge(fieldType);
            }
            for (WorkflowStep workflowStepListWorkflowStep : workflowStepField.getWorkflowStepList()) {
                workflowStepListWorkflowStep.getWorkflowStepFieldList().add(workflowStepField);
                workflowStepListWorkflowStep = em.merge(workflowStepListWorkflowStep);
            }
            for (WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField : workflowStepField.getWorkflowInstanceHasTransitionStepFieldList()) {
                WorkflowStepField oldWorkflowStepFieldOfWorkflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField = workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField.getWorkflowStepField();
                workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField.setWorkflowStepField(workflowStepField);
                workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField = em.merge(workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField);
                if (oldWorkflowStepFieldOfWorkflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField != null) {
                    oldWorkflowStepFieldOfWorkflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldList().remove(workflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField);
                    oldWorkflowStepFieldOfWorkflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField = em.merge(oldWorkflowStepFieldOfWorkflowInstanceHasTransitionStepFieldListWorkflowInstanceHasTransitionStepField);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findWorkflowStepField(workflowStepField.getWorkflowStepFieldPK()) != null) {
                throw new PreexistingEntityException("WorkflowStepField " + workflowStepField + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(WorkflowStepField workflowStepField) throws IllegalOrphanException, NonexistentEntityException, Exception {
        workflowStepField.getWorkflowStepFieldPK().setFieldType(workflowStepField.getFieldType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorkflowStepField persistentWorkflowStepField = em.find(WorkflowStepField.class, workflowStepField.getWorkflowStepFieldPK());
            FieldType fieldTypeOld = persistentWorkflowStepField.getFieldType();
            FieldType fieldTypeNew = workflowStepField.getFieldType();
            List<WorkflowStep> workflowStepListOld = persistentWorkflowStepField.getWorkflowStepList();
            List<WorkflowStep> workflowStepListNew = workflowStepField.getWorkflowStepList();
            List<WorkflowInstanceHasTransitionStepField> workflowInstanceHasTransitionStepFieldListOld = persistentWorkflowStepField.getWorkflowInstanceHasTransitionStepFieldList();
            List<WorkflowInstanceHasTransitionStepField> workflowInstanceHasTransitionStepFieldListNew = workflowStepField.getWorkflowInstanceHasTransitionStepFieldList();
            List<String> illegalOrphanMessages = null;
            for (WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepFieldListOldWorkflowInstanceHasTransitionStepField : workflowInstanceHasTransitionStepFieldListOld) {
                if (!workflowInstanceHasTransitionStepFieldListNew.contains(workflowInstanceHasTransitionStepFieldListOldWorkflowInstanceHasTransitionStepField)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain WorkflowInstanceHasTransitionStepField " + workflowInstanceHasTransitionStepFieldListOldWorkflowInstanceHasTransitionStepField + " since its workflowStepField field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (fieldTypeNew != null) {
                fieldTypeNew = em.getReference(fieldTypeNew.getClass(), fieldTypeNew.getId());
                workflowStepField.setFieldType(fieldTypeNew);
            }
            List<WorkflowStep> attachedWorkflowStepListNew = new ArrayList<>();
            for (WorkflowStep workflowStepListNewWorkflowStepToAttach : workflowStepListNew) {
                workflowStepListNewWorkflowStepToAttach = em.getReference(workflowStepListNewWorkflowStepToAttach.getClass(), workflowStepListNewWorkflowStepToAttach.getWorkflowStepPK());
                attachedWorkflowStepListNew.add(workflowStepListNewWorkflowStepToAttach);
            }
            workflowStepListNew = attachedWorkflowStepListNew;
            workflowStepField.setWorkflowStepList(workflowStepListNew);
            List<WorkflowInstanceHasTransitionStepField> attachedWorkflowInstanceHasTransitionStepFieldListNew = new ArrayList<>();
            for (WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepFieldToAttach : workflowInstanceHasTransitionStepFieldListNew) {
                workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepFieldToAttach = em.getReference(workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepFieldToAttach.getClass(), workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepFieldToAttach.getWorkflowInstanceHasTransitionStepFieldPK());
                attachedWorkflowInstanceHasTransitionStepFieldListNew.add(workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepFieldToAttach);
            }
            workflowInstanceHasTransitionStepFieldListNew = attachedWorkflowInstanceHasTransitionStepFieldListNew;
            workflowStepField.setWorkflowInstanceHasTransitionStepFieldList(workflowInstanceHasTransitionStepFieldListNew);
            workflowStepField = em.merge(workflowStepField);
            if (fieldTypeOld != null && !fieldTypeOld.equals(fieldTypeNew)) {
                fieldTypeOld.getWorkflowStepFieldList().remove(workflowStepField);
                fieldTypeOld = em.merge(fieldTypeOld);
            }
            if (fieldTypeNew != null && !fieldTypeNew.equals(fieldTypeOld)) {
                fieldTypeNew.getWorkflowStepFieldList().add(workflowStepField);
                fieldTypeNew = em.merge(fieldTypeNew);
            }
            for (WorkflowStep workflowStepListOldWorkflowStep : workflowStepListOld) {
                if (!workflowStepListNew.contains(workflowStepListOldWorkflowStep)) {
                    workflowStepListOldWorkflowStep.getWorkflowStepFieldList().remove(workflowStepField);
                    workflowStepListOldWorkflowStep = em.merge(workflowStepListOldWorkflowStep);
                }
            }
            for (WorkflowStep workflowStepListNewWorkflowStep : workflowStepListNew) {
                if (!workflowStepListOld.contains(workflowStepListNewWorkflowStep)) {
                    workflowStepListNewWorkflowStep.getWorkflowStepFieldList().add(workflowStepField);
                    workflowStepListNewWorkflowStep = em.merge(workflowStepListNewWorkflowStep);
                }
            }
            for (WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField : workflowInstanceHasTransitionStepFieldListNew) {
                if (!workflowInstanceHasTransitionStepFieldListOld.contains(workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField)) {
                    WorkflowStepField oldWorkflowStepFieldOfWorkflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField = workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField.getWorkflowStepField();
                    workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField.setWorkflowStepField(workflowStepField);
                    workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField = em.merge(workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField);
                    if (oldWorkflowStepFieldOfWorkflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField != null && !oldWorkflowStepFieldOfWorkflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField.equals(workflowStepField)) {
                        oldWorkflowStepFieldOfWorkflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField.getWorkflowInstanceHasTransitionStepFieldList().remove(workflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField);
                        oldWorkflowStepFieldOfWorkflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField = em.merge(oldWorkflowStepFieldOfWorkflowInstanceHasTransitionStepFieldListNewWorkflowInstanceHasTransitionStepField);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                WorkflowStepFieldPK id = workflowStepField.getWorkflowStepFieldPK();
                if (findWorkflowStepField(id) == null) {
                    throw new NonexistentEntityException("The workflowStepField with id " + id + " no longer exists.");
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

    public void destroy(WorkflowStepFieldPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            WorkflowStepField workflowStepField;
            try {
                workflowStepField = em.getReference(WorkflowStepField.class, id);
                workflowStepField.getWorkflowStepFieldPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The workflowStepField with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<WorkflowInstanceHasTransitionStepField> workflowInstanceHasTransitionStepFieldListOrphanCheck = workflowStepField.getWorkflowInstanceHasTransitionStepFieldList();
            for (WorkflowInstanceHasTransitionStepField workflowInstanceHasTransitionStepFieldListOrphanCheckWorkflowInstanceHasTransitionStepField : workflowInstanceHasTransitionStepFieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This WorkflowStepField (" + workflowStepField + ") cannot be destroyed since the WorkflowInstanceHasTransitionStepField " + workflowInstanceHasTransitionStepFieldListOrphanCheckWorkflowInstanceHasTransitionStepField + " in its workflowInstanceHasTransitionStepFieldList field has a non-nullable workflowStepField field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            FieldType fieldType = workflowStepField.getFieldType();
            if (fieldType != null) {
                fieldType.getWorkflowStepFieldList().remove(workflowStepField);
                fieldType = em.merge(fieldType);
            }
            List<WorkflowStep> workflowStepList = workflowStepField.getWorkflowStepList();
            for (WorkflowStep workflowStepListWorkflowStep : workflowStepList) {
                workflowStepListWorkflowStep.getWorkflowStepFieldList().remove(workflowStepField);
                workflowStepListWorkflowStep = em.merge(workflowStepListWorkflowStep);
            }
            em.remove(workflowStepField);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<WorkflowStepField> findWorkflowStepFieldEntities() {
        return findWorkflowStepFieldEntities(true, -1, -1);
    }

    public List<WorkflowStepField> findWorkflowStepFieldEntities(int maxResults, int firstResult) {
        return findWorkflowStepFieldEntities(false, maxResults, firstResult);
    }

    private List<WorkflowStepField> findWorkflowStepFieldEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WorkflowStepField.class));
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

    public WorkflowStepField findWorkflowStepField(WorkflowStepFieldPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WorkflowStepField.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getWorkflowStepFieldCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WorkflowStepField> rt = cq.from(WorkflowStepField.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
