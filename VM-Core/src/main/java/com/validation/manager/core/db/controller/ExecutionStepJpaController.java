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
import com.validation.manager.core.db.ExecutionStepHasAttachment;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.ExecutionStepHasIssue;
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
        if (executionStep.getExecutionStepHasAttachmentList() == null) {
            executionStep.setExecutionStepHasAttachmentList(new ArrayList<ExecutionStepHasAttachment>());
        }
        if (executionStep.getExecutionStepHasIssueList() == null) {
            executionStep.setExecutionStepHasIssueList(new ArrayList<ExecutionStepHasIssue>());
        }
        executionStep.getExecutionStepPK().setTestCaseExecutionId(executionStep.getTestCaseExecution().getId());
        executionStep.getExecutionStepPK().setStepTestCaseId(executionStep.getStep().getStepPK().getTestCaseId());
        executionStep.getExecutionStepPK().setStepId(executionStep.getStep().getStepPK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionResult resultId = executionStep.getResultId();
            if (resultId != null) {
                resultId = em.getReference(resultId.getClass(), resultId.getId());
                executionStep.setResultId(resultId);
            }
            VmUser assignee = executionStep.getAssignee();
            if (assignee != null) {
                assignee = em.getReference(assignee.getClass(), assignee.getId());
                executionStep.setAssignee(assignee);
            }
            VmUser assigner = executionStep.getAssigner();
            if (assigner != null) {
                assigner = em.getReference(assigner.getClass(), assigner.getId());
                executionStep.setAssigner(assigner);
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
            List<ExecutionStepHasAttachment> attachedExecutionStepHasAttachmentList = new ArrayList<ExecutionStepHasAttachment>();
            for (ExecutionStepHasAttachment executionStepHasAttachmentListExecutionStepHasAttachmentToAttach : executionStep.getExecutionStepHasAttachmentList()) {
                executionStepHasAttachmentListExecutionStepHasAttachmentToAttach = em.getReference(executionStepHasAttachmentListExecutionStepHasAttachmentToAttach.getClass(), executionStepHasAttachmentListExecutionStepHasAttachmentToAttach.getExecutionStepHasAttachmentPK());
                attachedExecutionStepHasAttachmentList.add(executionStepHasAttachmentListExecutionStepHasAttachmentToAttach);
            }
            executionStep.setExecutionStepHasAttachmentList(attachedExecutionStepHasAttachmentList);
            List<ExecutionStepHasIssue> attachedExecutionStepHasIssueList = new ArrayList<ExecutionStepHasIssue>();
            for (ExecutionStepHasIssue executionStepHasIssueListExecutionStepHasIssueToAttach : executionStep.getExecutionStepHasIssueList()) {
                executionStepHasIssueListExecutionStepHasIssueToAttach = em.getReference(executionStepHasIssueListExecutionStepHasIssueToAttach.getClass(), executionStepHasIssueListExecutionStepHasIssueToAttach.getExecutionStepHasIssuePK());
                attachedExecutionStepHasIssueList.add(executionStepHasIssueListExecutionStepHasIssueToAttach);
            }
            executionStep.setExecutionStepHasIssueList(attachedExecutionStepHasIssueList);
            em.persist(executionStep);
            if (resultId != null) {
                resultId.getExecutionStepList().add(executionStep);
                resultId = em.merge(resultId);
            }
            if (assignee != null) {
                assignee.getExecutionStepList().add(executionStep);
                assignee = em.merge(assignee);
            }
            if (assigner != null) {
                assigner.getExecutionStepList().add(executionStep);
                assigner = em.merge(assigner);
            }
            if (step != null) {
                step.getExecutionStepList().add(executionStep);
                step = em.merge(step);
            }
            if (testCaseExecution != null) {
                testCaseExecution.getExecutionStepList().add(executionStep);
                testCaseExecution = em.merge(testCaseExecution);
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
            for (ExecutionStepHasIssue executionStepHasIssueListExecutionStepHasIssue : executionStep.getExecutionStepHasIssueList()) {
                ExecutionStep oldExecutionStepOfExecutionStepHasIssueListExecutionStepHasIssue = executionStepHasIssueListExecutionStepHasIssue.getExecutionStep();
                executionStepHasIssueListExecutionStepHasIssue.setExecutionStep(executionStep);
                executionStepHasIssueListExecutionStepHasIssue = em.merge(executionStepHasIssueListExecutionStepHasIssue);
                if (oldExecutionStepOfExecutionStepHasIssueListExecutionStepHasIssue != null) {
                    oldExecutionStepOfExecutionStepHasIssueListExecutionStepHasIssue.getExecutionStepHasIssueList().remove(executionStepHasIssueListExecutionStepHasIssue);
                    oldExecutionStepOfExecutionStepHasIssueListExecutionStepHasIssue = em.merge(oldExecutionStepOfExecutionStepHasIssueListExecutionStepHasIssue);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findExecutionStep(executionStep.getExecutionStepPK()) != null) {
                throw new PreexistingEntityException("ExecutionStep " + executionStep + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ExecutionStep executionStep) throws IllegalOrphanException, NonexistentEntityException, Exception {
        executionStep.getExecutionStepPK().setTestCaseExecutionId(executionStep.getTestCaseExecution().getId());
        executionStep.getExecutionStepPK().setStepTestCaseId(executionStep.getStep().getStepPK().getTestCaseId());
        executionStep.getExecutionStepPK().setStepId(executionStep.getStep().getStepPK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStep persistentExecutionStep = em.find(ExecutionStep.class, executionStep.getExecutionStepPK());
            ExecutionResult resultIdOld = persistentExecutionStep.getResultId();
            ExecutionResult resultIdNew = executionStep.getResultId();
            VmUser assigneeOld = persistentExecutionStep.getAssignee();
            VmUser assigneeNew = executionStep.getAssignee();
            VmUser assignerOld = persistentExecutionStep.getAssigner();
            VmUser assignerNew = executionStep.getAssigner();
            Step stepOld = persistentExecutionStep.getStep();
            Step stepNew = executionStep.getStep();
            TestCaseExecution testCaseExecutionOld = persistentExecutionStep.getTestCaseExecution();
            TestCaseExecution testCaseExecutionNew = executionStep.getTestCaseExecution();
            List<ExecutionStepHasAttachment> executionStepHasAttachmentListOld = persistentExecutionStep.getExecutionStepHasAttachmentList();
            List<ExecutionStepHasAttachment> executionStepHasAttachmentListNew = executionStep.getExecutionStepHasAttachmentList();
            List<ExecutionStepHasIssue> executionStepHasIssueListOld = persistentExecutionStep.getExecutionStepHasIssueList();
            List<ExecutionStepHasIssue> executionStepHasIssueListNew = executionStep.getExecutionStepHasIssueList();
            List<String> illegalOrphanMessages = null;
            for (ExecutionStepHasAttachment executionStepHasAttachmentListOldExecutionStepHasAttachment : executionStepHasAttachmentListOld) {
                if (!executionStepHasAttachmentListNew.contains(executionStepHasAttachmentListOldExecutionStepHasAttachment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ExecutionStepHasAttachment " + executionStepHasAttachmentListOldExecutionStepHasAttachment + " since its executionStep field is not nullable.");
                }
            }
            for (ExecutionStepHasIssue executionStepHasIssueListOldExecutionStepHasIssue : executionStepHasIssueListOld) {
                if (!executionStepHasIssueListNew.contains(executionStepHasIssueListOldExecutionStepHasIssue)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ExecutionStepHasIssue " + executionStepHasIssueListOldExecutionStepHasIssue + " since its executionStep field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (resultIdNew != null) {
                resultIdNew = em.getReference(resultIdNew.getClass(), resultIdNew.getId());
                executionStep.setResultId(resultIdNew);
            }
            if (assigneeNew != null) {
                assigneeNew = em.getReference(assigneeNew.getClass(), assigneeNew.getId());
                executionStep.setAssignee(assigneeNew);
            }
            if (assignerNew != null) {
                assignerNew = em.getReference(assignerNew.getClass(), assignerNew.getId());
                executionStep.setAssigner(assignerNew);
            }
            if (stepNew != null) {
                stepNew = em.getReference(stepNew.getClass(), stepNew.getStepPK());
                executionStep.setStep(stepNew);
            }
            if (testCaseExecutionNew != null) {
                testCaseExecutionNew = em.getReference(testCaseExecutionNew.getClass(), testCaseExecutionNew.getId());
                executionStep.setTestCaseExecution(testCaseExecutionNew);
            }
            List<ExecutionStepHasAttachment> attachedExecutionStepHasAttachmentListNew = new ArrayList<ExecutionStepHasAttachment>();
            for (ExecutionStepHasAttachment executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach : executionStepHasAttachmentListNew) {
                executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach = em.getReference(executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach.getClass(), executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach.getExecutionStepHasAttachmentPK());
                attachedExecutionStepHasAttachmentListNew.add(executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach);
            }
            executionStepHasAttachmentListNew = attachedExecutionStepHasAttachmentListNew;
            executionStep.setExecutionStepHasAttachmentList(executionStepHasAttachmentListNew);
            List<ExecutionStepHasIssue> attachedExecutionStepHasIssueListNew = new ArrayList<ExecutionStepHasIssue>();
            for (ExecutionStepHasIssue executionStepHasIssueListNewExecutionStepHasIssueToAttach : executionStepHasIssueListNew) {
                executionStepHasIssueListNewExecutionStepHasIssueToAttach = em.getReference(executionStepHasIssueListNewExecutionStepHasIssueToAttach.getClass(), executionStepHasIssueListNewExecutionStepHasIssueToAttach.getExecutionStepHasIssuePK());
                attachedExecutionStepHasIssueListNew.add(executionStepHasIssueListNewExecutionStepHasIssueToAttach);
            }
            executionStepHasIssueListNew = attachedExecutionStepHasIssueListNew;
            executionStep.setExecutionStepHasIssueList(executionStepHasIssueListNew);
            executionStep = em.merge(executionStep);
            if (resultIdOld != null && !resultIdOld.equals(resultIdNew)) {
                resultIdOld.getExecutionStepList().remove(executionStep);
                resultIdOld = em.merge(resultIdOld);
            }
            if (resultIdNew != null && !resultIdNew.equals(resultIdOld)) {
                resultIdNew.getExecutionStepList().add(executionStep);
                resultIdNew = em.merge(resultIdNew);
            }
            if (assigneeOld != null && !assigneeOld.equals(assigneeNew)) {
                assigneeOld.getExecutionStepList().remove(executionStep);
                assigneeOld = em.merge(assigneeOld);
            }
            if (assigneeNew != null && !assigneeNew.equals(assigneeOld)) {
                assigneeNew.getExecutionStepList().add(executionStep);
                assigneeNew = em.merge(assigneeNew);
            }
            if (assignerOld != null && !assignerOld.equals(assignerNew)) {
                assignerOld.getExecutionStepList().remove(executionStep);
                assignerOld = em.merge(assignerOld);
            }
            if (assignerNew != null && !assignerNew.equals(assignerOld)) {
                assignerNew.getExecutionStepList().add(executionStep);
                assignerNew = em.merge(assignerNew);
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
            for (ExecutionStepHasIssue executionStepHasIssueListNewExecutionStepHasIssue : executionStepHasIssueListNew) {
                if (!executionStepHasIssueListOld.contains(executionStepHasIssueListNewExecutionStepHasIssue)) {
                    ExecutionStep oldExecutionStepOfExecutionStepHasIssueListNewExecutionStepHasIssue = executionStepHasIssueListNewExecutionStepHasIssue.getExecutionStep();
                    executionStepHasIssueListNewExecutionStepHasIssue.setExecutionStep(executionStep);
                    executionStepHasIssueListNewExecutionStepHasIssue = em.merge(executionStepHasIssueListNewExecutionStepHasIssue);
                    if (oldExecutionStepOfExecutionStepHasIssueListNewExecutionStepHasIssue != null && !oldExecutionStepOfExecutionStepHasIssueListNewExecutionStepHasIssue.equals(executionStep)) {
                        oldExecutionStepOfExecutionStepHasIssueListNewExecutionStepHasIssue.getExecutionStepHasIssueList().remove(executionStepHasIssueListNewExecutionStepHasIssue);
                        oldExecutionStepOfExecutionStepHasIssueListNewExecutionStepHasIssue = em.merge(oldExecutionStepOfExecutionStepHasIssueListNewExecutionStepHasIssue);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ExecutionStepPK id = executionStep.getExecutionStepPK();
                if (findExecutionStep(id) == null) {
                    throw new NonexistentEntityException("The executionStep with id " + id + " no longer exists.");
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

    public void destroy(ExecutionStepPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStep executionStep;
            try {
                executionStep = em.getReference(ExecutionStep.class, id);
                executionStep.getExecutionStepPK();
            }
            catch (EntityNotFoundException enfe) {
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
            List<ExecutionStepHasIssue> executionStepHasIssueListOrphanCheck = executionStep.getExecutionStepHasIssueList();
            for (ExecutionStepHasIssue executionStepHasIssueListOrphanCheckExecutionStepHasIssue : executionStepHasIssueListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ExecutionStep (" + executionStep + ") cannot be destroyed since the ExecutionStepHasIssue " + executionStepHasIssueListOrphanCheckExecutionStepHasIssue + " in its executionStepHasIssueList field has a non-nullable executionStep field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            ExecutionResult resultId = executionStep.getResultId();
            if (resultId != null) {
                resultId.getExecutionStepList().remove(executionStep);
                resultId = em.merge(resultId);
            }
            VmUser assignee = executionStep.getAssignee();
            if (assignee != null) {
                assignee.getExecutionStepList().remove(executionStep);
                assignee = em.merge(assignee);
            }
            VmUser assigner = executionStep.getAssigner();
            if (assigner != null) {
                assigner.getExecutionStepList().remove(executionStep);
                assigner = em.merge(assigner);
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
            em.remove(executionStep);
            em.getTransaction().commit();
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

    public ExecutionStep findExecutionStep(ExecutionStepPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ExecutionStep.class, id);
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

}
