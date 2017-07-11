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

import com.validation.manager.core.db.ExceptionHasInvestigation;
import com.validation.manager.core.db.ExceptionHasInvestigationPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.Investigation;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ExceptionHasInvestigationJpaController implements Serializable {

    public ExceptionHasInvestigationJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ExceptionHasInvestigation exceptionHasInvestigation) throws PreexistingEntityException, Exception {
        if (exceptionHasInvestigation.getExceptionHasInvestigationPK() == null) {
            exceptionHasInvestigation.setExceptionHasInvestigationPK(new ExceptionHasInvestigationPK());
        }
        exceptionHasInvestigation.getExceptionHasInvestigationPK().setInvestigationId(exceptionHasInvestigation.getInvestigation().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Investigation investigation = exceptionHasInvestigation.getInvestigation();
            if (investigation != null) {
                investigation = em.getReference(investigation.getClass(), investigation.getId());
                exceptionHasInvestigation.setInvestigation(investigation);
            }
            em.persist(exceptionHasInvestigation);
            if (investigation != null) {
                investigation.getExceptionHasInvestigationList().add(exceptionHasInvestigation);
                investigation = em.merge(investigation);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findExceptionHasInvestigation(exceptionHasInvestigation.getExceptionHasInvestigationPK()) != null) {
                throw new PreexistingEntityException("ExceptionHasInvestigation " + exceptionHasInvestigation + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ExceptionHasInvestigation exceptionHasInvestigation) throws NonexistentEntityException, Exception {
        exceptionHasInvestigation.getExceptionHasInvestigationPK().setInvestigationId(exceptionHasInvestigation.getInvestigation().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExceptionHasInvestigation persistentExceptionHasInvestigation = em.find(ExceptionHasInvestigation.class, exceptionHasInvestigation.getExceptionHasInvestigationPK());
            Investigation investigationOld = persistentExceptionHasInvestigation.getInvestigation();
            Investigation investigationNew = exceptionHasInvestigation.getInvestigation();
            if (investigationNew != null) {
                investigationNew = em.getReference(investigationNew.getClass(), investigationNew.getId());
                exceptionHasInvestigation.setInvestigation(investigationNew);
            }
            exceptionHasInvestigation = em.merge(exceptionHasInvestigation);
            if (investigationOld != null && !investigationOld.equals(investigationNew)) {
                investigationOld.getExceptionHasInvestigationList().remove(exceptionHasInvestigation);
                investigationOld = em.merge(investigationOld);
            }
            if (investigationNew != null && !investigationNew.equals(investigationOld)) {
                investigationNew.getExceptionHasInvestigationList().add(exceptionHasInvestigation);
                investigationNew = em.merge(investigationNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ExceptionHasInvestigationPK id = exceptionHasInvestigation.getExceptionHasInvestigationPK();
                if (findExceptionHasInvestigation(id) == null) {
                    throw new NonexistentEntityException("The exceptionHasInvestigation with id " + id + " no longer exists.");
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

    public void destroy(ExceptionHasInvestigationPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExceptionHasInvestigation exceptionHasInvestigation;
            try {
                exceptionHasInvestigation = em.getReference(ExceptionHasInvestigation.class, id);
                exceptionHasInvestigation.getExceptionHasInvestigationPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The exceptionHasInvestigation with id " + id + " no longer exists.", enfe);
            }
            Investigation investigation = exceptionHasInvestigation.getInvestigation();
            if (investigation != null) {
                investigation.getExceptionHasInvestigationList().remove(exceptionHasInvestigation);
                investigation = em.merge(investigation);
            }
            em.remove(exceptionHasInvestigation);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ExceptionHasInvestigation> findExceptionHasInvestigationEntities() {
        return findExceptionHasInvestigationEntities(true, -1, -1);
    }

    public List<ExceptionHasInvestigation> findExceptionHasInvestigationEntities(int maxResults, int firstResult) {
        return findExceptionHasInvestigationEntities(false, maxResults, firstResult);
    }

    private List<ExceptionHasInvestigation> findExceptionHasInvestigationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ExceptionHasInvestigation.class));
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

    public ExceptionHasInvestigation findExceptionHasInvestigation(ExceptionHasInvestigationPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ExceptionHasInvestigation.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getExceptionHasInvestigationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ExceptionHasInvestigation> rt = cq.from(ExceptionHasInvestigation.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
