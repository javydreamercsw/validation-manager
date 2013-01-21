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
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.AssignmentStatus;
import com.validation.manager.core.db.AssigmentType;
import com.validation.manager.core.db.UserAssigment;
import com.validation.manager.core.db.UserAssigmentPK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UserAssigmentJpaController implements Serializable {

    public UserAssigmentJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserAssigment userAssigment) throws PreexistingEntityException, Exception {
        if (userAssigment.getUserAssigmentPK() == null) {
            userAssigment.setUserAssigmentPK(new UserAssigmentPK());
        }
        userAssigment.getUserAssigmentPK().setAssigmentTypeId(userAssigment.getAssigmentType().getId());
        userAssigment.getUserAssigmentPK().setAssignmentStatusId(userAssigment.getAssignmentStatus().getId());
        userAssigment.getUserAssigmentPK().setAssignerId(userAssigment.getVmUser().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmUser assigneeId = userAssigment.getAssigneeId();
            if (assigneeId != null) {
                assigneeId = em.getReference(assigneeId.getClass(), assigneeId.getId());
                userAssigment.setAssigneeId(assigneeId);
            }
            VmUser vmUser = userAssigment.getVmUser();
            if (vmUser != null) {
                vmUser = em.getReference(vmUser.getClass(), vmUser.getId());
                userAssigment.setVmUser(vmUser);
            }
            AssignmentStatus assignmentStatus = userAssigment.getAssignmentStatus();
            if (assignmentStatus != null) {
                assignmentStatus = em.getReference(assignmentStatus.getClass(), assignmentStatus.getId());
                userAssigment.setAssignmentStatus(assignmentStatus);
            }
            AssigmentType assigmentType = userAssigment.getAssigmentType();
            if (assigmentType != null) {
                assigmentType = em.getReference(assigmentType.getClass(), assigmentType.getId());
                userAssigment.setAssigmentType(assigmentType);
            }
            em.persist(userAssigment);
            if (assigneeId != null) {
                assigneeId.getUserAssigmentList().add(userAssigment);
                assigneeId = em.merge(assigneeId);
            }
            if (vmUser != null) {
                vmUser.getUserAssigmentList().add(userAssigment);
                vmUser = em.merge(vmUser);
            }
            if (assignmentStatus != null) {
                assignmentStatus.getUserAssigmentList().add(userAssigment);
                assignmentStatus = em.merge(assignmentStatus);
            }
            if (assigmentType != null) {
                assigmentType.getUserAssigmentList().add(userAssigment);
                assigmentType = em.merge(assigmentType);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUserAssigment(userAssigment.getUserAssigmentPK()) != null) {
                throw new PreexistingEntityException("UserAssigment " + userAssigment + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserAssigment userAssigment) throws NonexistentEntityException, Exception {
        userAssigment.getUserAssigmentPK().setAssigmentTypeId(userAssigment.getAssigmentType().getId());
        userAssigment.getUserAssigmentPK().setAssignmentStatusId(userAssigment.getAssignmentStatus().getId());
        userAssigment.getUserAssigmentPK().setAssignerId(userAssigment.getVmUser().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserAssigment persistentUserAssigment = em.find(UserAssigment.class, userAssigment.getUserAssigmentPK());
            VmUser assigneeIdOld = persistentUserAssigment.getAssigneeId();
            VmUser assigneeIdNew = userAssigment.getAssigneeId();
            VmUser vmUserOld = persistentUserAssigment.getVmUser();
            VmUser vmUserNew = userAssigment.getVmUser();
            AssignmentStatus assignmentStatusOld = persistentUserAssigment.getAssignmentStatus();
            AssignmentStatus assignmentStatusNew = userAssigment.getAssignmentStatus();
            AssigmentType assigmentTypeOld = persistentUserAssigment.getAssigmentType();
            AssigmentType assigmentTypeNew = userAssigment.getAssigmentType();
            if (assigneeIdNew != null) {
                assigneeIdNew = em.getReference(assigneeIdNew.getClass(), assigneeIdNew.getId());
                userAssigment.setAssigneeId(assigneeIdNew);
            }
            if (vmUserNew != null) {
                vmUserNew = em.getReference(vmUserNew.getClass(), vmUserNew.getId());
                userAssigment.setVmUser(vmUserNew);
            }
            if (assignmentStatusNew != null) {
                assignmentStatusNew = em.getReference(assignmentStatusNew.getClass(), assignmentStatusNew.getId());
                userAssigment.setAssignmentStatus(assignmentStatusNew);
            }
            if (assigmentTypeNew != null) {
                assigmentTypeNew = em.getReference(assigmentTypeNew.getClass(), assigmentTypeNew.getId());
                userAssigment.setAssigmentType(assigmentTypeNew);
            }
            userAssigment = em.merge(userAssigment);
            if (assigneeIdOld != null && !assigneeIdOld.equals(assigneeIdNew)) {
                assigneeIdOld.getUserAssigmentList().remove(userAssigment);
                assigneeIdOld = em.merge(assigneeIdOld);
            }
            if (assigneeIdNew != null && !assigneeIdNew.equals(assigneeIdOld)) {
                assigneeIdNew.getUserAssigmentList().add(userAssigment);
                assigneeIdNew = em.merge(assigneeIdNew);
            }
            if (vmUserOld != null && !vmUserOld.equals(vmUserNew)) {
                vmUserOld.getUserAssigmentList().remove(userAssigment);
                vmUserOld = em.merge(vmUserOld);
            }
            if (vmUserNew != null && !vmUserNew.equals(vmUserOld)) {
                vmUserNew.getUserAssigmentList().add(userAssigment);
                vmUserNew = em.merge(vmUserNew);
            }
            if (assignmentStatusOld != null && !assignmentStatusOld.equals(assignmentStatusNew)) {
                assignmentStatusOld.getUserAssigmentList().remove(userAssigment);
                assignmentStatusOld = em.merge(assignmentStatusOld);
            }
            if (assignmentStatusNew != null && !assignmentStatusNew.equals(assignmentStatusOld)) {
                assignmentStatusNew.getUserAssigmentList().add(userAssigment);
                assignmentStatusNew = em.merge(assignmentStatusNew);
            }
            if (assigmentTypeOld != null && !assigmentTypeOld.equals(assigmentTypeNew)) {
                assigmentTypeOld.getUserAssigmentList().remove(userAssigment);
                assigmentTypeOld = em.merge(assigmentTypeOld);
            }
            if (assigmentTypeNew != null && !assigmentTypeNew.equals(assigmentTypeOld)) {
                assigmentTypeNew.getUserAssigmentList().add(userAssigment);
                assigmentTypeNew = em.merge(assigmentTypeNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                UserAssigmentPK id = userAssigment.getUserAssigmentPK();
                if (findUserAssigment(id) == null) {
                    throw new NonexistentEntityException("The userAssigment with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(UserAssigmentPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserAssigment userAssigment;
            try {
                userAssigment = em.getReference(UserAssigment.class, id);
                userAssigment.getUserAssigmentPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userAssigment with id " + id + " no longer exists.", enfe);
            }
            VmUser assigneeId = userAssigment.getAssigneeId();
            if (assigneeId != null) {
                assigneeId.getUserAssigmentList().remove(userAssigment);
                assigneeId = em.merge(assigneeId);
            }
            VmUser vmUser = userAssigment.getVmUser();
            if (vmUser != null) {
                vmUser.getUserAssigmentList().remove(userAssigment);
                vmUser = em.merge(vmUser);
            }
            AssignmentStatus assignmentStatus = userAssigment.getAssignmentStatus();
            if (assignmentStatus != null) {
                assignmentStatus.getUserAssigmentList().remove(userAssigment);
                assignmentStatus = em.merge(assignmentStatus);
            }
            AssigmentType assigmentType = userAssigment.getAssigmentType();
            if (assigmentType != null) {
                assigmentType.getUserAssigmentList().remove(userAssigment);
                assigmentType = em.merge(assigmentType);
            }
            em.remove(userAssigment);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserAssigment> findUserAssigmentEntities() {
        return findUserAssigmentEntities(true, -1, -1);
    }

    public List<UserAssigment> findUserAssigmentEntities(int maxResults, int firstResult) {
        return findUserAssigmentEntities(false, maxResults, firstResult);
    }

    private List<UserAssigment> findUserAssigmentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserAssigment.class));
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

    public UserAssigment findUserAssigment(UserAssigmentPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserAssigment.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserAssigmentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserAssigment> rt = cq.from(UserAssigment.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
