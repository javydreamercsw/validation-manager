/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.controller;

import com.validation.manager.core.db.Role;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.VmUser;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.UserRight;
import com.validation.manager.core.db.UserTestProjectRole;
import com.validation.manager.core.db.UserTestPlanRole;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RoleJpaController implements Serializable {

    public RoleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Role role) throws PreexistingEntityException, Exception {
        if (role.getVmUserList() == null) {
            role.setVmUserList(new ArrayList<VmUser>());
        }
        if (role.getUserRightList() == null) {
            role.setUserRightList(new ArrayList<UserRight>());
        }
        if (role.getUserTestProjectRoleList() == null) {
            role.setUserTestProjectRoleList(new ArrayList<UserTestProjectRole>());
        }
        if (role.getUserTestPlanRoleList() == null) {
            role.setUserTestPlanRoleList(new ArrayList<UserTestPlanRole>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<VmUser> attachedVmUserList = new ArrayList<VmUser>();
            for (VmUser vmUserListVmUserToAttach : role.getVmUserList()) {
                vmUserListVmUserToAttach = em.getReference(vmUserListVmUserToAttach.getClass(), vmUserListVmUserToAttach.getId());
                attachedVmUserList.add(vmUserListVmUserToAttach);
            }
            role.setVmUserList(attachedVmUserList);
            List<UserRight> attachedUserRightList = new ArrayList<UserRight>();
            for (UserRight userRightListUserRightToAttach : role.getUserRightList()) {
                userRightListUserRightToAttach = em.getReference(userRightListUserRightToAttach.getClass(), userRightListUserRightToAttach.getId());
                attachedUserRightList.add(userRightListUserRightToAttach);
            }
            role.setUserRightList(attachedUserRightList);
            List<UserTestProjectRole> attachedUserTestProjectRoleList = new ArrayList<UserTestProjectRole>();
            for (UserTestProjectRole userTestProjectRoleListUserTestProjectRoleToAttach : role.getUserTestProjectRoleList()) {
                userTestProjectRoleListUserTestProjectRoleToAttach = em.getReference(userTestProjectRoleListUserTestProjectRoleToAttach.getClass(), userTestProjectRoleListUserTestProjectRoleToAttach.getUserTestProjectRolePK());
                attachedUserTestProjectRoleList.add(userTestProjectRoleListUserTestProjectRoleToAttach);
            }
            role.setUserTestProjectRoleList(attachedUserTestProjectRoleList);
            List<UserTestPlanRole> attachedUserTestPlanRoleList = new ArrayList<UserTestPlanRole>();
            for (UserTestPlanRole userTestPlanRoleListUserTestPlanRoleToAttach : role.getUserTestPlanRoleList()) {
                userTestPlanRoleListUserTestPlanRoleToAttach = em.getReference(userTestPlanRoleListUserTestPlanRoleToAttach.getClass(), userTestPlanRoleListUserTestPlanRoleToAttach.getUserTestPlanRolePK());
                attachedUserTestPlanRoleList.add(userTestPlanRoleListUserTestPlanRoleToAttach);
            }
            role.setUserTestPlanRoleList(attachedUserTestPlanRoleList);
            em.persist(role);
            for (VmUser vmUserListVmUser : role.getVmUserList()) {
                vmUserListVmUser.getRoleList().add(role);
                vmUserListVmUser = em.merge(vmUserListVmUser);
            }
            for (UserRight userRightListUserRight : role.getUserRightList()) {
                userRightListUserRight.getRoleList().add(role);
                userRightListUserRight = em.merge(userRightListUserRight);
            }
            for (UserTestProjectRole userTestProjectRoleListUserTestProjectRole : role.getUserTestProjectRoleList()) {
                Role oldRoleOfUserTestProjectRoleListUserTestProjectRole = userTestProjectRoleListUserTestProjectRole.getRole();
                userTestProjectRoleListUserTestProjectRole.setRole(role);
                userTestProjectRoleListUserTestProjectRole = em.merge(userTestProjectRoleListUserTestProjectRole);
                if (oldRoleOfUserTestProjectRoleListUserTestProjectRole != null) {
                    oldRoleOfUserTestProjectRoleListUserTestProjectRole.getUserTestProjectRoleList().remove(userTestProjectRoleListUserTestProjectRole);
                    oldRoleOfUserTestProjectRoleListUserTestProjectRole = em.merge(oldRoleOfUserTestProjectRoleListUserTestProjectRole);
                }
            }
            for (UserTestPlanRole userTestPlanRoleListUserTestPlanRole : role.getUserTestPlanRoleList()) {
                Role oldRoleOfUserTestPlanRoleListUserTestPlanRole = userTestPlanRoleListUserTestPlanRole.getRole();
                userTestPlanRoleListUserTestPlanRole.setRole(role);
                userTestPlanRoleListUserTestPlanRole = em.merge(userTestPlanRoleListUserTestPlanRole);
                if (oldRoleOfUserTestPlanRoleListUserTestPlanRole != null) {
                    oldRoleOfUserTestPlanRoleListUserTestPlanRole.getUserTestPlanRoleList().remove(userTestPlanRoleListUserTestPlanRole);
                    oldRoleOfUserTestPlanRoleListUserTestPlanRole = em.merge(oldRoleOfUserTestPlanRoleListUserTestPlanRole);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRole(role.getId()) != null) {
                throw new PreexistingEntityException("Role " + role + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Role role) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Role persistentRole = em.find(Role.class, role.getId());
            List<VmUser> vmUserListOld = persistentRole.getVmUserList();
            List<VmUser> vmUserListNew = role.getVmUserList();
            List<UserRight> userRightListOld = persistentRole.getUserRightList();
            List<UserRight> userRightListNew = role.getUserRightList();
            List<UserTestProjectRole> userTestProjectRoleListOld = persistentRole.getUserTestProjectRoleList();
            List<UserTestProjectRole> userTestProjectRoleListNew = role.getUserTestProjectRoleList();
            List<UserTestPlanRole> userTestPlanRoleListOld = persistentRole.getUserTestPlanRoleList();
            List<UserTestPlanRole> userTestPlanRoleListNew = role.getUserTestPlanRoleList();
            List<String> illegalOrphanMessages = null;
            for (UserTestProjectRole userTestProjectRoleListOldUserTestProjectRole : userTestProjectRoleListOld) {
                if (!userTestProjectRoleListNew.contains(userTestProjectRoleListOldUserTestProjectRole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserTestProjectRole " + userTestProjectRoleListOldUserTestProjectRole + " since its role field is not nullable.");
                }
            }
            for (UserTestPlanRole userTestPlanRoleListOldUserTestPlanRole : userTestPlanRoleListOld) {
                if (!userTestPlanRoleListNew.contains(userTestPlanRoleListOldUserTestPlanRole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserTestPlanRole " + userTestPlanRoleListOldUserTestPlanRole + " since its role field is not nullable.");
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
            role.setVmUserList(vmUserListNew);
            List<UserRight> attachedUserRightListNew = new ArrayList<UserRight>();
            for (UserRight userRightListNewUserRightToAttach : userRightListNew) {
                userRightListNewUserRightToAttach = em.getReference(userRightListNewUserRightToAttach.getClass(), userRightListNewUserRightToAttach.getId());
                attachedUserRightListNew.add(userRightListNewUserRightToAttach);
            }
            userRightListNew = attachedUserRightListNew;
            role.setUserRightList(userRightListNew);
            List<UserTestProjectRole> attachedUserTestProjectRoleListNew = new ArrayList<UserTestProjectRole>();
            for (UserTestProjectRole userTestProjectRoleListNewUserTestProjectRoleToAttach : userTestProjectRoleListNew) {
                userTestProjectRoleListNewUserTestProjectRoleToAttach = em.getReference(userTestProjectRoleListNewUserTestProjectRoleToAttach.getClass(), userTestProjectRoleListNewUserTestProjectRoleToAttach.getUserTestProjectRolePK());
                attachedUserTestProjectRoleListNew.add(userTestProjectRoleListNewUserTestProjectRoleToAttach);
            }
            userTestProjectRoleListNew = attachedUserTestProjectRoleListNew;
            role.setUserTestProjectRoleList(userTestProjectRoleListNew);
            List<UserTestPlanRole> attachedUserTestPlanRoleListNew = new ArrayList<UserTestPlanRole>();
            for (UserTestPlanRole userTestPlanRoleListNewUserTestPlanRoleToAttach : userTestPlanRoleListNew) {
                userTestPlanRoleListNewUserTestPlanRoleToAttach = em.getReference(userTestPlanRoleListNewUserTestPlanRoleToAttach.getClass(), userTestPlanRoleListNewUserTestPlanRoleToAttach.getUserTestPlanRolePK());
                attachedUserTestPlanRoleListNew.add(userTestPlanRoleListNewUserTestPlanRoleToAttach);
            }
            userTestPlanRoleListNew = attachedUserTestPlanRoleListNew;
            role.setUserTestPlanRoleList(userTestPlanRoleListNew);
            role = em.merge(role);
            for (VmUser vmUserListOldVmUser : vmUserListOld) {
                if (!vmUserListNew.contains(vmUserListOldVmUser)) {
                    vmUserListOldVmUser.getRoleList().remove(role);
                    vmUserListOldVmUser = em.merge(vmUserListOldVmUser);
                }
            }
            for (VmUser vmUserListNewVmUser : vmUserListNew) {
                if (!vmUserListOld.contains(vmUserListNewVmUser)) {
                    vmUserListNewVmUser.getRoleList().add(role);
                    vmUserListNewVmUser = em.merge(vmUserListNewVmUser);
                }
            }
            for (UserRight userRightListOldUserRight : userRightListOld) {
                if (!userRightListNew.contains(userRightListOldUserRight)) {
                    userRightListOldUserRight.getRoleList().remove(role);
                    userRightListOldUserRight = em.merge(userRightListOldUserRight);
                }
            }
            for (UserRight userRightListNewUserRight : userRightListNew) {
                if (!userRightListOld.contains(userRightListNewUserRight)) {
                    userRightListNewUserRight.getRoleList().add(role);
                    userRightListNewUserRight = em.merge(userRightListNewUserRight);
                }
            }
            for (UserTestProjectRole userTestProjectRoleListNewUserTestProjectRole : userTestProjectRoleListNew) {
                if (!userTestProjectRoleListOld.contains(userTestProjectRoleListNewUserTestProjectRole)) {
                    Role oldRoleOfUserTestProjectRoleListNewUserTestProjectRole = userTestProjectRoleListNewUserTestProjectRole.getRole();
                    userTestProjectRoleListNewUserTestProjectRole.setRole(role);
                    userTestProjectRoleListNewUserTestProjectRole = em.merge(userTestProjectRoleListNewUserTestProjectRole);
                    if (oldRoleOfUserTestProjectRoleListNewUserTestProjectRole != null && !oldRoleOfUserTestProjectRoleListNewUserTestProjectRole.equals(role)) {
                        oldRoleOfUserTestProjectRoleListNewUserTestProjectRole.getUserTestProjectRoleList().remove(userTestProjectRoleListNewUserTestProjectRole);
                        oldRoleOfUserTestProjectRoleListNewUserTestProjectRole = em.merge(oldRoleOfUserTestProjectRoleListNewUserTestProjectRole);
                    }
                }
            }
            for (UserTestPlanRole userTestPlanRoleListNewUserTestPlanRole : userTestPlanRoleListNew) {
                if (!userTestPlanRoleListOld.contains(userTestPlanRoleListNewUserTestPlanRole)) {
                    Role oldRoleOfUserTestPlanRoleListNewUserTestPlanRole = userTestPlanRoleListNewUserTestPlanRole.getRole();
                    userTestPlanRoleListNewUserTestPlanRole.setRole(role);
                    userTestPlanRoleListNewUserTestPlanRole = em.merge(userTestPlanRoleListNewUserTestPlanRole);
                    if (oldRoleOfUserTestPlanRoleListNewUserTestPlanRole != null && !oldRoleOfUserTestPlanRoleListNewUserTestPlanRole.equals(role)) {
                        oldRoleOfUserTestPlanRoleListNewUserTestPlanRole.getUserTestPlanRoleList().remove(userTestPlanRoleListNewUserTestPlanRole);
                        oldRoleOfUserTestPlanRoleListNewUserTestPlanRole = em.merge(oldRoleOfUserTestPlanRoleListNewUserTestPlanRole);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = role.getId();
                if (findRole(id) == null) {
                    throw new NonexistentEntityException("The role with id " + id + " no longer exists.");
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
            Role role;
            try {
                role = em.getReference(Role.class, id);
                role.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The role with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<UserTestProjectRole> userTestProjectRoleListOrphanCheck = role.getUserTestProjectRoleList();
            for (UserTestProjectRole userTestProjectRoleListOrphanCheckUserTestProjectRole : userTestProjectRoleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Role (" + role + ") cannot be destroyed since the UserTestProjectRole " + userTestProjectRoleListOrphanCheckUserTestProjectRole + " in its userTestProjectRoleList field has a non-nullable role field.");
            }
            List<UserTestPlanRole> userTestPlanRoleListOrphanCheck = role.getUserTestPlanRoleList();
            for (UserTestPlanRole userTestPlanRoleListOrphanCheckUserTestPlanRole : userTestPlanRoleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Role (" + role + ") cannot be destroyed since the UserTestPlanRole " + userTestPlanRoleListOrphanCheckUserTestPlanRole + " in its userTestPlanRoleList field has a non-nullable role field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<VmUser> vmUserList = role.getVmUserList();
            for (VmUser vmUserListVmUser : vmUserList) {
                vmUserListVmUser.getRoleList().remove(role);
                vmUserListVmUser = em.merge(vmUserListVmUser);
            }
            List<UserRight> userRightList = role.getUserRightList();
            for (UserRight userRightListUserRight : userRightList) {
                userRightListUserRight.getRoleList().remove(role);
                userRightListUserRight = em.merge(userRightListUserRight);
            }
            em.remove(role);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Role> findRoleEntities() {
        return findRoleEntities(true, -1, -1);
    }

    public List<Role> findRoleEntities(int maxResults, int firstResult) {
        return findRoleEntities(false, maxResults, firstResult);
    }

    private List<Role> findRoleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Role.class));
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

    public Role findRole(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Role.class, id);
        } finally {
            em.close();
        }
    }

    public int getRoleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Role> rt = cq.from(Role.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
