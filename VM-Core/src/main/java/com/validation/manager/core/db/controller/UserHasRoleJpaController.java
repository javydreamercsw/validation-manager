/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.UserHasRole;
import com.validation.manager.core.db.UserHasRolePK;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
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
        userHasRole.getUserHasRolePK().setUserId(userHasRole.getVmUser().getId());
        userHasRole.getUserHasRolePK().setRoleId(userHasRole.getRole().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project projectId = userHasRole.getProjectId();
            if (projectId != null) {
                projectId = em.getReference(projectId.getClass(), projectId.getId());
                userHasRole.setProjectId(projectId);
            }
            Role role = userHasRole.getRole();
            if (role != null) {
                role = em.getReference(role.getClass(), role.getId());
                userHasRole.setRole(role);
            }
            VmUser vmUser = userHasRole.getVmUser();
            if (vmUser != null) {
                vmUser = em.getReference(vmUser.getClass(), vmUser.getId());
                userHasRole.setVmUser(vmUser);
            }
            em.persist(userHasRole);
            if (projectId != null) {
                projectId.getUserHasRoleList().add(userHasRole);
                projectId = em.merge(projectId);
            }
            if (role != null) {
                role.getUserHasRoleList().add(userHasRole);
                role = em.merge(role);
            }
            if (vmUser != null) {
                vmUser.getUserHasRoleList().add(userHasRole);
                vmUser = em.merge(vmUser);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findUserHasRole(userHasRole.getUserHasRolePK()) != null) {
                throw new PreexistingEntityException("UserHasRole " + userHasRole + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserHasRole userHasRole) throws NonexistentEntityException, Exception {
        userHasRole.getUserHasRolePK().setUserId(userHasRole.getVmUser().getId());
        userHasRole.getUserHasRolePK().setRoleId(userHasRole.getRole().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserHasRole persistentUserHasRole = em.find(UserHasRole.class, userHasRole.getUserHasRolePK());
            Project projectIdOld = persistentUserHasRole.getProjectId();
            Project projectIdNew = userHasRole.getProjectId();
            Role roleOld = persistentUserHasRole.getRole();
            Role roleNew = userHasRole.getRole();
            VmUser vmUserOld = persistentUserHasRole.getVmUser();
            VmUser vmUserNew = userHasRole.getVmUser();
            if (projectIdNew != null) {
                projectIdNew = em.getReference(projectIdNew.getClass(), projectIdNew.getId());
                userHasRole.setProjectId(projectIdNew);
            }
            if (roleNew != null) {
                roleNew = em.getReference(roleNew.getClass(), roleNew.getId());
                userHasRole.setRole(roleNew);
            }
            if (vmUserNew != null) {
                vmUserNew = em.getReference(vmUserNew.getClass(), vmUserNew.getId());
                userHasRole.setVmUser(vmUserNew);
            }
            userHasRole = em.merge(userHasRole);
            if (projectIdOld != null && !projectIdOld.equals(projectIdNew)) {
                projectIdOld.getUserHasRoleList().remove(userHasRole);
                projectIdOld = em.merge(projectIdOld);
            }
            if (projectIdNew != null && !projectIdNew.equals(projectIdOld)) {
                projectIdNew.getUserHasRoleList().add(userHasRole);
                projectIdNew = em.merge(projectIdNew);
            }
            if (roleOld != null && !roleOld.equals(roleNew)) {
                roleOld.getUserHasRoleList().remove(userHasRole);
                roleOld = em.merge(roleOld);
            }
            if (roleNew != null && !roleNew.equals(roleOld)) {
                roleNew.getUserHasRoleList().add(userHasRole);
                roleNew = em.merge(roleNew);
            }
            if (vmUserOld != null && !vmUserOld.equals(vmUserNew)) {
                vmUserOld.getUserHasRoleList().remove(userHasRole);
                vmUserOld = em.merge(vmUserOld);
            }
            if (vmUserNew != null && !vmUserNew.equals(vmUserOld)) {
                vmUserNew.getUserHasRoleList().add(userHasRole);
                vmUserNew = em.merge(vmUserNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                UserHasRolePK id = userHasRole.getUserHasRolePK();
                if (findUserHasRole(id) == null) {
                    throw new NonexistentEntityException("The userHasRole with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
        finally {
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
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userHasRole with id " + id + " no longer exists.", enfe);
            }
            Project projectId = userHasRole.getProjectId();
            if (projectId != null) {
                projectId.getUserHasRoleList().remove(userHasRole);
                projectId = em.merge(projectId);
            }
            Role role = userHasRole.getRole();
            if (role != null) {
                role.getUserHasRoleList().remove(userHasRole);
                role = em.merge(role);
            }
            VmUser vmUser = userHasRole.getVmUser();
            if (vmUser != null) {
                vmUser.getUserHasRoleList().remove(userHasRole);
                vmUser = em.merge(vmUser);
            }
            em.remove(userHasRole);
            em.getTransaction().commit();
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

    public UserHasRole findUserHasRole(UserHasRolePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserHasRole.class, id);
        }
        finally {
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
        }
        finally {
            em.close();
        }
    }

}
