/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.VmUserT;
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
public class VmUserTJpaController implements Serializable {

    public VmUserTJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(VmUserT vmUserT) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(vmUserT);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findVmUserT(vmUserT.getRecordId()) != null) {
                throw new PreexistingEntityException("VmUserT " + vmUserT + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(VmUserT vmUserT) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            vmUserT = em.merge(vmUserT);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = vmUserT.getRecordId();
                if (findVmUserT(id) == null) {
                    throw new NonexistentEntityException("The vmUserT with id " + id + " no longer exists.");
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
            VmUserT vmUserT;
            try {
                vmUserT = em.getReference(VmUserT.class, id);
                vmUserT.getRecordId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vmUserT with id " + id + " no longer exists.", enfe);
            }
            em.remove(vmUserT);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<VmUserT> findVmUserTEntities() {
        return findVmUserTEntities(true, -1, -1);
    }

    public List<VmUserT> findVmUserTEntities(int maxResults, int firstResult) {
        return findVmUserTEntities(false, maxResults, firstResult);
    }

    private List<VmUserT> findVmUserTEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(VmUserT.class));
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

    public VmUserT findVmUserT(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(VmUserT.class, id);
        } finally {
            em.close();
        }
    }

    public int getVmUserTCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<VmUserT> rt = cq.from(VmUserT.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}