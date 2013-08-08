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
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.UserHasRole;
import com.validation.manager.core.db.UserHasRolePK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UserHasRoleJpaController implements Serializable {

    public UserHasRoleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserHasRole userHasRole) throws PreexistingEntityException, Exception {
        if (userHasRole.getUserHasRolePK() == null) {
            userHasRole.setUserHasRolePK(new UserHasRolePK());
        }
        userHasRole.getUserHasRolePK().setRoleId(userHasRole.getRole().getId());
        userHasRole.getUserHasRolePK().setUserId(userHasRole.getVmUser().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmUser vmUser = userHasRole.getVmUser();
            if (vmUser != null) {
                vmUser = em.getReference(vmUser.getClass(), vmUser.getId());
                userHasRole.setVmUser(vmUser);
            }
            Role role = userHasRole.getRole();
            if (role != null) {
                role = em.getReference(role.getClass(), role.getId());
                userHasRole.setRole(role);
            }
            em.persist(userHasRole);
            if (vmUser != null) {
                vmUser.getUserHasRoleList().add(userHasRole);
                vmUser = em.merge(vmUser);
            }
            if (role != null) {
                role.getUserHasRoleList().add(userHasRole);
                role = em.merge(role);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUserHasRole(userHasRole.getUserHasRolePK()) != null) {
                throw new PreexistingEntityException("UserHasRole " + userHasRole + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserHasRole userHasRole) throws NonexistentEntityException, Exception {
        userHasRole.getUserHasRolePK().setRoleId(userHasRole.getRole().getId());
        userHasRole.getUserHasRolePK().setUserId(userHasRole.getVmUser().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserHasRole persistentUserHasRole = em.find(UserHasRole.class, userHasRole.getUserHasRolePK());
            VmUser vmUserOld = persistentUserHasRole.getVmUser();
            VmUser vmUserNew = userHasRole.getVmUser();
            Role roleOld = persistentUserHasRole.getRole();
            Role roleNew = userHasRole.getRole();
            if (vmUserNew != null) {
                vmUserNew = em.getReference(vmUserNew.getClass(), vmUserNew.getId());
                userHasRole.setVmUser(vmUserNew);
            }
            if (roleNew != null) {
                roleNew = em.getReference(roleNew.getClass(), roleNew.getId());
                userHasRole.setRole(roleNew);
            }
            userHasRole = em.merge(userHasRole);
            if (vmUserOld != null && !vmUserOld.equals(vmUserNew)) {
                vmUserOld.getUserHasRoleList().remove(userHasRole);
                vmUserOld = em.merge(vmUserOld);
            }
            if (vmUserNew != null && !vmUserNew.equals(vmUserOld)) {
                vmUserNew.getUserHasRoleList().add(userHasRole);
                vmUserNew = em.merge(vmUserNew);
            }
            if (roleOld != null && !roleOld.equals(roleNew)) {
                roleOld.getUserHasRoleList().remove(userHasRole);
                roleOld = em.merge(roleOld);
            }
            if (roleNew != null && !roleNew.equals(roleOld)) {
                roleNew.getUserHasRoleList().add(userHasRole);
                roleNew = em.merge(roleNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                UserHasRolePK id = userHasRole.getUserHasRolePK();
                if (findUserHasRole(id) == null) {
                    throw new NonexistentEntityException("The userHasRole with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(UserHasRolePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserHasRole userHasRole;
            try {
                userHasRole = em.getReference(UserHasRole.class, id);
                userHasRole.getUserHasRolePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userHasRole with id " + id + " no longer exists.", enfe);
            }
            VmUser vmUser = userHasRole.getVmUser();
            if (vmUser != null) {
                vmUser.getUserHasRoleList().remove(userHasRole);
                vmUser = em.merge(vmUser);
            }
            Role role = userHasRole.getRole();
            if (role != null) {
                role.getUserHasRoleList().remove(userHasRole);
                role = em.merge(role);
            }
            em.remove(userHasRole);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserHasRole> findUserHasRoleEntities() {
        return findUserHasRoleEntities(true, -1, -1);
    }

    public List<UserHasRole> findUserHasRoleEntities(int maxResults, int firstResult) {
        return findUserHasRoleEntities(false, maxResults, firstResult);
    }

    private List<UserHasRole> findUserHasRoleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserHasRole.class));
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

    public UserHasRole findUserHasRole(UserHasRolePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserHasRole.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserHasRoleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserHasRole> rt = cq.from(UserHasRole.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
