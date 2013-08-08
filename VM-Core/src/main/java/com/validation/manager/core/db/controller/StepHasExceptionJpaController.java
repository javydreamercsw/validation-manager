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
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.StepHasException;
import com.validation.manager.core.db.StepHasExceptionPK;
import com.validation.manager.core.db.VmException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class StepHasExceptionJpaController implements Serializable {

    public StepHasExceptionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(StepHasException stepHasException) throws PreexistingEntityException, Exception {
        if (stepHasException.getStepHasExceptionPK() == null) {
            stepHasException.setStepHasExceptionPK(new StepHasExceptionPK());
        }
        stepHasException.getStepHasExceptionPK().setStepTestCaseTestId(stepHasException.getStep().getStepPK().getTestCaseTestId());
        stepHasException.getStepHasExceptionPK().setExceptionId(stepHasException.getVmException().getVmExceptionPK().getId());
        stepHasException.getStepHasExceptionPK().setStepId(stepHasException.getStep().getStepPK().getId());
        stepHasException.getStepHasExceptionPK().setExceptionReporterId(stepHasException.getVmException().getVmExceptionPK().getReporterId());
        stepHasException.getStepHasExceptionPK().setStepTestCaseId(stepHasException.getStep().getStepPK().getTestCaseId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Step step = stepHasException.getStep();
            if (step != null) {
                step = em.getReference(step.getClass(), step.getStepPK());
                stepHasException.setStep(step);
            }
            VmException vmException = stepHasException.getVmException();
            if (vmException != null) {
                vmException = em.getReference(vmException.getClass(), vmException.getVmExceptionPK());
                stepHasException.setVmException(vmException);
            }
            em.persist(stepHasException);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findStepHasException(stepHasException.getStepHasExceptionPK()) != null) {
                throw new PreexistingEntityException("StepHasException " + stepHasException + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(StepHasException stepHasException) throws NonexistentEntityException, Exception {
        stepHasException.getStepHasExceptionPK().setStepTestCaseTestId(stepHasException.getStep().getStepPK().getTestCaseTestId());
        stepHasException.getStepHasExceptionPK().setExceptionId(stepHasException.getVmException().getVmExceptionPK().getId());
        stepHasException.getStepHasExceptionPK().setStepId(stepHasException.getStep().getStepPK().getId());
        stepHasException.getStepHasExceptionPK().setExceptionReporterId(stepHasException.getVmException().getVmExceptionPK().getReporterId());
        stepHasException.getStepHasExceptionPK().setStepTestCaseId(stepHasException.getStep().getStepPK().getTestCaseId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StepHasException persistentStepHasException = em.find(StepHasException.class, stepHasException.getStepHasExceptionPK());
            Step stepOld = persistentStepHasException.getStep();
            Step stepNew = stepHasException.getStep();
            VmException vmExceptionOld = persistentStepHasException.getVmException();
            VmException vmExceptionNew = stepHasException.getVmException();
            if (stepNew != null) {
                stepNew = em.getReference(stepNew.getClass(), stepNew.getStepPK());
                stepHasException.setStep(stepNew);
            }
            if (vmExceptionNew != null) {
                vmExceptionNew = em.getReference(vmExceptionNew.getClass(), vmExceptionNew.getVmExceptionPK());
                stepHasException.setVmException(vmExceptionNew);
            }
            stepHasException = em.merge(stepHasException);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                StepHasExceptionPK id = stepHasException.getStepHasExceptionPK();
                if (findStepHasException(id) == null) {
                    throw new NonexistentEntityException("The stepHasException with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(StepHasExceptionPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StepHasException stepHasException;
            try {
                stepHasException = em.getReference(StepHasException.class, id);
                stepHasException.getStepHasExceptionPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The stepHasException with id " + id + " no longer exists.", enfe);
            }
            em.remove(stepHasException);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<StepHasException> findStepHasExceptionEntities() {
        return findStepHasExceptionEntities(true, -1, -1);
    }

    public List<StepHasException> findStepHasExceptionEntities(int maxResults, int firstResult) {
        return findStepHasExceptionEntities(false, maxResults, firstResult);
    }

    private List<StepHasException> findStepHasExceptionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(StepHasException.class));
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

    public StepHasException findStepHasException(StepHasExceptionPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(StepHasException.class, id);
        } finally {
            em.close();
        }
    }

    public int getStepHasExceptionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<StepHasException> rt = cq.from(StepHasException.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
