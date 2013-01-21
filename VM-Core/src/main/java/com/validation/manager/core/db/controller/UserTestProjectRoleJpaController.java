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
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.UserTestProjectRole;
import com.validation.manager.core.db.UserTestProjectRolePK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UserTestProjectRoleJpaController implements Serializable {

    public UserTestProjectRoleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserTestProjectRole userTestProjectRole) throws PreexistingEntityException, Exception {
        if (userTestProjectRole.getUserTestProjectRolePK() == null) {
            userTestProjectRole.setUserTestProjectRolePK(new UserTestProjectRolePK());
        }
        userTestProjectRole.getUserTestProjectRolePK().setTestProjectId(userTestProjectRole.getTestProject().getId());
        userTestProjectRole.getUserTestProjectRolePK().setUserId(userTestProjectRole.getVmUser().getId());
        userTestProjectRole.getUserTestProjectRolePK().setRoleId(userTestProjectRole.getRole().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Role role = userTestProjectRole.getRole();
            if (role != null) {
                role = em.getReference(role.getClass(), role.getId());
                userTestProjectRole.setRole(role);
            }
            VmUser vmUser = userTestProjectRole.getVmUser();
            if (vmUser != null) {
                vmUser = em.getReference(vmUser.getClass(), vmUser.getId());
                userTestProjectRole.setVmUser(vmUser);
            }
            TestProject testProject = userTestProjectRole.getTestProject();
            if (testProject != null) {
                testProject = em.getReference(testProject.getClass(), testProject.getId());
                userTestProjectRole.setTestProject(testProject);
            }
            em.persist(userTestProjectRole);
            if (role != null) {
                role.getUserTestProjectRoleList().add(userTestProjectRole);
                role = em.merge(role);
            }
            if (vmUser != null) {
                vmUser.getUserTestProjectRoleList().add(userTestProjectRole);
                vmUser = em.merge(vmUser);
            }
            if (testProject != null) {
                testProject.getUserTestProjectRoleList().add(userTestProjectRole);
                testProject = em.merge(testProject);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUserTestProjectRole(userTestProjectRole.getUserTestProjectRolePK()) != null) {
                throw new PreexistingEntityException("UserTestProjectRole " + userTestProjectRole + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserTestProjectRole userTestProjectRole) throws NonexistentEntityException, Exception {
        userTestProjectRole.getUserTestProjectRolePK().setTestProjectId(userTestProjectRole.getTestProject().getId());
        userTestProjectRole.getUserTestProjectRolePK().setUserId(userTestProjectRole.getVmUser().getId());
        userTestProjectRole.getUserTestProjectRolePK().setRoleId(userTestProjectRole.getRole().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserTestProjectRole persistentUserTestProjectRole = em.find(UserTestProjectRole.class, userTestProjectRole.getUserTestProjectRolePK());
            Role roleOld = persistentUserTestProjectRole.getRole();
            Role roleNew = userTestProjectRole.getRole();
            VmUser vmUserOld = persistentUserTestProjectRole.getVmUser();
            VmUser vmUserNew = userTestProjectRole.getVmUser();
            TestProject testProjectOld = persistentUserTestProjectRole.getTestProject();
            TestProject testProjectNew = userTestProjectRole.getTestProject();
            if (roleNew != null) {
                roleNew = em.getReference(roleNew.getClass(), roleNew.getId());
                userTestProjectRole.setRole(roleNew);
            }
            if (vmUserNew != null) {
                vmUserNew = em.getReference(vmUserNew.getClass(), vmUserNew.getId());
                userTestProjectRole.setVmUser(vmUserNew);
            }
            if (testProjectNew != null) {
                testProjectNew = em.getReference(testProjectNew.getClass(), testProjectNew.getId());
                userTestProjectRole.setTestProject(testProjectNew);
            }
            userTestProjectRole = em.merge(userTestProjectRole);
            if (roleOld != null && !roleOld.equals(roleNew)) {
                roleOld.getUserTestProjectRoleList().remove(userTestProjectRole);
                roleOld = em.merge(roleOld);
            }
            if (roleNew != null && !roleNew.equals(roleOld)) {
                roleNew.getUserTestProjectRoleList().add(userTestProjectRole);
                roleNew = em.merge(roleNew);
            }
            if (vmUserOld != null && !vmUserOld.equals(vmUserNew)) {
                vmUserOld.getUserTestProjectRoleList().remove(userTestProjectRole);
                vmUserOld = em.merge(vmUserOld);
            }
            if (vmUserNew != null && !vmUserNew.equals(vmUserOld)) {
                vmUserNew.getUserTestProjectRoleList().add(userTestProjectRole);
                vmUserNew = em.merge(vmUserNew);
            }
            if (testProjectOld != null && !testProjectOld.equals(testProjectNew)) {
                testProjectOld.getUserTestProjectRoleList().remove(userTestProjectRole);
                testProjectOld = em.merge(testProjectOld);
            }
            if (testProjectNew != null && !testProjectNew.equals(testProjectOld)) {
                testProjectNew.getUserTestProjectRoleList().add(userTestProjectRole);
                testProjectNew = em.merge(testProjectNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                UserTestProjectRolePK id = userTestProjectRole.getUserTestProjectRolePK();
                if (findUserTestProjectRole(id) == null) {
                    throw new NonexistentEntityException("The userTestProjectRole with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(UserTestProjectRolePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserTestProjectRole userTestProjectRole;
            try {
                userTestProjectRole = em.getReference(UserTestProjectRole.class, id);
                userTestProjectRole.getUserTestProjectRolePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userTestProjectRole with id " + id + " no longer exists.", enfe);
            }
            Role role = userTestProjectRole.getRole();
            if (role != null) {
                role.getUserTestProjectRoleList().remove(userTestProjectRole);
                role = em.merge(role);
            }
            VmUser vmUser = userTestProjectRole.getVmUser();
            if (vmUser != null) {
                vmUser.getUserTestProjectRoleList().remove(userTestProjectRole);
                vmUser = em.merge(vmUser);
            }
            TestProject testProject = userTestProjectRole.getTestProject();
            if (testProject != null) {
                testProject.getUserTestProjectRoleList().remove(userTestProjectRole);
                testProject = em.merge(testProject);
            }
            em.remove(userTestProjectRole);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserTestProjectRole> findUserTestProjectRoleEntities() {
        return findUserTestProjectRoleEntities(true, -1, -1);
    }

    public List<UserTestProjectRole> findUserTestProjectRoleEntities(int maxResults, int firstResult) {
        return findUserTestProjectRoleEntities(false, maxResults, firstResult);
    }

    private List<UserTestProjectRole> findUserTestProjectRoleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserTestProjectRole.class));
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

    public UserTestProjectRole findUserTestProjectRole(UserTestProjectRolePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserTestProjectRole.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserTestProjectRoleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserTestProjectRole> rt = cq.from(UserTestProjectRole.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
