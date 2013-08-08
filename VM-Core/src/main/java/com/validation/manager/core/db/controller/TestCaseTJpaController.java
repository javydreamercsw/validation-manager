/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.TestCaseT;
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
public class TestCaseTJpaController implements Serializable {

    public TestCaseTJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TestCaseT testCaseT) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(testCaseT);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTestCaseT(testCaseT.getRecordId()) != null) {
                throw new PreexistingEntityException("TestCaseT " + testCaseT + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TestCaseT testCaseT) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            testCaseT = em.merge(testCaseT);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = testCaseT.getRecordId();
                if (findTestCaseT(id) == null) {
                    throw new NonexistentEntityException("The testCaseT with id " + id + " no longer exists.");
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
            TestCaseT testCaseT;
            try {
                testCaseT = em.getReference(TestCaseT.class, id);
                testCaseT.getRecordId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The testCaseT with id " + id + " no longer exists.", enfe);
            }
            em.remove(testCaseT);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TestCaseT> findTestCaseTEntities() {
        return findTestCaseTEntities(true, -1, -1);
    }

    public List<TestCaseT> findTestCaseTEntities(int maxResults, int firstResult) {
        return findTestCaseTEntities(false, maxResults, firstResult);
    }

    private List<TestCaseT> findTestCaseTEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TestCaseT.class));
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

    public TestCaseT findTestCaseT(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TestCaseT.class, id);
        } finally {
            em.close();
        }
    }

    public int getTestCaseTCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TestCaseT> rt = cq.from(TestCaseT.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
