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
import com.validation.manager.core.db.RootCause;
import com.validation.manager.core.db.UserHasRootCause;
import com.validation.manager.core.db.UserHasRootCausePK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UserHasRootCauseJpaController implements Serializable {

    public UserHasRootCauseJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserHasRootCause userHasRootCause) throws PreexistingEntityException, Exception {
        if (userHasRootCause.getUserHasRootCausePK() == null) {
            userHasRootCause.setUserHasRootCausePK(new UserHasRootCausePK());
        }
        userHasRootCause.getUserHasRootCausePK().setUserId(userHasRootCause.getVmUser().getId());
        userHasRootCause.getUserHasRootCausePK().setRootCauseRootCauseTypeId(userHasRootCause.getRootCause().getRootCausePK().getRootCauseTypeId());
        userHasRootCause.getUserHasRootCausePK().setRootCauseId(userHasRootCause.getRootCause().getRootCausePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            VmUser vmUser = userHasRootCause.getVmUser();
            if (vmUser != null) {
                vmUser = em.getReference(vmUser.getClass(), vmUser.getId());
                userHasRootCause.setVmUser(vmUser);
            }
            RootCause rootCause = userHasRootCause.getRootCause();
            if (rootCause != null) {
                rootCause = em.getReference(rootCause.getClass(), rootCause.getRootCausePK());
                userHasRootCause.setRootCause(rootCause);
            }
            em.persist(userHasRootCause);
            if (vmUser != null) {
                vmUser.getUserHasRootCauseList().add(userHasRootCause);
                vmUser = em.merge(vmUser);
            }
            if (rootCause != null) {
                rootCause.getUserHasRootCauseList().add(userHasRootCause);
                rootCause = em.merge(rootCause);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUserHasRootCause(userHasRootCause.getUserHasRootCausePK()) != null) {
                throw new PreexistingEntityException("UserHasRootCause " + userHasRootCause + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserHasRootCause userHasRootCause) throws NonexistentEntityException, Exception {
        userHasRootCause.getUserHasRootCausePK().setUserId(userHasRootCause.getVmUser().getId());
        userHasRootCause.getUserHasRootCausePK().setRootCauseRootCauseTypeId(userHasRootCause.getRootCause().getRootCausePK().getRootCauseTypeId());
        userHasRootCause.getUserHasRootCausePK().setRootCauseId(userHasRootCause.getRootCause().getRootCausePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserHasRootCause persistentUserHasRootCause = em.find(UserHasRootCause.class, userHasRootCause.getUserHasRootCausePK());
            VmUser vmUserOld = persistentUserHasRootCause.getVmUser();
            VmUser vmUserNew = userHasRootCause.getVmUser();
            RootCause rootCauseOld = persistentUserHasRootCause.getRootCause();
            RootCause rootCauseNew = userHasRootCause.getRootCause();
            if (vmUserNew != null) {
                vmUserNew = em.getReference(vmUserNew.getClass(), vmUserNew.getId());
                userHasRootCause.setVmUser(vmUserNew);
            }
            if (rootCauseNew != null) {
                rootCauseNew = em.getReference(rootCauseNew.getClass(), rootCauseNew.getRootCausePK());
                userHasRootCause.setRootCause(rootCauseNew);
            }
            userHasRootCause = em.merge(userHasRootCause);
            if (vmUserOld != null && !vmUserOld.equals(vmUserNew)) {
                vmUserOld.getUserHasRootCauseList().remove(userHasRootCause);
                vmUserOld = em.merge(vmUserOld);
            }
            if (vmUserNew != null && !vmUserNew.equals(vmUserOld)) {
                vmUserNew.getUserHasRootCauseList().add(userHasRootCause);
                vmUserNew = em.merge(vmUserNew);
            }
            if (rootCauseOld != null && !rootCauseOld.equals(rootCauseNew)) {
                rootCauseOld.getUserHasRootCauseList().remove(userHasRootCause);
                rootCauseOld = em.merge(rootCauseOld);
            }
            if (rootCauseNew != null && !rootCauseNew.equals(rootCauseOld)) {
                rootCauseNew.getUserHasRootCauseList().add(userHasRootCause);
                rootCauseNew = em.merge(rootCauseNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                UserHasRootCausePK id = userHasRootCause.getUserHasRootCausePK();
                if (findUserHasRootCause(id) == null) {
                    throw new NonexistentEntityException("The userHasRootCause with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(UserHasRootCausePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserHasRootCause userHasRootCause;
            try {
                userHasRootCause = em.getReference(UserHasRootCause.class, id);
                userHasRootCause.getUserHasRootCausePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userHasRootCause with id " + id + " no longer exists.", enfe);
            }
            VmUser vmUser = userHasRootCause.getVmUser();
            if (vmUser != null) {
                vmUser.getUserHasRootCauseList().remove(userHasRootCause);
                vmUser = em.merge(vmUser);
            }
            RootCause rootCause = userHasRootCause.getRootCause();
            if (rootCause != null) {
                rootCause.getUserHasRootCauseList().remove(userHasRootCause);
                rootCause = em.merge(rootCause);
            }
            em.remove(userHasRootCause);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserHasRootCause> findUserHasRootCauseEntities() {
        return findUserHasRootCauseEntities(true, -1, -1);
    }

    public List<UserHasRootCause> findUserHasRootCauseEntities(int maxResults, int firstResult) {
        return findUserHasRootCauseEntities(false, maxResults, firstResult);
    }

    private List<UserHasRootCause> findUserHasRootCauseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserHasRootCause.class));
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

    public UserHasRootCause findUserHasRootCause(UserHasRootCausePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserHasRootCause.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserHasRootCauseCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserHasRootCause> rt = cq.from(UserHasRootCause.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
