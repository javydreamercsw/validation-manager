/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.Baseline;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class BaselineJpaController implements Serializable {

    public BaselineJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Baseline baseline) {
        if (baseline.getRequirementList() == null) {
            baseline.setRequirementList(new ArrayList<Requirement>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Requirement> attachedRequirementList = new ArrayList<Requirement>();
            for (Requirement requirementListRequirementToAttach : baseline.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getId());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            baseline.setRequirementList(attachedRequirementList);
            em.persist(baseline);
            for (Requirement requirementListRequirement : baseline.getRequirementList()) {
                requirementListRequirement.getBaselineList().add(baseline);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Baseline baseline) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Baseline persistentBaseline = em.find(Baseline.class, baseline.getId());
            List<Requirement> requirementListOld = persistentBaseline.getRequirementList();
            List<Requirement> requirementListNew = baseline.getRequirementList();
            List<Requirement> attachedRequirementListNew = new ArrayList<Requirement>();
            for (Requirement requirementListNewRequirementToAttach : requirementListNew) {
                requirementListNewRequirementToAttach = em.getReference(requirementListNewRequirementToAttach.getClass(), requirementListNewRequirementToAttach.getId());
                attachedRequirementListNew.add(requirementListNewRequirementToAttach);
            }
            requirementListNew = attachedRequirementListNew;
            baseline.setRequirementList(requirementListNew);
            baseline = em.merge(baseline);
            for (Requirement requirementListOldRequirement : requirementListOld) {
                if (!requirementListNew.contains(requirementListOldRequirement)) {
                    requirementListOldRequirement.getBaselineList().remove(baseline);
                    requirementListOldRequirement = em.merge(requirementListOldRequirement);
                }
            }
            for (Requirement requirementListNewRequirement : requirementListNew) {
                if (!requirementListOld.contains(requirementListNewRequirement)) {
                    requirementListNewRequirement.getBaselineList().add(baseline);
                    requirementListNewRequirement = em.merge(requirementListNewRequirement);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = baseline.getId();
                if (findBaseline(id) == null) {
                    throw new NonexistentEntityException("The baseline with id " + id + " no longer exists.");
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
            Baseline baseline;
            try {
                baseline = em.getReference(Baseline.class, id);
                baseline.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The baseline with id " + id + " no longer exists.", enfe);
            }
            List<Requirement> requirementList = baseline.getRequirementList();
            for (Requirement requirementListRequirement : requirementList) {
                requirementListRequirement.getBaselineList().remove(baseline);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            em.remove(baseline);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Baseline> findBaselineEntities() {
        return findBaselineEntities(true, -1, -1);
    }

    public List<Baseline> findBaselineEntities(int maxResults, int firstResult) {
        return findBaselineEntities(false, maxResults, firstResult);
    }

    private List<Baseline> findBaselineEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Baseline.class));
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

    public Baseline findBaseline(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Baseline.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getBaselineCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Baseline> rt = cq.from(Baseline.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
