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
import com.validation.manager.core.db.IssueType;
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
public class IssueTypeJpaController implements Serializable {

    public IssueTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(IssueType issueType) {
        if (issueType.getIssueList() == null) {
            issueType.setIssueList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Issue> attachedIssueList = new ArrayList<>();
            for (Issue issueListIssueToAttach : issueType.getIssueList()) {
                issueListIssueToAttach = em.getReference(issueListIssueToAttach.getClass(), issueListIssueToAttach.getIssuePK());
                attachedIssueList.add(issueListIssueToAttach);
            }
            issueType.setIssueList(attachedIssueList);
            em.persist(issueType);
            for (Issue issueListIssue : issueType.getIssueList()) {
                IssueType oldIssueTypeOfIssueListIssue = issueListIssue.getIssueType();
                issueListIssue.setIssueType(issueType);
                issueListIssue = em.merge(issueListIssue);
                if (oldIssueTypeOfIssueListIssue != null) {
                    oldIssueTypeOfIssueListIssue.getIssueList().remove(issueListIssue);
                    oldIssueTypeOfIssueListIssue = em.merge(oldIssueTypeOfIssueListIssue);
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

    public void edit(IssueType issueType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            IssueType persistentIssueType = em.find(IssueType.class, issueType.getId());
            List<Issue> issueListOld = persistentIssueType.getIssueList();
            List<Issue> issueListNew = issueType.getIssueList();
            List<String> illegalOrphanMessages = null;
            for (Issue issueListOldIssue : issueListOld) {
                if (!issueListNew.contains(issueListOldIssue)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Issue " + issueListOldIssue + " since its issueType field is not nullable.");
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
            issueType.setIssueList(issueListNew);
            issueType = em.merge(issueType);
            for (Issue issueListNewIssue : issueListNew) {
                if (!issueListOld.contains(issueListNewIssue)) {
                    IssueType oldIssueTypeOfIssueListNewIssue = issueListNewIssue.getIssueType();
                    issueListNewIssue.setIssueType(issueType);
                    issueListNewIssue = em.merge(issueListNewIssue);
                    if (oldIssueTypeOfIssueListNewIssue != null && !oldIssueTypeOfIssueListNewIssue.equals(issueType)) {
                        oldIssueTypeOfIssueListNewIssue.getIssueList().remove(issueListNewIssue);
                        oldIssueTypeOfIssueListNewIssue = em.merge(oldIssueTypeOfIssueListNewIssue);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = issueType.getId();
                if (findIssueType(id) == null) {
                    throw new NonexistentEntityException("The issueType with id " + id + " no longer exists.");
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
            IssueType issueType;
            try {
                issueType = em.getReference(IssueType.class, id);
                issueType.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The issueType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Issue> issueListOrphanCheck = issueType.getIssueList();
            for (Issue issueListOrphanCheckIssue : issueListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This IssueType (" + issueType + ") cannot be destroyed since the Issue " + issueListOrphanCheckIssue + " in its issueList field has a non-nullable issueType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(issueType);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<IssueType> findIssueTypeEntities() {
        return findIssueTypeEntities(true, -1, -1);
    }

    public List<IssueType> findIssueTypeEntities(int maxResults, int firstResult) {
        return findIssueTypeEntities(false, maxResults, firstResult);
    }

    private List<IssueType> findIssueTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(IssueType.class));
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

    public IssueType findIssueType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(IssueType.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getIssueTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<IssueType> rt = cq.from(IssueType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
