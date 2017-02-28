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
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.StepPK;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.VmException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
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
        if (step.getVmExceptionList() == null) {
            step.setVmExceptionList(new ArrayList<VmException>());
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
            List<VmException> attachedVmExceptionList = new ArrayList<VmException>();
            for (VmException vmExceptionListVmExceptionToAttach : step.getVmExceptionList()) {
                vmExceptionListVmExceptionToAttach = em.getReference(vmExceptionListVmExceptionToAttach.getClass(), vmExceptionListVmExceptionToAttach.getVmExceptionPK());
                attachedVmExceptionList.add(vmExceptionListVmExceptionToAttach);
            }
            step.setVmExceptionList(attachedVmExceptionList);
            em.persist(step);
            if (testCase != null) {
                testCase.getStepList().add(step);
                testCase = em.merge(testCase);
            }
            for (Requirement requirementListRequirement : step.getRequirementList()) {
                requirementListRequirement.getStepList().add(step);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            for (VmException vmExceptionListVmException : step.getVmExceptionList()) {
                vmExceptionListVmException.getStepList().add(step);
                vmExceptionListVmException = em.merge(vmExceptionListVmException);
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

    public void edit(Step step) throws NonexistentEntityException, Exception {
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
            List<VmException> vmExceptionListOld = persistentStep.getVmExceptionList();
            List<VmException> vmExceptionListNew = step.getVmExceptionList();
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
            List<VmException> attachedVmExceptionListNew = new ArrayList<VmException>();
            for (VmException vmExceptionListNewVmExceptionToAttach : vmExceptionListNew) {
                vmExceptionListNewVmExceptionToAttach = em.getReference(vmExceptionListNewVmExceptionToAttach.getClass(), vmExceptionListNewVmExceptionToAttach.getVmExceptionPK());
                attachedVmExceptionListNew.add(vmExceptionListNewVmExceptionToAttach);
            }
            vmExceptionListNew = attachedVmExceptionListNew;
            step.setVmExceptionList(vmExceptionListNew);
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

    public void destroy(StepPK id) throws NonexistentEntityException {
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
            List<VmException> vmExceptionList = step.getVmExceptionList();
            for (VmException vmExceptionListVmException : vmExceptionList) {
                vmExceptionListVmException.getStepList().remove(step);
                vmExceptionListVmException = em.merge(vmExceptionListVmException);
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
