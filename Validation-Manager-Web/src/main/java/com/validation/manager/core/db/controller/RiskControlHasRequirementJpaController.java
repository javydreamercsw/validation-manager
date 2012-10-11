/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RiskControlHasRequirement;
import com.validation.manager.core.db.RiskControlHasRequirementPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import com.validation.manager.core.db.fmea.RiskControl;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RiskControlHasRequirementJpaController implements Serializable {

    public RiskControlHasRequirementJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RiskControlHasRequirement riskControlHasRequirement) throws PreexistingEntityException, Exception {
        if (riskControlHasRequirement.getRiskControlHasRequirementPK() == null) {
            riskControlHasRequirement.setRiskControlHasRequirementPK(new RiskControlHasRequirementPK());
        }
        riskControlHasRequirement.getRiskControlHasRequirementPK().setRiskControlId(riskControlHasRequirement.getRiskControl().getRiskControlPK().getId());
        riskControlHasRequirement.getRiskControlHasRequirementPK().setRequirementId(riskControlHasRequirement.getRequirement().getRequirementPK().getId());
        riskControlHasRequirement.getRiskControlHasRequirementPK().setRiskControlRiskControlTypeId(riskControlHasRequirement.getRiskControl().getRiskControlPK().getRiskControlTypeId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControl riskControl = riskControlHasRequirement.getRiskControl();
            if (riskControl != null) {
                riskControl = em.getReference(riskControl.getClass(), riskControl.getRiskControlPK());
                riskControlHasRequirement.setRiskControl(riskControl);
            }
            Requirement requirement = riskControlHasRequirement.getRequirement();
            if (requirement != null) {
                requirement = em.getReference(requirement.getClass(), requirement.getRequirementPK());
                riskControlHasRequirement.setRequirement(requirement);
            }
            em.persist(riskControlHasRequirement);
            if (riskControl != null) {
                riskControl.getRiskControlHasRequirementList().add(riskControlHasRequirement);
                riskControl = em.merge(riskControl);
            }
            if (requirement != null) {
                requirement.getRiskControlHasRequirementList().add(riskControlHasRequirement);
                requirement = em.merge(requirement);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRiskControlHasRequirement(riskControlHasRequirement.getRiskControlHasRequirementPK()) != null) {
                throw new PreexistingEntityException("RiskControlHasRequirement " + riskControlHasRequirement + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RiskControlHasRequirement riskControlHasRequirement) throws NonexistentEntityException, Exception {
        riskControlHasRequirement.getRiskControlHasRequirementPK().setRiskControlId(riskControlHasRequirement.getRiskControl().getRiskControlPK().getId());
        riskControlHasRequirement.getRiskControlHasRequirementPK().setRequirementId(riskControlHasRequirement.getRequirement().getRequirementPK().getId());
        riskControlHasRequirement.getRiskControlHasRequirementPK().setRiskControlRiskControlTypeId(riskControlHasRequirement.getRiskControl().getRiskControlPK().getRiskControlTypeId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControlHasRequirement persistentRiskControlHasRequirement = em.find(RiskControlHasRequirement.class, riskControlHasRequirement.getRiskControlHasRequirementPK());
            RiskControl riskControlOld = persistentRiskControlHasRequirement.getRiskControl();
            RiskControl riskControlNew = riskControlHasRequirement.getRiskControl();
            Requirement requirementOld = persistentRiskControlHasRequirement.getRequirement();
            Requirement requirementNew = riskControlHasRequirement.getRequirement();
            if (riskControlNew != null) {
                riskControlNew = em.getReference(riskControlNew.getClass(), riskControlNew.getRiskControlPK());
                riskControlHasRequirement.setRiskControl(riskControlNew);
            }
            if (requirementNew != null) {
                requirementNew = em.getReference(requirementNew.getClass(), requirementNew.getRequirementPK());
                riskControlHasRequirement.setRequirement(requirementNew);
            }
            riskControlHasRequirement = em.merge(riskControlHasRequirement);
            if (riskControlOld != null && !riskControlOld.equals(riskControlNew)) {
                riskControlOld.getRiskControlHasRequirementList().remove(riskControlHasRequirement);
                riskControlOld = em.merge(riskControlOld);
            }
            if (riskControlNew != null && !riskControlNew.equals(riskControlOld)) {
                riskControlNew.getRiskControlHasRequirementList().add(riskControlHasRequirement);
                riskControlNew = em.merge(riskControlNew);
            }
            if (requirementOld != null && !requirementOld.equals(requirementNew)) {
                requirementOld.getRiskControlHasRequirementList().remove(riskControlHasRequirement);
                requirementOld = em.merge(requirementOld);
            }
            if (requirementNew != null && !requirementNew.equals(requirementOld)) {
                requirementNew.getRiskControlHasRequirementList().add(riskControlHasRequirement);
                requirementNew = em.merge(requirementNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RiskControlHasRequirementPK id = riskControlHasRequirement.getRiskControlHasRequirementPK();
                if (findRiskControlHasRequirement(id) == null) {
                    throw new NonexistentEntityException("The riskControlHasRequirement with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RiskControlHasRequirementPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RiskControlHasRequirement riskControlHasRequirement;
            try {
                riskControlHasRequirement = em.getReference(RiskControlHasRequirement.class, id);
                riskControlHasRequirement.getRiskControlHasRequirementPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The riskControlHasRequirement with id " + id + " no longer exists.", enfe);
            }
            RiskControl riskControl = riskControlHasRequirement.getRiskControl();
            if (riskControl != null) {
                riskControl.getRiskControlHasRequirementList().remove(riskControlHasRequirement);
                riskControl = em.merge(riskControl);
            }
            Requirement requirement = riskControlHasRequirement.getRequirement();
            if (requirement != null) {
                requirement.getRiskControlHasRequirementList().remove(riskControlHasRequirement);
                requirement = em.merge(requirement);
            }
            em.remove(riskControlHasRequirement);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RiskControlHasRequirement> findRiskControlHasRequirementEntities() {
        return findRiskControlHasRequirementEntities(true, -1, -1);
    }

    public List<RiskControlHasRequirement> findRiskControlHasRequirementEntities(int maxResults, int firstResult) {
        return findRiskControlHasRequirementEntities(false, maxResults, firstResult);
    }

    private List<RiskControlHasRequirement> findRiskControlHasRequirementEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RiskControlHasRequirement.class));
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

    public RiskControlHasRequirement findRiskControlHasRequirement(RiskControlHasRequirementPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RiskControlHasRequirement.class, id);
        } finally {
            em.close();
        }
    }

    public int getRiskControlHasRequirementCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RiskControlHasRequirement> rt = cq.from(RiskControlHasRequirement.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
