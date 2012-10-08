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
import com.validation.manager.core.db.RequirementStatus;
import com.validation.manager.core.db.RequirementType;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementPK;
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
public class RequirementJpaController implements Serializable {

    public RequirementJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Requirement requirement) throws PreexistingEntityException, Exception {
        if (requirement.getRequirementPK() == null) {
            requirement.setRequirementPK(new RequirementPK());
        }
        if (requirement.getRequirementList() == null) {
            requirement.setRequirementList(new ArrayList<Requirement>());
        }
        if (requirement.getRequirementList1() == null) {
            requirement.setRequirementList1(new ArrayList<Requirement>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RequirementStatus requirementStatusId = requirement.getRequirementStatus();
            if (requirementStatusId != null) {
                requirementStatusId = em.getReference(requirementStatusId.getClass(), requirementStatusId.getId());
                requirement.setRequirementStatus(requirementStatusId);
            }
            RequirementType requirementTypeId = requirement.getRequirementType();
            if (requirementTypeId != null) {
                requirementTypeId = em.getReference(requirementTypeId.getClass(), requirementTypeId.getId());
                requirement.setRequirementType(requirementTypeId);
            }
            Project productId = requirement.getProject();
            if (productId != null) {
                productId = em.getReference(productId.getClass(), productId.getId());
                requirement.setProject(productId);
            }
            List<Requirement> attachedRequirementList = new ArrayList<Requirement>();
            for (Requirement requirementListRequirementToAttach : requirement.getRequirementList()) {
                requirementListRequirementToAttach = em.getReference(requirementListRequirementToAttach.getClass(), requirementListRequirementToAttach.getRequirementPK());
                attachedRequirementList.add(requirementListRequirementToAttach);
            }
            requirement.setRequirementList(attachedRequirementList);
            List<Requirement> attachedRequirementList1 = new ArrayList<Requirement>();
            for (Requirement requirementList1RequirementToAttach : requirement.getRequirementList1()) {
                requirementList1RequirementToAttach = em.getReference(requirementList1RequirementToAttach.getClass(), requirementList1RequirementToAttach.getRequirementPK());
                attachedRequirementList1.add(requirementList1RequirementToAttach);
            }
            requirement.setRequirementList1(attachedRequirementList1);
            em.persist(requirement);
            if (requirementStatusId != null) {
                requirementStatusId.getRequirementList().add(requirement);
                requirementStatusId = em.merge(requirementStatusId);
            }
            if (requirementTypeId != null) {
                requirementTypeId.getRequirementList().add(requirement);
                requirementTypeId = em.merge(requirementTypeId);
            }
            if (productId != null) {
                productId.getRequirementList().add(requirement);
                productId = em.merge(productId);
            }
            for (Requirement requirementListRequirement : requirement.getRequirementList()) {
                requirementListRequirement.getRequirementList().add(requirement);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            for (Requirement requirementList1Requirement : requirement.getRequirementList1()) {
                requirementList1Requirement.getRequirementList().add(requirement);
                requirementList1Requirement = em.merge(requirementList1Requirement);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRequirement(requirement.getRequirementPK()) != null) {
                throw new PreexistingEntityException("Requirement " + requirement + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Requirement requirement) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Requirement persistentRequirement = em.find(Requirement.class, requirement.getRequirementPK());
            RequirementStatus requirementStatusIdOld = persistentRequirement.getRequirementStatus();
            RequirementStatus requirementStatusIdNew = requirement.getRequirementStatus();
            RequirementType requirementTypeIdOld = persistentRequirement.getRequirementType();
            RequirementType requirementTypeIdNew = requirement.getRequirementType();
            Project productIdOld = persistentRequirement.getProject();
            Project productIdNew = requirement.getProject();
            List<Requirement> requirementListOld = persistentRequirement.getRequirementList();
            List<Requirement> requirementListNew = requirement.getRequirementList();
            List<Requirement> requirementList1Old = persistentRequirement.getRequirementList1();
            List<Requirement> requirementList1New = requirement.getRequirementList1();
            if (requirementStatusIdNew != null) {
                requirementStatusIdNew = em.getReference(requirementStatusIdNew.getClass(), requirementStatusIdNew.getId());
                requirement.setRequirementStatus(requirementStatusIdNew);
            }
            if (requirementTypeIdNew != null) {
                requirementTypeIdNew = em.getReference(requirementTypeIdNew.getClass(), requirementTypeIdNew.getId());
                requirement.setRequirementType(requirementTypeIdNew);
            }
            if (productIdNew != null) {
                productIdNew = em.getReference(productIdNew.getClass(), productIdNew.getId());
                requirement.setProject(productIdNew);
            }
            List<Requirement> attachedRequirementListNew = new ArrayList<Requirement>();
            for (Requirement requirementListNewRequirementToAttach : requirementListNew) {
                requirementListNewRequirementToAttach = em.getReference(requirementListNewRequirementToAttach.getClass(), requirementListNewRequirementToAttach.getRequirementPK());
                attachedRequirementListNew.add(requirementListNewRequirementToAttach);
            }
            requirementListNew = attachedRequirementListNew;
            requirement.setRequirementList(requirementListNew);
            List<Requirement> attachedRequirementList1New = new ArrayList<Requirement>();
            for (Requirement requirementList1NewRequirementToAttach : requirementList1New) {
                requirementList1NewRequirementToAttach = em.getReference(requirementList1NewRequirementToAttach.getClass(), requirementList1NewRequirementToAttach.getRequirementPK());
                attachedRequirementList1New.add(requirementList1NewRequirementToAttach);
            }
            requirementList1New = attachedRequirementList1New;
            requirement.setRequirementList1(requirementList1New);
            requirement = em.merge(requirement);
            if (requirementStatusIdOld != null && !requirementStatusIdOld.equals(requirementStatusIdNew)) {
                requirementStatusIdOld.getRequirementList().remove(requirement);
                requirementStatusIdOld = em.merge(requirementStatusIdOld);
            }
            if (requirementStatusIdNew != null && !requirementStatusIdNew.equals(requirementStatusIdOld)) {
                requirementStatusIdNew.getRequirementList().add(requirement);
                requirementStatusIdNew = em.merge(requirementStatusIdNew);
            }
            if (requirementTypeIdOld != null && !requirementTypeIdOld.equals(requirementTypeIdNew)) {
                requirementTypeIdOld.getRequirementList().remove(requirement);
                requirementTypeIdOld = em.merge(requirementTypeIdOld);
            }
            if (requirementTypeIdNew != null && !requirementTypeIdNew.equals(requirementTypeIdOld)) {
                requirementTypeIdNew.getRequirementList().add(requirement);
                requirementTypeIdNew = em.merge(requirementTypeIdNew);
            }
            if (productIdOld != null && !productIdOld.equals(productIdNew)) {
                productIdOld.getRequirementList().remove(requirement);
                productIdOld = em.merge(productIdOld);
            }
            if (productIdNew != null && !productIdNew.equals(productIdOld)) {
                productIdNew.getRequirementList().add(requirement);
                productIdNew = em.merge(productIdNew);
            }
            for (Requirement requirementListOldRequirement : requirementListOld) {
                if (!requirementListNew.contains(requirementListOldRequirement)) {
                    requirementListOldRequirement.getRequirementList().remove(requirement);
                    requirementListOldRequirement = em.merge(requirementListOldRequirement);
                }
            }
            for (Requirement requirementListNewRequirement : requirementListNew) {
                if (!requirementListOld.contains(requirementListNewRequirement)) {
                    requirementListNewRequirement.getRequirementList().add(requirement);
                    requirementListNewRequirement = em.merge(requirementListNewRequirement);
                }
            }
            for (Requirement requirementList1OldRequirement : requirementList1Old) {
                if (!requirementList1New.contains(requirementList1OldRequirement)) {
                    requirementList1OldRequirement.getRequirementList().remove(requirement);
                    requirementList1OldRequirement = em.merge(requirementList1OldRequirement);
                }
            }
            for (Requirement requirementList1NewRequirement : requirementList1New) {
                if (!requirementList1Old.contains(requirementList1NewRequirement)) {
                    requirementList1NewRequirement.getRequirementList().add(requirement);
                    requirementList1NewRequirement = em.merge(requirementList1NewRequirement);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RequirementPK id = requirement.getRequirementPK();
                if (findRequirement(id) == null) {
                    throw new NonexistentEntityException("The requirement with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RequirementPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Requirement requirement;
            try {
                requirement = em.getReference(Requirement.class, id);
                requirement.getRequirementPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The requirement with id " + id + " no longer exists.", enfe);
            }
            RequirementStatus requirementStatusId = requirement.getRequirementStatus();
            if (requirementStatusId != null) {
                requirementStatusId.getRequirementList().remove(requirement);
                requirementStatusId = em.merge(requirementStatusId);
            }
            RequirementType requirementTypeId = requirement.getRequirementType();
            if (requirementTypeId != null) {
                requirementTypeId.getRequirementList().remove(requirement);
                requirementTypeId = em.merge(requirementTypeId);
            }
            Project productId = requirement.getProject();
            if (productId != null) {
                productId.getRequirementList().remove(requirement);
                productId = em.merge(productId);
            }
            List<Requirement> requirementList = requirement.getRequirementList();
            for (Requirement requirementListRequirement : requirementList) {
                requirementListRequirement.getRequirementList().remove(requirement);
                requirementListRequirement = em.merge(requirementListRequirement);
            }
            List<Requirement> requirementList1 = requirement.getRequirementList1();
            for (Requirement requirementList1Requirement : requirementList1) {
                requirementList1Requirement.getRequirementList().remove(requirement);
                requirementList1Requirement = em.merge(requirementList1Requirement);
            }
            em.remove(requirement);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Requirement> findRequirementEntities() {
        return findRequirementEntities(true, -1, -1);
    }

    public List<Requirement> findRequirementEntities(int maxResults, int firstResult) {
        return findRequirementEntities(false, maxResults, firstResult);
    }

    private List<Requirement> findRequirementEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Requirement.class));
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

    public Requirement findRequirement(RequirementPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Requirement.class, id);
        } finally {
            em.close();
        }
    }

    public int getRequirementCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Requirement> rt = cq.from(Requirement.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
