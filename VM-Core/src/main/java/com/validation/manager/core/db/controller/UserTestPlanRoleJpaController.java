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
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.UserTestPlanRole;
import com.validation.manager.core.db.UserTestPlanRolePK;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class UserTestPlanRoleJpaController implements Serializable {

    public UserTestPlanRoleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserTestPlanRole userTestPlanRole) throws PreexistingEntityException, Exception {
        if (userTestPlanRole.getUserTestPlanRolePK() == null) {
            userTestPlanRole.setUserTestPlanRolePK(new UserTestPlanRolePK());
        }
        userTestPlanRole.getUserTestPlanRolePK().setUserId(userTestPlanRole.getVmUser().getId());
        userTestPlanRole.getUserTestPlanRolePK().setTestPlanId(userTestPlanRole.getTestPlan().getTestPlanPK().getId());
        userTestPlanRole.getUserTestPlanRolePK().setRoleId(userTestPlanRole.getRole().getId());
        userTestPlanRole.getUserTestPlanRolePK().setTestPlanTestProjectId(userTestPlanRole.getTestPlan().getTestPlanPK().getTestProjectId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TestPlan testPlan = userTestPlanRole.getTestPlan();
            if (testPlan != null) {
                testPlan = em.getReference(testPlan.getClass(), testPlan.getTestPlanPK());
                userTestPlanRole.setTestPlan(testPlan);
            }
            VmUser vmUser = userTestPlanRole.getVmUser();
            if (vmUser != null) {
                vmUser = em.getReference(vmUser.getClass(), vmUser.getId());
                userTestPlanRole.setVmUser(vmUser);
            }
            Role role = userTestPlanRole.getRole();
            if (role != null) {
                role = em.getReference(role.getClass(), role.getId());
                userTestPlanRole.setRole(role);
            }
            em.persist(userTestPlanRole);
            if (testPlan != null) {
                testPlan.getUserTestPlanRoleList().add(userTestPlanRole);
                testPlan = em.merge(testPlan);
            }
            if (vmUser != null) {
                vmUser.getUserTestPlanRoleList().add(userTestPlanRole);
                vmUser = em.merge(vmUser);
            }
            if (role != null) {
                role.getUserTestPlanRoleList().add(userTestPlanRole);
                role = em.merge(role);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findUserTestPlanRole(userTestPlanRole.getUserTestPlanRolePK()) != null) {
                throw new PreexistingEntityException("UserTestPlanRole " + userTestPlanRole + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserTestPlanRole userTestPlanRole) throws NonexistentEntityException, Exception {
        userTestPlanRole.getUserTestPlanRolePK().setUserId(userTestPlanRole.getVmUser().getId());
        userTestPlanRole.getUserTestPlanRolePK().setTestPlanId(userTestPlanRole.getTestPlan().getTestPlanPK().getId());
        userTestPlanRole.getUserTestPlanRolePK().setRoleId(userTestPlanRole.getRole().getId());
        userTestPlanRole.getUserTestPlanRolePK().setTestPlanTestProjectId(userTestPlanRole.getTestPlan().getTestPlanPK().getTestProjectId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserTestPlanRole persistentUserTestPlanRole = em.find(UserTestPlanRole.class, userTestPlanRole.getUserTestPlanRolePK());
            TestPlan testPlanOld = persistentUserTestPlanRole.getTestPlan();
            TestPlan testPlanNew = userTestPlanRole.getTestPlan();
            VmUser vmUserOld = persistentUserTestPlanRole.getVmUser();
            VmUser vmUserNew = userTestPlanRole.getVmUser();
            Role roleOld = persistentUserTestPlanRole.getRole();
            Role roleNew = userTestPlanRole.getRole();
            if (testPlanNew != null) {
                testPlanNew = em.getReference(testPlanNew.getClass(), testPlanNew.getTestPlanPK());
                userTestPlanRole.setTestPlan(testPlanNew);
            }
            if (vmUserNew != null) {
                vmUserNew = em.getReference(vmUserNew.getClass(), vmUserNew.getId());
                userTestPlanRole.setVmUser(vmUserNew);
            }
            if (roleNew != null) {
                roleNew = em.getReference(roleNew.getClass(), roleNew.getId());
                userTestPlanRole.setRole(roleNew);
            }
            userTestPlanRole = em.merge(userTestPlanRole);
            if (testPlanOld != null && !testPlanOld.equals(testPlanNew)) {
                testPlanOld.getUserTestPlanRoleList().remove(userTestPlanRole);
                testPlanOld = em.merge(testPlanOld);
            }
            if (testPlanNew != null && !testPlanNew.equals(testPlanOld)) {
                testPlanNew.getUserTestPlanRoleList().add(userTestPlanRole);
                testPlanNew = em.merge(testPlanNew);
            }
            if (vmUserOld != null && !vmUserOld.equals(vmUserNew)) {
                vmUserOld.getUserTestPlanRoleList().remove(userTestPlanRole);
                vmUserOld = em.merge(vmUserOld);
            }
            if (vmUserNew != null && !vmUserNew.equals(vmUserOld)) {
                vmUserNew.getUserTestPlanRoleList().add(userTestPlanRole);
                vmUserNew = em.merge(vmUserNew);
            }
            if (roleOld != null && !roleOld.equals(roleNew)) {
                roleOld.getUserTestPlanRoleList().remove(userTestPlanRole);
                roleOld = em.merge(roleOld);
            }
            if (roleNew != null && !roleNew.equals(roleOld)) {
                roleNew.getUserTestPlanRoleList().add(userTestPlanRole);
                roleNew = em.merge(roleNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                UserTestPlanRolePK id = userTestPlanRole.getUserTestPlanRolePK();
                if (findUserTestPlanRole(id) == null) {
                    throw new NonexistentEntityException("The userTestPlanRole with id " + id + " no longer exists.");
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

    public void destroy(UserTestPlanRolePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserTestPlanRole userTestPlanRole;
            try {
                userTestPlanRole = em.getReference(UserTestPlanRole.class, id);
                userTestPlanRole.getUserTestPlanRolePK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userTestPlanRole with id " + id + " no longer exists.", enfe);
            }
            TestPlan testPlan = userTestPlanRole.getTestPlan();
            if (testPlan != null) {
                testPlan.getUserTestPlanRoleList().remove(userTestPlanRole);
                testPlan = em.merge(testPlan);
            }
            VmUser vmUser = userTestPlanRole.getVmUser();
            if (vmUser != null) {
                vmUser.getUserTestPlanRoleList().remove(userTestPlanRole);
                vmUser = em.merge(vmUser);
            }
            Role role = userTestPlanRole.getRole();
            if (role != null) {
                role.getUserTestPlanRoleList().remove(userTestPlanRole);
                role = em.merge(role);
            }
            em.remove(userTestPlanRole);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserTestPlanRole> findUserTestPlanRoleEntities() {
        return findUserTestPlanRoleEntities(true, -1, -1);
    }

    public List<UserTestPlanRole> findUserTestPlanRoleEntities(int maxResults, int firstResult) {
        return findUserTestPlanRoleEntities(false, maxResults, firstResult);
    }

    private List<UserTestPlanRole> findUserTestPlanRoleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserTestPlanRole.class));
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

    public UserTestPlanRole findUserTestPlanRole(UserTestPlanRolePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserTestPlanRole.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getUserTestPlanRoleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserTestPlanRole> rt = cq.from(UserTestPlanRole.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
