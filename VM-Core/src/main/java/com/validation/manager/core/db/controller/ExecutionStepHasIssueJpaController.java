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
import com.validation.manager.core.db.ExecutionStepHasIssue;
import com.validation.manager.core.db.ExecutionStepHasIssuePK;
import com.validation.manager.core.db.Issue;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ExecutionStepHasIssueJpaController implements Serializable {

    public ExecutionStepHasIssueJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ExecutionStepHasIssue executionStepHasIssue) throws PreexistingEntityException, Exception {
        if (executionStepHasIssue.getExecutionStepHasIssuePK() == null) {
            executionStepHasIssue.setExecutionStepHasIssuePK(new ExecutionStepHasIssuePK());
        }
        if (executionStepHasIssue.getVmUserList() == null) {
            executionStepHasIssue.setVmUserList(new ArrayList<>());
        }
        executionStepHasIssue.getExecutionStepHasIssuePK().setExecutionStepStepId(executionStepHasIssue.getExecutionStep().getExecutionStepPK().getStepId());
        executionStepHasIssue.getExecutionStepHasIssuePK().setExecutionStepStepTestCaseId(executionStepHasIssue.getExecutionStep().getExecutionStepPK().getStepTestCaseId());
        executionStepHasIssue.getExecutionStepHasIssuePK().setIssueId(executionStepHasIssue.getIssue().getIssuePK().getId());
        executionStepHasIssue.getExecutionStepHasIssuePK().setExecutionStepTestCaseExecutionId(executionStepHasIssue.getExecutionStep().getExecutionStepPK().getTestCaseExecutionId());
        executionStepHasIssue.getExecutionStepHasIssuePK().setIssueIssueTypeId(executionStepHasIssue.getIssue().getIssuePK().getIssueTypeId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStep executionStep = executionStepHasIssue.getExecutionStep();
            if (executionStep != null) {
                executionStep = em.getReference(executionStep.getClass(), executionStep.getExecutionStepPK());
                executionStepHasIssue.setExecutionStep(executionStep);
            }
            Issue issue = executionStepHasIssue.getIssue();
            if (issue != null) {
                issue = em.getReference(issue.getClass(), issue.getIssuePK());
                executionStepHasIssue.setIssue(issue);
            }
            List<VmUser> attachedVmUserList = new ArrayList<>();
            for (VmUser vmUserListVmUserToAttach : executionStepHasIssue.getVmUserList()) {
                vmUserListVmUserToAttach = em.getReference(vmUserListVmUserToAttach.getClass(), vmUserListVmUserToAttach.getId());
                attachedVmUserList.add(vmUserListVmUserToAttach);
            }
            executionStepHasIssue.setVmUserList(attachedVmUserList);
            em.persist(executionStepHasIssue);
            if (executionStep != null) {
                executionStep.getExecutionStepHasIssueList().add(executionStepHasIssue);
                executionStep = em.merge(executionStep);
            }
            if (issue != null) {
                issue.getExecutionStepHasIssueList().add(executionStepHasIssue);
                issue = em.merge(issue);
            }
            for (VmUser vmUserListVmUser : executionStepHasIssue.getVmUserList()) {
                vmUserListVmUser.getExecutionStepHasIssueList().add(executionStepHasIssue);
                vmUserListVmUser = em.merge(vmUserListVmUser);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findExecutionStepHasIssue(executionStepHasIssue.getExecutionStepHasIssuePK()) != null) {
                throw new PreexistingEntityException("ExecutionStepHasIssue " + executionStepHasIssue + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ExecutionStepHasIssue executionStepHasIssue) throws NonexistentEntityException, Exception {
        executionStepHasIssue.getExecutionStepHasIssuePK().setExecutionStepStepId(executionStepHasIssue.getExecutionStep().getExecutionStepPK().getStepId());
        executionStepHasIssue.getExecutionStepHasIssuePK().setExecutionStepStepTestCaseId(executionStepHasIssue.getExecutionStep().getExecutionStepPK().getStepTestCaseId());
        executionStepHasIssue.getExecutionStepHasIssuePK().setIssueId(executionStepHasIssue.getIssue().getIssuePK().getId());
        executionStepHasIssue.getExecutionStepHasIssuePK().setExecutionStepTestCaseExecutionId(executionStepHasIssue.getExecutionStep().getExecutionStepPK().getTestCaseExecutionId());
        executionStepHasIssue.getExecutionStepHasIssuePK().setIssueIssueTypeId(executionStepHasIssue.getIssue().getIssuePK().getIssueTypeId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStepHasIssue persistentExecutionStepHasIssue = em.find(ExecutionStepHasIssue.class, executionStepHasIssue.getExecutionStepHasIssuePK());
            ExecutionStep executionStepOld = persistentExecutionStepHasIssue.getExecutionStep();
            ExecutionStep executionStepNew = executionStepHasIssue.getExecutionStep();
            Issue issueOld = persistentExecutionStepHasIssue.getIssue();
            Issue issueNew = executionStepHasIssue.getIssue();
            List<VmUser> vmUserListOld = persistentExecutionStepHasIssue.getVmUserList();
            List<VmUser> vmUserListNew = executionStepHasIssue.getVmUserList();
            if (executionStepNew != null) {
                executionStepNew = em.getReference(executionStepNew.getClass(), executionStepNew.getExecutionStepPK());
                executionStepHasIssue.setExecutionStep(executionStepNew);
            }
            if (issueNew != null) {
                issueNew = em.getReference(issueNew.getClass(), issueNew.getIssuePK());
                executionStepHasIssue.setIssue(issueNew);
            }
            List<VmUser> attachedVmUserListNew = new ArrayList<>();
            for (VmUser vmUserListNewVmUserToAttach : vmUserListNew) {
                vmUserListNewVmUserToAttach = em.getReference(vmUserListNewVmUserToAttach.getClass(), vmUserListNewVmUserToAttach.getId());
                attachedVmUserListNew.add(vmUserListNewVmUserToAttach);
            }
            vmUserListNew = attachedVmUserListNew;
            executionStepHasIssue.setVmUserList(vmUserListNew);
            executionStepHasIssue = em.merge(executionStepHasIssue);
            if (executionStepOld != null && !executionStepOld.equals(executionStepNew)) {
                executionStepOld.getExecutionStepHasIssueList().remove(executionStepHasIssue);
                executionStepOld = em.merge(executionStepOld);
            }
            if (executionStepNew != null && !executionStepNew.equals(executionStepOld)) {
                executionStepNew.getExecutionStepHasIssueList().add(executionStepHasIssue);
                executionStepNew = em.merge(executionStepNew);
            }
            if (issueOld != null && !issueOld.equals(issueNew)) {
                issueOld.getExecutionStepHasIssueList().remove(executionStepHasIssue);
                issueOld = em.merge(issueOld);
            }
            if (issueNew != null && !issueNew.equals(issueOld)) {
                issueNew.getExecutionStepHasIssueList().add(executionStepHasIssue);
                issueNew = em.merge(issueNew);
            }
            for (VmUser vmUserListOldVmUser : vmUserListOld) {
                if (!vmUserListNew.contains(vmUserListOldVmUser)) {
                    vmUserListOldVmUser.getExecutionStepHasIssueList().remove(executionStepHasIssue);
                    vmUserListOldVmUser = em.merge(vmUserListOldVmUser);
                }
            }
            for (VmUser vmUserListNewVmUser : vmUserListNew) {
                if (!vmUserListOld.contains(vmUserListNewVmUser)) {
                    vmUserListNewVmUser.getExecutionStepHasIssueList().add(executionStepHasIssue);
                    vmUserListNewVmUser = em.merge(vmUserListNewVmUser);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ExecutionStepHasIssuePK id = executionStepHasIssue.getExecutionStepHasIssuePK();
                if (findExecutionStepHasIssue(id) == null) {
                    throw new NonexistentEntityException("The executionStepHasIssue with id " + id + " no longer exists.");
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

    public void destroy(ExecutionStepHasIssuePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStepHasIssue executionStepHasIssue;
            try {
                executionStepHasIssue = em.getReference(ExecutionStepHasIssue.class, id);
                executionStepHasIssue.getExecutionStepHasIssuePK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The executionStepHasIssue with id " + id + " no longer exists.", enfe);
            }
            ExecutionStep executionStep = executionStepHasIssue.getExecutionStep();
            if (executionStep != null) {
                executionStep.getExecutionStepHasIssueList().remove(executionStepHasIssue);
                executionStep = em.merge(executionStep);
            }
            Issue issue = executionStepHasIssue.getIssue();
            if (issue != null) {
                issue.getExecutionStepHasIssueList().remove(executionStepHasIssue);
                issue = em.merge(issue);
            }
            List<VmUser> vmUserList = executionStepHasIssue.getVmUserList();
            for (VmUser vmUserListVmUser : vmUserList) {
                vmUserListVmUser.getExecutionStepHasIssueList().remove(executionStepHasIssue);
                vmUserListVmUser = em.merge(vmUserListVmUser);
            }
            em.remove(executionStepHasIssue);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ExecutionStepHasIssue> findExecutionStepHasIssueEntities() {
        return findExecutionStepHasIssueEntities(true, -1, -1);
    }

    public List<ExecutionStepHasIssue> findExecutionStepHasIssueEntities(int maxResults, int firstResult) {
        return findExecutionStepHasIssueEntities(false, maxResults, firstResult);
    }

    private List<ExecutionStepHasIssue> findExecutionStepHasIssueEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ExecutionStepHasIssue.class));
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

    public ExecutionStepHasIssue findExecutionStepHasIssue(ExecutionStepHasIssuePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ExecutionStepHasIssue.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getExecutionStepHasIssueCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ExecutionStepHasIssue> rt = cq.from(ExecutionStepHasIssue.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
