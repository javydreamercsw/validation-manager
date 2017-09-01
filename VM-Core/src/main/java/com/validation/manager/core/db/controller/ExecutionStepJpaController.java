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
import com.validation.manager.core.db.ExecutionResult;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.ReviewResult;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.ExecutionStepHasAttachment;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.ExecutionStepHasIssue;
import com.validation.manager.core.db.ExecutionStepHasVmUser;
import com.validation.manager.core.db.ExecutionStepAnswer;
import com.validation.manager.core.db.ExecutionStepPK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
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
            executionStep.setExecutionStepHasAttachmentList(new ArrayList<>());
        }
        if (executionStep.getExecutionStepHasIssueList() == null) {
            executionStep.setExecutionStepHasIssueList(new ArrayList<>());
        }
        if (executionStep.getExecutionStepHasVmUserList() == null) {
            executionStep.setExecutionStepHasVmUserList(new ArrayList<>());
        }
        if (executionStep.getHistoryList() == null) {
            executionStep.setHistoryList(new ArrayList<>());
        }
        if (executionStep.getExecutionStepAnswerList() == null) {
            executionStep.setExecutionStepAnswerList(new ArrayList<>());
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
            ReviewResult reviewResultId = executionStep.getReviewResultId();
            if (reviewResultId != null) {
                reviewResultId = em.getReference(reviewResultId.getClass(), reviewResultId.getId());
                executionStep.setReviewResultId(reviewResultId);
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
            VmUser reviewer = executionStep.getReviewer();
            if (reviewer != null) {
                reviewer = em.getReference(reviewer.getClass(), reviewer.getId());
                executionStep.setReviewer(reviewer);
            }
            History stepHistory = executionStep.getStepHistory();
            if (stepHistory != null) {
                stepHistory = em.getReference(stepHistory.getClass(), stepHistory.getId());
                executionStep.setStepHistory(stepHistory);
            }
            List<ExecutionStepHasAttachment> attachedExecutionStepHasAttachmentList = new ArrayList<>();
            for (ExecutionStepHasAttachment executionStepHasAttachmentListExecutionStepHasAttachmentToAttach : executionStep.getExecutionStepHasAttachmentList()) {
                executionStepHasAttachmentListExecutionStepHasAttachmentToAttach = em.getReference(executionStepHasAttachmentListExecutionStepHasAttachmentToAttach.getClass(), executionStepHasAttachmentListExecutionStepHasAttachmentToAttach.getExecutionStepHasAttachmentPK());
                attachedExecutionStepHasAttachmentList.add(executionStepHasAttachmentListExecutionStepHasAttachmentToAttach);
            }
            executionStep.setExecutionStepHasAttachmentList(attachedExecutionStepHasAttachmentList);
            List<ExecutionStepHasIssue> attachedExecutionStepHasIssueList = new ArrayList<>();
            for (ExecutionStepHasIssue executionStepHasIssueListExecutionStepHasIssueToAttach : executionStep.getExecutionStepHasIssueList()) {
                executionStepHasIssueListExecutionStepHasIssueToAttach = em.getReference(executionStepHasIssueListExecutionStepHasIssueToAttach.getClass(), executionStepHasIssueListExecutionStepHasIssueToAttach.getExecutionStepHasIssuePK());
                attachedExecutionStepHasIssueList.add(executionStepHasIssueListExecutionStepHasIssueToAttach);
            }
            executionStep.setExecutionStepHasIssueList(attachedExecutionStepHasIssueList);
            List<ExecutionStepHasVmUser> attachedExecutionStepHasVmUserList = new ArrayList<>();
            for (ExecutionStepHasVmUser executionStepHasVmUserListExecutionStepHasVmUserToAttach : executionStep.getExecutionStepHasVmUserList()) {
                executionStepHasVmUserListExecutionStepHasVmUserToAttach = em.getReference(executionStepHasVmUserListExecutionStepHasVmUserToAttach.getClass(), executionStepHasVmUserListExecutionStepHasVmUserToAttach.getExecutionStepHasVmUserPK());
                attachedExecutionStepHasVmUserList.add(executionStepHasVmUserListExecutionStepHasVmUserToAttach);
            }
            executionStep.setExecutionStepHasVmUserList(attachedExecutionStepHasVmUserList);
            List<History> attachedHistoryList = new ArrayList<>();
            for (History historyListHistoryToAttach : executionStep.getHistoryList()) {
                historyListHistoryToAttach = em.getReference(historyListHistoryToAttach.getClass(), historyListHistoryToAttach.getId());
                attachedHistoryList.add(historyListHistoryToAttach);
            }
            executionStep.setHistoryList(attachedHistoryList);
            List<ExecutionStepAnswer> attachedExecutionStepAnswerList = new ArrayList<>();
            for (ExecutionStepAnswer executionStepAnswerListExecutionStepAnswerToAttach : executionStep.getExecutionStepAnswerList()) {
                executionStepAnswerListExecutionStepAnswerToAttach = em.getReference(executionStepAnswerListExecutionStepAnswerToAttach.getClass(), executionStepAnswerListExecutionStepAnswerToAttach.getExecutionStepAnswerPK());
                attachedExecutionStepAnswerList.add(executionStepAnswerListExecutionStepAnswerToAttach);
            }
            executionStep.setExecutionStepAnswerList(attachedExecutionStepAnswerList);
            em.persist(executionStep);
            if (resultId != null) {
                resultId.getExecutionStepList().add(executionStep);
                resultId = em.merge(resultId);
            }
            if (reviewResultId != null) {
                reviewResultId.getExecutionStepList().add(executionStep);
                reviewResultId = em.merge(reviewResultId);
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
            if (reviewer != null) {
                reviewer.getExecutionStepList().add(executionStep);
                reviewer = em.merge(reviewer);
            }
            if (stepHistory != null) {
                stepHistory.getExecutionStepList().add(executionStep);
                stepHistory = em.merge(stepHistory);
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
            for (ExecutionStepHasVmUser executionStepHasVmUserListExecutionStepHasVmUser : executionStep.getExecutionStepHasVmUserList()) {
                ExecutionStep oldExecutionStepOfExecutionStepHasVmUserListExecutionStepHasVmUser = executionStepHasVmUserListExecutionStepHasVmUser.getExecutionStep();
                executionStepHasVmUserListExecutionStepHasVmUser.setExecutionStep(executionStep);
                executionStepHasVmUserListExecutionStepHasVmUser = em.merge(executionStepHasVmUserListExecutionStepHasVmUser);
                if (oldExecutionStepOfExecutionStepHasVmUserListExecutionStepHasVmUser != null) {
                    oldExecutionStepOfExecutionStepHasVmUserListExecutionStepHasVmUser.getExecutionStepHasVmUserList().remove(executionStepHasVmUserListExecutionStepHasVmUser);
                    oldExecutionStepOfExecutionStepHasVmUserListExecutionStepHasVmUser = em.merge(oldExecutionStepOfExecutionStepHasVmUserListExecutionStepHasVmUser);
                }
            }
            for (History historyListHistory : executionStep.getHistoryList()) {
                historyListHistory.getExecutionStepList().add(executionStep);
                historyListHistory = em.merge(historyListHistory);
            }
            for (ExecutionStepAnswer executionStepAnswerListExecutionStepAnswer : executionStep.getExecutionStepAnswerList()) {
                ExecutionStep oldExecutionStepOfExecutionStepAnswerListExecutionStepAnswer = executionStepAnswerListExecutionStepAnswer.getExecutionStep();
                executionStepAnswerListExecutionStepAnswer.setExecutionStep(executionStep);
                executionStepAnswerListExecutionStepAnswer = em.merge(executionStepAnswerListExecutionStepAnswer);
                if (oldExecutionStepOfExecutionStepAnswerListExecutionStepAnswer != null) {
                    oldExecutionStepOfExecutionStepAnswerListExecutionStepAnswer.getExecutionStepAnswerList().remove(executionStepAnswerListExecutionStepAnswer);
                    oldExecutionStepOfExecutionStepAnswerListExecutionStepAnswer = em.merge(oldExecutionStepOfExecutionStepAnswerListExecutionStepAnswer);
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
            ReviewResult reviewResultIdOld = persistentExecutionStep.getReviewResultId();
            ReviewResult reviewResultIdNew = executionStep.getReviewResultId();
            VmUser assigneeOld = persistentExecutionStep.getAssignee();
            VmUser assigneeNew = executionStep.getAssignee();
            VmUser assignerOld = persistentExecutionStep.getAssigner();
            VmUser assignerNew = executionStep.getAssigner();
            Step stepOld = persistentExecutionStep.getStep();
            Step stepNew = executionStep.getStep();
            TestCaseExecution testCaseExecutionOld = persistentExecutionStep.getTestCaseExecution();
            TestCaseExecution testCaseExecutionNew = executionStep.getTestCaseExecution();
            VmUser reviewerOld = persistentExecutionStep.getReviewer();
            VmUser reviewerNew = executionStep.getReviewer();
            History stepHistoryOld = persistentExecutionStep.getStepHistory();
            History stepHistoryNew = executionStep.getStepHistory();
            List<ExecutionStepHasAttachment> executionStepHasAttachmentListOld = persistentExecutionStep.getExecutionStepHasAttachmentList();
            List<ExecutionStepHasAttachment> executionStepHasAttachmentListNew = executionStep.getExecutionStepHasAttachmentList();
            List<ExecutionStepHasIssue> executionStepHasIssueListOld = persistentExecutionStep.getExecutionStepHasIssueList();
            List<ExecutionStepHasIssue> executionStepHasIssueListNew = executionStep.getExecutionStepHasIssueList();
            List<ExecutionStepHasVmUser> executionStepHasVmUserListOld = persistentExecutionStep.getExecutionStepHasVmUserList();
            List<ExecutionStepHasVmUser> executionStepHasVmUserListNew = executionStep.getExecutionStepHasVmUserList();
            List<History> historyListOld = persistentExecutionStep.getHistoryList();
            List<History> historyListNew = executionStep.getHistoryList();
            List<ExecutionStepAnswer> executionStepAnswerListOld = persistentExecutionStep.getExecutionStepAnswerList();
            List<ExecutionStepAnswer> executionStepAnswerListNew = executionStep.getExecutionStepAnswerList();
            List<String> illegalOrphanMessages = null;
            for (ExecutionStepHasAttachment executionStepHasAttachmentListOldExecutionStepHasAttachment : executionStepHasAttachmentListOld) {
                if (!executionStepHasAttachmentListNew.contains(executionStepHasAttachmentListOldExecutionStepHasAttachment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain ExecutionStepHasAttachment " + executionStepHasAttachmentListOldExecutionStepHasAttachment + " since its executionStep field is not nullable.");
                }
            }
            for (ExecutionStepHasIssue executionStepHasIssueListOldExecutionStepHasIssue : executionStepHasIssueListOld) {
                if (!executionStepHasIssueListNew.contains(executionStepHasIssueListOldExecutionStepHasIssue)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain ExecutionStepHasIssue " + executionStepHasIssueListOldExecutionStepHasIssue + " since its executionStep field is not nullable.");
                }
            }
            for (ExecutionStepHasVmUser executionStepHasVmUserListOldExecutionStepHasVmUser : executionStepHasVmUserListOld) {
                if (!executionStepHasVmUserListNew.contains(executionStepHasVmUserListOldExecutionStepHasVmUser)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain ExecutionStepHasVmUser " + executionStepHasVmUserListOldExecutionStepHasVmUser + " since its executionStep field is not nullable.");
                }
            }
            for (ExecutionStepAnswer executionStepAnswerListOldExecutionStepAnswer : executionStepAnswerListOld) {
                if (!executionStepAnswerListNew.contains(executionStepAnswerListOldExecutionStepAnswer)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain ExecutionStepAnswer " + executionStepAnswerListOldExecutionStepAnswer + " since its executionStep field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (resultIdNew != null) {
                resultIdNew = em.getReference(resultIdNew.getClass(), resultIdNew.getId());
                executionStep.setResultId(resultIdNew);
            }
            if (reviewResultIdNew != null) {
                reviewResultIdNew = em.getReference(reviewResultIdNew.getClass(), reviewResultIdNew.getId());
                executionStep.setReviewResultId(reviewResultIdNew);
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
            if (reviewerNew != null) {
                reviewerNew = em.getReference(reviewerNew.getClass(), reviewerNew.getId());
                executionStep.setReviewer(reviewerNew);
            }
            if (stepHistoryNew != null) {
                stepHistoryNew = em.getReference(stepHistoryNew.getClass(), stepHistoryNew.getId());
                executionStep.setStepHistory(stepHistoryNew);
            }
            List<ExecutionStepHasAttachment> attachedExecutionStepHasAttachmentListNew = new ArrayList<>();
            for (ExecutionStepHasAttachment executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach : executionStepHasAttachmentListNew) {
                executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach = em.getReference(executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach.getClass(), executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach.getExecutionStepHasAttachmentPK());
                attachedExecutionStepHasAttachmentListNew.add(executionStepHasAttachmentListNewExecutionStepHasAttachmentToAttach);
            }
            executionStepHasAttachmentListNew = attachedExecutionStepHasAttachmentListNew;
            executionStep.setExecutionStepHasAttachmentList(executionStepHasAttachmentListNew);
            List<ExecutionStepHasIssue> attachedExecutionStepHasIssueListNew = new ArrayList<>();
            for (ExecutionStepHasIssue executionStepHasIssueListNewExecutionStepHasIssueToAttach : executionStepHasIssueListNew) {
                executionStepHasIssueListNewExecutionStepHasIssueToAttach = em.getReference(executionStepHasIssueListNewExecutionStepHasIssueToAttach.getClass(), executionStepHasIssueListNewExecutionStepHasIssueToAttach.getExecutionStepHasIssuePK());
                attachedExecutionStepHasIssueListNew.add(executionStepHasIssueListNewExecutionStepHasIssueToAttach);
            }
            executionStepHasIssueListNew = attachedExecutionStepHasIssueListNew;
            executionStep.setExecutionStepHasIssueList(executionStepHasIssueListNew);
            List<ExecutionStepHasVmUser> attachedExecutionStepHasVmUserListNew = new ArrayList<>();
            for (ExecutionStepHasVmUser executionStepHasVmUserListNewExecutionStepHasVmUserToAttach : executionStepHasVmUserListNew) {
                executionStepHasVmUserListNewExecutionStepHasVmUserToAttach = em.getReference(executionStepHasVmUserListNewExecutionStepHasVmUserToAttach.getClass(), executionStepHasVmUserListNewExecutionStepHasVmUserToAttach.getExecutionStepHasVmUserPK());
                attachedExecutionStepHasVmUserListNew.add(executionStepHasVmUserListNewExecutionStepHasVmUserToAttach);
            }
            executionStepHasVmUserListNew = attachedExecutionStepHasVmUserListNew;
            executionStep.setExecutionStepHasVmUserList(executionStepHasVmUserListNew);
            List<History> attachedHistoryListNew = new ArrayList<>();
            for (History historyListNewHistoryToAttach : historyListNew) {
                historyListNewHistoryToAttach = em.getReference(historyListNewHistoryToAttach.getClass(), historyListNewHistoryToAttach.getId());
                attachedHistoryListNew.add(historyListNewHistoryToAttach);
            }
            historyListNew = attachedHistoryListNew;
            executionStep.setHistoryList(historyListNew);
            List<ExecutionStepAnswer> attachedExecutionStepAnswerListNew = new ArrayList<>();
            for (ExecutionStepAnswer executionStepAnswerListNewExecutionStepAnswerToAttach : executionStepAnswerListNew) {
                executionStepAnswerListNewExecutionStepAnswerToAttach = em.getReference(executionStepAnswerListNewExecutionStepAnswerToAttach.getClass(), executionStepAnswerListNewExecutionStepAnswerToAttach.getExecutionStepAnswerPK());
                attachedExecutionStepAnswerListNew.add(executionStepAnswerListNewExecutionStepAnswerToAttach);
            }
            executionStepAnswerListNew = attachedExecutionStepAnswerListNew;
            executionStep.setExecutionStepAnswerList(executionStepAnswerListNew);
            executionStep = em.merge(executionStep);
            if (resultIdOld != null && !resultIdOld.equals(resultIdNew)) {
                resultIdOld.getExecutionStepList().remove(executionStep);
                resultIdOld = em.merge(resultIdOld);
            }
            if (resultIdNew != null && !resultIdNew.equals(resultIdOld)) {
                resultIdNew.getExecutionStepList().add(executionStep);
                resultIdNew = em.merge(resultIdNew);
            }
            if (reviewResultIdOld != null && !reviewResultIdOld.equals(reviewResultIdNew)) {
                reviewResultIdOld.getExecutionStepList().remove(executionStep);
                reviewResultIdOld = em.merge(reviewResultIdOld);
            }
            if (reviewResultIdNew != null && !reviewResultIdNew.equals(reviewResultIdOld)) {
                reviewResultIdNew.getExecutionStepList().add(executionStep);
                reviewResultIdNew = em.merge(reviewResultIdNew);
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
            if (reviewerOld != null && !reviewerOld.equals(reviewerNew)) {
                reviewerOld.getExecutionStepList().remove(executionStep);
                reviewerOld = em.merge(reviewerOld);
            }
            if (reviewerNew != null && !reviewerNew.equals(reviewerOld)) {
                reviewerNew.getExecutionStepList().add(executionStep);
                reviewerNew = em.merge(reviewerNew);
            }
            if (stepHistoryOld != null && !stepHistoryOld.equals(stepHistoryNew)) {
                stepHistoryOld.getExecutionStepList().remove(executionStep);
                stepHistoryOld = em.merge(stepHistoryOld);
            }
            if (stepHistoryNew != null && !stepHistoryNew.equals(stepHistoryOld)) {
                stepHistoryNew.getExecutionStepList().add(executionStep);
                stepHistoryNew = em.merge(stepHistoryNew);
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
            for (ExecutionStepHasVmUser executionStepHasVmUserListNewExecutionStepHasVmUser : executionStepHasVmUserListNew) {
                if (!executionStepHasVmUserListOld.contains(executionStepHasVmUserListNewExecutionStepHasVmUser)) {
                    ExecutionStep oldExecutionStepOfExecutionStepHasVmUserListNewExecutionStepHasVmUser = executionStepHasVmUserListNewExecutionStepHasVmUser.getExecutionStep();
                    executionStepHasVmUserListNewExecutionStepHasVmUser.setExecutionStep(executionStep);
                    executionStepHasVmUserListNewExecutionStepHasVmUser = em.merge(executionStepHasVmUserListNewExecutionStepHasVmUser);
                    if (oldExecutionStepOfExecutionStepHasVmUserListNewExecutionStepHasVmUser != null && !oldExecutionStepOfExecutionStepHasVmUserListNewExecutionStepHasVmUser.equals(executionStep)) {
                        oldExecutionStepOfExecutionStepHasVmUserListNewExecutionStepHasVmUser.getExecutionStepHasVmUserList().remove(executionStepHasVmUserListNewExecutionStepHasVmUser);
                        oldExecutionStepOfExecutionStepHasVmUserListNewExecutionStepHasVmUser = em.merge(oldExecutionStepOfExecutionStepHasVmUserListNewExecutionStepHasVmUser);
                    }
                }
            }
            for (History historyListOldHistory : historyListOld) {
                if (!historyListNew.contains(historyListOldHistory)) {
                    historyListOldHistory.getExecutionStepList().remove(executionStep);
                    historyListOldHistory = em.merge(historyListOldHistory);
                }
            }
            for (History historyListNewHistory : historyListNew) {
                if (!historyListOld.contains(historyListNewHistory)) {
                    historyListNewHistory.getExecutionStepList().add(executionStep);
                    historyListNewHistory = em.merge(historyListNewHistory);
                }
            }
            for (ExecutionStepAnswer executionStepAnswerListNewExecutionStepAnswer : executionStepAnswerListNew) {
                if (!executionStepAnswerListOld.contains(executionStepAnswerListNewExecutionStepAnswer)) {
                    ExecutionStep oldExecutionStepOfExecutionStepAnswerListNewExecutionStepAnswer = executionStepAnswerListNewExecutionStepAnswer.getExecutionStep();
                    executionStepAnswerListNewExecutionStepAnswer.setExecutionStep(executionStep);
                    executionStepAnswerListNewExecutionStepAnswer = em.merge(executionStepAnswerListNewExecutionStepAnswer);
                    if (oldExecutionStepOfExecutionStepAnswerListNewExecutionStepAnswer != null && !oldExecutionStepOfExecutionStepAnswerListNewExecutionStepAnswer.equals(executionStep)) {
                        oldExecutionStepOfExecutionStepAnswerListNewExecutionStepAnswer.getExecutionStepAnswerList().remove(executionStepAnswerListNewExecutionStepAnswer);
                        oldExecutionStepOfExecutionStepAnswerListNewExecutionStepAnswer = em.merge(oldExecutionStepOfExecutionStepAnswerListNewExecutionStepAnswer);
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
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This ExecutionStep (" + executionStep + ") cannot be destroyed since the ExecutionStepHasAttachment " + executionStepHasAttachmentListOrphanCheckExecutionStepHasAttachment + " in its executionStepHasAttachmentList field has a non-nullable executionStep field.");
            }
            List<ExecutionStepHasIssue> executionStepHasIssueListOrphanCheck = executionStep.getExecutionStepHasIssueList();
            for (ExecutionStepHasIssue executionStepHasIssueListOrphanCheckExecutionStepHasIssue : executionStepHasIssueListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This ExecutionStep (" + executionStep + ") cannot be destroyed since the ExecutionStepHasIssue " + executionStepHasIssueListOrphanCheckExecutionStepHasIssue + " in its executionStepHasIssueList field has a non-nullable executionStep field.");
            }
            List<ExecutionStepHasVmUser> executionStepHasVmUserListOrphanCheck = executionStep.getExecutionStepHasVmUserList();
            for (ExecutionStepHasVmUser executionStepHasVmUserListOrphanCheckExecutionStepHasVmUser : executionStepHasVmUserListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This ExecutionStep (" + executionStep + ") cannot be destroyed since the ExecutionStepHasVmUser " + executionStepHasVmUserListOrphanCheckExecutionStepHasVmUser + " in its executionStepHasVmUserList field has a non-nullable executionStep field.");
            }
            List<ExecutionStepAnswer> executionStepAnswerListOrphanCheck = executionStep.getExecutionStepAnswerList();
            for (ExecutionStepAnswer executionStepAnswerListOrphanCheckExecutionStepAnswer : executionStepAnswerListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This ExecutionStep (" + executionStep + ") cannot be destroyed since the ExecutionStepAnswer " + executionStepAnswerListOrphanCheckExecutionStepAnswer + " in its executionStepAnswerList field has a non-nullable executionStep field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            ExecutionResult resultId = executionStep.getResultId();
            if (resultId != null) {
                resultId.getExecutionStepList().remove(executionStep);
                resultId = em.merge(resultId);
            }
            ReviewResult reviewResultId = executionStep.getReviewResultId();
            if (reviewResultId != null) {
                reviewResultId.getExecutionStepList().remove(executionStep);
                reviewResultId = em.merge(reviewResultId);
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
            VmUser reviewer = executionStep.getReviewer();
            if (reviewer != null) {
                reviewer.getExecutionStepList().remove(executionStep);
                reviewer = em.merge(reviewer);
            }
            History stepHistory = executionStep.getStepHistory();
            if (stepHistory != null) {
                stepHistory.getExecutionStepList().remove(executionStep);
                stepHistory = em.merge(stepHistory);
            }
            List<History> historyList = executionStep.getHistoryList();
            for (History historyListHistory : historyList) {
                historyListHistory.getExecutionStepList().remove(executionStep);
                historyListHistory = em.merge(historyListHistory);
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
