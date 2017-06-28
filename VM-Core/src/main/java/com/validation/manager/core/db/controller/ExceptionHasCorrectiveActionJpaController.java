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
import com.validation.manager.core.db.CorrectiveAction;
import com.validation.manager.core.db.ExceptionHasCorrectiveAction;
import com.validation.manager.core.db.ExceptionHasCorrectiveActionPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ExceptionHasCorrectiveActionJpaController implements Serializable {

    public ExceptionHasCorrectiveActionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ExceptionHasCorrectiveAction exceptionHasCorrectiveAction) throws PreexistingEntityException, Exception {
        if (exceptionHasCorrectiveAction.getExceptionHasCorrectiveActionPK() == null) {
            exceptionHasCorrectiveAction.setExceptionHasCorrectiveActionPK(new ExceptionHasCorrectiveActionPK());
        }
        exceptionHasCorrectiveAction.getExceptionHasCorrectiveActionPK().setCorrectiveActionId(exceptionHasCorrectiveAction.getCorrectiveAction().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CorrectiveAction correctiveAction = exceptionHasCorrectiveAction.getCorrectiveAction();
            if (correctiveAction != null) {
                correctiveAction = em.getReference(correctiveAction.getClass(), correctiveAction.getId());
                exceptionHasCorrectiveAction.setCorrectiveAction(correctiveAction);
            }
            em.persist(exceptionHasCorrectiveAction);
            if (correctiveAction != null) {
                correctiveAction.getExceptionHasCorrectiveActionList().add(exceptionHasCorrectiveAction);
                correctiveAction = em.merge(correctiveAction);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findExceptionHasCorrectiveAction(exceptionHasCorrectiveAction.getExceptionHasCorrectiveActionPK()) != null) {
                throw new PreexistingEntityException("ExceptionHasCorrectiveAction " + exceptionHasCorrectiveAction + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ExceptionHasCorrectiveAction exceptionHasCorrectiveAction) throws NonexistentEntityException, Exception {
        exceptionHasCorrectiveAction.getExceptionHasCorrectiveActionPK().setCorrectiveActionId(exceptionHasCorrectiveAction.getCorrectiveAction().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExceptionHasCorrectiveAction persistentExceptionHasCorrectiveAction = em.find(ExceptionHasCorrectiveAction.class, exceptionHasCorrectiveAction.getExceptionHasCorrectiveActionPK());
            CorrectiveAction correctiveActionOld = persistentExceptionHasCorrectiveAction.getCorrectiveAction();
            CorrectiveAction correctiveActionNew = exceptionHasCorrectiveAction.getCorrectiveAction();
            if (correctiveActionNew != null) {
                correctiveActionNew = em.getReference(correctiveActionNew.getClass(), correctiveActionNew.getId());
                exceptionHasCorrectiveAction.setCorrectiveAction(correctiveActionNew);
            }
            exceptionHasCorrectiveAction = em.merge(exceptionHasCorrectiveAction);
            if (correctiveActionOld != null && !correctiveActionOld.equals(correctiveActionNew)) {
                correctiveActionOld.getExceptionHasCorrectiveActionList().remove(exceptionHasCorrectiveAction);
                correctiveActionOld = em.merge(correctiveActionOld);
            }
            if (correctiveActionNew != null && !correctiveActionNew.equals(correctiveActionOld)) {
                correctiveActionNew.getExceptionHasCorrectiveActionList().add(exceptionHasCorrectiveAction);
                correctiveActionNew = em.merge(correctiveActionNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ExceptionHasCorrectiveActionPK id = exceptionHasCorrectiveAction.getExceptionHasCorrectiveActionPK();
                if (findExceptionHasCorrectiveAction(id) == null) {
                    throw new NonexistentEntityException("The exceptionHasCorrectiveAction with id " + id + " no longer exists.");
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

    public void destroy(ExceptionHasCorrectiveActionPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExceptionHasCorrectiveAction exceptionHasCorrectiveAction;
            try {
                exceptionHasCorrectiveAction = em.getReference(ExceptionHasCorrectiveAction.class, id);
                exceptionHasCorrectiveAction.getExceptionHasCorrectiveActionPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The exceptionHasCorrectiveAction with id " + id + " no longer exists.", enfe);
            }
            CorrectiveAction correctiveAction = exceptionHasCorrectiveAction.getCorrectiveAction();
            if (correctiveAction != null) {
                correctiveAction.getExceptionHasCorrectiveActionList().remove(exceptionHasCorrectiveAction);
                correctiveAction = em.merge(correctiveAction);
            }
            em.remove(exceptionHasCorrectiveAction);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ExceptionHasCorrectiveAction> findExceptionHasCorrectiveActionEntities() {
        return findExceptionHasCorrectiveActionEntities(true, -1, -1);
    }

    public List<ExceptionHasCorrectiveAction> findExceptionHasCorrectiveActionEntities(int maxResults, int firstResult) {
        return findExceptionHasCorrectiveActionEntities(false, maxResults, firstResult);
    }

    private List<ExceptionHasCorrectiveAction> findExceptionHasCorrectiveActionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ExceptionHasCorrectiveAction.class));
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

    public ExceptionHasCorrectiveAction findExceptionHasCorrectiveAction(ExceptionHasCorrectiveActionPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ExceptionHasCorrectiveAction.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getExceptionHasCorrectiveActionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ExceptionHasCorrectiveAction> rt = cq.from(ExceptionHasCorrectiveAction.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
