/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.Test;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCasePK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.fmea.RiskControl;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
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
        if (testCase.getStepList() == null) {
            testCase.setStepList(new ArrayList<Step>());
        }
        if (testCase.getRiskControlList() == null) {
            testCase.setRiskControlList(new ArrayList<RiskControl>());
        }
        testCase.getTestCasePK().setTestId(testCase.getTest().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmUser authorId = testCase.getAuthorId();
            if (authorId != null) {
                authorId = em.getReference(authorId.getClass(), authorId.getId());
                testCase.setAuthorId(authorId);
            }
            Test test = testCase.getTest();
            if (test != null) {
                test = em.getReference(test.getClass(), test.getId());
                testCase.setTest(test);
            }
            List<Step> attachedStepList = new ArrayList<Step>();
            for (Step stepListStepToAttach : testCase.getStepList()) {
                stepListStepToAttach = em.getReference(stepListStepToAttach.getClass(), stepListStepToAttach.getStepPK());
                attachedStepList.add(stepListStepToAttach);
            }
            testCase.setStepList(attachedStepList);
            List<RiskControl> attachedRiskControlList = new ArrayList<RiskControl>();
            for (RiskControl riskControlListRiskControlToAttach : testCase.getRiskControlList()) {
                riskControlListRiskControlToAttach = em.getReference(riskControlListRiskControlToAttach.getClass(), riskControlListRiskControlToAttach.getRiskControlPK());
                attachedRiskControlList.add(riskControlListRiskControlToAttach);
            }
            testCase.setRiskControlList(attachedRiskControlList);
            em.persist(testCase);
            if (authorId != null) {
                authorId.getTestCaseList().add(testCase);
                authorId = em.merge(authorId);
            }
            if (test != null) {
                test.getTestCaseList().add(testCase);
                test = em.merge(test);
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
            for (RiskControl riskControlListRiskControl : testCase.getRiskControlList()) {
                riskControlListRiskControl.getTestCaseList().add(testCase);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTestCase(testCase.getTestCasePK()) != null) {
                throw new PreexistingEntityException("TestCase " + testCase + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TestCase testCase) throws IllegalOrphanException, NonexistentEntityException, Exception {
        testCase.getTestCasePK().setTestId(testCase.getTest().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestCase persistentTestCase = em.find(TestCase.class, testCase.getTestCasePK());
            VmUser authorIdOld = persistentTestCase.getAuthorId();
            VmUser authorIdNew = testCase.getAuthorId();
            Test testOld = persistentTestCase.getTest();
            Test testNew = testCase.getTest();
            List<Step> stepListOld = persistentTestCase.getStepList();
            List<Step> stepListNew = testCase.getStepList();
            List<RiskControl> riskControlListOld = persistentTestCase.getRiskControlList();
            List<RiskControl> riskControlListNew = testCase.getRiskControlList();
            List<String> illegalOrphanMessages = null;
            for (Step stepListOldStep : stepListOld) {
                if (!stepListNew.contains(stepListOldStep)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Step " + stepListOldStep + " since its testCase field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (authorIdNew != null) {
                authorIdNew = em.getReference(authorIdNew.getClass(), authorIdNew.getId());
                testCase.setAuthorId(authorIdNew);
            }
            if (testNew != null) {
                testNew = em.getReference(testNew.getClass(), testNew.getId());
                testCase.setTest(testNew);
            }
            List<Step> attachedStepListNew = new ArrayList<Step>();
            for (Step stepListNewStepToAttach : stepListNew) {
                stepListNewStepToAttach = em.getReference(stepListNewStepToAttach.getClass(), stepListNewStepToAttach.getStepPK());
                attachedStepListNew.add(stepListNewStepToAttach);
            }
            stepListNew = attachedStepListNew;
            testCase.setStepList(stepListNew);
            List<RiskControl> attachedRiskControlListNew = new ArrayList<RiskControl>();
            for (RiskControl riskControlListNewRiskControlToAttach : riskControlListNew) {
                riskControlListNewRiskControlToAttach = em.getReference(riskControlListNewRiskControlToAttach.getClass(), riskControlListNewRiskControlToAttach.getRiskControlPK());
                attachedRiskControlListNew.add(riskControlListNewRiskControlToAttach);
            }
            riskControlListNew = attachedRiskControlListNew;
            testCase.setRiskControlList(riskControlListNew);
            testCase = em.merge(testCase);
            if (authorIdOld != null && !authorIdOld.equals(authorIdNew)) {
                authorIdOld.getTestCaseList().remove(testCase);
                authorIdOld = em.merge(authorIdOld);
            }
            if (authorIdNew != null && !authorIdNew.equals(authorIdOld)) {
                authorIdNew.getTestCaseList().add(testCase);
                authorIdNew = em.merge(authorIdNew);
            }
            if (testOld != null && !testOld.equals(testNew)) {
                testOld.getTestCaseList().remove(testCase);
                testOld = em.merge(testOld);
            }
            if (testNew != null && !testNew.equals(testOld)) {
                testNew.getTestCaseList().add(testCase);
                testNew = em.merge(testNew);
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
            for (RiskControl riskControlListOldRiskControl : riskControlListOld) {
                if (!riskControlListNew.contains(riskControlListOldRiskControl)) {
                    riskControlListOldRiskControl.getTestCaseList().remove(testCase);
                    riskControlListOldRiskControl = em.merge(riskControlListOldRiskControl);
                }
            }
            for (RiskControl riskControlListNewRiskControl : riskControlListNew) {
                if (!riskControlListOld.contains(riskControlListNewRiskControl)) {
                    riskControlListNewRiskControl.getTestCaseList().add(testCase);
                    riskControlListNewRiskControl = em.merge(riskControlListNewRiskControl);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                TestCasePK id = testCase.getTestCasePK();
                if (findTestCase(id) == null) {
                    throw new NonexistentEntityException("The testCase with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
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
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The testCase with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Step> stepListOrphanCheck = testCase.getStepList();
            for (Step stepListOrphanCheckStep : stepListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TestCase (" + testCase + ") cannot be destroyed since the Step " + stepListOrphanCheckStep + " in its stepList field has a non-nullable testCase field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            VmUser authorId = testCase.getAuthorId();
            if (authorId != null) {
                authorId.getTestCaseList().remove(testCase);
                authorId = em.merge(authorId);
            }
            Test test = testCase.getTest();
            if (test != null) {
                test.getTestCaseList().remove(testCase);
                test = em.merge(test);
            }
            List<RiskControl> riskControlList = testCase.getRiskControlList();
            for (RiskControl riskControlListRiskControl : riskControlList) {
                riskControlListRiskControl.getTestCaseList().remove(testCase);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
            }
            em.remove(testCase);
            em.getTransaction().commit();
        } finally {
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
        } finally {
            em.close();
        }
    }

    public TestCase findTestCase(TestCasePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TestCase.class, id);
        } finally {
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
        } finally {
            em.close();
        }
    }
    
}
