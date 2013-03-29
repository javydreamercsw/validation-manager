/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.Test;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.TestCase;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.TestPlanHasTest;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestJpaController implements Serializable {

    public TestJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Test test) {
        if (test.getTestCaseList() == null) {
            test.setTestCaseList(new ArrayList<TestCase>());
        }
        if (test.getTestPlanHasTestList() == null) {
            test.setTestPlanHasTestList(new ArrayList<TestPlanHasTest>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<TestCase> attachedTestCaseList = new ArrayList<TestCase>();
            for (TestCase testCaseListTestCaseToAttach : test.getTestCaseList()) {
                testCaseListTestCaseToAttach = em.getReference(testCaseListTestCaseToAttach.getClass(), testCaseListTestCaseToAttach.getTestCasePK());
                attachedTestCaseList.add(testCaseListTestCaseToAttach);
            }
            test.setTestCaseList(attachedTestCaseList);
            List<TestPlanHasTest> attachedTestPlanHasTestList = new ArrayList<TestPlanHasTest>();
            for (TestPlanHasTest testPlanHasTestListTestPlanHasTestToAttach : test.getTestPlanHasTestList()) {
                testPlanHasTestListTestPlanHasTestToAttach = em.getReference(testPlanHasTestListTestPlanHasTestToAttach.getClass(), testPlanHasTestListTestPlanHasTestToAttach.getTestPlanHasTestPK());
                attachedTestPlanHasTestList.add(testPlanHasTestListTestPlanHasTestToAttach);
            }
            test.setTestPlanHasTestList(attachedTestPlanHasTestList);
            em.persist(test);
            for (TestCase testCaseListTestCase : test.getTestCaseList()) {
                Test oldTestOfTestCaseListTestCase = testCaseListTestCase.getTest();
                testCaseListTestCase.setTest(test);
                testCaseListTestCase = em.merge(testCaseListTestCase);
                if (oldTestOfTestCaseListTestCase != null) {
                    oldTestOfTestCaseListTestCase.getTestCaseList().remove(testCaseListTestCase);
                    oldTestOfTestCaseListTestCase = em.merge(oldTestOfTestCaseListTestCase);
                }
            }
            for (TestPlanHasTest testPlanHasTestListTestPlanHasTest : test.getTestPlanHasTestList()) {
                Test oldTestOfTestPlanHasTestListTestPlanHasTest = testPlanHasTestListTestPlanHasTest.getTest();
                testPlanHasTestListTestPlanHasTest.setTest(test);
                testPlanHasTestListTestPlanHasTest = em.merge(testPlanHasTestListTestPlanHasTest);
                if (oldTestOfTestPlanHasTestListTestPlanHasTest != null) {
                    oldTestOfTestPlanHasTestListTestPlanHasTest.getTestPlanHasTestList().remove(testPlanHasTestListTestPlanHasTest);
                    oldTestOfTestPlanHasTestListTestPlanHasTest = em.merge(oldTestOfTestPlanHasTestListTestPlanHasTest);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Test test) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Test persistentTest = em.find(Test.class, test.getId());
            List<TestCase> testCaseListOld = persistentTest.getTestCaseList();
            List<TestCase> testCaseListNew = test.getTestCaseList();
            List<TestPlanHasTest> testPlanHasTestListOld = persistentTest.getTestPlanHasTestList();
            List<TestPlanHasTest> testPlanHasTestListNew = test.getTestPlanHasTestList();
            List<String> illegalOrphanMessages = null;
            for (TestCase testCaseListOldTestCase : testCaseListOld) {
                if (!testCaseListNew.contains(testCaseListOldTestCase)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TestCase " + testCaseListOldTestCase + " since its test field is not nullable.");
                }
            }
            for (TestPlanHasTest testPlanHasTestListOldTestPlanHasTest : testPlanHasTestListOld) {
                if (!testPlanHasTestListNew.contains(testPlanHasTestListOldTestPlanHasTest)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TestPlanHasTest " + testPlanHasTestListOldTestPlanHasTest + " since its test field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<TestCase> attachedTestCaseListNew = new ArrayList<TestCase>();
            for (TestCase testCaseListNewTestCaseToAttach : testCaseListNew) {
                testCaseListNewTestCaseToAttach = em.getReference(testCaseListNewTestCaseToAttach.getClass(), testCaseListNewTestCaseToAttach.getTestCasePK());
                attachedTestCaseListNew.add(testCaseListNewTestCaseToAttach);
            }
            testCaseListNew = attachedTestCaseListNew;
            test.setTestCaseList(testCaseListNew);
            List<TestPlanHasTest> attachedTestPlanHasTestListNew = new ArrayList<TestPlanHasTest>();
            for (TestPlanHasTest testPlanHasTestListNewTestPlanHasTestToAttach : testPlanHasTestListNew) {
                testPlanHasTestListNewTestPlanHasTestToAttach = em.getReference(testPlanHasTestListNewTestPlanHasTestToAttach.getClass(), testPlanHasTestListNewTestPlanHasTestToAttach.getTestPlanHasTestPK());
                attachedTestPlanHasTestListNew.add(testPlanHasTestListNewTestPlanHasTestToAttach);
            }
            testPlanHasTestListNew = attachedTestPlanHasTestListNew;
            test.setTestPlanHasTestList(testPlanHasTestListNew);
            test = em.merge(test);
            for (TestCase testCaseListNewTestCase : testCaseListNew) {
                if (!testCaseListOld.contains(testCaseListNewTestCase)) {
                    Test oldTestOfTestCaseListNewTestCase = testCaseListNewTestCase.getTest();
                    testCaseListNewTestCase.setTest(test);
                    testCaseListNewTestCase = em.merge(testCaseListNewTestCase);
                    if (oldTestOfTestCaseListNewTestCase != null && !oldTestOfTestCaseListNewTestCase.equals(test)) {
                        oldTestOfTestCaseListNewTestCase.getTestCaseList().remove(testCaseListNewTestCase);
                        oldTestOfTestCaseListNewTestCase = em.merge(oldTestOfTestCaseListNewTestCase);
                    }
                }
            }
            for (TestPlanHasTest testPlanHasTestListNewTestPlanHasTest : testPlanHasTestListNew) {
                if (!testPlanHasTestListOld.contains(testPlanHasTestListNewTestPlanHasTest)) {
                    Test oldTestOfTestPlanHasTestListNewTestPlanHasTest = testPlanHasTestListNewTestPlanHasTest.getTest();
                    testPlanHasTestListNewTestPlanHasTest.setTest(test);
                    testPlanHasTestListNewTestPlanHasTest = em.merge(testPlanHasTestListNewTestPlanHasTest);
                    if (oldTestOfTestPlanHasTestListNewTestPlanHasTest != null && !oldTestOfTestPlanHasTestListNewTestPlanHasTest.equals(test)) {
                        oldTestOfTestPlanHasTestListNewTestPlanHasTest.getTestPlanHasTestList().remove(testPlanHasTestListNewTestPlanHasTest);
                        oldTestOfTestPlanHasTestListNewTestPlanHasTest = em.merge(oldTestOfTestPlanHasTestListNewTestPlanHasTest);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = test.getId();
                if (findTest(id) == null) {
                    throw new NonexistentEntityException("The test with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Test test;
            try {
                test = em.getReference(Test.class, id);
                test.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The test with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<TestCase> testCaseListOrphanCheck = test.getTestCaseList();
            for (TestCase testCaseListOrphanCheckTestCase : testCaseListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Test (" + test + ") cannot be destroyed since the TestCase " + testCaseListOrphanCheckTestCase + " in its testCaseList field has a non-nullable test field.");
            }
            List<TestPlanHasTest> testPlanHasTestListOrphanCheck = test.getTestPlanHasTestList();
            for (TestPlanHasTest testPlanHasTestListOrphanCheckTestPlanHasTest : testPlanHasTestListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Test (" + test + ") cannot be destroyed since the TestPlanHasTest " + testPlanHasTestListOrphanCheckTestPlanHasTest + " in its testPlanHasTestList field has a non-nullable test field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(test);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Test> findTestEntities() {
        return findTestEntities(true, -1, -1);
    }

    public List<Test> findTestEntities(int maxResults, int firstResult) {
        return findTestEntities(false, maxResults, firstResult);
    }

    private List<Test> findTestEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Test.class));
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

    public Test findTest(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Test.class, id);
        } finally {
            em.close();
        }
    }

    public int getTestCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Test> rt = cq.from(Test.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
