/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementHasException;
import com.validation.manager.core.db.RequirementHasExceptionPK;
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
public class RequirementHasExceptionJpaController implements Serializable {

    public RequirementHasExceptionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RequirementHasException requirementHasException) throws PreexistingEntityException, Exception {
        if (requirementHasException.getRequirementHasExceptionPK() == null) {
            requirementHasException.setRequirementHasExceptionPK(new RequirementHasExceptionPK());
        }
        requirementHasException.getRequirementHasExceptionPK().setExceptionId(requirementHasException.getVmException().getVmExceptionPK().getId());
        requirementHasException.getRequirementHasExceptionPK().setExceptionReporterId(requirementHasException.getVmException().getVmExceptionPK().getReporterId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Requirement requirement = requirementHasException.getRequirement();
            if (requirement != null) {
                requirement = em.getReference(requirement.getClass(), requirement.getRequirementPK());
                requirementHasException.setRequirement(requirement);
            }
            VmException vmException = requirementHasException.getVmException();
            if (vmException != null) {
                vmException = em.getReference(vmException.getClass(), vmException.getVmExceptionPK());
                requirementHasException.setVmException(vmException);
            }
            em.persist(requirementHasException);
            if (requirement != null) {
                requirement.getRequirementHasExceptionList().add(requirementHasException);
                requirement = em.merge(requirement);
            }
            if (vmException != null) {
                vmException.getRequirementHasExceptionList().add(requirementHasException);
                vmException = em.merge(vmException);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRequirementHasException(requirementHasException.getRequirementHasExceptionPK()) != null) {
                throw new PreexistingEntityException("RequirementHasException " + requirementHasException + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RequirementHasException requirementHasException) throws NonexistentEntityException, Exception {
        requirementHasException.getRequirementHasExceptionPK().setExceptionId(requirementHasException.getVmException().getVmExceptionPK().getId());
        requirementHasException.getRequirementHasExceptionPK().setExceptionReporterId(requirementHasException.getVmException().getVmExceptionPK().getReporterId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementHasException persistentRequirementHasException = em.find(RequirementHasException.class, requirementHasException.getRequirementHasExceptionPK());
            Requirement requirementOld = persistentRequirementHasException.getRequirement();
            Requirement requirementNew = requirementHasException.getRequirement();
            VmException vmExceptionOld = persistentRequirementHasException.getVmException();
            VmException vmExceptionNew = requirementHasException.getVmException();
            if (requirementNew != null) {
                requirementNew = em.getReference(requirementNew.getClass(), requirementNew.getRequirementPK());
                requirementHasException.setRequirement(requirementNew);
            }
            if (vmExceptionNew != null) {
                vmExceptionNew = em.getReference(vmExceptionNew.getClass(), vmExceptionNew.getVmExceptionPK());
                requirementHasException.setVmException(vmExceptionNew);
            }
            requirementHasException = em.merge(requirementHasException);
            if (requirementOld != null && !requirementOld.equals(requirementNew)) {
                requirementOld.getRequirementHasExceptionList().remove(requirementHasException);
                requirementOld = em.merge(requirementOld);
            }
            if (requirementNew != null && !requirementNew.equals(requirementOld)) {
                requirementNew.getRequirementHasExceptionList().add(requirementHasException);
                requirementNew = em.merge(requirementNew);
            }
            if (vmExceptionOld != null && !vmExceptionOld.equals(vmExceptionNew)) {
                vmExceptionOld.getRequirementHasExceptionList().remove(requirementHasException);
                vmExceptionOld = em.merge(vmExceptionOld);
            }
            if (vmExceptionNew != null && !vmExceptionNew.equals(vmExceptionOld)) {
                vmExceptionNew.getRequirementHasExceptionList().add(requirementHasException);
                vmExceptionNew = em.merge(vmExceptionNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RequirementHasExceptionPK id = requirementHasException.getRequirementHasExceptionPK();
                if (findRequirementHasException(id) == null) {
                    throw new NonexistentEntityException("The requirementHasException with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RequirementHasExceptionPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementHasException requirementHasException;
            try {
                requirementHasException = em.getReference(RequirementHasException.class, id);
                requirementHasException.getRequirementHasExceptionPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The requirementHasException with id " + id + " no longer exists.", enfe);
            }
            Requirement requirement = requirementHasException.getRequirement();
            if (requirement != null) {
                requirement.getRequirementHasExceptionList().remove(requirementHasException);
                requirement = em.merge(requirement);
            }
            VmException vmException = requirementHasException.getVmException();
            if (vmException != null) {
                vmException.getRequirementHasExceptionList().remove(requirementHasException);
                vmException = em.merge(vmException);
            }
            em.remove(requirementHasException);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RequirementHasException> findRequirementHasExceptionEntities() {
        return findRequirementHasExceptionEntities(true, -1, -1);
    }

    public List<RequirementHasException> findRequirementHasExceptionEntities(int maxResults, int firstResult) {
        return findRequirementHasExceptionEntities(false, maxResults, firstResult);
    }

    private List<RequirementHasException> findRequirementHasExceptionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RequirementHasException.class));
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

    public RequirementHasException findRequirementHasException(RequirementHasExceptionPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RequirementHasException.class, id);
        } finally {
            em.close();
        }
    }

    public int getRequirementHasExceptionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RequirementHasException> rt = cq.from(RequirementHasException.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
