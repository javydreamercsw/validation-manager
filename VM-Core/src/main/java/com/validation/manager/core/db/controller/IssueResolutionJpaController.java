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
import com.validation.manager.core.db.Issue;
import com.validation.manager.core.db.IssueResolution;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class IssueResolutionJpaController implements Serializable {

    public IssueResolutionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(IssueResolution issueResolution) {
        if (issueResolution.getIssueList() == null) {
            issueResolution.setIssueList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Issue> attachedIssueList = new ArrayList<>();
            for (Issue issueListIssueToAttach : issueResolution.getIssueList()) {
                issueListIssueToAttach = em.getReference(issueListIssueToAttach.getClass(), issueListIssueToAttach.getIssuePK());
                attachedIssueList.add(issueListIssueToAttach);
            }
            issueResolution.setIssueList(attachedIssueList);
            em.persist(issueResolution);
            for (Issue issueListIssue : issueResolution.getIssueList()) {
                IssueResolution oldIssueResolutionIdOfIssueListIssue = issueListIssue.getIssueResolutionId();
                issueListIssue.setIssueResolutionId(issueResolution);
                issueListIssue = em.merge(issueListIssue);
                if (oldIssueResolutionIdOfIssueListIssue != null) {
                    oldIssueResolutionIdOfIssueListIssue.getIssueList().remove(issueListIssue);
                    oldIssueResolutionIdOfIssueListIssue = em.merge(oldIssueResolutionIdOfIssueListIssue);
                }
            }
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(IssueResolution issueResolution) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            IssueResolution persistentIssueResolution = em.find(IssueResolution.class, issueResolution.getId());
            List<Issue> issueListOld = persistentIssueResolution.getIssueList();
            List<Issue> issueListNew = issueResolution.getIssueList();
            List<String> illegalOrphanMessages = null;
            for (Issue issueListOldIssue : issueListOld) {
                if (!issueListNew.contains(issueListOldIssue)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Issue " + issueListOldIssue + " since its issueResolutionId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Issue> attachedIssueListNew = new ArrayList<>();
            for (Issue issueListNewIssueToAttach : issueListNew) {
                issueListNewIssueToAttach = em.getReference(issueListNewIssueToAttach.getClass(), issueListNewIssueToAttach.getIssuePK());
                attachedIssueListNew.add(issueListNewIssueToAttach);
            }
            issueListNew = attachedIssueListNew;
            issueResolution.setIssueList(issueListNew);
            issueResolution = em.merge(issueResolution);
            for (Issue issueListNewIssue : issueListNew) {
                if (!issueListOld.contains(issueListNewIssue)) {
                    IssueResolution oldIssueResolutionIdOfIssueListNewIssue = issueListNewIssue.getIssueResolutionId();
                    issueListNewIssue.setIssueResolutionId(issueResolution);
                    issueListNewIssue = em.merge(issueListNewIssue);
                    if (oldIssueResolutionIdOfIssueListNewIssue != null && !oldIssueResolutionIdOfIssueListNewIssue.equals(issueResolution)) {
                        oldIssueResolutionIdOfIssueListNewIssue.getIssueList().remove(issueListNewIssue);
                        oldIssueResolutionIdOfIssueListNewIssue = em.merge(oldIssueResolutionIdOfIssueListNewIssue);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = issueResolution.getId();
                if (findIssueResolution(id) == null) {
                    throw new NonexistentEntityException("The issueResolution with id " + id + " no longer exists.");
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

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            IssueResolution issueResolution;
            try {
                issueResolution = em.getReference(IssueResolution.class, id);
                issueResolution.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The issueResolution with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Issue> issueListOrphanCheck = issueResolution.getIssueList();
            for (Issue issueListOrphanCheckIssue : issueListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This IssueResolution (" + issueResolution + ") cannot be destroyed since the Issue " + issueListOrphanCheckIssue + " in its issueList field has a non-nullable issueResolutionId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(issueResolution);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<IssueResolution> findIssueResolutionEntities() {
        return findIssueResolutionEntities(true, -1, -1);
    }

    public List<IssueResolution> findIssueResolutionEntities(int maxResults, int firstResult) {
        return findIssueResolutionEntities(false, maxResults, firstResult);
    }

    private List<IssueResolution> findIssueResolutionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(IssueResolution.class));
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

    public IssueResolution findIssueResolution(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(IssueResolution.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getIssueResolutionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<IssueResolution> rt = cq.from(IssueResolution.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
