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
import com.validation.manager.core.db.ReviewResult;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ReviewResultJpaController implements Serializable {

    public ReviewResultJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ReviewResult reviewResult) {
        if (reviewResult.getExecutionStepList() == null) {
            reviewResult.setExecutionStepList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<ExecutionStep> attachedExecutionStepList = new ArrayList<>();
            for (ExecutionStep executionStepListExecutionStepToAttach : reviewResult.getExecutionStepList()) {
                executionStepListExecutionStepToAttach = em.getReference(executionStepListExecutionStepToAttach.getClass(), executionStepListExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepList.add(executionStepListExecutionStepToAttach);
            }
            reviewResult.setExecutionStepList(attachedExecutionStepList);
            em.persist(reviewResult);
            for (ExecutionStep executionStepListExecutionStep : reviewResult.getExecutionStepList()) {
                ReviewResult oldReviewResultIdOfExecutionStepListExecutionStep = executionStepListExecutionStep.getReviewResultId();
                executionStepListExecutionStep.setReviewResultId(reviewResult);
                executionStepListExecutionStep = em.merge(executionStepListExecutionStep);
                if (oldReviewResultIdOfExecutionStepListExecutionStep != null) {
                    oldReviewResultIdOfExecutionStepListExecutionStep.getExecutionStepList().remove(executionStepListExecutionStep);
                    oldReviewResultIdOfExecutionStepListExecutionStep = em.merge(oldReviewResultIdOfExecutionStepListExecutionStep);
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

    public void edit(ReviewResult reviewResult) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ReviewResult persistentReviewResult = em.find(ReviewResult.class, reviewResult.getId());
            List<ExecutionStep> executionStepListOld = persistentReviewResult.getExecutionStepList();
            List<ExecutionStep> executionStepListNew = reviewResult.getExecutionStepList();
            List<ExecutionStep> attachedExecutionStepListNew = new ArrayList<>();
            for (ExecutionStep executionStepListNewExecutionStepToAttach : executionStepListNew) {
                executionStepListNewExecutionStepToAttach = em.getReference(executionStepListNewExecutionStepToAttach.getClass(), executionStepListNewExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepListNew.add(executionStepListNewExecutionStepToAttach);
            }
            executionStepListNew = attachedExecutionStepListNew;
            reviewResult.setExecutionStepList(executionStepListNew);
            reviewResult = em.merge(reviewResult);
            for (ExecutionStep executionStepListOldExecutionStep : executionStepListOld) {
                if (!executionStepListNew.contains(executionStepListOldExecutionStep)) {
                    executionStepListOldExecutionStep.setReviewResultId(null);
                    executionStepListOldExecutionStep = em.merge(executionStepListOldExecutionStep);
                }
            }
            for (ExecutionStep executionStepListNewExecutionStep : executionStepListNew) {
                if (!executionStepListOld.contains(executionStepListNewExecutionStep)) {
                    ReviewResult oldReviewResultIdOfExecutionStepListNewExecutionStep = executionStepListNewExecutionStep.getReviewResultId();
                    executionStepListNewExecutionStep.setReviewResultId(reviewResult);
                    executionStepListNewExecutionStep = em.merge(executionStepListNewExecutionStep);
                    if (oldReviewResultIdOfExecutionStepListNewExecutionStep != null && !oldReviewResultIdOfExecutionStepListNewExecutionStep.equals(reviewResult)) {
                        oldReviewResultIdOfExecutionStepListNewExecutionStep.getExecutionStepList().remove(executionStepListNewExecutionStep);
                        oldReviewResultIdOfExecutionStepListNewExecutionStep = em.merge(oldReviewResultIdOfExecutionStepListNewExecutionStep);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = reviewResult.getId();
                if (findReviewResult(id) == null) {
                    throw new NonexistentEntityException("The reviewResult with id " + id + " no longer exists.");
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

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ReviewResult reviewResult;
            try {
                reviewResult = em.getReference(ReviewResult.class, id);
                reviewResult.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The reviewResult with id " + id + " no longer exists.", enfe);
            }
            List<ExecutionStep> executionStepList = reviewResult.getExecutionStepList();
            for (ExecutionStep executionStepListExecutionStep : executionStepList) {
                executionStepListExecutionStep.setReviewResultId(null);
                executionStepListExecutionStep = em.merge(executionStepListExecutionStep);
            }
            em.remove(reviewResult);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ReviewResult> findReviewResultEntities() {
        return findReviewResultEntities(true, -1, -1);
    }

    public List<ReviewResult> findReviewResultEntities(int maxResults, int firstResult) {
        return findReviewResultEntities(false, maxResults, firstResult);
    }

    private List<ReviewResult> findReviewResultEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ReviewResult.class));
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

    public ReviewResult findReviewResult(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ReviewResult.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getReviewResultCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ReviewResult> rt = cq.from(ReviewResult.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
