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
import com.validation.manager.core.db.VmException;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.StepHasRequirement;
import com.validation.manager.core.db.StepHasException;
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
        if (step.getVmExceptionList() == null) {
            step.setVmExceptionList(new ArrayList<VmException>());
        }
        if (step.getRequirementList() == null) {
            step.setRequirementList(new ArrayList<Requirement>());
        }
        if (step.getStepHasRequirementList() == null) {
            step.setStepHasRequirementList(new ArrayList<StepHasRequirement>());
        }
        if (step.getStepHasExceptionList() == null) {
            step.setStepHasExceptionList(new ArrayList<StepHasException>());
        }
        step.getStepPK().setTestCaseTestId(step.getTestCase().getTestCasePK().getTestId());
        step.getStepPK().setTestCaseId(step.getTestCase().getTestCasePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestCase testCase = step.getTestCase();
            if (testCase != null) {
                testCase = em.getReference(testCase.getClass(), testCase.getTestCasePK());
                step.setTestCase(testCase);
            }
            List<VmException> attachedVmExceptionList = new ArrayList<VmException>();
            for (VmException vmExceptionListVmExceptionToAttach : step.getVmExceptionList()) {
                vmExceptionListVmExceptionToAttach = em.getReference(vmExceptionListVmExceptionToAttach.getClass(), vmExceptionListVmExceptionToAttach.getVmExceptionPK());
                attachedVmExceptionList.add(vmExceptionListVmExceptionToAttach);
            }
            step.setVmExceptionList(attachedVmExceptionList);
            List<Requirement> attachedRequirementList = new ArrayList<Requirement>();
            for (Requirement requirementListRequirementToAttach : step.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getRequirementPK());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            step.setRequirementList(attachedRequirementList);
            List<StepHasRequirement> attachedStepHasRequirementList = new ArrayList<StepHasRequirement>();
            for (StepHasRequirement stepHasRequirementListStepHasRequirementToAttach : step.getStepHasRequirementList()) {
                stepHasRequirementListStepHasRequirementToAttach = em.getReference(stepHasRequirementListStepHasRequirementToAttach.getClass(), stepHasRequirementListStepHasRequirementToAttach.getStepHasRequirementPK());
                attachedStepHasRequirementList.add(stepHasRequirementListStepHasRequirementToAttach);
            }
            step.setStepHasRequirementList(attachedStepHasRequirementList);
            List<StepHasException> attachedStepHasExceptionList = new ArrayList<StepHasException>();
            for (StepHasException stepHasExceptionListStepHasExceptionToAttach : step.getStepHasExceptionList()) {
                stepHasExceptionListStepHasExceptionToAttach = em.getReference(stepHasExceptionListStepHasExceptionToAttach.getClass(), stepHasExceptionListStepHasExceptionToAttach.getStepHasExceptionPK());
                attachedStepHasExceptionList.add(stepHasExceptionListStepHasExceptionToAttach);
            }
            step.setStepHasExceptionList(attachedStepHasExceptionList);
            em.persist(step);
            if (testCase != null) {
                testCase.getStepList().add(step);
                testCase = em.merge(testCase);
            }
            for (VmException vmExceptionListVmException : step.getVmExceptionList()) {
                vmExceptionListVmException.getStepList().add(step);
                vmExceptionListVmException = em.merge(vmExceptionListVmException);
            }
            for (Requirement requirementListRequirement : step.getRequirementList()) {
                requirementListRequirement.getStepList().add(step);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            for (StepHasRequirement stepHasRequirementListStepHasRequirement : step.getStepHasRequirementList()) {
                Step oldStepOfStepHasRequirementListStepHasRequirement = stepHasRequirementListStepHasRequirement.getStep();
                stepHasRequirementListStepHasRequirement.setStep(step);
                stepHasRequirementListStepHasRequirement = em.merge(stepHasRequirementListStepHasRequirement);
                if (oldStepOfStepHasRequirementListStepHasRequirement != null) {
                    oldStepOfStepHasRequirementListStepHasRequirement.getStepHasRequirementList().remove(stepHasRequirementListStepHasRequirement);
                    oldStepOfStepHasRequirementListStepHasRequirement = em.merge(oldStepOfStepHasRequirementListStepHasRequirement);
                }
            }
            for (StepHasException stepHasExceptionListStepHasException : step.getStepHasExceptionList()) {
                Step oldStepOfStepHasExceptionListStepHasException = stepHasExceptionListStepHasException.getStep();
                stepHasExceptionListStepHasException.setStep(step);
                stepHasExceptionListStepHasException = em.merge(stepHasExceptionListStepHasException);
                if (oldStepOfStepHasExceptionListStepHasException != null) {
                    oldStepOfStepHasExceptionListStepHasException.getStepHasExceptionList().remove(stepHasExceptionListStepHasException);
                    oldStepOfStepHasExceptionListStepHasException = em.merge(oldStepOfStepHasExceptionListStepHasException);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findStep(step.getStepPK()) != null) {
                throw new PreexistingEntityException("Step " + step + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Step step) throws IllegalOrphanException, NonexistentEntityException, Exception {
        step.getStepPK().setTestCaseTestId(step.getTestCase().getTestCasePK().getTestId());
        step.getStepPK().setTestCaseId(step.getTestCase().getTestCasePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Step persistentStep = em.find(Step.class, step.getStepPK());
            TestCase testCaseOld = persistentStep.getTestCase();
            TestCase testCaseNew = step.getTestCase();
            List<VmException> vmExceptionListOld = persistentStep.getVmExceptionList();
            List<VmException> vmExceptionListNew = step.getVmExceptionList();
            List<Requirement> requirementListOld = persistentStep.getRequirementList();
            List<Requirement> requirementListNew = step.getRequirementList();
            List<StepHasRequirement> stepHasRequirementListOld = persistentStep.getStepHasRequirementList();
            List<StepHasRequirement> stepHasRequirementListNew = step.getStepHasRequirementList();
            List<StepHasException> stepHasExceptionListOld = persistentStep.getStepHasExceptionList();
            List<StepHasException> stepHasExceptionListNew = step.getStepHasExceptionList();
            List<String> illegalOrphanMessages = null;
            for (StepHasRequirement stepHasRequirementListOldStepHasRequirement : stepHasRequirementListOld) {
                if (!stepHasRequirementListNew.contains(stepHasRequirementListOldStepHasRequirement)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain StepHasRequirement " + stepHasRequirementListOldStepHasRequirement + " since its step field is not nullable.");
                }
            }
            for (StepHasException stepHasExceptionListOldStepHasException : stepHasExceptionListOld) {
                if (!stepHasExceptionListNew.contains(stepHasExceptionListOldStepHasException)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain StepHasException " + stepHasExceptionListOldStepHasException + " since its step field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (testCaseNew != null) {
                testCaseNew = em.getReference(testCaseNew.getClass(), testCaseNew.getTestCasePK());
                step.setTestCase(testCaseNew);
            }
            List<VmException> attachedVmExceptionListNew = new ArrayList<VmException>();
            for (VmException vmExceptionListNewVmExceptionToAttach : vmExceptionListNew) {
                vmExceptionListNewVmExceptionToAttach = em.getReference(vmExceptionListNewVmExceptionToAttach.getClass(), vmExceptionListNewVmExceptionToAttach.getVmExceptionPK());
                attachedVmExceptionListNew.add(vmExceptionListNewVmExceptionToAttach);
            }
            vmExceptionListNew = attachedVmExceptionListNew;
            step.setVmExceptionList(vmExceptionListNew);
            List<Requirement> attachedRequirementListNew = new ArrayList<Requirement>();
            for (Requirement requirementListNewRequirementToAttach : requirementListNew) {
                requirementListNewRequirementToAttach = em.getReference(requirementListNewRequirementToAttach.getClass(), requirementListNewRequirementToAttach.getRequirementPK());
                attachedRequirementListNew.add(requirementListNewRequirementToAttach);
            }
            requirementListNew = attachedRequirementListNew;
            step.setRequirementList(requirementListNew);
            List<StepHasRequirement> attachedStepHasRequirementListNew = new ArrayList<StepHasRequirement>();
            for (StepHasRequirement stepHasRequirementListNewStepHasRequirementToAttach : stepHasRequirementListNew) {
                stepHasRequirementListNewStepHasRequirementToAttach = em.getReference(stepHasRequirementListNewStepHasRequirementToAttach.getClass(), stepHasRequirementListNewStepHasRequirementToAttach.getStepHasRequirementPK());
                attachedStepHasRequirementListNew.add(stepHasRequirementListNewStepHasRequirementToAttach);
            }
            stepHasRequirementListNew = attachedStepHasRequirementListNew;
            step.setStepHasRequirementList(stepHasRequirementListNew);
            List<StepHasException> attachedStepHasExceptionListNew = new ArrayList<StepHasException>();
            for (StepHasException stepHasExceptionListNewStepHasExceptionToAttach : stepHasExceptionListNew) {
                stepHasExceptionListNewStepHasExceptionToAttach = em.getReference(stepHasExceptionListNewStepHasExceptionToAttach.getClass(), stepHasExceptionListNewStepHasExceptionToAttach.getStepHasExceptionPK());
                attachedStepHasExceptionListNew.add(stepHasExceptionListNewStepHasExceptionToAttach);
            }
            stepHasExceptionListNew = attachedStepHasExceptionListNew;
            step.setStepHasExceptionList(stepHasExceptionListNew);
            step = em.merge(step);
            if (testCaseOld != null && !testCaseOld.equals(testCaseNew)) {
                testCaseOld.getStepList().remove(step);
                testCaseOld = em.merge(testCaseOld);
            }
            if (testCaseNew != null && !testCaseNew.equals(testCaseOld)) {
                testCaseNew.getStepList().add(step);
                testCaseNew = em.merge(testCaseNew);
            }
            for (VmException vmExceptionListOldVmException : vmExceptionListOld) {
                if (!vmExceptionListNew.contains(vmExceptionListOldVmException)) {
                    vmExceptionListOldVmException.getStepList().remove(step);
                    vmExceptionListOldVmException = em.merge(vmExceptionListOldVmException);
                }
            }
            for (VmException vmExceptionListNewVmException : vmExceptionListNew) {
                if (!vmExceptionListOld.contains(vmExceptionListNewVmException)) {
                    vmExceptionListNewVmException.getStepList().add(step);
                    vmExceptionListNewVmException = em.merge(vmExceptionListNewVmException);
                }
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
            for (StepHasRequirement stepHasRequirementListNewStepHasRequirement : stepHasRequirementListNew) {
                if (!stepHasRequirementListOld.contains(stepHasRequirementListNewStepHasRequirement)) {
                    Step oldStepOfStepHasRequirementListNewStepHasRequirement = stepHasRequirementListNewStepHasRequirement.getStep();
                    stepHasRequirementListNewStepHasRequirement.setStep(step);
                    stepHasRequirementListNewStepHasRequirement = em.merge(stepHasRequirementListNewStepHasRequirement);
                    if (oldStepOfStepHasRequirementListNewStepHasRequirement != null && !oldStepOfStepHasRequirementListNewStepHasRequirement.equals(step)) {
                        oldStepOfStepHasRequirementListNewStepHasRequirement.getStepHasRequirementList().remove(stepHasRequirementListNewStepHasRequirement);
                        oldStepOfStepHasRequirementListNewStepHasRequirement = em.merge(oldStepOfStepHasRequirementListNewStepHasRequirement);
                    }
                }
            }
            for (StepHasException stepHasExceptionListNewStepHasException : stepHasExceptionListNew) {
                if (!stepHasExceptionListOld.contains(stepHasExceptionListNewStepHasException)) {
                    Step oldStepOfStepHasExceptionListNewStepHasException = stepHasExceptionListNewStepHasException.getStep();
                    stepHasExceptionListNewStepHasException.setStep(step);
                    stepHasExceptionListNewStepHasException = em.merge(stepHasExceptionListNewStepHasException);
                    if (oldStepOfStepHasExceptionListNewStepHasException != null && !oldStepOfStepHasExceptionListNewStepHasException.equals(step)) {
                        oldStepOfStepHasExceptionListNewStepHasException.getStepHasExceptionList().remove(stepHasExceptionListNewStepHasException);
                        oldStepOfStepHasExceptionListNewStepHasException = em.merge(oldStepOfStepHasExceptionListNewStepHasException);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                StepPK id = step.getStepPK();
                if (findStep(id) == null) {
                    throw new NonexistentEntityException("The step with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
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
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The step with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<StepHasRequirement> stepHasRequirementListOrphanCheck = step.getStepHasRequirementList();
            for (StepHasRequirement stepHasRequirementListOrphanCheckStepHasRequirement : stepHasRequirementListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Step (" + step + ") cannot be destroyed since the StepHasRequirement " + stepHasRequirementListOrphanCheckStepHasRequirement + " in its stepHasRequirementList field has a non-nullable step field.");
            }
            List<StepHasException> stepHasExceptionListOrphanCheck = step.getStepHasExceptionList();
            for (StepHasException stepHasExceptionListOrphanCheckStepHasException : stepHasExceptionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Step (" + step + ") cannot be destroyed since the StepHasException " + stepHasExceptionListOrphanCheckStepHasException + " in its stepHasExceptionList field has a non-nullable step field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            TestCase testCase = step.getTestCase();
            if (testCase != null) {
                testCase.getStepList().remove(step);
                testCase = em.merge(testCase);
            }
            List<VmException> vmExceptionList = step.getVmExceptionList();
            for (VmException vmExceptionListVmException : vmExceptionList) {
                vmExceptionListVmException.getStepList().remove(step);
                vmExceptionListVmException = em.merge(vmExceptionListVmException);
            }
            List<Requirement> requirementList = step.getRequirementList();
            for (Requirement requirementListRequirement : requirementList) {
                requirementListRequirement.getStepList().remove(step);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            em.remove(step);
            em.getTransaction().commit();
        } finally {
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
        } finally {
            em.close();
        }
    }

    public Step findStep(StepPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Step.class, id);
        } finally {
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
        } finally {
            em.close();
        }
    }
    
}
