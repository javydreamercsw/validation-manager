/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.TestProjectT;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestProjectTJpaController implements Serializable {

    public TestProjectTJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TestProjectT testProjectT) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(testProjectT);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTestProjectT(testProjectT.getRecordId()) != null) {
                throw new PreexistingEntityException("TestProjectT " + testProjectT + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TestProjectT testProjectT) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            testProjectT = em.merge(testProjectT);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = testProjectT.getRecordId();
                if (findTestProjectT(id) == null) {
                    throw new NonexistentEntityException("The testProjectT with id " + id + " no longer exists.");
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
            TestProjectT testProjectT;
            try {
                testProjectT = em.getReference(TestProjectT.class, id);
                testProjectT.getRecordId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The testProjectT with id " + id + " no longer exists.", enfe);
            }
            em.remove(testProjectT);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TestProjectT> findTestProjectTEntities() {
        return findTestProjectTEntities(true, -1, -1);
    }

    public List<TestProjectT> findTestProjectTEntities(int maxResults, int firstResult) {
        return findTestProjectTEntities(false, maxResults, firstResult);
    }

    private List<TestProjectT> findTestProjectTEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TestProjectT.class));
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

    public TestProjectT findTestProjectT(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TestProjectT.class, id);
        } finally {
            em.close();
        }
    }

    public int getTestProjectTCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TestProjectT> rt = cq.from(TestProjectT.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
