/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.Test;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCasePK;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import com.validation.manager.core.db.fmea.RiskControl;
import java.io.Serializable;
import java.util.ArrayList;
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
        if (testCase.getRiskControlList() == null) {
            testCase.setRiskControlList(new ArrayList<RiskControl>());
        }
        if (testCase.getStepList() == null) {
            testCase.setStepList(new ArrayList<Step>());
        }
        testCase.getTestCasePK().setTestId(testCase.getTest().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Test test = testCase.getTest();
            if (test != null) {
                test = em.getReference(test.getClass(), test.getId());
                testCase.setTest(test);
            }
            VmUser vmUser = testCase.getVmUser();
            if (vmUser != null) {
                vmUser = em.getReference(vmUser.getClass(), vmUser.getId());
                testCase.setVmUser(vmUser);
            }
            List<RiskControl> attachedRiskControlList = new ArrayList<RiskControl>();
            for (RiskControl riskControlListRiskControlToAttach : testCase.getRiskControlList()) {
                riskControlListRiskControlToAttach = em.getReference(riskControlListRiskControlToAttach.getClass(), riskControlListRiskControlToAttach.getRiskControlPK());
                attachedRiskControlList.add(riskControlListRiskControlToAttach);
            }
            testCase.setRiskControlList(attachedRiskControlList);
            List<Step> attachedStepList = new ArrayList<Step>();
            for (Step stepListStepToAttach : testCase.getStepList()) {
                stepListStepToAttach = em.getReference(stepListStepToAttach.getClass(), stepListStepToAttach.getStepPK());
                attachedStepList.add(stepListStepToAttach);
            }
            testCase.setStepList(attachedStepList);
            em.persist(testCase);
            if (test != null) {
                test.getTestCaseList().add(testCase);
                test = em.merge(test);
            }
            if (vmUser != null) {
                vmUser.getTestCaseList().add(testCase);
                vmUser = em.merge(vmUser);
            }
            for (RiskControl riskControlListRiskControl : testCase.getRiskControlList()) {
                riskControlListRiskControl.getTestCaseList().add(testCase);
                riskControlListRiskControl = em.merge(riskControlListRiskControl);
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
            Test testOld = persistentTestCase.getTest();
            Test testNew = testCase.getTest();
            VmUser vmUserOld = persistentTestCase.getVmUser();
            VmUser vmUserNew = testCase.getVmUser();
            List<RiskControl> riskControlListOld = persistentTestCase.getRiskControlList();
            List<RiskControl> riskControlListNew = testCase.getRiskControlList();
            List<Step> stepListOld = persistentTestCase.getStepList();
            List<Step> stepListNew = testCase.getStepList();
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
            if (testNew != null) {
                testNew = em.getReference(testNew.getClass(), testNew.getId());
                testCase.setTest(testNew);
            }
            if (vmUserNew != null) {
                vmUserNew = em.getReference(vmUserNew.getClass(), vmUserNew.getId());
                testCase.setVmUser(vmUserNew);
            }
            List<RiskControl> attachedRiskControlListNew = new ArrayList<RiskControl>();
            for (RiskControl riskControlListNewRiskControlToAttach : riskControlListNew) {
                riskControlListNewRiskControlToAttach = em.getReference(riskControlListNewRiskControlToAttach.getClass(), riskControlListNewRiskControlToAttach.getRiskControlPK());
                attachedRiskControlListNew.add(riskControlListNewRiskControlToAttach);
            }
            riskControlListNew = attachedRiskControlListNew;
            testCase.setRiskControlList(riskControlListNew);
            List<Step> attachedStepListNew = new ArrayList<Step>();
            for (Step stepListNewStepToAttach : stepListNew) {
                stepListNewStepToAttach = em.getReference(stepListNewStepToAttach.getClass(), stepListNewStepToAttach.getStepPK());
                attachedStepListNew.add(stepListNewStepToAttach);
            }
            stepListNew = attachedStepListNew;
            testCase.setStepList(stepListNew);
            testCase = em.merge(testCase);
            if (testOld != null && !testOld.equals(testNew)) {
                testOld.getTestCaseList().remove(testCase);
                testOld = em.merge(testOld);
            }
            if (testNew != null && !testNew.equals(testOld)) {
                testNew.getTestCaseList().add(testCase);
                testNew = em.merge(testNew);
            }
            if (vmUserOld != null && !vmUserOld.equals(vmUserNew)) {
                vmUserOld.getTestCaseList().remove(testCase);
                vmUserOld = em.merge(vmUserOld);
            }
            if (vmUserNew != null && !vmUserNew.equals(vmUserOld)) {
                vmUserNew.getTestCaseList().add(testCase);
                vmUserNew = em.merge(vmUserNew);
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
            Test test = testCase.getTest();
            if (test != null) {
                test.getTestCaseList().remove(testCase);
                test = em.merge(test);
            }
            VmUser vmUser = testCase.getVmUser();
            if (vmUser != null) {
                vmUser.getTestCaseList().remove(testCase);
                vmUser = em.merge(vmUser);
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
