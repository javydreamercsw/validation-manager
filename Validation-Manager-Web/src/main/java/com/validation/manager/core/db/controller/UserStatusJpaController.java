/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.UserStatus;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UserStatusJpaController implements Serializable {

    public UserStatusJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserStatus userStatus) {
        if (userStatus.getVmUserList() == null) {
            userStatus.setVmUserList(new ArrayList<VmUser>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<VmUser> attachedVmUserList = new ArrayList<VmUser>();
            for (VmUser vmUserListVmUserToAttach : userStatus.getVmUserList()) {
                vmUserListVmUserToAttach = em.getReference(vmUserListVmUserToAttach.getClass(), vmUserListVmUserToAttach.getId());
                attachedVmUserList.add(vmUserListVmUserToAttach);
            }
            userStatus.setVmUserList(attachedVmUserList);
            em.persist(userStatus);
            for (VmUser vmUserListVmUser : userStatus.getVmUserList()) {
                UserStatus oldUserStatusOfVmUserListVmUser = vmUserListVmUser.getUserStatus();
                vmUserListVmUser.setUserStatus(userStatus);
                vmUserListVmUser = em.merge(vmUserListVmUser);
                if (oldUserStatusOfVmUserListVmUser != null) {
                    oldUserStatusOfVmUserListVmUser.getVmUserList().remove(vmUserListVmUser);
                    oldUserStatusOfVmUserListVmUser = em.merge(oldUserStatusOfVmUserListVmUser);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserStatus userStatus) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserStatus persistentUserStatus = em.find(UserStatus.class, userStatus.getId());
            List<VmUser> vmUserListOld = persistentUserStatus.getVmUserList();
            List<VmUser> vmUserListNew = userStatus.getVmUserList();
            List<String> illegalOrphanMessages = null;
            for (VmUser vmUserListOldVmUser : vmUserListOld) {
                if (!vmUserListNew.contains(vmUserListOldVmUser)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain VmUser " + vmUserListOldVmUser + " since its userStatus field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<VmUser> attachedVmUserListNew = new ArrayList<VmUser>();
            for (VmUser vmUserListNewVmUserToAttach : vmUserListNew) {
                vmUserListNewVmUserToAttach = em.getReference(vmUserListNewVmUserToAttach.getClass(), vmUserListNewVmUserToAttach.getId());
                attachedVmUserListNew.add(vmUserListNewVmUserToAttach);
            }
            vmUserListNew = attachedVmUserListNew;
            userStatus.setVmUserList(vmUserListNew);
            userStatus = em.merge(userStatus);
            for (VmUser vmUserListNewVmUser : vmUserListNew) {
                if (!vmUserListOld.contains(vmUserListNewVmUser)) {
                    UserStatus oldUserStatusOfVmUserListNewVmUser = vmUserListNewVmUser.getUserStatus();
                    vmUserListNewVmUser.setUserStatus(userStatus);
                    vmUserListNewVmUser = em.merge(vmUserListNewVmUser);
                    if (oldUserStatusOfVmUserListNewVmUser != null && !oldUserStatusOfVmUserListNewVmUser.equals(userStatus)) {
                        oldUserStatusOfVmUserListNewVmUser.getVmUserList().remove(vmUserListNewVmUser);
                        oldUserStatusOfVmUserListNewVmUser = em.merge(oldUserStatusOfVmUserListNewVmUser);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = userStatus.getId();
                if (findUserStatus(id) == null) {
                    throw new NonexistentEntityException("The userStatus with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserStatus userStatus;
            try {
                userStatus = em.getReference(UserStatus.class, id);
                userStatus.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userStatus with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<VmUser> vmUserListOrphanCheck = userStatus.getVmUserList();
            for (VmUser vmUserListOrphanCheckVmUser : vmUserListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This UserStatus (" + userStatus + ") cannot be destroyed since the VmUser " + vmUserListOrphanCheckVmUser + " in its vmUserList field has a non-nullable userStatus field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(userStatus);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserStatus> findUserStatusEntities() {
        return findUserStatusEntities(true, -1, -1);
    }

    public List<UserStatus> findUserStatusEntities(int maxResults, int firstResult) {
        return findUserStatusEntities(false, maxResults, firstResult);
    }

    private List<UserStatus> findUserStatusEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserStatus.class));
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

    public UserStatus findUserStatus(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserStatus.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserStatusCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserStatus> rt = cq.from(UserStatus.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
