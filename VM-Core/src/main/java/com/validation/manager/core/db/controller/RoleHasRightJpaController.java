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
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.RoleHasRight;
import com.validation.manager.core.db.RoleHasRightPK;
import com.validation.manager.core.db.UserRight;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RoleHasRightJpaController implements Serializable {

    public RoleHasRightJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RoleHasRight roleHasRight) throws PreexistingEntityException, Exception {
        if (roleHasRight.getRoleHasRightPK() == null) {
            roleHasRight.setRoleHasRightPK(new RoleHasRightPK());
        }
        roleHasRight.getRoleHasRightPK().setRoleId(roleHasRight.getRole().getId());
        roleHasRight.getRoleHasRightPK().setRightId(roleHasRight.getUserRight().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Role role = roleHasRight.getRole();
            if (role != null) {
                role = em.getReference(role.getClass(), role.getId());
                roleHasRight.setRole(role);
            }
            UserRight userRight = roleHasRight.getUserRight();
            if (userRight != null) {
                userRight = em.getReference(userRight.getClass(), userRight.getId());
                roleHasRight.setUserRight(userRight);
            }
            em.persist(roleHasRight);
            if (role != null) {
                role.getRoleHasRightList().add(roleHasRight);
                role = em.merge(role);
            }
            if (userRight != null) {
                userRight.getRoleHasRightList().add(roleHasRight);
                userRight = em.merge(userRight);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRoleHasRight(roleHasRight.getRoleHasRightPK()) != null) {
                throw new PreexistingEntityException("RoleHasRight " + roleHasRight + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RoleHasRight roleHasRight) throws NonexistentEntityException, Exception {
        roleHasRight.getRoleHasRightPK().setRoleId(roleHasRight.getRole().getId());
        roleHasRight.getRoleHasRightPK().setRightId(roleHasRight.getUserRight().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RoleHasRight persistentRoleHasRight = em.find(RoleHasRight.class, roleHasRight.getRoleHasRightPK());
            Role roleOld = persistentRoleHasRight.getRole();
            Role roleNew = roleHasRight.getRole();
            UserRight userRightOld = persistentRoleHasRight.getUserRight();
            UserRight userRightNew = roleHasRight.getUserRight();
            if (roleNew != null) {
                roleNew = em.getReference(roleNew.getClass(), roleNew.getId());
                roleHasRight.setRole(roleNew);
            }
            if (userRightNew != null) {
                userRightNew = em.getReference(userRightNew.getClass(), userRightNew.getId());
                roleHasRight.setUserRight(userRightNew);
            }
            roleHasRight = em.merge(roleHasRight);
            if (roleOld != null && !roleOld.equals(roleNew)) {
                roleOld.getRoleHasRightList().remove(roleHasRight);
                roleOld = em.merge(roleOld);
            }
            if (roleNew != null && !roleNew.equals(roleOld)) {
                roleNew.getRoleHasRightList().add(roleHasRight);
                roleNew = em.merge(roleNew);
            }
            if (userRightOld != null && !userRightOld.equals(userRightNew)) {
                userRightOld.getRoleHasRightList().remove(roleHasRight);
                userRightOld = em.merge(userRightOld);
            }
            if (userRightNew != null && !userRightNew.equals(userRightOld)) {
                userRightNew.getRoleHasRightList().add(roleHasRight);
                userRightNew = em.merge(userRightNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RoleHasRightPK id = roleHasRight.getRoleHasRightPK();
                if (findRoleHasRight(id) == null) {
                    throw new NonexistentEntityException("The roleHasRight with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RoleHasRightPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RoleHasRight roleHasRight;
            try {
                roleHasRight = em.getReference(RoleHasRight.class, id);
                roleHasRight.getRoleHasRightPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The roleHasRight with id " + id + " no longer exists.", enfe);
            }
            Role role = roleHasRight.getRole();
            if (role != null) {
                role.getRoleHasRightList().remove(roleHasRight);
                role = em.merge(role);
            }
            UserRight userRight = roleHasRight.getUserRight();
            if (userRight != null) {
                userRight.getRoleHasRightList().remove(roleHasRight);
                userRight = em.merge(userRight);
            }
            em.remove(roleHasRight);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RoleHasRight> findRoleHasRightEntities() {
        return findRoleHasRightEntities(true, -1, -1);
    }

    public List<RoleHasRight> findRoleHasRightEntities(int maxResults, int firstResult) {
        return findRoleHasRightEntities(false, maxResults, firstResult);
    }

    private List<RoleHasRight> findRoleHasRightEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RoleHasRight.class));
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

    public RoleHasRight findRoleHasRight(RoleHasRightPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RoleHasRight.class, id);
        } finally {
            em.close();
        }
    }

    public int getRoleHasRightCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RoleHasRight> rt = cq.from(RoleHasRight.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
