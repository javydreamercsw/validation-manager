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
import com.validation.manager.core.db.IssueResolution;
import com.validation.manager.core.db.IssueType;
import com.validation.manager.core.db.ExecutionStepHasIssue;
import com.validation.manager.core.db.Issue;
import com.validation.manager.core.db.IssuePK;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
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
public class IssueJpaController implements Serializable {

    public IssueJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Issue issue) throws PreexistingEntityException, Exception {
        if (issue.getIssuePK() == null) {
            issue.setIssuePK(new IssuePK());
        }
        if (issue.getExecutionStepHasIssueList() == null) {
            issue.setExecutionStepHasIssueList(new ArrayList<>());
        }
        issue.getIssuePK().setIssueTypeId(issue.getIssueType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            IssueResolution issueResolutionId = issue.getIssueResolutionId();
            if (issueResolutionId != null) {
                issueResolutionId = em.getReference(issueResolutionId.getClass(), issueResolutionId.getId());
                issue.setIssueResolutionId(issueResolutionId);
            }
            IssueType issueType = issue.getIssueType();
            if (issueType != null) {
                issueType = em.getReference(issueType.getClass(), issueType.getId());
                issue.setIssueType(issueType);
            }
            List<ExecutionStepHasIssue> attachedExecutionStepHasIssueList = new ArrayList<>();
            for (ExecutionStepHasIssue executionStepHasIssueListExecutionStepHasIssueToAttach : issue.getExecutionStepHasIssueList()) {
                executionStepHasIssueListExecutionStepHasIssueToAttach = em.getReference(executionStepHasIssueListExecutionStepHasIssueToAttach.getClass(), executionStepHasIssueListExecutionStepHasIssueToAttach.getExecutionStepHasIssuePK());
                attachedExecutionStepHasIssueList.add(executionStepHasIssueListExecutionStepHasIssueToAttach);
            }
            issue.setExecutionStepHasIssueList(attachedExecutionStepHasIssueList);
            em.persist(issue);
            if (issueResolutionId != null) {
                issueResolutionId.getIssueList().add(issue);
                issueResolutionId = em.merge(issueResolutionId);
            }
            if (issueType != null) {
                issueType.getIssueList().add(issue);
                issueType = em.merge(issueType);
            }
            for (ExecutionStepHasIssue executionStepHasIssueListExecutionStepHasIssue : issue.getExecutionStepHasIssueList()) {
                Issue oldIssueOfExecutionStepHasIssueListExecutionStepHasIssue = executionStepHasIssueListExecutionStepHasIssue.getIssue();
                executionStepHasIssueListExecutionStepHasIssue.setIssue(issue);
                executionStepHasIssueListExecutionStepHasIssue = em.merge(executionStepHasIssueListExecutionStepHasIssue);
                if (oldIssueOfExecutionStepHasIssueListExecutionStepHasIssue != null) {
                    oldIssueOfExecutionStepHasIssueListExecutionStepHasIssue.getExecutionStepHasIssueList().remove(executionStepHasIssueListExecutionStepHasIssue);
                    oldIssueOfExecutionStepHasIssueListExecutionStepHasIssue = em.merge(oldIssueOfExecutionStepHasIssueListExecutionStepHasIssue);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findIssue(issue.getIssuePK()) != null) {
                throw new PreexistingEntityException("Issue " + issue + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Issue issue) throws IllegalOrphanException, NonexistentEntityException, Exception {
        issue.getIssuePK().setIssueTypeId(issue.getIssueType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Issue persistentIssue = em.find(Issue.class, issue.getIssuePK());
            IssueResolution issueResolutionIdOld = persistentIssue.getIssueResolutionId();
            IssueResolution issueResolutionIdNew = issue.getIssueResolutionId();
            IssueType issueTypeOld = persistentIssue.getIssueType();
            IssueType issueTypeNew = issue.getIssueType();
            List<ExecutionStepHasIssue> executionStepHasIssueListOld = persistentIssue.getExecutionStepHasIssueList();
            List<ExecutionStepHasIssue> executionStepHasIssueListNew = issue.getExecutionStepHasIssueList();
            List<String> illegalOrphanMessages = null;
            for (ExecutionStepHasIssue executionStepHasIssueListOldExecutionStepHasIssue : executionStepHasIssueListOld) {
                if (!executionStepHasIssueListNew.contains(executionStepHasIssueListOldExecutionStepHasIssue)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain ExecutionStepHasIssue " + executionStepHasIssueListOldExecutionStepHasIssue + " since its issue field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (issueResolutionIdNew != null) {
                issueResolutionIdNew = em.getReference(issueResolutionIdNew.getClass(), issueResolutionIdNew.getId());
                issue.setIssueResolutionId(issueResolutionIdNew);
            }
            if (issueTypeNew != null) {
                issueTypeNew = em.getReference(issueTypeNew.getClass(), issueTypeNew.getId());
                issue.setIssueType(issueTypeNew);
            }
            List<ExecutionStepHasIssue> attachedExecutionStepHasIssueListNew = new ArrayList<>();
            for (ExecutionStepHasIssue executionStepHasIssueListNewExecutionStepHasIssueToAttach : executionStepHasIssueListNew) {
                executionStepHasIssueListNewExecutionStepHasIssueToAttach = em.getReference(executionStepHasIssueListNewExecutionStepHasIssueToAttach.getClass(), executionStepHasIssueListNewExecutionStepHasIssueToAttach.getExecutionStepHasIssuePK());
                attachedExecutionStepHasIssueListNew.add(executionStepHasIssueListNewExecutionStepHasIssueToAttach);
            }
            executionStepHasIssueListNew = attachedExecutionStepHasIssueListNew;
            issue.setExecutionStepHasIssueList(executionStepHasIssueListNew);
            issue = em.merge(issue);
            if (issueResolutionIdOld != null && !issueResolutionIdOld.equals(issueResolutionIdNew)) {
                issueResolutionIdOld.getIssueList().remove(issue);
                issueResolutionIdOld = em.merge(issueResolutionIdOld);
            }
            if (issueResolutionIdNew != null && !issueResolutionIdNew.equals(issueResolutionIdOld)) {
                issueResolutionIdNew.getIssueList().add(issue);
                issueResolutionIdNew = em.merge(issueResolutionIdNew);
            }
            if (issueTypeOld != null && !issueTypeOld.equals(issueTypeNew)) {
                issueTypeOld.getIssueList().remove(issue);
                issueTypeOld = em.merge(issueTypeOld);
            }
            if (issueTypeNew != null && !issueTypeNew.equals(issueTypeOld)) {
                issueTypeNew.getIssueList().add(issue);
                issueTypeNew = em.merge(issueTypeNew);
            }
            for (ExecutionStepHasIssue executionStepHasIssueListNewExecutionStepHasIssue : executionStepHasIssueListNew) {
                if (!executionStepHasIssueListOld.contains(executionStepHasIssueListNewExecutionStepHasIssue)) {
                    Issue oldIssueOfExecutionStepHasIssueListNewExecutionStepHasIssue = executionStepHasIssueListNewExecutionStepHasIssue.getIssue();
                    executionStepHasIssueListNewExecutionStepHasIssue.setIssue(issue);
                    executionStepHasIssueListNewExecutionStepHasIssue = em.merge(executionStepHasIssueListNewExecutionStepHasIssue);
                    if (oldIssueOfExecutionStepHasIssueListNewExecutionStepHasIssue != null && !oldIssueOfExecutionStepHasIssueListNewExecutionStepHasIssue.equals(issue)) {
                        oldIssueOfExecutionStepHasIssueListNewExecutionStepHasIssue.getExecutionStepHasIssueList().remove(executionStepHasIssueListNewExecutionStepHasIssue);
                        oldIssueOfExecutionStepHasIssueListNewExecutionStepHasIssue = em.merge(oldIssueOfExecutionStepHasIssueListNewExecutionStepHasIssue);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                IssuePK id = issue.getIssuePK();
                if (findIssue(id) == null) {
                    throw new NonexistentEntityException("The issue with id " + id + " no longer exists.");
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

    public void destroy(IssuePK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Issue issue;
            try {
                issue = em.getReference(Issue.class, id);
                issue.getIssuePK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The issue with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ExecutionStepHasIssue> executionStepHasIssueListOrphanCheck = issue.getExecutionStepHasIssueList();
            for (ExecutionStepHasIssue executionStepHasIssueListOrphanCheckExecutionStepHasIssue : executionStepHasIssueListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Issue (" + issue + ") cannot be destroyed since the ExecutionStepHasIssue " + executionStepHasIssueListOrphanCheckExecutionStepHasIssue + " in its executionStepHasIssueList field has a non-nullable issue field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            IssueResolution issueResolutionId = issue.getIssueResolutionId();
            if (issueResolutionId != null) {
                issueResolutionId.getIssueList().remove(issue);
                issueResolutionId = em.merge(issueResolutionId);
            }
            IssueType issueType = issue.getIssueType();
            if (issueType != null) {
                issueType.getIssueList().remove(issue);
                issueType = em.merge(issueType);
            }
            em.remove(issue);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Issue> findIssueEntities() {
        return findIssueEntities(true, -1, -1);
    }

    public List<Issue> findIssueEntities(int maxResults, int firstResult) {
        return findIssueEntities(false, maxResults, firstResult);
    }

    private List<Issue> findIssueEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Issue.class));
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

    public Issue findIssue(IssuePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Issue.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getIssueCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Issue> rt = cq.from(Issue.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
