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

import com.validation.manager.core.db.ExceptionHasRootCause;
import com.validation.manager.core.db.ExceptionHasRootCausePK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.RootCause;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ExceptionHasRootCauseJpaController implements Serializable {

    public ExceptionHasRootCauseJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ExceptionHasRootCause exceptionHasRootCause) throws PreexistingEntityException, Exception {
        if (exceptionHasRootCause.getExceptionHasRootCausePK() == null) {
            exceptionHasRootCause.setExceptionHasRootCausePK(new ExceptionHasRootCausePK());
        }
        exceptionHasRootCause.getExceptionHasRootCausePK().setRootCauseRootCauseTypeId(exceptionHasRootCause.getRootCause().getRootCausePK().getRootCauseTypeId());
        exceptionHasRootCause.getExceptionHasRootCausePK().setRootCauseId(exceptionHasRootCause.getRootCause().getRootCausePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RootCause rootCause = exceptionHasRootCause.getRootCause();
            if (rootCause != null) {
                rootCause = em.getReference(rootCause.getClass(), rootCause.getRootCausePK());
                exceptionHasRootCause.setRootCause(rootCause);
            }
            em.persist(exceptionHasRootCause);
            if (rootCause != null) {
                rootCause.getExceptionHasRootCauseList().add(exceptionHasRootCause);
                rootCause = em.merge(rootCause);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findExceptionHasRootCause(exceptionHasRootCause.getExceptionHasRootCausePK()) != null) {
                throw new PreexistingEntityException("ExceptionHasRootCause " + exceptionHasRootCause + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ExceptionHasRootCause exceptionHasRootCause) throws NonexistentEntityException, Exception {
        exceptionHasRootCause.getExceptionHasRootCausePK().setRootCauseRootCauseTypeId(exceptionHasRootCause.getRootCause().getRootCausePK().getRootCauseTypeId());
        exceptionHasRootCause.getExceptionHasRootCausePK().setRootCauseId(exceptionHasRootCause.getRootCause().getRootCausePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExceptionHasRootCause persistentExceptionHasRootCause = em.find(ExceptionHasRootCause.class, exceptionHasRootCause.getExceptionHasRootCausePK());
            RootCause rootCauseOld = persistentExceptionHasRootCause.getRootCause();
            RootCause rootCauseNew = exceptionHasRootCause.getRootCause();
            if (rootCauseNew != null) {
                rootCauseNew = em.getReference(rootCauseNew.getClass(), rootCauseNew.getRootCausePK());
                exceptionHasRootCause.setRootCause(rootCauseNew);
            }
            exceptionHasRootCause = em.merge(exceptionHasRootCause);
            if (rootCauseOld != null && !rootCauseOld.equals(rootCauseNew)) {
                rootCauseOld.getExceptionHasRootCauseList().remove(exceptionHasRootCause);
                rootCauseOld = em.merge(rootCauseOld);
            }
            if (rootCauseNew != null && !rootCauseNew.equals(rootCauseOld)) {
                rootCauseNew.getExceptionHasRootCauseList().add(exceptionHasRootCause);
                rootCauseNew = em.merge(rootCauseNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ExceptionHasRootCausePK id = exceptionHasRootCause.getExceptionHasRootCausePK();
                if (findExceptionHasRootCause(id) == null) {
                    throw new NonexistentEntityException("The exceptionHasRootCause with id " + id + " no longer exists.");
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

    public void destroy(ExceptionHasRootCausePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExceptionHasRootCause exceptionHasRootCause;
            try {
                exceptionHasRootCause = em.getReference(ExceptionHasRootCause.class, id);
                exceptionHasRootCause.getExceptionHasRootCausePK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The exceptionHasRootCause with id " + id + " no longer exists.", enfe);
            }
            RootCause rootCause = exceptionHasRootCause.getRootCause();
            if (rootCause != null) {
                rootCause.getExceptionHasRootCauseList().remove(exceptionHasRootCause);
                rootCause = em.merge(rootCause);
            }
            em.remove(exceptionHasRootCause);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ExceptionHasRootCause> findExceptionHasRootCauseEntities() {
        return findExceptionHasRootCauseEntities(true, -1, -1);
    }

    public List<ExceptionHasRootCause> findExceptionHasRootCauseEntities(int maxResults, int firstResult) {
        return findExceptionHasRootCauseEntities(false, maxResults, firstResult);
    }

    private List<ExceptionHasRootCause> findExceptionHasRootCauseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ExceptionHasRootCause.class));
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

    public ExceptionHasRootCause findExceptionHasRootCause(ExceptionHasRootCausePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ExceptionHasRootCause.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getExceptionHasRootCauseCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ExceptionHasRootCause> rt = cq.from(ExceptionHasRootCause.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
