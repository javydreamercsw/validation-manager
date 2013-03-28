/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.Test;
import com.validation.manager.core.db.TestPlanHasTest;
import com.validation.manager.core.db.TestPlanHasTestPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestPlanHasTestJpaController implements Serializable {

    public TestPlanHasTestJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TestPlanHasTest testPlanHasTest) throws PreexistingEntityException, Exception {
        if (testPlanHasTest.getTestPlanHasTestPK() == null) {
            testPlanHasTest.setTestPlanHasTestPK(new TestPlanHasTestPK());
        }
        testPlanHasTest.getTestPlanHasTestPK().setTestPlanId(testPlanHasTest.getTestPlan().getTestPlanPK().getId());
        testPlanHasTest.getTestPlanHasTestPK().setTestPlanTestProjectId(testPlanHasTest.getTestPlan().getTestPlanPK().getTestProjectId());
        testPlanHasTest.getTestPlanHasTestPK().setTestId(testPlanHasTest.getTest().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestPlan testPlan = testPlanHasTest.getTestPlan();
            if (testPlan != null) {
                testPlan = em.getReference(testPlan.getClass(), testPlan.getTestPlanPK());
                testPlanHasTest.setTestPlan(testPlan);
            }
            Test test = testPlanHasTest.getTest();
            if (test != null) {
                test = em.getReference(test.getClass(), test.getId());
                testPlanHasTest.setTest(test);
            }
            em.persist(testPlanHasTest);
            if (testPlan != null) {
                testPlan.getTestPlanHasTestList().add(testPlanHasTest);
                testPlan = em.merge(testPlan);
            }
            if (test != null) {
                test.getTestPlanHasTestList().add(testPlanHasTest);
                test = em.merge(test);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTestPlanHasTest(testPlanHasTest.getTestPlanHasTestPK()) != null) {
                throw new PreexistingEntityException("TestPlanHasTest " + testPlanHasTest + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TestPlanHasTest testPlanHasTest) throws NonexistentEntityException, Exception {
        testPlanHasTest.getTestPlanHasTestPK().setTestPlanId(testPlanHasTest.getTestPlan().getTestPlanPK().getId());
        testPlanHasTest.getTestPlanHasTestPK().setTestPlanTestProjectId(testPlanHasTest.getTestPlan().getTestPlanPK().getTestProjectId());
        testPlanHasTest.getTestPlanHasTestPK().setTestId(testPlanHasTest.getTest().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestPlanHasTest persistentTestPlanHasTest = em.find(TestPlanHasTest.class, testPlanHasTest.getTestPlanHasTestPK());
            TestPlan testPlanOld = persistentTestPlanHasTest.getTestPlan();
            TestPlan testPlanNew = testPlanHasTest.getTestPlan();
            Test testOld = persistentTestPlanHasTest.getTest();
            Test testNew = testPlanHasTest.getTest();
            if (testPlanNew != null) {
                testPlanNew = em.getReference(testPlanNew.getClass(), testPlanNew.getTestPlanPK());
                testPlanHasTest.setTestPlan(testPlanNew);
            }
            if (testNew != null) {
                testNew = em.getReference(testNew.getClass(), testNew.getId());
                testPlanHasTest.setTest(testNew);
            }
            testPlanHasTest = em.merge(testPlanHasTest);
            if (testPlanOld != null && !testPlanOld.equals(testPlanNew)) {
                testPlanOld.getTestPlanHasTestList().remove(testPlanHasTest);
                testPlanOld = em.merge(testPlanOld);
            }
            if (testPlanNew != null && !testPlanNew.equals(testPlanOld)) {
                testPlanNew.getTestPlanHasTestList().add(testPlanHasTest);
                testPlanNew = em.merge(testPlanNew);
            }
            if (testOld != null && !testOld.equals(testNew)) {
                testOld.getTestPlanHasTestList().remove(testPlanHasTest);
                testOld = em.merge(testOld);
            }
            if (testNew != null && !testNew.equals(testOld)) {
                testNew.getTestPlanHasTestList().add(testPlanHasTest);
                testNew = em.merge(testNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                TestPlanHasTestPK id = testPlanHasTest.getTestPlanHasTestPK();
                if (findTestPlanHasTest(id) == null) {
                    throw new NonexistentEntityException("The testPlanHasTest with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(TestPlanHasTestPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestPlanHasTest testPlanHasTest;
            try {
                testPlanHasTest = em.getReference(TestPlanHasTest.class, id);
                testPlanHasTest.getTestPlanHasTestPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The testPlanHasTest with id " + id + " no longer exists.", enfe);
            }
            TestPlan testPlan = testPlanHasTest.getTestPlan();
            if (testPlan != null) {
                testPlan.getTestPlanHasTestList().remove(testPlanHasTest);
                testPlan = em.merge(testPlan);
            }
            Test test = testPlanHasTest.getTest();
            if (test != null) {
                test.getTestPlanHasTestList().remove(testPlanHasTest);
                test = em.merge(test);
            }
            em.remove(testPlanHasTest);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TestPlanHasTest> findTestPlanHasTestEntities() {
        return findTestPlanHasTestEntities(true, -1, -1);
    }

    public List<TestPlanHasTest> findTestPlanHasTestEntities(int maxResults, int firstResult) {
        return findTestPlanHasTestEntities(false, maxResults, firstResult);
    }

    private List<TestPlanHasTest> findTestPlanHasTestEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TestPlanHasTest.class));
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

    public TestPlanHasTest findTestPlanHasTest(TestPlanHasTestPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TestPlanHasTest.class, id);
        } finally {
            em.close();
        }
    }

    public int getTestPlanHasTestCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TestPlanHasTest> rt = cq.from(TestPlanHasTest.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
