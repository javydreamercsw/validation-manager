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

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseType;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TestCaseTypeJpaController implements Serializable {

    public TestCaseTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TestCaseType testCaseType) throws PreexistingEntityException, Exception {
        if (testCaseType.getTestCaseList() == null) {
            testCaseType.setTestCaseList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<TestCase> attachedTestCaseList = new ArrayList<>();
            for (TestCase testCaseListTestCaseToAttach : testCaseType.getTestCaseList()) {
                testCaseListTestCaseToAttach = em.getReference(testCaseListTestCaseToAttach.getClass(), testCaseListTestCaseToAttach.getTestCasePK());
                attachedTestCaseList.add(testCaseListTestCaseToAttach);
            }
            testCaseType.setTestCaseList(attachedTestCaseList);
            em.persist(testCaseType);
            for (TestCase testCaseListTestCase : testCaseType.getTestCaseList()) {
                TestCaseType oldTestCaseTypeOfTestCaseListTestCase = testCaseListTestCase.getTestCaseType();
                testCaseListTestCase.setTestCaseType(testCaseType);
                testCaseListTestCase = em.merge(testCaseListTestCase);
                if (oldTestCaseTypeOfTestCaseListTestCase != null) {
                    oldTestCaseTypeOfTestCaseListTestCase.getTestCaseList().remove(testCaseListTestCase);
                    oldTestCaseTypeOfTestCaseListTestCase = em.merge(oldTestCaseTypeOfTestCaseListTestCase);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findTestCaseType(testCaseType.getId()) != null) {
                throw new PreexistingEntityException("TestCaseType " + testCaseType + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TestCaseType testCaseType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestCaseType persistentTestCaseType = em.find(TestCaseType.class, testCaseType.getId());
            List<TestCase> testCaseListOld = persistentTestCaseType.getTestCaseList();
            List<TestCase> testCaseListNew = testCaseType.getTestCaseList();
            List<String> illegalOrphanMessages = null;
            for (TestCase testCaseListOldTestCase : testCaseListOld) {
                if (!testCaseListNew.contains(testCaseListOldTestCase)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain TestCase " + testCaseListOldTestCase + " since its testCaseType field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<TestCase> attachedTestCaseListNew = new ArrayList<>();
            for (TestCase testCaseListNewTestCaseToAttach : testCaseListNew) {
                testCaseListNewTestCaseToAttach = em.getReference(testCaseListNewTestCaseToAttach.getClass(), testCaseListNewTestCaseToAttach.getTestCasePK());
                attachedTestCaseListNew.add(testCaseListNewTestCaseToAttach);
            }
            testCaseListNew = attachedTestCaseListNew;
            testCaseType.setTestCaseList(testCaseListNew);
            testCaseType = em.merge(testCaseType);
            for (TestCase testCaseListNewTestCase : testCaseListNew) {
                if (!testCaseListOld.contains(testCaseListNewTestCase)) {
                    TestCaseType oldTestCaseTypeOfTestCaseListNewTestCase = testCaseListNewTestCase.getTestCaseType();
                    testCaseListNewTestCase.setTestCaseType(testCaseType);
                    testCaseListNewTestCase = em.merge(testCaseListNewTestCase);
                    if (oldTestCaseTypeOfTestCaseListNewTestCase != null && !oldTestCaseTypeOfTestCaseListNewTestCase.equals(testCaseType)) {
                        oldTestCaseTypeOfTestCaseListNewTestCase.getTestCaseList().remove(testCaseListNewTestCase);
                        oldTestCaseTypeOfTestCaseListNewTestCase = em.merge(oldTestCaseTypeOfTestCaseListNewTestCase);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = testCaseType.getId();
                if (findTestCaseType(id) == null) {
                    throw new NonexistentEntityException("The testCaseType with id " + id + " no longer exists.");
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

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestCaseType testCaseType;
            try {
                testCaseType = em.getReference(TestCaseType.class, id);
                testCaseType.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The testCaseType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<TestCase> testCaseListOrphanCheck = testCaseType.getTestCaseList();
            for (TestCase testCaseListOrphanCheckTestCase : testCaseListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TestCaseType (" + testCaseType + ") cannot be destroyed since the TestCase " + testCaseListOrphanCheckTestCase + " in its testCaseList field has a non-nullable testCaseType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(testCaseType);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TestCaseType> findTestCaseTypeEntities() {
        return findTestCaseTypeEntities(true, -1, -1);
    }

    public List<TestCaseType> findTestCaseTypeEntities(int maxResults, int firstResult) {
        return findTestCaseTypeEntities(false, maxResults, firstResult);
    }

    private List<TestCaseType> findTestCaseTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TestCaseType.class));
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

    public TestCaseType findTestCaseType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TestCaseType.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getTestCaseTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TestCaseType> rt = cq.from(TestCaseType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
