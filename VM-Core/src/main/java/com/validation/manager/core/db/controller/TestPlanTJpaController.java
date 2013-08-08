/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.TestPlanT;
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
public class TestPlanTJpaController implements Serializable {

    public TestPlanTJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TestPlanT testPlanT) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(testPlanT);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTestPlanT(testPlanT.getRecordId()) != null) {
                throw new PreexistingEntityException("TestPlanT " + testPlanT + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TestPlanT testPlanT) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            testPlanT = em.merge(testPlanT);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = testPlanT.getRecordId();
                if (findTestPlanT(id) == null) {
                    throw new NonexistentEntityException("The testPlanT with id " + id + " no longer exists.");
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
            TestPlanT testPlanT;
            try {
                testPlanT = em.getReference(TestPlanT.class, id);
                testPlanT.getRecordId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The testPlanT with id " + id + " no longer exists.", enfe);
            }
            em.remove(testPlanT);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TestPlanT> findTestPlanTEntities() {
        return findTestPlanTEntities(true, -1, -1);
    }

    public List<TestPlanT> findTestPlanTEntities(int maxResults, int firstResult) {
        return findTestPlanTEntities(false, maxResults, firstResult);
    }

    private List<TestPlanT> findTestPlanTEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TestPlanT.class));
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

    public TestPlanT findTestPlanT(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TestPlanT.class, id);
        } finally {
            em.close();
        }
    }

    public int getTestPlanTCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TestPlanT> rt = cq.from(TestPlanT.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
