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
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TestCaseExecutionJpaController implements Serializable {

    public TestCaseExecutionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TestCaseExecution testCaseExecution) {
        if (testCaseExecution.getExecutionStepList() == null) {
            testCaseExecution.setExecutionStepList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<ExecutionStep> attachedExecutionStepList = new ArrayList<>();
            for (ExecutionStep executionStepListExecutionStepToAttach : testCaseExecution.getExecutionStepList()) {
                executionStepListExecutionStepToAttach = em.getReference(executionStepListExecutionStepToAttach.getClass(), executionStepListExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepList.add(executionStepListExecutionStepToAttach);
            }
            testCaseExecution.setExecutionStepList(attachedExecutionStepList);
            em.persist(testCaseExecution);
            for (ExecutionStep executionStepListExecutionStep : testCaseExecution.getExecutionStepList()) {
                TestCaseExecution oldTestCaseExecutionOfExecutionStepListExecutionStep = executionStepListExecutionStep.getTestCaseExecution();
                executionStepListExecutionStep.setTestCaseExecution(testCaseExecution);
                executionStepListExecutionStep = em.merge(executionStepListExecutionStep);
                if (oldTestCaseExecutionOfExecutionStepListExecutionStep != null) {
                    oldTestCaseExecutionOfExecutionStepListExecutionStep.getExecutionStepList().remove(executionStepListExecutionStep);
                    oldTestCaseExecutionOfExecutionStepListExecutionStep = em.merge(oldTestCaseExecutionOfExecutionStepListExecutionStep);
                }
            }
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TestCaseExecution testCaseExecution) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestCaseExecution persistentTestCaseExecution = em.find(TestCaseExecution.class, testCaseExecution.getId());
            List<ExecutionStep> executionStepListOld = persistentTestCaseExecution.getExecutionStepList();
            List<ExecutionStep> executionStepListNew = testCaseExecution.getExecutionStepList();
            List<String> illegalOrphanMessages = null;
            for (ExecutionStep executionStepListOldExecutionStep : executionStepListOld) {
                if (!executionStepListNew.contains(executionStepListOldExecutionStep)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain ExecutionStep " + executionStepListOldExecutionStep + " since its testCaseExecution field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<ExecutionStep> attachedExecutionStepListNew = new ArrayList<>();
            for (ExecutionStep executionStepListNewExecutionStepToAttach : executionStepListNew) {
                executionStepListNewExecutionStepToAttach = em.getReference(executionStepListNewExecutionStepToAttach.getClass(), executionStepListNewExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepListNew.add(executionStepListNewExecutionStepToAttach);
            }
            executionStepListNew = attachedExecutionStepListNew;
            testCaseExecution.setExecutionStepList(executionStepListNew);
            testCaseExecution = em.merge(testCaseExecution);
            for (ExecutionStep executionStepListNewExecutionStep : executionStepListNew) {
                if (!executionStepListOld.contains(executionStepListNewExecutionStep)) {
                    TestCaseExecution oldTestCaseExecutionOfExecutionStepListNewExecutionStep = executionStepListNewExecutionStep.getTestCaseExecution();
                    executionStepListNewExecutionStep.setTestCaseExecution(testCaseExecution);
                    executionStepListNewExecutionStep = em.merge(executionStepListNewExecutionStep);
                    if (oldTestCaseExecutionOfExecutionStepListNewExecutionStep != null && !oldTestCaseExecutionOfExecutionStepListNewExecutionStep.equals(testCaseExecution)) {
                        oldTestCaseExecutionOfExecutionStepListNewExecutionStep.getExecutionStepList().remove(executionStepListNewExecutionStep);
                        oldTestCaseExecutionOfExecutionStepListNewExecutionStep = em.merge(oldTestCaseExecutionOfExecutionStepListNewExecutionStep);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = testCaseExecution.getId();
                if (findTestCaseExecution(id) == null) {
                    throw new NonexistentEntityException("The testCaseExecution with id " + id + " no longer exists.");
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
            TestCaseExecution testCaseExecution;
            try {
                testCaseExecution = em.getReference(TestCaseExecution.class, id);
                testCaseExecution.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The testCaseExecution with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ExecutionStep> executionStepListOrphanCheck = testCaseExecution.getExecutionStepList();
            for (ExecutionStep executionStepListOrphanCheckExecutionStep : executionStepListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TestCaseExecution (" + testCaseExecution + ") cannot be destroyed since the ExecutionStep " + executionStepListOrphanCheckExecutionStep + " in its executionStepList field has a non-nullable testCaseExecution field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(testCaseExecution);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TestCaseExecution> findTestCaseExecutionEntities() {
        return findTestCaseExecutionEntities(true, -1, -1);
    }

    public List<TestCaseExecution> findTestCaseExecutionEntities(int maxResults, int firstResult) {
        return findTestCaseExecutionEntities(false, maxResults, firstResult);
    }

    private List<TestCaseExecution> findTestCaseExecutionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TestCaseExecution.class));
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

    public TestCaseExecution findTestCaseExecution(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TestCaseExecution.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getTestCaseExecutionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TestCaseExecution> rt = cq.from(TestCaseExecution.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
