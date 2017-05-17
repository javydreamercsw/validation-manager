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
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.Requirement;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.StepPK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class StepJpaController implements Serializable {

    public StepJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Step step) throws PreexistingEntityException, Exception {
        if (step.getStepPK() == null) {
            step.setStepPK(new StepPK());
        }
        if (step.getRequirementList() == null) {
            step.setRequirementList(new ArrayList<Requirement>());
        }
        if (step.getExecutionStepList() == null) {
            step.setExecutionStepList(new ArrayList<ExecutionStep>());
        }
        if (step.getHistoryList() == null) {
            step.setHistoryList(new ArrayList<History>());
        }
        step.getStepPK().setTestCaseId(step.getTestCase().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestCase testCase = step.getTestCase();
            if (testCase != null) {
                testCase = em.getReference(testCase.getClass(), testCase.getId());
                step.setTestCase(testCase);
            }
            List<Requirement> attachedRequirementList = new ArrayList<Requirement>();
            for (Requirement requirementListRequirementToAttach : step.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getId());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            step.setRequirementList(attachedRequirementList);
            List<ExecutionStep> attachedExecutionStepList = new ArrayList<ExecutionStep>();
            for (ExecutionStep executionStepListExecutionStepToAttach : step.getExecutionStepList()) {
                executionStepListExecutionStepToAttach = em.getReference(executionStepListExecutionStepToAttach.getClass(), executionStepListExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepList.add(executionStepListExecutionStepToAttach);
            }
            step.setExecutionStepList(attachedExecutionStepList);
            List<History> attachedHistoryList = new ArrayList<History>();
            for (History historyListHistoryToAttach : step.getHistoryList()) {
                historyListHistoryToAttach = em.getReference(historyListHistoryToAttach.getClass(), historyListHistoryToAttach.getId());
                attachedHistoryList.add(historyListHistoryToAttach);
            }
            step.setHistoryList(attachedHistoryList);
            em.persist(step);
            if (testCase != null) {
                testCase.getStepList().add(step);
                testCase = em.merge(testCase);
            }
            for (Requirement requirementListRequirement : step.getRequirementList()) {
                requirementListRequirement.getStepList().add(step);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            for (ExecutionStep executionStepListExecutionStep : step.getExecutionStepList()) {
                Step oldStepOfExecutionStepListExecutionStep = executionStepListExecutionStep.getStep();
                executionStepListExecutionStep.setStep(step);
                executionStepListExecutionStep = em.merge(executionStepListExecutionStep);
                if (oldStepOfExecutionStepListExecutionStep != null) {
                    oldStepOfExecutionStepListExecutionStep.getExecutionStepList().remove(executionStepListExecutionStep);
                    oldStepOfExecutionStepListExecutionStep = em.merge(oldStepOfExecutionStepListExecutionStep);
                }
            }
            for (History historyListHistory : step.getHistoryList()) {
                Step oldStepOfHistoryListHistory = historyListHistory.getStep();
                historyListHistory.setStep(step);
                historyListHistory = em.merge(historyListHistory);
                if (oldStepOfHistoryListHistory != null) {
                    oldStepOfHistoryListHistory.getHistoryList().remove(historyListHistory);
                    oldStepOfHistoryListHistory = em.merge(oldStepOfHistoryListHistory);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findStep(step.getStepPK()) != null) {
                throw new PreexistingEntityException("Step " + step + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Step step) throws IllegalOrphanException, NonexistentEntityException, Exception {
        step.getStepPK().setTestCaseId(step.getTestCase().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Step persistentStep = em.find(Step.class, step.getStepPK());
            TestCase testCaseOld = persistentStep.getTestCase();
            TestCase testCaseNew = step.getTestCase();
            List<Requirement> requirementListOld = persistentStep.getRequirementList();
            List<Requirement> requirementListNew = step.getRequirementList();
            List<ExecutionStep> executionStepListOld = persistentStep.getExecutionStepList();
            List<ExecutionStep> executionStepListNew = step.getExecutionStepList();
            List<History> historyListOld = persistentStep.getHistoryList();
            List<History> historyListNew = step.getHistoryList();
            List<String> illegalOrphanMessages = null;
            for (ExecutionStep executionStepListOldExecutionStep : executionStepListOld) {
                if (!executionStepListNew.contains(executionStepListOldExecutionStep)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ExecutionStep " + executionStepListOldExecutionStep + " since its step field is not nullable.");
                }
            }
            for (History historyListOldHistory : historyListOld) {
                if (!historyListNew.contains(historyListOldHistory)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain History " + historyListOldHistory + " since its step field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (testCaseNew != null) {
                testCaseNew = em.getReference(testCaseNew.getClass(), testCaseNew.getId());
                step.setTestCase(testCaseNew);
            }
            List<Requirement> attachedRequirementListNew = new ArrayList<Requirement>();
            for (Requirement requirementListNewRequirementToAttach : requirementListNew) {
                requirementListNewRequirementToAttach = em.getReference(requirementListNewRequirementToAttach.getClass(), requirementListNewRequirementToAttach.getId());
                attachedRequirementListNew.add(requirementListNewRequirementToAttach);
            }
            requirementListNew = attachedRequirementListNew;
            step.setRequirementList(requirementListNew);
            List<ExecutionStep> attachedExecutionStepListNew = new ArrayList<ExecutionStep>();
            for (ExecutionStep executionStepListNewExecutionStepToAttach : executionStepListNew) {
                executionStepListNewExecutionStepToAttach = em.getReference(executionStepListNewExecutionStepToAttach.getClass(), executionStepListNewExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepListNew.add(executionStepListNewExecutionStepToAttach);
            }
            executionStepListNew = attachedExecutionStepListNew;
            step.setExecutionStepList(executionStepListNew);
            List<History> attachedHistoryListNew = new ArrayList<History>();
            for (History historyListNewHistoryToAttach : historyListNew) {
                historyListNewHistoryToAttach = em.getReference(historyListNewHistoryToAttach.getClass(), historyListNewHistoryToAttach.getId());
                attachedHistoryListNew.add(historyListNewHistoryToAttach);
            }
            historyListNew = attachedHistoryListNew;
            step.setHistoryList(historyListNew);
            step = em.merge(step);
            if (testCaseOld != null && !testCaseOld.equals(testCaseNew)) {
                testCaseOld.getStepList().remove(step);
                testCaseOld = em.merge(testCaseOld);
            }
            if (testCaseNew != null && !testCaseNew.equals(testCaseOld)) {
                testCaseNew.getStepList().add(step);
                testCaseNew = em.merge(testCaseNew);
            }
            for (Requirement requirementListOldRequirement : requirementListOld) {
                if (!requirementListNew.contains(requirementListOldRequirement)) {
                    requirementListOldRequirement.getStepList().remove(step);
                    requirementListOldRequirement = em.merge(requirementListOldRequirement);
                }
            }
            for (Requirement requirementListNewRequirement : requirementListNew) {
                if (!requirementListOld.contains(requirementListNewRequirement)) {
                    requirementListNewRequirement.getStepList().add(step);
                    requirementListNewRequirement = em.merge(requirementListNewRequirement);
                }
            }
            for (ExecutionStep executionStepListNewExecutionStep : executionStepListNew) {
                if (!executionStepListOld.contains(executionStepListNewExecutionStep)) {
                    Step oldStepOfExecutionStepListNewExecutionStep = executionStepListNewExecutionStep.getStep();
                    executionStepListNewExecutionStep.setStep(step);
                    executionStepListNewExecutionStep = em.merge(executionStepListNewExecutionStep);
                    if (oldStepOfExecutionStepListNewExecutionStep != null && !oldStepOfExecutionStepListNewExecutionStep.equals(step)) {
                        oldStepOfExecutionStepListNewExecutionStep.getExecutionStepList().remove(executionStepListNewExecutionStep);
                        oldStepOfExecutionStepListNewExecutionStep = em.merge(oldStepOfExecutionStepListNewExecutionStep);
                    }
                }
            }
            for (History historyListNewHistory : historyListNew) {
                if (!historyListOld.contains(historyListNewHistory)) {
                    Step oldStepOfHistoryListNewHistory = historyListNewHistory.getStep();
                    historyListNewHistory.setStep(step);
                    historyListNewHistory = em.merge(historyListNewHistory);
                    if (oldStepOfHistoryListNewHistory != null && !oldStepOfHistoryListNewHistory.equals(step)) {
                        oldStepOfHistoryListNewHistory.getHistoryList().remove(historyListNewHistory);
                        oldStepOfHistoryListNewHistory = em.merge(oldStepOfHistoryListNewHistory);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                StepPK id = step.getStepPK();
                if (findStep(id) == null) {
                    throw new NonexistentEntityException("The step with id " + id + " no longer exists.");
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

    public void destroy(StepPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Step step;
            try {
                step = em.getReference(Step.class, id);
                step.getStepPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The step with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ExecutionStep> executionStepListOrphanCheck = step.getExecutionStepList();
            for (ExecutionStep executionStepListOrphanCheckExecutionStep : executionStepListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Step (" + step + ") cannot be destroyed since the ExecutionStep " + executionStepListOrphanCheckExecutionStep + " in its executionStepList field has a non-nullable step field.");
            }
            List<History> historyListOrphanCheck = step.getHistoryList();
            for (History historyListOrphanCheckHistory : historyListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Step (" + step + ") cannot be destroyed since the History " + historyListOrphanCheckHistory + " in its historyList field has a non-nullable step field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            TestCase testCase = step.getTestCase();
            if (testCase != null) {
                testCase.getStepList().remove(step);
                testCase = em.merge(testCase);
            }
            List<Requirement> requirementList = step.getRequirementList();
            for (Requirement requirementListRequirement : requirementList) {
                requirementListRequirement.getStepList().remove(step);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            em.remove(step);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Step> findStepEntities() {
        return findStepEntities(true, -1, -1);
    }

    public List<Step> findStepEntities(int maxResults, int firstResult) {
        return findStepEntities(false, maxResults, firstResult);
    }

    private List<Step> findStepEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Step.class));
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

    public Step findStep(StepPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Step.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getStepCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Step> rt = cq.from(Step.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
