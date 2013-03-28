/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import com.validation.manager.core.db.fmea.RootCause;
import com.validation.manager.core.db.fmea.RootCausePK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.fmea.RootCauseType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RootCauseJpaController implements Serializable {

    public RootCauseJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RootCause rootCause) throws PreexistingEntityException, Exception {
        if (rootCause.getRootCausePK() == null) {
            rootCause.setRootCausePK(new RootCausePK());
        }
        rootCause.getRootCausePK().setRootCauseTypeId(rootCause.getRootCauseType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RootCauseType rootCauseType = rootCause.getRootCauseType();
            if (rootCauseType != null) {
                rootCauseType = em.getReference(rootCauseType.getClass(), rootCauseType.getId());
                rootCause.setRootCauseType(rootCauseType);
            }
            em.persist(rootCause);
            if (rootCauseType != null) {
                rootCauseType.getRootCauseList().add(rootCause);
                rootCauseType = em.merge(rootCauseType);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRootCause(rootCause.getRootCausePK()) != null) {
                throw new PreexistingEntityException("RootCause " + rootCause + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RootCause rootCause) throws NonexistentEntityException, Exception {
        rootCause.getRootCausePK().setRootCauseTypeId(rootCause.getRootCauseType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RootCause persistentRootCause = em.find(RootCause.class, rootCause.getRootCausePK());
            RootCauseType rootCauseTypeOld = persistentRootCause.getRootCauseType();
            RootCauseType rootCauseTypeNew = rootCause.getRootCauseType();
            if (rootCauseTypeNew != null) {
                rootCauseTypeNew = em.getReference(rootCauseTypeNew.getClass(), rootCauseTypeNew.getId());
                rootCause.setRootCauseType(rootCauseTypeNew);
            }
            rootCause = em.merge(rootCause);
            if (rootCauseTypeOld != null && !rootCauseTypeOld.equals(rootCauseTypeNew)) {
                rootCauseTypeOld.getRootCauseList().remove(rootCause);
                rootCauseTypeOld = em.merge(rootCauseTypeOld);
            }
            if (rootCauseTypeNew != null && !rootCauseTypeNew.equals(rootCauseTypeOld)) {
                rootCauseTypeNew.getRootCauseList().add(rootCause);
                rootCauseTypeNew = em.merge(rootCauseTypeNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RootCausePK id = rootCause.getRootCausePK();
                if (findRootCause(id) == null) {
                    throw new NonexistentEntityException("The rootCause with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RootCausePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RootCause rootCause;
            try {
                rootCause = em.getReference(RootCause.class, id);
                rootCause.getRootCausePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rootCause with id " + id + " no longer exists.", enfe);
            }
            RootCauseType rootCauseType = rootCause.getRootCauseType();
            if (rootCauseType != null) {
                rootCauseType.getRootCauseList().remove(rootCause);
                rootCauseType = em.merge(rootCauseType);
            }
            em.remove(rootCause);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RootCause> findRootCauseEntities() {
        return findRootCauseEntities(true, -1, -1);
    }

    public List<RootCause> findRootCauseEntities(int maxResults, int firstResult) {
        return findRootCauseEntities(false, maxResults, firstResult);
    }

    private List<RootCause> findRootCauseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RootCause.class));
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

    public RootCause findRootCause(RootCausePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RootCause.class, id);
        } finally {
            em.close();
        }
    }

    public int getRootCauseCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RootCause> rt = cq.from(RootCause.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
