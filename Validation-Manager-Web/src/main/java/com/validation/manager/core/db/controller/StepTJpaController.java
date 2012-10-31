/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.StepT;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class StepTJpaController implements Serializable {

    public StepTJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(StepT stepT) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(stepT);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findStepT(stepT.getRecordId()) != null) {
                throw new PreexistingEntityException("StepT " + stepT + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(StepT stepT) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            stepT = em.merge(stepT);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = stepT.getRecordId();
                if (findStepT(id) == null) {
                    throw new NonexistentEntityException("The stepT with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StepT stepT;
            try {
                stepT = em.getReference(StepT.class, id);
                stepT.getRecordId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The stepT with id " + id + " no longer exists.", enfe);
            }
            em.remove(stepT);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<StepT> findStepTEntities() {
        return findStepTEntities(true, -1, -1);
    }

    public List<StepT> findStepTEntities(int maxResults, int firstResult) {
        return findStepTEntities(false, maxResults, firstResult);
    }

    private List<StepT> findStepTEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(StepT.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public StepT findStepT(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(StepT.class, id);
        } finally {
            em.close();
        }
    }

    public int getStepTCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<StepT> rt = cq.from(StepT.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
