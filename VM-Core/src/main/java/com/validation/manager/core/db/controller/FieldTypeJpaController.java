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

import com.validation.manager.core.db.FieldType;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.HistoryField;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.WorkflowStepField;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FieldTypeJpaController implements Serializable {

    public FieldTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(FieldType fieldType) {
        if (fieldType.getHistoryFieldList() == null) {
            fieldType.setHistoryFieldList(new ArrayList<>());
        }
        if (fieldType.getWorkflowStepFieldList() == null) {
            fieldType.setWorkflowStepFieldList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<HistoryField> attachedHistoryFieldList = new ArrayList<>();
            for (HistoryField historyFieldListHistoryFieldToAttach : fieldType.getHistoryFieldList()) {
                historyFieldListHistoryFieldToAttach = em.getReference(historyFieldListHistoryFieldToAttach.getClass(), historyFieldListHistoryFieldToAttach.getHistoryFieldPK());
                attachedHistoryFieldList.add(historyFieldListHistoryFieldToAttach);
            }
            fieldType.setHistoryFieldList(attachedHistoryFieldList);
            List<WorkflowStepField> attachedWorkflowStepFieldList = new ArrayList<>();
            for (WorkflowStepField workflowStepFieldListWorkflowStepFieldToAttach : fieldType.getWorkflowStepFieldList()) {
                workflowStepFieldListWorkflowStepFieldToAttach = em.getReference(workflowStepFieldListWorkflowStepFieldToAttach.getClass(), workflowStepFieldListWorkflowStepFieldToAttach.getWorkflowStepFieldPK());
                attachedWorkflowStepFieldList.add(workflowStepFieldListWorkflowStepFieldToAttach);
            }
            fieldType.setWorkflowStepFieldList(attachedWorkflowStepFieldList);
            em.persist(fieldType);
            for (HistoryField historyFieldListHistoryField : fieldType.getHistoryFieldList()) {
                FieldType oldFieldTypeOfHistoryFieldListHistoryField = historyFieldListHistoryField.getFieldType();
                historyFieldListHistoryField.setFieldType(fieldType);
                historyFieldListHistoryField = em.merge(historyFieldListHistoryField);
                if (oldFieldTypeOfHistoryFieldListHistoryField != null) {
                    oldFieldTypeOfHistoryFieldListHistoryField.getHistoryFieldList().remove(historyFieldListHistoryField);
                    oldFieldTypeOfHistoryFieldListHistoryField = em.merge(oldFieldTypeOfHistoryFieldListHistoryField);
                }
            }
            for (WorkflowStepField workflowStepFieldListWorkflowStepField : fieldType.getWorkflowStepFieldList()) {
                FieldType oldFieldTypeOfWorkflowStepFieldListWorkflowStepField = workflowStepFieldListWorkflowStepField.getFieldType();
                workflowStepFieldListWorkflowStepField.setFieldType(fieldType);
                workflowStepFieldListWorkflowStepField = em.merge(workflowStepFieldListWorkflowStepField);
                if (oldFieldTypeOfWorkflowStepFieldListWorkflowStepField != null) {
                    oldFieldTypeOfWorkflowStepFieldListWorkflowStepField.getWorkflowStepFieldList().remove(workflowStepFieldListWorkflowStepField);
                    oldFieldTypeOfWorkflowStepFieldListWorkflowStepField = em.merge(oldFieldTypeOfWorkflowStepFieldListWorkflowStepField);
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

    public void edit(FieldType fieldType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FieldType persistentFieldType = em.find(FieldType.class, fieldType.getId());
            List<HistoryField> historyFieldListOld = persistentFieldType.getHistoryFieldList();
            List<HistoryField> historyFieldListNew = fieldType.getHistoryFieldList();
            List<WorkflowStepField> workflowStepFieldListOld = persistentFieldType.getWorkflowStepFieldList();
            List<WorkflowStepField> workflowStepFieldListNew = fieldType.getWorkflowStepFieldList();
            List<String> illegalOrphanMessages = null;
            for (HistoryField historyFieldListOldHistoryField : historyFieldListOld) {
                if (!historyFieldListNew.contains(historyFieldListOldHistoryField)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain HistoryField " + historyFieldListOldHistoryField + " since its fieldType field is not nullable.");
                }
            }
            for (WorkflowStepField workflowStepFieldListOldWorkflowStepField : workflowStepFieldListOld) {
                if (!workflowStepFieldListNew.contains(workflowStepFieldListOldWorkflowStepField)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain WorkflowStepField " + workflowStepFieldListOldWorkflowStepField + " since its fieldType field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<HistoryField> attachedHistoryFieldListNew = new ArrayList<>();
            for (HistoryField historyFieldListNewHistoryFieldToAttach : historyFieldListNew) {
                historyFieldListNewHistoryFieldToAttach = em.getReference(historyFieldListNewHistoryFieldToAttach.getClass(), historyFieldListNewHistoryFieldToAttach.getHistoryFieldPK());
                attachedHistoryFieldListNew.add(historyFieldListNewHistoryFieldToAttach);
            }
            historyFieldListNew = attachedHistoryFieldListNew;
            fieldType.setHistoryFieldList(historyFieldListNew);
            List<WorkflowStepField> attachedWorkflowStepFieldListNew = new ArrayList<>();
            for (WorkflowStepField workflowStepFieldListNewWorkflowStepFieldToAttach : workflowStepFieldListNew) {
                workflowStepFieldListNewWorkflowStepFieldToAttach = em.getReference(workflowStepFieldListNewWorkflowStepFieldToAttach.getClass(), workflowStepFieldListNewWorkflowStepFieldToAttach.getWorkflowStepFieldPK());
                attachedWorkflowStepFieldListNew.add(workflowStepFieldListNewWorkflowStepFieldToAttach);
            }
            workflowStepFieldListNew = attachedWorkflowStepFieldListNew;
            fieldType.setWorkflowStepFieldList(workflowStepFieldListNew);
            fieldType = em.merge(fieldType);
            for (HistoryField historyFieldListNewHistoryField : historyFieldListNew) {
                if (!historyFieldListOld.contains(historyFieldListNewHistoryField)) {
                    FieldType oldFieldTypeOfHistoryFieldListNewHistoryField = historyFieldListNewHistoryField.getFieldType();
                    historyFieldListNewHistoryField.setFieldType(fieldType);
                    historyFieldListNewHistoryField = em.merge(historyFieldListNewHistoryField);
                    if (oldFieldTypeOfHistoryFieldListNewHistoryField != null && !oldFieldTypeOfHistoryFieldListNewHistoryField.equals(fieldType)) {
                        oldFieldTypeOfHistoryFieldListNewHistoryField.getHistoryFieldList().remove(historyFieldListNewHistoryField);
                        oldFieldTypeOfHistoryFieldListNewHistoryField = em.merge(oldFieldTypeOfHistoryFieldListNewHistoryField);
                    }
                }
            }
            for (WorkflowStepField workflowStepFieldListNewWorkflowStepField : workflowStepFieldListNew) {
                if (!workflowStepFieldListOld.contains(workflowStepFieldListNewWorkflowStepField)) {
                    FieldType oldFieldTypeOfWorkflowStepFieldListNewWorkflowStepField = workflowStepFieldListNewWorkflowStepField.getFieldType();
                    workflowStepFieldListNewWorkflowStepField.setFieldType(fieldType);
                    workflowStepFieldListNewWorkflowStepField = em.merge(workflowStepFieldListNewWorkflowStepField);
                    if (oldFieldTypeOfWorkflowStepFieldListNewWorkflowStepField != null && !oldFieldTypeOfWorkflowStepFieldListNewWorkflowStepField.equals(fieldType)) {
                        oldFieldTypeOfWorkflowStepFieldListNewWorkflowStepField.getWorkflowStepFieldList().remove(workflowStepFieldListNewWorkflowStepField);
                        oldFieldTypeOfWorkflowStepFieldListNewWorkflowStepField = em.merge(oldFieldTypeOfWorkflowStepFieldListNewWorkflowStepField);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = fieldType.getId();
                if (findFieldType(id) == null) {
                    throw new NonexistentEntityException("The fieldType with id " + id + " no longer exists.");
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
            FieldType fieldType;
            try {
                fieldType = em.getReference(FieldType.class, id);
                fieldType.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The fieldType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<HistoryField> historyFieldListOrphanCheck = fieldType.getHistoryFieldList();
            for (HistoryField historyFieldListOrphanCheckHistoryField : historyFieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This FieldType (" + fieldType + ") cannot be destroyed since the HistoryField " + historyFieldListOrphanCheckHistoryField + " in its historyFieldList field has a non-nullable fieldType field.");
            }
            List<WorkflowStepField> workflowStepFieldListOrphanCheck = fieldType.getWorkflowStepFieldList();
            for (WorkflowStepField workflowStepFieldListOrphanCheckWorkflowStepField : workflowStepFieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This FieldType (" + fieldType + ") cannot be destroyed since the WorkflowStepField " + workflowStepFieldListOrphanCheckWorkflowStepField + " in its workflowStepFieldList field has a non-nullable fieldType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(fieldType);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<FieldType> findFieldTypeEntities() {
        return findFieldTypeEntities(true, -1, -1);
    }

    public List<FieldType> findFieldTypeEntities(int maxResults, int firstResult) {
        return findFieldTypeEntities(false, maxResults, firstResult);
    }

    private List<FieldType> findFieldTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(FieldType.class));
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

    public FieldType findFieldType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(FieldType.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getFieldTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<FieldType> rt = cq.from(FieldType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
