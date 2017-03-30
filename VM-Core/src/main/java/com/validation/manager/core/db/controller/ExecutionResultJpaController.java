/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.ExecutionResult;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ExecutionResultJpaController implements Serializable {

    public ExecutionResultJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ExecutionResult executionResult) throws PreexistingEntityException, Exception {
        if (executionResult.getExecutionStepList() == null) {
            executionResult.setExecutionStepList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<ExecutionStep> attachedExecutionStepList = new ArrayList<>();
            for (ExecutionStep executionStepListExecutionStepToAttach : executionResult.getExecutionStepList()) {
                executionStepListExecutionStepToAttach = em.getReference(executionStepListExecutionStepToAttach.getClass(), executionStepListExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepList.add(executionStepListExecutionStepToAttach);
            }
            executionResult.setExecutionStepList(attachedExecutionStepList);
            em.persist(executionResult);
            for (ExecutionStep executionStepListExecutionStep : executionResult.getExecutionStepList()) {
                ExecutionResult oldResultIdOfExecutionStepListExecutionStep = executionStepListExecutionStep.getResultId();
                executionStepListExecutionStep.setResultId(executionResult);
                executionStepListExecutionStep = em.merge(executionStepListExecutionStep);
                if (oldResultIdOfExecutionStepListExecutionStep != null) {
                    oldResultIdOfExecutionStepListExecutionStep.getExecutionStepList().remove(executionStepListExecutionStep);
                    oldResultIdOfExecutionStepListExecutionStep = em.merge(oldResultIdOfExecutionStepListExecutionStep);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findExecutionResult(executionResult.getId()) != null) {
                throw new PreexistingEntityException("ExecutionResult " + executionResult + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ExecutionResult executionResult) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ExecutionResult persistentExecutionResult = em.find(ExecutionResult.class, executionResult.getId());
            List<ExecutionStep> executionStepListOld = persistentExecutionResult.getExecutionStepList();
            List<ExecutionStep> executionStepListNew = executionResult.getExecutionStepList();
            List<ExecutionStep> attachedExecutionStepListNew = new ArrayList<>();
            for (ExecutionStep executionStepListNewExecutionStepToAttach : executionStepListNew) {
                executionStepListNewExecutionStepToAttach = em.getReference(executionStepListNewExecutionStepToAttach.getClass(), executionStepListNewExecutionStepToAttach.getExecutionStepPK());
                attachedExecutionStepListNew.add(executionStepListNewExecutionStepToAttach);
            }
            executionStepListNew = attachedExecutionStepListNew;
            executionResult.setExecutionStepList(executionStepListNew);
            executionResult = em.merge(executionResult);
            for (ExecutionStep executionStepListOldExecutionStep : executionStepListOld) {
                if (!executionStepListNew.contains(executionStepListOldExecutionStep)) {
                    executionStepListOldExecutionStep.setResultId(null);
                    executionStepListOldExecutionStep = em.merge(executionStepListOldExecutionStep);
                }
            }
            for (ExecutionStep executionStepListNewExecutionStep : executionStepListNew) {
                if (!executionStepListOld.contains(executionStepListNewExecutionStep)) {
                    ExecutionResult oldResultIdOfExecutionStepListNewExecutionStep = executionStepListNewExecutionStep.getResultId();
                    executionStepListNewExecutionStep.setResultId(executionResult);
                    executionStepListNewExecutionStep = em.merge(executionStepListNewExecutionStep);
                    if (oldResultIdOfExecutionStepListNewExecutionStep != null && !oldResultIdOfExecutionStepListNewExecutionStep.equals(executionResult)) {
                        oldResultIdOfExecutionStepListNewExecutionStep.getExecutionStepList().remove(executionStepListNewExecutionStep);
                        oldResultIdOfExecutionStepListNewExecutionStep = em.merge(oldResultIdOfExecutionStepListNewExecutionStep);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = executionResult.getId();
                if (findExecutionResult(id) == null) {
                    throw new NonexistentEntityException("The executionResult with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
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
            ExecutionResult executionResult;
            try {
                executionResult = em.getReference(ExecutionResult.class, id);
                executionResult.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The executionResult with id " + id + " no longer exists.", enfe);
            }
            List<ExecutionStep> executionStepList = executionResult.getExecutionStepList();
            for (ExecutionStep executionStepListExecutionStep : executionStepList) {
                executionStepListExecutionStep.setResultId(null);
                executionStepListExecutionStep = em.merge(executionStepListExecutionStep);
            }
            em.remove(executionResult);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ExecutionResult> findExecutionResultEntities() {
        return findExecutionResultEntities(true, -1, -1);
    }

    public List<ExecutionResult> findExecutionResultEntities(int maxResults, int firstResult) {
        return findExecutionResultEntities(false, maxResults, firstResult);
    }

    private List<ExecutionResult> findExecutionResultEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ExecutionResult.class));
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

    public ExecutionResult findExecutionResult(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ExecutionResult.class, id);
        } finally {
            em.close();
        }
    }

    public int getExecutionResultCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ExecutionResult> rt = cq.from(ExecutionResult.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
