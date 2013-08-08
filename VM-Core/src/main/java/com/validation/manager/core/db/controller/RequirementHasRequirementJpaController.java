/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.RequirementHasRequirement;
import com.validation.manager.core.db.RequirementHasRequirementPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementHasRequirementJpaController implements Serializable {

    public RequirementHasRequirementJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RequirementHasRequirement requirementHasRequirement) throws PreexistingEntityException, Exception {
        if (requirementHasRequirement.getRequirementHasRequirementPK() == null) {
            requirementHasRequirement.setRequirementHasRequirementPK(new RequirementHasRequirementPK());
        }
        requirementHasRequirement.getRequirementHasRequirementPK().setRequirementVersion(requirementHasRequirement.getChildRequirement().getRequirementPK().getVersion());
        requirementHasRequirement.getRequirementHasRequirementPK().setParentRequirementVersion(requirementHasRequirement.getParentRequirement().getRequirementPK().getVersion());
        requirementHasRequirement.getRequirementHasRequirementPK().setParentRequirementId(requirementHasRequirement.getParentRequirement().getRequirementPK().getId());
        requirementHasRequirement.getRequirementHasRequirementPK().setRequirementId(requirementHasRequirement.getChildRequirement().getRequirementPK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(requirementHasRequirement);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRequirementHasRequirement(requirementHasRequirement.getRequirementHasRequirementPK()) != null) {
                throw new PreexistingEntityException("RequirementHasRequirement " + requirementHasRequirement + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RequirementHasRequirement requirementHasRequirement) throws NonexistentEntityException, Exception {
        requirementHasRequirement.getRequirementHasRequirementPK().setRequirementVersion(requirementHasRequirement.getChildRequirement().getRequirementPK().getVersion());
        requirementHasRequirement.getRequirementHasRequirementPK().setParentRequirementVersion(requirementHasRequirement.getParentRequirement().getRequirementPK().getVersion());
        requirementHasRequirement.getRequirementHasRequirementPK().setParentRequirementId(requirementHasRequirement.getParentRequirement().getRequirementPK().getId());
        requirementHasRequirement.getRequirementHasRequirementPK().setRequirementId(requirementHasRequirement.getChildRequirement().getRequirementPK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            requirementHasRequirement = em.merge(requirementHasRequirement);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RequirementHasRequirementPK id = requirementHasRequirement.getRequirementHasRequirementPK();
                if (findRequirementHasRequirement(id) == null) {
                    throw new NonexistentEntityException("The requirementHasRequirement with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RequirementHasRequirementPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementHasRequirement requirementHasRequirement;
            try {
                requirementHasRequirement = em.getReference(RequirementHasRequirement.class, id);
                requirementHasRequirement.getRequirementHasRequirementPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The requirementHasRequirement with id " + id + " no longer exists.", enfe);
            }
            em.remove(requirementHasRequirement);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RequirementHasRequirement> findRequirementHasRequirementEntities() {
        return findRequirementHasRequirementEntities(true, -1, -1);
    }

    public List<RequirementHasRequirement> findRequirementHasRequirementEntities(int maxResults, int firstResult) {
        return findRequirementHasRequirementEntities(false, maxResults, firstResult);
    }

    private List<RequirementHasRequirement> findRequirementHasRequirementEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RequirementHasRequirement.class));
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

    public RequirementHasRequirement findRequirementHasRequirement(RequirementHasRequirementPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RequirementHasRequirement.class, id);
        } finally {
            em.close();
        }
    }

    public int getRequirementHasRequirementCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RequirementHasRequirement> rt = cq.from(RequirementHasRequirement.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
