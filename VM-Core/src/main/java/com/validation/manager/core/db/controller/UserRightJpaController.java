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
import com.validation.manager.core.db.Role;
import com.validation.manager.core.db.UserRight;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class UserRightJpaController implements Serializable {

    public UserRightJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserRight userRight) {
        if (userRight.getRoleList() == null) {
            userRight.setRoleList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Role> attachedRoleList = new ArrayList<>();
            for (Role roleListRoleToAttach : userRight.getRoleList()) {
                roleListRoleToAttach = em.getReference(roleListRoleToAttach.getClass(), roleListRoleToAttach.getId());
                attachedRoleList.add(roleListRoleToAttach);
            }
            userRight.setRoleList(attachedRoleList);
            em.persist(userRight);
            for (Role roleListRole : userRight.getRoleList()) {
                roleListRole.getUserRightList().add(userRight);
                roleListRole = em.merge(roleListRole);
            }
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserRight userRight) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserRight persistentUserRight = em.find(UserRight.class, userRight.getId());
            List<Role> roleListOld = persistentUserRight.getRoleList();
            List<Role> roleListNew = userRight.getRoleList();
            List<Role> attachedRoleListNew = new ArrayList<>();
            for (Role roleListNewRoleToAttach : roleListNew) {
                roleListNewRoleToAttach = em.getReference(roleListNewRoleToAttach.getClass(), roleListNewRoleToAttach.getId());
                attachedRoleListNew.add(roleListNewRoleToAttach);
            }
            roleListNew = attachedRoleListNew;
            userRight.setRoleList(roleListNew);
            userRight = em.merge(userRight);
            for (Role roleListOldRole : roleListOld) {
                if (!roleListNew.contains(roleListOldRole)) {
                    roleListOldRole.getUserRightList().remove(userRight);
                    roleListOldRole = em.merge(roleListOldRole);
                }
            }
            for (Role roleListNewRole : roleListNew) {
                if (!roleListOld.contains(roleListNewRole)) {
                    roleListNewRole.getUserRightList().add(userRight);
                    roleListNewRole = em.merge(roleListNewRole);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = userRight.getId();
                if (findUserRight(id) == null) {
                    throw new NonexistentEntityException("The userRight with id " + id + " no longer exists.");
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

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserRight userRight;
            try {
                userRight = em.getReference(UserRight.class, id);
                userRight.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userRight with id " + id + " no longer exists.", enfe);
            }
            List<Role> roleList = userRight.getRoleList();
            for (Role roleListRole : roleList) {
                roleListRole.getUserRightList().remove(userRight);
                roleListRole = em.merge(roleListRole);
            }
            em.remove(userRight);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserRight> findUserRightEntities() {
        return findUserRightEntities(true, -1, -1);
    }

    public List<UserRight> findUserRightEntities(int maxResults, int firstResult) {
        return findUserRightEntities(false, maxResults, firstResult);
    }

    private List<UserRight> findUserRightEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserRight.class));
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

    public UserRight findUserRight(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserRight.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getUserRightCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserRight> rt = cq.from(UserRight.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
