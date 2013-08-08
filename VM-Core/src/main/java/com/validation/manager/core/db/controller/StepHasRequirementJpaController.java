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
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.StepHasRequirement;
import com.validation.manager.core.db.StepHasRequirementPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class StepHasRequirementJpaController implements Serializable {

    public StepHasRequirementJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(StepHasRequirement stepHasRequirement) throws PreexistingEntityException, Exception {
        if (stepHasRequirement.getStepHasRequirementPK() == null) {
            stepHasRequirement.setStepHasRequirementPK(new StepHasRequirementPK());
        }
        stepHasRequirement.getStepHasRequirementPK().setRequirementVersion(stepHasRequirement.getRequirement().getRequirementPK().getVersion());
        stepHasRequirement.getStepHasRequirementPK().setStepTestCaseId(stepHasRequirement.getStep().getStepPK().getTestCaseId());
        stepHasRequirement.getStepHasRequirementPK().setRequirementId(stepHasRequirement.getRequirement().getRequirementPK().getId());
        stepHasRequirement.getStepHasRequirementPK().setStepId(stepHasRequirement.getStep().getStepPK().getId());
        stepHasRequirement.getStepHasRequirementPK().setStepTestCaseTestId(stepHasRequirement.getStep().getStepPK().getTestCaseTestId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Step step = stepHasRequirement.getStep();
            if (step != null) {
                step = em.getReference(step.getClass(), step.getStepPK());
                stepHasRequirement.setStep(step);
            }
            Requirement requirement = stepHasRequirement.getRequirement();
            if (requirement != null) {
                requirement = em.getReference(requirement.getClass(), requirement.getRequirementPK());
                stepHasRequirement.setRequirement(requirement);
            }
            em.persist(stepHasRequirement);
            if (step != null) {
                step.getStepHasRequirementList().add(stepHasRequirement);
                step = em.merge(step);
            }
            if (requirement != null) {
                requirement.getStepHasRequirementList().add(stepHasRequirement);
                requirement = em.merge(requirement);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findStepHasRequirement(stepHasRequirement.getStepHasRequirementPK()) != null) {
                throw new PreexistingEntityException("StepHasRequirement " + stepHasRequirement + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(StepHasRequirement stepHasRequirement) throws NonexistentEntityException, Exception {
        stepHasRequirement.getStepHasRequirementPK().setRequirementVersion(stepHasRequirement.getRequirement().getRequirementPK().getVersion());
        stepHasRequirement.getStepHasRequirementPK().setStepTestCaseId(stepHasRequirement.getStep().getStepPK().getTestCaseId());
        stepHasRequirement.getStepHasRequirementPK().setRequirementId(stepHasRequirement.getRequirement().getRequirementPK().getId());
        stepHasRequirement.getStepHasRequirementPK().setStepId(stepHasRequirement.getStep().getStepPK().getId());
        stepHasRequirement.getStepHasRequirementPK().setStepTestCaseTestId(stepHasRequirement.getStep().getStepPK().getTestCaseTestId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StepHasRequirement persistentStepHasRequirement = em.find(StepHasRequirement.class, stepHasRequirement.getStepHasRequirementPK());
            Step stepOld = persistentStepHasRequirement.getStep();
            Step stepNew = stepHasRequirement.getStep();
            Requirement requirementOld = persistentStepHasRequirement.getRequirement();
            Requirement requirementNew = stepHasRequirement.getRequirement();
            if (stepNew != null) {
                stepNew = em.getReference(stepNew.getClass(), stepNew.getStepPK());
                stepHasRequirement.setStep(stepNew);
            }
            if (requirementNew != null) {
                requirementNew = em.getReference(requirementNew.getClass(), requirementNew.getRequirementPK());
                stepHasRequirement.setRequirement(requirementNew);
            }
            stepHasRequirement = em.merge(stepHasRequirement);
            if (stepOld != null && !stepOld.equals(stepNew)) {
                stepOld.getStepHasRequirementList().remove(stepHasRequirement);
                stepOld = em.merge(stepOld);
            }
            if (stepNew != null && !stepNew.equals(stepOld)) {
                stepNew.getStepHasRequirementList().add(stepHasRequirement);
                stepNew = em.merge(stepNew);
            }
            if (requirementOld != null && !requirementOld.equals(requirementNew)) {
                requirementOld.getStepHasRequirementList().remove(stepHasRequirement);
                requirementOld = em.merge(requirementOld);
            }
            if (requirementNew != null && !requirementNew.equals(requirementOld)) {
                requirementNew.getStepHasRequirementList().add(stepHasRequirement);
                requirementNew = em.merge(requirementNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                StepHasRequirementPK id = stepHasRequirement.getStepHasRequirementPK();
                if (findStepHasRequirement(id) == null) {
                    throw new NonexistentEntityException("The stepHasRequirement with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(StepHasRequirementPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StepHasRequirement stepHasRequirement;
            try {
                stepHasRequirement = em.getReference(StepHasRequirement.class, id);
                stepHasRequirement.getStepHasRequirementPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The stepHasRequirement with id " + id + " no longer exists.", enfe);
            }
            Step step = stepHasRequirement.getStep();
            if (step != null) {
                step.getStepHasRequirementList().remove(stepHasRequirement);
                step = em.merge(step);
            }
            Requirement requirement = stepHasRequirement.getRequirement();
            if (requirement != null) {
                requirement.getStepHasRequirementList().remove(stepHasRequirement);
                requirement = em.merge(requirement);
            }
            em.remove(stepHasRequirement);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<StepHasRequirement> findStepHasRequirementEntities() {
        return findStepHasRequirementEntities(true, -1, -1);
    }

    public List<StepHasRequirement> findStepHasRequirementEntities(int maxResults, int firstResult) {
        return findStepHasRequirementEntities(false, maxResults, firstResult);
    }

    private List<StepHasRequirement> findStepHasRequirementEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(StepHasRequirement.class));
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

    public StepHasRequirement findStepHasRequirement(StepHasRequirementPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(StepHasRequirement.class, id);
        } finally {
            em.close();
        }
    }

    public int getStepHasRequirementCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<StepHasRequirement> rt = cq.from(StepHasRequirement.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
