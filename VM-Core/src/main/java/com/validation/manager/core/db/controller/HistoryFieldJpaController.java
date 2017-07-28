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
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.HistoryField;
import com.validation.manager.core.db.HistoryFieldPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class HistoryFieldJpaController implements Serializable {

    public HistoryFieldJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(HistoryField historyField) throws PreexistingEntityException, Exception {
        if (historyField.getHistoryFieldPK() == null) {
            historyField.setHistoryFieldPK(new HistoryFieldPK());
        }
        historyField.getHistoryFieldPK().setFieldTypeId(historyField.getFieldType().getId());
        historyField.getHistoryFieldPK().setHistoryId(historyField.getHistory().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FieldType fieldType = historyField.getFieldType();
            if (fieldType != null) {
                fieldType = em.getReference(fieldType.getClass(), fieldType.getId());
                historyField.setFieldType(fieldType);
            }
            History history = historyField.getHistory();
            if (history != null) {
                history = em.getReference(history.getClass(), history.getId());
                historyField.setHistory(history);
            }
            em.persist(historyField);
            if (fieldType != null) {
                fieldType.getHistoryFieldList().add(historyField);
                fieldType = em.merge(fieldType);
            }
            if (history != null) {
                history.getHistoryFieldList().add(historyField);
                history = em.merge(history);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findHistoryField(historyField.getHistoryFieldPK()) != null) {
                throw new PreexistingEntityException("HistoryField " + historyField + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(HistoryField historyField) throws NonexistentEntityException, Exception {
        historyField.getHistoryFieldPK().setFieldTypeId(historyField.getFieldType().getId());
        historyField.getHistoryFieldPK().setHistoryId(historyField.getHistory().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            HistoryField persistentHistoryField = em.find(HistoryField.class, historyField.getHistoryFieldPK());
            FieldType fieldTypeOld = persistentHistoryField.getFieldType();
            FieldType fieldTypeNew = historyField.getFieldType();
            History historyOld = persistentHistoryField.getHistory();
            History historyNew = historyField.getHistory();
            if (fieldTypeNew != null) {
                fieldTypeNew = em.getReference(fieldTypeNew.getClass(), fieldTypeNew.getId());
                historyField.setFieldType(fieldTypeNew);
            }
            if (historyNew != null) {
                historyNew = em.getReference(historyNew.getClass(), historyNew.getId());
                historyField.setHistory(historyNew);
            }
            historyField = em.merge(historyField);
            if (fieldTypeOld != null && !fieldTypeOld.equals(fieldTypeNew)) {
                fieldTypeOld.getHistoryFieldList().remove(historyField);
                fieldTypeOld = em.merge(fieldTypeOld);
            }
            if (fieldTypeNew != null && !fieldTypeNew.equals(fieldTypeOld)) {
                fieldTypeNew.getHistoryFieldList().add(historyField);
                fieldTypeNew = em.merge(fieldTypeNew);
            }
            if (historyOld != null && !historyOld.equals(historyNew)) {
                historyOld.getHistoryFieldList().remove(historyField);
                historyOld = em.merge(historyOld);
            }
            if (historyNew != null && !historyNew.equals(historyOld)) {
                historyNew.getHistoryFieldList().add(historyField);
                historyNew = em.merge(historyNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                HistoryFieldPK id = historyField.getHistoryFieldPK();
                if (findHistoryField(id) == null) {
                    throw new NonexistentEntityException("The historyField with id " + id + " no longer exists.");
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

    public void destroy(HistoryFieldPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            HistoryField historyField;
            try {
                historyField = em.getReference(HistoryField.class, id);
                historyField.getHistoryFieldPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The historyField with id " + id + " no longer exists.", enfe);
            }
            FieldType fieldType = historyField.getFieldType();
            if (fieldType != null) {
                fieldType.getHistoryFieldList().remove(historyField);
                fieldType = em.merge(fieldType);
            }
            History history = historyField.getHistory();
            if (history != null) {
                history.getHistoryFieldList().remove(historyField);
                history = em.merge(history);
            }
            em.remove(historyField);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<HistoryField> findHistoryFieldEntities() {
        return findHistoryFieldEntities(true, -1, -1);
    }

    public List<HistoryField> findHistoryFieldEntities(int maxResults, int firstResult) {
        return findHistoryFieldEntities(false, maxResults, firstResult);
    }

    private List<HistoryField> findHistoryFieldEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(HistoryField.class));
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

    public HistoryField findHistoryField(HistoryFieldPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(HistoryField.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getHistoryFieldCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<HistoryField> rt = cq.from(HistoryField.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
