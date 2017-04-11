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
import com.validation.manager.core.db.ExecutionResult;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.VmException;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.ExecutionStepHasAttachment;
import com.validation.manager.core.db.ExecutionStepPK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ExecutionStepJpaController implements Serializable {

    public ExecutionStepJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ExecutionStep executionStep) throws PreexistingEntityException, Exception {
        if (executionStep.getExecutionStepPK() == null) {
            executionStep.setExecutionStepPK(new ExecutionStepPK());
        }
        if (executionStep.getVmExceptionList() == null) {
            executionStep.setVmExceptionList(new ArrayList<VmException>());
        }
        if (executionStep.getExecutionStepHasAttachmentList() == null) {
            executionStep.setExecutionStepHasAttachmentList(new ArrayList<ExecutionStepHasAttachment>());
        }
        executionStep.getExecutionStepPK().setStepId(executionStep.getStep().getStepPK().getId());
        executionStep.getExecutionStepPK().setTestCaseExecutionId(executionStep.getTestCaseExecution().getId());
        executionStep.getExecutionStepPK().setStepTestCaseId(executionStep.getStep().getStepPK().getTestCaseId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionResult resultId = executionStep.getResultId();
            if (resultId != null) {
                resultId = em.getReference(resultId.getClass(), resultId.getId());
                executionStep.setResultId(resultId);
            }
            VmUser vmUserId = executionStep.getVmUserId();
            if (vmUserId != null) {
                vmUserId = em.getReference(vmUserId.getClass(), vmUserId.getId());
                executionStep.setVmUserId(vmUserId);
            }
            Step step = executionStep.getStep();
            if (step != null) {
                step = em.getReference(step.getClass(), step.getStepPK());
                executionStep.setStep(step);
            }
            TestCaseExecution testCaseExecution = executionStep.getTestCaseExecution();
            if (testCaseExecution != null) {
                testCaseExecution = em.getReference(testCaseExecution.getClass(), testCaseExecution.getId());
                executionStep.setTestCaseExecution(testCaseExecution);
            }
            List<VmException> attachedVmExceptionList = new ArrayList<VmException>();
            for (VmException vmExceptionListVmExceptionToAttach : executionStep.getVmExceptionList()) {
                vmExceptionListVmExceptionToAttach = em.getReference(vmExceptionListVmExceptionToAttach.getClass(), vmExceptionListVmExceptionToAttach.getVmExceptionPK());
                attachedVmExceptionList.add(vmExceptionListVmExceptionToAttach);
            }
            executionStep.setVmExceptionList(attachedVmExceptionList);
            List<ExecutionStepHasAttachment> attachedExecutionStepHasAttachmentList = new ArrayList<ExecutionStepHasAttachment>();
            for (ExecutionStepHasAttachment executionStepHasAttachmentListExecutionStepHasAttachmentToAttach : executionStep.getExecutionStepHasAttachmentList()) {
                executionStepHasAttachmentListExecutionStepHasAttachmentToAttach = em.getReference(executionStepHasAttachmentListExecutionStepHasAttachmentToAttach.getClass(), executionStepHasAttachmentListExecutionStepHasAttachmentToAttach.getExecutionStepHasAttachmentPK());
                attachedExecutionStepHasAttachmentList.add(executionStepHasAttachmentListExecutionStepHasAttachmentToAttach);
            }
            executionStep.setExecutionStepHasAttachmentList(attachedExecutionStepHasAttachmentList);
            em.persist(executionStep);
            if (resultId != null) {
                resultId.getExecutionStepList().add(executionStep);
                resultId = em.merge(resultId);
            }
            if (vmUserId != null) {
                vmUserId.getExecutionSteps().add(executionStep);
                vmUserId = em.merge(vmUserId);
            }
            if (step != null) {
                step.getExecutionStepList().add(executionStep);
                step = em.merge(step);
            }
            if (testCaseExecution != null) {
                testCaseExecution.getExecutionStepList().add(executionStep);
                testCaseExecution = em.merge(testCaseExecution);
            }
            for (VmException vmExceptionListVmException : executionStep.getVmExceptionList()) {
                vmExceptionListVmException.getExecutionStepList().add(executionStep);
                vmExceptionListVmException = em.merge(vmExceptionListVmException);
            }
            for (ExecutionStepHasAttachment executionStepHasAttachmentListExecutionStepHasAttachment : executionStep.getExecutionStepHasAttachmentList()) {
                ExecutionStep oldExecutionStepOfExecutionStepHasAttachmentListExecutionStepHasAttachment = executionStepHasAttachmentListExecutionStepHasAttachment.getExecutionStep();
                executionStepHasAttachmentListExecutionStepHasAttachment.setExecutionStep(executionStep);
                executionStepHasAttachmentListExecutionStepHasAttachment = em.merge(executionStepHasAttachmentListExecutionStepHasAttachment);
                if (oldExecutionStepOfExecutionStepHasAttachmentListExecutionStepHasAttachment != null) {
                    oldExecutionStepOfExecutionStepHasAttachmentListExecutionStepHasAttachment.getExecutionStepHasAttachmentList().remove(executionStepHasAttachmentListExecutionStepHasAttachment);
                    oldExecutionStepOfExecutionStepHasAttachmentListExecutionStepHasAttachment = em.merge(oldExecutionStepOfExecutionStepHasAttachmentListExecutionStepHasAttachment);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findExecutionStep(executionStep.getExecutionStepPK()) != null) {
                throw new PreexistingEntityException("ExecutionStep " + executionStep + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ExecutionStep executionStep) throws IllegalOrphanException, NonexistentEntityException, Exception {
        executionStep.getExecutionStepPK().setStepId(executionStep.getStep().getStepPK().getId());
        executionStep.getExecutionStepPK().setTestCaseExecutionId(executionStep.getTestCaseExecution().getId());
        executionStep.getExecutionStepPK().setStepTestCaseId(executionStep.getStep().getStepPK().getTestCaseId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStep persistentExecutionStep = em.find(ExecutionStep.class, executionStep.getExecutionStepPK());
            ExecutionResult resultIdOld = persistentExecutionStep.getResultId();
            ExecutionResult resultIdNew = executionStep.getResultId();
            VmUser vmUserIdOld = persistentExecutionStep.getVmUserId();
            VmUser vmUserIdNew = executionStep.getVmUserId();
            Step stepOld = persistentExecutionStep.getStep();
            Step stepNew = executionStep.getStep();
            TestCaseExecution testCaseExecutionOld = persistentExecutionStep.getTestCaseExecution();
            TestCaseExecution testCaseExecutionNew = executionStep.getTestCaseExecution();
            List<VmException> vmExceptionListOld = persistentExecutionStep.getVmExceptionList();
            List<VmException> vmExceptionListNew = executionStep.getVmExceptionList();
            List<ExecutionStepHasAttachment> executionStepHasAttachmentListOld = persistentExecutionStep.getExecutionStepHasAttachmentList();
            List<ExecutionStepHasAttachment> executionStepHasAttachmentListNew = executionStep.getExecutionStepHasAttachmentList();
            List<String> illegalOrphanMessages = null;
            for (ExecutionStepHasAttachment executionStepHasAttachmentListOldExecutionStepHasAttachment : executionStepHasAttachmentListOld) {
                if (!executionStepHasAttachmentListNew.contains(executionStepHasAttachmentListOldExecutionStepHasAttachment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ExecutionStepHasAttachment " + executionStepHasAttachmentListOldExecutionStepHasAttachment + " since its executionStep field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (resultIdNew != null) {
                resultIdNew = em.getReference(resultIdNew.getClass(), resultIdNew.getId());
                executionStep.setResultId(resultIdNew);
            }
            if (vmUserIdNew != null) {
                vmUserIdNew = em.getReference(vmUserIdNew.getClass(), vmUserIdNew.getId());
                executionStep.setVmUserId(vmUserIdNew);
            }
            if (stepNew != null) {
                stepNew = em.getReference(stepNew.getClass(), stepNew.getStepPK());
                executionStep.setStep(stepNew);
            }
            if (testCaseExecutionNew != null) {
                testCaseExecutionNew = em.getReference(testCaseExecutionNew.getClass(), testCaseExecutionNew.getId());
                executionStep.setTestCaseExecution(testCaseExecutionNew);
            }
            List<VmException> attachedVmExceptionListNew = new ArrayList<VmException>();
            for (VmException vmExceptionListNewVmExceptionToAttach : vmExceptionListNew) {
                vmExceptionListNewVmExceptionToAttach = em.getReference(vmExceptionListNewVmExceptionToAttach.getClass(), vmExceptionListNewVmExceptionToAttach.getVmExceptionPK());
                attachedVmExceptionListNew.add(vmExceptionListNewVmExceptionToAttach);
            }
            vmExceptionListNew = attachedVmExceptionListNew;
            executionStep.setVmExceptionList(vmExceptionListNew);
            List<ExecutionStepHasAttachment> attachedExecutionStepHasAttachmentListNew = new ArrayList<ExecutionStepHasAttachment>();
            for (ExecutionStepHasAttachment executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach : executionStepHasAttachmentListNew) {
                executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach = em.getReference(executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach.getClass(), executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach.getExecutionStepHasAttachmentPK());
                attachedExecutionStepHasAttachmentListNew.add(executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach);
            }
            executionStepHasAttachmentListNew = attachedExecutionStepHasAttachmentListNew;
            executionStep.setExecutionStepHasAttachmentList(executionStepHasAttachmentListNew);
            executionStep = em.merge(executionStep);
            if (resultIdOld != null && !resultIdOld.equals(resultIdNew)) {
                resultIdOld.getExecutionStepList().remove(executionStep);
                resultIdOld = em.merge(resultIdOld);
            }
            if (resultIdNew != null && !resultIdNew.equals(resultIdOld)) {
                resultIdNew.getExecutionStepList().add(executionStep);
                resultIdNew = em.merge(resultIdNew);
            }
            if (vmUserIdOld != null && !vmUserIdOld.equals(vmUserIdNew)) {
                vmUserIdOld.getExecutionSteps().remove(executionStep);
                vmUserIdOld = em.merge(vmUserIdOld);
            }
            if (vmUserIdNew != null && !vmUserIdNew.equals(vmUserIdOld)) {
                vmUserIdNew.getExecutionSteps().add(executionStep);
                vmUserIdNew = em.merge(vmUserIdNew);
            }
            if (stepOld != null && !stepOld.equals(stepNew)) {
                stepOld.getExecutionStepList().remove(executionStep);
                stepOld = em.merge(stepOld);
            }
            if (stepNew != null && !stepNew.equals(stepOld)) {
                stepNew.getExecutionStepList().add(executionStep);
                stepNew = em.merge(stepNew);
            }
            if (testCaseExecutionOld != null && !testCaseExecutionOld.equals(testCaseExecutionNew)) {
                testCaseExecutionOld.getExecutionStepList().remove(executionStep);
                testCaseExecutionOld = em.merge(testCaseExecutionOld);
            }
            if (testCaseExecutionNew != null && !testCaseExecutionNew.equals(testCaseExecutionOld)) {
                testCaseExecutionNew.getExecutionStepList().add(executionStep);
                testCaseExecutionNew = em.merge(testCaseExecutionNew);
            }
            for (VmException vmExceptionListOldVmException : vmExceptionListOld) {
                if (!vmExceptionListNew.contains(vmExceptionListOldVmException)) {
                    vmExceptionListOldVmException.getExecutionStepList().remove(executionStep);
                    vmExceptionListOldVmException = em.merge(vmExceptionListOldVmException);
                }
            }
            for (VmException vmExceptionListNewVmException : vmExceptionListNew) {
                if (!vmExceptionListOld.contains(vmExceptionListNewVmException)) {
                    vmExceptionListNewVmException.getExecutionStepList().add(executionStep);
                    vmExceptionListNewVmException = em.merge(vmExceptionListNewVmException);
                }
            }
            for (ExecutionStepHasAttachment executionStepHasAttachmentListNewExecutionStepHasAttachment : executionStepHasAttachmentListNew) {
                if (!executionStepHasAttachmentListOld.contains(executionStepHasAttachmentListNewExecutionStepHasAttachment)) {
                    ExecutionStep oldExecutionStepOfExecutionStepHasAttachmentListNewExecutionStepHasAttachment = executionStepHasAttachmentListNewExecutionStepHasAttachment.getExecutionStep();
                    executionStepHasAttachmentListNewExecutionStepHasAttachment.setExecutionStep(executionStep);
                    executionStepHasAttachmentListNewExecutionStepHasAttachment = em.merge(executionStepHasAttachmentListNewExecutionStepHasAttachment);
                    if (oldExecutionStepOfExecutionStepHasAttachmentListNewExecutionStepHasAttachment != null && !oldExecutionStepOfExecutionStepHasAttachmentListNewExecutionStepHasAttachment.equals(executionStep)) {
                        oldExecutionStepOfExecutionStepHasAttachmentListNewExecutionStepHasAttachment.getExecutionStepHasAttachmentList().remove(executionStepHasAttachmentListNewExecutionStepHasAttachment);
                        oldExecutionStepOfExecutionStepHasAttachmentListNewExecutionStepHasAttachment = em.merge(oldExecutionStepOfExecutionStepHasAttachmentListNewExecutionStepHasAttachment);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ExecutionStepPK id = executionStep.getExecutionStepPK();
                if (findExecutionStep(id) == null) {
                    throw new NonexistentEntityException("The executionStep with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ExecutionStepPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStep executionStep;
            try {
                executionStep = em.getReference(ExecutionStep.class, id);
                executionStep.getExecutionStepPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The executionStep with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ExecutionStepHasAttachment> executionStepHasAttachmentListOrphanCheck = executionStep.getExecutionStepHasAttachmentList();
            for (ExecutionStepHasAttachment executionStepHasAttachmentListOrphanCheckExecutionStepHasAttachment : executionStepHasAttachmentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ExecutionStep (" + executionStep + ") cannot be destroyed since the ExecutionStepHasAttachment " + executionStepHasAttachmentListOrphanCheckExecutionStepHasAttachment + " in its executionStepHasAttachmentList field has a non-nullable executionStep field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            ExecutionResult resultId = executionStep.getResultId();
            if (resultId != null) {
                resultId.getExecutionStepList().remove(executionStep);
                resultId = em.merge(resultId);
            }
            VmUser vmUserId = executionStep.getVmUserId();
            if (vmUserId != null) {
                vmUserId.getExecutionSteps().remove(executionStep);
                vmUserId = em.merge(vmUserId);
            }
            Step step = executionStep.getStep();
            if (step != null) {
                step.getExecutionStepList().remove(executionStep);
                step = em.merge(step);
            }
            TestCaseExecution testCaseExecution = executionStep.getTestCaseExecution();
            if (testCaseExecution != null) {
                testCaseExecution.getExecutionStepList().remove(executionStep);
                testCaseExecution = em.merge(testCaseExecution);
            }
            List<VmException> vmExceptionList = executionStep.getVmExceptionList();
            for (VmException vmExceptionListVmException : vmExceptionList) {
                vmExceptionListVmException.getExecutionStepList().remove(executionStep);
                vmExceptionListVmException = em.merge(vmExceptionListVmException);
            }
            em.remove(executionStep);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ExecutionStep> findExecutionStepEntities() {
        return findExecutionStepEntities(true, -1, -1);
    }

    public List<ExecutionStep> findExecutionStepEntities(int maxResults, int firstResult) {
        return findExecutionStepEntities(false, maxResults, firstResult);
    }

    private List<ExecutionStep> findExecutionStepEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ExecutionStep.class));
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

    public ExecutionStep findExecutionStep(ExecutionStepPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ExecutionStep.class, id);
        } finally {
            em.close();
        }
    }

    public int getExecutionStepCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ExecutionStep> rt = cq.from(ExecutionStep.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
