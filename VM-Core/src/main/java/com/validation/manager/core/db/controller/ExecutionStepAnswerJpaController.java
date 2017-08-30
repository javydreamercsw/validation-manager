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
import com.validation.manager.core.db.ExecutionStepAnswer;
import com.validation.manager.core.db.ExecutionStepAnswerPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ExecutionStepAnswerJpaController implements Serializable {

    public ExecutionStepAnswerJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ExecutionStepAnswer executionStepAnswer) throws PreexistingEntityException, Exception {
        if (executionStepAnswer.getExecutionStepAnswerPK() == null) {
            executionStepAnswer.setExecutionStepAnswerPK(new ExecutionStepAnswerPK());
        }
        executionStepAnswer.getExecutionStepAnswerPK().setExecutionStepStepTestCaseId(executionStepAnswer.getExecutionStep().getExecutionStepPK().getStepTestCaseId());
        executionStepAnswer.getExecutionStepAnswerPK().setExecutionStepStepId(executionStepAnswer.getExecutionStep().getExecutionStepPK().getStepId());
        executionStepAnswer.getExecutionStepAnswerPK().setExecutionStepTestCaseExecutionId(executionStepAnswer.getExecutionStep().getExecutionStepPK().getTestCaseExecutionId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStep executionStep = executionStepAnswer.getExecutionStep();
            if (executionStep != null) {
                executionStep = em.getReference(executionStep.getClass(), executionStep.getExecutionStepPK());
                executionStepAnswer.setExecutionStep(executionStep);
            }
            em.persist(executionStepAnswer);
            if (executionStep != null) {
                executionStep.getExecutionStepAnswerList().add(executionStepAnswer);
                executionStep = em.merge(executionStep);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findExecutionStepAnswer(executionStepAnswer.getExecutionStepAnswerPK()) != null) {
                throw new PreexistingEntityException("ExecutionStepAnswer " + executionStepAnswer + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ExecutionStepAnswer executionStepAnswer) throws NonexistentEntityException, Exception {
        executionStepAnswer.getExecutionStepAnswerPK().setExecutionStepStepTestCaseId(executionStepAnswer.getExecutionStep().getExecutionStepPK().getStepTestCaseId());
        executionStepAnswer.getExecutionStepAnswerPK().setExecutionStepStepId(executionStepAnswer.getExecutionStep().getExecutionStepPK().getStepId());
        executionStepAnswer.getExecutionStepAnswerPK().setExecutionStepTestCaseExecutionId(executionStepAnswer.getExecutionStep().getExecutionStepPK().getTestCaseExecutionId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStepAnswer persistentExecutionStepAnswer = em.find(ExecutionStepAnswer.class, executionStepAnswer.getExecutionStepAnswerPK());
            ExecutionStep executionStepOld = persistentExecutionStepAnswer.getExecutionStep();
            ExecutionStep executionStepNew = executionStepAnswer.getExecutionStep();
            if (executionStepNew != null) {
                executionStepNew = em.getReference(executionStepNew.getClass(), executionStepNew.getExecutionStepPK());
                executionStepAnswer.setExecutionStep(executionStepNew);
            }
            executionStepAnswer = em.merge(executionStepAnswer);
            if (executionStepOld != null && !executionStepOld.equals(executionStepNew)) {
                executionStepOld.getExecutionStepAnswerList().remove(executionStepAnswer);
                executionStepOld = em.merge(executionStepOld);
            }
            if (executionStepNew != null && !executionStepNew.equals(executionStepOld)) {
                executionStepNew.getExecutionStepAnswerList().add(executionStepAnswer);
                executionStepNew = em.merge(executionStepNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ExecutionStepAnswerPK id = executionStepAnswer.getExecutionStepAnswerPK();
                if (findExecutionStepAnswer(id) == null) {
                    throw new NonexistentEntityException("The executionStepAnswer with id " + id + " no longer exists.");
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

    public void destroy(ExecutionStepAnswerPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionStepAnswer executionStepAnswer;
            try {
                executionStepAnswer = em.getReference(ExecutionStepAnswer.class, id);
                executionStepAnswer.getExecutionStepAnswerPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The executionStepAnswer with id " + id + " no longer exists.", enfe);
            }
            ExecutionStep executionStep = executionStepAnswer.getExecutionStep();
            if (executionStep != null) {
                executionStep.getExecutionStepAnswerList().remove(executionStepAnswer);
                executionStep = em.merge(executionStep);
            }
            em.remove(executionStepAnswer);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ExecutionStepAnswer> findExecutionStepAnswerEntities() {
        return findExecutionStepAnswerEntities(true, -1, -1);
    }

    public List<ExecutionStepAnswer> findExecutionStepAnswerEntities(int maxResults, int firstResult) {
        return findExecutionStepAnswerEntities(false, maxResults, firstResult);
    }

    private List<ExecutionStepAnswer> findExecutionStepAnswerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ExecutionStepAnswer.class));
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

    public ExecutionStepAnswer findExecutionStepAnswer(ExecutionStepAnswerPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ExecutionStepAnswer.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getExecutionStepAnswerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ExecutionStepAnswer> rt = cq.from(ExecutionStepAnswer.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
