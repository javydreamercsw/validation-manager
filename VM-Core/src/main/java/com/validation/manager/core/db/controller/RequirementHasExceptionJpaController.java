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
import com.validation.manager.core.db.VmException;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementHasException;
import com.validation.manager.core.db.RequirementHasExceptionPK;
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
        requirementHasException.getRequirementHasExceptionPK().setVmExceptionReporterId(requirementHasException.getVmException().getVmExceptionPK().getReporterId());
        requirementHasException.getRequirementHasExceptionPK().setRequirementId(requirementHasException.getRequirement().getRequirementPK().getId());
        requirementHasException.getRequirementHasExceptionPK().setVmExceptionId(requirementHasException.getVmException().getVmExceptionPK().getId());
        requirementHasException.getRequirementHasExceptionPK().setRequirementVersion(requirementHasException.getRequirement().getRequirementPK().getVersion());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmException vmException = requirementHasException.getVmException();
            if (vmException != null) {
                vmException = em.getReference(vmException.getClass(), vmException.getVmExceptionPK());
                requirementHasException.setVmException(vmException);
            }
            Requirement requirement = requirementHasException.getRequirement();
            if (requirement != null) {
                requirement = em.getReference(requirement.getClass(), requirement.getRequirementPK());
                requirementHasException.setRequirement(requirement);
            }
            VmException vmException1 = requirementHasException.getVmException1();
            if (vmException1 != null) {
                vmException1 = em.getReference(vmException1.getClass(), vmException1.getVmExceptionPK());
                requirementHasException.setVmException1(vmException1);
            }
            em.persist(requirementHasException);
            if (vmException != null) {
                vmException.getRequirementHasExceptionList().add(requirementHasException);
                vmException = em.merge(vmException);
            }
            if (requirement != null) {
                requirement.getRequirementHasExceptionList().add(requirementHasException);
                requirement = em.merge(requirement);
            }
            if (vmException1 != null) {
                vmException1.getRequirementHasExceptionList().add(requirementHasException);
                vmException1 = em.merge(vmException1);
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
        requirementHasException.getRequirementHasExceptionPK().setVmExceptionReporterId(requirementHasException.getVmException().getVmExceptionPK().getReporterId());
        requirementHasException.getRequirementHasExceptionPK().setRequirementId(requirementHasException.getRequirement().getRequirementPK().getId());
        requirementHasException.getRequirementHasExceptionPK().setVmExceptionId(requirementHasException.getVmException().getVmExceptionPK().getId());
        requirementHasException.getRequirementHasExceptionPK().setRequirementVersion(requirementHasException.getRequirement().getRequirementPK().getVersion());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementHasException persistentRequirementHasException = em.find(RequirementHasException.class, requirementHasException.getRequirementHasExceptionPK());
            VmException vmExceptionOld = persistentRequirementHasException.getVmException();
            VmException vmExceptionNew = requirementHasException.getVmException();
            Requirement requirementOld = persistentRequirementHasException.getRequirement();
            Requirement requirementNew = requirementHasException.getRequirement();
            VmException vmException1Old = persistentRequirementHasException.getVmException1();
            VmException vmException1New = requirementHasException.getVmException1();
            if (vmExceptionNew != null) {
                vmExceptionNew = em.getReference(vmExceptionNew.getClass(), vmExceptionNew.getVmExceptionPK());
                requirementHasException.setVmException(vmExceptionNew);
            }
            if (requirementNew != null) {
                requirementNew = em.getReference(requirementNew.getClass(), requirementNew.getRequirementPK());
                requirementHasException.setRequirement(requirementNew);
            }
            if (vmException1New != null) {
                vmException1New = em.getReference(vmException1New.getClass(), vmException1New.getVmExceptionPK());
                requirementHasException.setVmException1(vmException1New);
            }
            requirementHasException = em.merge(requirementHasException);
            if (vmExceptionOld != null && !vmExceptionOld.equals(vmExceptionNew)) {
                vmExceptionOld.getRequirementHasExceptionList().remove(requirementHasException);
                vmExceptionOld = em.merge(vmExceptionOld);
            }
            if (vmExceptionNew != null && !vmExceptionNew.equals(vmExceptionOld)) {
                vmExceptionNew.getRequirementHasExceptionList().add(requirementHasException);
                vmExceptionNew = em.merge(vmExceptionNew);
            }
            if (requirementOld != null && !requirementOld.equals(requirementNew)) {
                requirementOld.getRequirementHasExceptionList().remove(requirementHasException);
                requirementOld = em.merge(requirementOld);
            }
            if (requirementNew != null && !requirementNew.equals(requirementOld)) {
                requirementNew.getRequirementHasExceptionList().add(requirementHasException);
                requirementNew = em.merge(requirementNew);
            }
            if (vmException1Old != null && !vmException1Old.equals(vmException1New)) {
                vmException1Old.getRequirementHasExceptionList().remove(requirementHasException);
                vmException1Old = em.merge(vmException1Old);
            }
            if (vmException1New != null && !vmException1New.equals(vmException1Old)) {
                vmException1New.getRequirementHasExceptionList().add(requirementHasException);
                vmException1New = em.merge(vmException1New);
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
            VmException vmException = requirementHasException.getVmException();
            if (vmException != null) {
                vmException.getRequirementHasExceptionList().remove(requirementHasException);
                vmException = em.merge(vmException);
            }
            Requirement requirement = requirementHasException.getRequirement();
            if (requirement != null) {
                requirement.getRequirementHasExceptionList().remove(requirementHasException);
                requirement = em.merge(requirement);
            }
            VmException vmException1 = requirementHasException.getVmException1();
            if (vmException1 != null) {
                vmException1.getRequirementHasExceptionList().remove(requirementHasException);
                vmException1 = em.merge(vmException1);
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
