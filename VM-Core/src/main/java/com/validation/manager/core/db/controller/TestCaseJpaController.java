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
import com.validation.manager.core.db.TestCaseType;
import com.validation.manager.core.db.TestPlan;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.RiskControlHasTestCase;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCasePK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TestCaseJpaController implements Serializable {

    public TestCaseJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TestCase testCase) throws PreexistingEntityException, Exception {
        if (testCase.getTestCasePK() == null) {
            testCase.setTestCasePK(new TestCasePK());
        }
        if (testCase.getTestPlanList() == null) {
            testCase.setTestPlanList(new ArrayList<>());
        }
        if (testCase.getRiskControlHasTestCaseList() == null) {
            testCase.setRiskControlHasTestCaseList(new ArrayList<>());
        }
        if (testCase.getStepList() == null) {
            testCase.setStepList(new ArrayList<>());
        }
        testCase.getTestCasePK().setTestCaseTypeId(testCase.getTestCaseType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestCaseType testCaseType = testCase.getTestCaseType();
            if (testCaseType != null) {
                testCaseType = em.getReference(testCaseType.getClass(), testCaseType.getId());
                testCase.setTestCaseType(testCaseType);
            }
            List<TestPlan> attachedTestPlanList = new ArrayList<>();
            for (TestPlan testPlanListTestPlanToAttach : testCase.getTestPlanList()) {
                testPlanListTestPlanToAttach = em.getReference(testPlanListTestPlanToAttach.getClass(), testPlanListTestPlanToAttach.getTestPlanPK());
                attachedTestPlanList.add(testPlanListTestPlanToAttach);
            }
            testCase.setTestPlanList(attachedTestPlanList);
            List<RiskControlHasTestCase> attachedRiskControlHasTestCaseList = new ArrayList<>();
            for (RiskControlHasTestCase riskControlHasTestCaseListRiskControlHasTestCaseToAttach : testCase.getRiskControlHasTestCaseList()) {
                riskControlHasTestCaseListRiskControlHasTestCaseToAttach = em.getReference(riskControlHasTestCaseListRiskControlHasTestCaseToAttach.getClass(), riskControlHasTestCaseListRiskControlHasTestCaseToAttach.getRiskControlHasTestCasePK());
                attachedRiskControlHasTestCaseList.add(riskControlHasTestCaseListRiskControlHasTestCaseToAttach);
            }
            testCase.setRiskControlHasTestCaseList(attachedRiskControlHasTestCaseList);
            List<Step> attachedStepList = new ArrayList<>();
            for (Step stepListStepToAttach : testCase.getStepList()) {
                stepListStepToAttach = em.getReference(stepListStepToAttach.getClass(), stepListStepToAttach.getStepPK());
                attachedStepList.add(stepListStepToAttach);
            }
            testCase.setStepList(attachedStepList);
            em.persist(testCase);
            if (testCaseType != null) {
                testCaseType.getTestCaseList().add(testCase);
                testCaseType = em.merge(testCaseType);
            }
            for (TestPlan testPlanListTestPlan : testCase.getTestPlanList()) {
                testPlanListTestPlan.getTestCaseList().add(testCase);
                testPlanListTestPlan = em.merge(testPlanListTestPlan);
            }
            for (RiskControlHasTestCase riskControlHasTestCaseListRiskControlHasTestCase : testCase.getRiskControlHasTestCaseList()) {
                TestCase oldTestCaseOfRiskControlHasTestCaseListRiskControlHasTestCase = riskControlHasTestCaseListRiskControlHasTestCase.getTestCase();
                riskControlHasTestCaseListRiskControlHasTestCase.setTestCase(testCase);
                riskControlHasTestCaseListRiskControlHasTestCase = em.merge(riskControlHasTestCaseListRiskControlHasTestCase);
                if (oldTestCaseOfRiskControlHasTestCaseListRiskControlHasTestCase != null) {
                    oldTestCaseOfRiskControlHasTestCaseListRiskControlHasTestCase.getRiskControlHasTestCaseList().remove(riskControlHasTestCaseListRiskControlHasTestCase);
                    oldTestCaseOfRiskControlHasTestCaseListRiskControlHasTestCase = em.merge(oldTestCaseOfRiskControlHasTestCaseListRiskControlHasTestCase);
                }
            }
            for (Step stepListStep : testCase.getStepList()) {
                TestCase oldTestCaseOfStepListStep = stepListStep.getTestCase();
                stepListStep.setTestCase(testCase);
                stepListStep = em.merge(stepListStep);
                if (oldTestCaseOfStepListStep != null) {
                    oldTestCaseOfStepListStep.getStepList().remove(stepListStep);
                    oldTestCaseOfStepListStep = em.merge(oldTestCaseOfStepListStep);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findTestCase(testCase.getTestCasePK()) != null) {
                throw new PreexistingEntityException("TestCase " + testCase + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TestCase testCase) throws IllegalOrphanException, NonexistentEntityException, Exception {
        testCase.getTestCasePK().setTestCaseTypeId(testCase.getTestCaseType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestCase persistentTestCase = em.find(TestCase.class, testCase.getTestCasePK());
            TestCaseType testCaseTypeOld = persistentTestCase.getTestCaseType();
            TestCaseType testCaseTypeNew = testCase.getTestCaseType();
            List<TestPlan> testPlanListOld = persistentTestCase.getTestPlanList();
            List<TestPlan> testPlanListNew = testCase.getTestPlanList();
            List<RiskControlHasTestCase> riskControlHasTestCaseListOld = persistentTestCase.getRiskControlHasTestCaseList();
            List<RiskControlHasTestCase> riskControlHasTestCaseListNew = testCase.getRiskControlHasTestCaseList();
            List<Step> stepListOld = persistentTestCase.getStepList();
            List<Step> stepListNew = testCase.getStepList();
            List<String> illegalOrphanMessages = null;
            for (RiskControlHasTestCase riskControlHasTestCaseListOldRiskControlHasTestCase : riskControlHasTestCaseListOld) {
                if (!riskControlHasTestCaseListNew.contains(riskControlHasTestCaseListOldRiskControlHasTestCase)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RiskControlHasTestCase " + riskControlHasTestCaseListOldRiskControlHasTestCase + " since its testCase field is not nullable.");
                }
            }
            for (Step stepListOldStep : stepListOld) {
                if (!stepListNew.contains(stepListOldStep)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Step " + stepListOldStep + " since its testCase field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (testCaseTypeNew != null) {
                testCaseTypeNew = em.getReference(testCaseTypeNew.getClass(), testCaseTypeNew.getId());
                testCase.setTestCaseType(testCaseTypeNew);
            }
            List<TestPlan> attachedTestPlanListNew = new ArrayList<>();
            for (TestPlan testPlanListNewTestPlanToAttach : testPlanListNew) {
                testPlanListNewTestPlanToAttach = em.getReference(testPlanListNewTestPlanToAttach.getClass(), testPlanListNewTestPlanToAttach.getTestPlanPK());
                attachedTestPlanListNew.add(testPlanListNewTestPlanToAttach);
            }
            testPlanListNew = attachedTestPlanListNew;
            testCase.setTestPlanList(testPlanListNew);
            List<RiskControlHasTestCase> attachedRiskControlHasTestCaseListNew = new ArrayList<>();
            for (RiskControlHasTestCase riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach : riskControlHasTestCaseListNew) {
                riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach = em.getReference(riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach.getClass(), riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach.getRiskControlHasTestCasePK());
                attachedRiskControlHasTestCaseListNew.add(riskControlHasTestCaseListNewRiskControlHasTestCaseToAttach);
            }
            riskControlHasTestCaseListNew = attachedRiskControlHasTestCaseListNew;
            testCase.setRiskControlHasTestCaseList(riskControlHasTestCaseListNew);
            List<Step> attachedStepListNew = new ArrayList<>();
            for (Step stepListNewStepToAttach : stepListNew) {
                stepListNewStepToAttach = em.getReference(stepListNewStepToAttach.getClass(), stepListNewStepToAttach.getStepPK());
                attachedStepListNew.add(stepListNewStepToAttach);
            }
            stepListNew = attachedStepListNew;
            testCase.setStepList(stepListNew);
            testCase = em.merge(testCase);
            if (testCaseTypeOld != null && !testCaseTypeOld.equals(testCaseTypeNew)) {
                testCaseTypeOld.getTestCaseList().remove(testCase);
                testCaseTypeOld = em.merge(testCaseTypeOld);
            }
            if (testCaseTypeNew != null && !testCaseTypeNew.equals(testCaseTypeOld)) {
                testCaseTypeNew.getTestCaseList().add(testCase);
                testCaseTypeNew = em.merge(testCaseTypeNew);
            }
            for (TestPlan testPlanListOldTestPlan : testPlanListOld) {
                if (!testPlanListNew.contains(testPlanListOldTestPlan)) {
                    testPlanListOldTestPlan.getTestCaseList().remove(testCase);
                    testPlanListOldTestPlan = em.merge(testPlanListOldTestPlan);
                }
            }
            for (TestPlan testPlanListNewTestPlan : testPlanListNew) {
                if (!testPlanListOld.contains(testPlanListNewTestPlan)) {
                    testPlanListNewTestPlan.getTestCaseList().add(testCase);
                    testPlanListNewTestPlan = em.merge(testPlanListNewTestPlan);
                }
            }
            for (RiskControlHasTestCase riskControlHasTestCaseListNewRiskControlHasTestCase : riskControlHasTestCaseListNew) {
                if (!riskControlHasTestCaseListOld.contains(riskControlHasTestCaseListNewRiskControlHasTestCase)) {
                    TestCase oldTestCaseOfRiskControlHasTestCaseListNewRiskControlHasTestCase = riskControlHasTestCaseListNewRiskControlHasTestCase.getTestCase();
                    riskControlHasTestCaseListNewRiskControlHasTestCase.setTestCase(testCase);
                    riskControlHasTestCaseListNewRiskControlHasTestCase = em.merge(riskControlHasTestCaseListNewRiskControlHasTestCase);
                    if (oldTestCaseOfRiskControlHasTestCaseListNewRiskControlHasTestCase != null && !oldTestCaseOfRiskControlHasTestCaseListNewRiskControlHasTestCase.equals(testCase)) {
                        oldTestCaseOfRiskControlHasTestCaseListNewRiskControlHasTestCase.getRiskControlHasTestCaseList().remove(riskControlHasTestCaseListNewRiskControlHasTestCase);
                        oldTestCaseOfRiskControlHasTestCaseListNewRiskControlHasTestCase = em.merge(oldTestCaseOfRiskControlHasTestCaseListNewRiskControlHasTestCase);
                    }
                }
            }
            for (Step stepListNewStep : stepListNew) {
                if (!stepListOld.contains(stepListNewStep)) {
                    TestCase oldTestCaseOfStepListNewStep = stepListNewStep.getTestCase();
                    stepListNewStep.setTestCase(testCase);
                    stepListNewStep = em.merge(stepListNewStep);
                    if (oldTestCaseOfStepListNewStep != null && !oldTestCaseOfStepListNewStep.equals(testCase)) {
                        oldTestCaseOfStepListNewStep.getStepList().remove(stepListNewStep);
                        oldTestCaseOfStepListNewStep = em.merge(oldTestCaseOfStepListNewStep);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                TestCasePK id = testCase.getTestCasePK();
                if (findTestCase(id) == null) {
                    throw new NonexistentEntityException("The testCase with id " + id + " no longer exists.");
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

    public void destroy(TestCasePK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestCase testCase;
            try {
                testCase = em.getReference(TestCase.class, id);
                testCase.getTestCasePK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The testCase with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RiskControlHasTestCase> riskControlHasTestCaseListOrphanCheck = testCase.getRiskControlHasTestCaseList();
            for (RiskControlHasTestCase riskControlHasTestCaseListOrphanCheckRiskControlHasTestCase : riskControlHasTestCaseListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TestCase (" + testCase + ") cannot be destroyed since the RiskControlHasTestCase " + riskControlHasTestCaseListOrphanCheckRiskControlHasTestCase + " in its riskControlHasTestCaseList field has a non-nullable testCase field.");
            }
            List<Step> stepListOrphanCheck = testCase.getStepList();
            for (Step stepListOrphanCheckStep : stepListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TestCase (" + testCase + ") cannot be destroyed since the Step " + stepListOrphanCheckStep + " in its stepList field has a non-nullable testCase field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            TestCaseType testCaseType = testCase.getTestCaseType();
            if (testCaseType != null) {
                testCaseType.getTestCaseList().remove(testCase);
                testCaseType = em.merge(testCaseType);
            }
            List<TestPlan> testPlanList = testCase.getTestPlanList();
            for (TestPlan testPlanListTestPlan : testPlanList) {
                testPlanListTestPlan.getTestCaseList().remove(testCase);
                testPlanListTestPlan = em.merge(testPlanListTestPlan);
            }
            em.remove(testCase);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TestCase> findTestCaseEntities() {
        return findTestCaseEntities(true, -1, -1);
    }

    public List<TestCase> findTestCaseEntities(int maxResults, int firstResult) {
        return findTestCaseEntities(false, maxResults, firstResult);
    }

    private List<TestCase> findTestCaseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TestCase.class));
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

    public TestCase findTestCase(TestCasePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TestCase.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getTestCaseCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TestCase> rt = cq.from(TestCase.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
