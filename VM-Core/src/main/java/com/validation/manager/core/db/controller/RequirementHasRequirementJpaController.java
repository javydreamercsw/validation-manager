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
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementHasRequirement;
import com.validation.manager.core.db.RequirementHasRequirementPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
        requirementHasRequirement.getRequirementHasRequirementPK().setRequirementId(requirementHasRequirement.getChildRequirement().getRequirementPK().getId());
        requirementHasRequirement.getRequirementHasRequirementPK().setParentRequirementId(requirementHasRequirement.getParentRequirement().getRequirementPK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Requirement requirement = requirementHasRequirement.getParentRequirement();
            if (requirement != null) {
                requirement = em.getReference(requirement.getClass(), requirement.getRequirementPK());
                requirementHasRequirement.setParentRequirement(requirement);
            }
            Requirement requirement1 = requirementHasRequirement.getChildRequirement();
            if (requirement1 != null) {
                requirement1 = em.getReference(requirement1.getClass(), requirement1.getRequirementPK());
                requirementHasRequirement.setChildRequirement(requirement1);
            }
            em.persist(requirementHasRequirement);
            if (requirement != null) {
                requirement.getRequirementHasRequirementList().add(requirementHasRequirement);
                requirement = em.merge(requirement);
            }
            if (requirement1 != null) {
                requirement1.getRequirementHasRequirementList().add(requirementHasRequirement);
                requirement1 = em.merge(requirement1);
            }
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
        requirementHasRequirement.getRequirementHasRequirementPK().setRequirementId(requirementHasRequirement.getChildRequirement().getRequirementPK().getId());
        requirementHasRequirement.getRequirementHasRequirementPK().setParentRequirementId(requirementHasRequirement.getParentRequirement().getRequirementPK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementHasRequirement persistentRequirementHasRequirement = em.find(RequirementHasRequirement.class, requirementHasRequirement.getRequirementHasRequirementPK());
            Requirement requirementOld = persistentRequirementHasRequirement.getParentRequirement();
            Requirement requirementNew = requirementHasRequirement.getParentRequirement();
            Requirement requirement1Old = persistentRequirementHasRequirement.getChildRequirement();
            Requirement requirement1New = requirementHasRequirement.getChildRequirement();
            if (requirementNew != null) {
                requirementNew = em.getReference(requirementNew.getClass(), requirementNew.getRequirementPK());
                requirementHasRequirement.setParentRequirement(requirementNew);
            }
            if (requirement1New != null) {
                requirement1New = em.getReference(requirement1New.getClass(), requirement1New.getRequirementPK());
                requirementHasRequirement.setChildRequirement(requirement1New);
            }
            requirementHasRequirement = em.merge(requirementHasRequirement);
            if (requirementOld != null && !requirementOld.equals(requirementNew)) {
                requirementOld.getRequirementHasRequirementList().remove(requirementHasRequirement);
                requirementOld = em.merge(requirementOld);
            }
            if (requirementNew != null && !requirementNew.equals(requirementOld)) {
                requirementNew.getRequirementHasRequirementList().add(requirementHasRequirement);
                requirementNew = em.merge(requirementNew);
            }
            if (requirement1Old != null && !requirement1Old.equals(requirement1New)) {
                requirement1Old.getRequirementHasRequirementList().remove(requirementHasRequirement);
                requirement1Old = em.merge(requirement1Old);
            }
            if (requirement1New != null && !requirement1New.equals(requirement1Old)) {
                requirement1New.getRequirementHasRequirementList().add(requirementHasRequirement);
                requirement1New = em.merge(requirement1New);
            }
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
            Requirement requirement = requirementHasRequirement.getParentRequirement();
            if (requirement != null) {
                requirement.getRequirementHasRequirementList().remove(requirementHasRequirement);
                requirement = em.merge(requirement);
            }
            Requirement requirement1 = requirementHasRequirement.getChildRequirement();
            if (requirement1 != null) {
                requirement1.getRequirementHasRequirementList().remove(requirementHasRequirement);
                requirement1 = em.merge(requirement1);
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
