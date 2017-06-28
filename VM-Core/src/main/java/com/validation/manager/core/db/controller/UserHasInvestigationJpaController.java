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
import com.validation.manager.core.db.Investigation;
import com.validation.manager.core.db.UserHasInvestigation;
import com.validation.manager.core.db.UserHasInvestigationPK;
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
public class UserHasInvestigationJpaController implements Serializable {

    public UserHasInvestigationJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserHasInvestigation userHasInvestigation) throws PreexistingEntityException, Exception {
        if (userHasInvestigation.getUserHasInvestigationPK() == null) {
            userHasInvestigation.setUserHasInvestigationPK(new UserHasInvestigationPK());
        }
        userHasInvestigation.getUserHasInvestigationPK().setUserId(userHasInvestigation.getVmUser().getId());
        userHasInvestigation.getUserHasInvestigationPK().setInvestigationId(userHasInvestigation.getInvestigation().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Investigation investigation = userHasInvestigation.getInvestigation();
            if (investigation != null) {
                investigation = em.getReference(investigation.getClass(), investigation.getId());
                userHasInvestigation.setInvestigation(investigation);
            }
            VmUser vmUser = userHasInvestigation.getVmUser();
            if (vmUser != null) {
                vmUser = em.getReference(vmUser.getClass(), vmUser.getId());
                userHasInvestigation.setVmUser(vmUser);
            }
            em.persist(userHasInvestigation);
            if (investigation != null) {
                investigation.getUserHasInvestigationList().add(userHasInvestigation);
                investigation = em.merge(investigation);
            }
            if (vmUser != null) {
                vmUser.getUserHasInvestigationList().add(userHasInvestigation);
                vmUser = em.merge(vmUser);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findUserHasInvestigation(userHasInvestigation.getUserHasInvestigationPK()) != null) {
                throw new PreexistingEntityException("UserHasInvestigation " + userHasInvestigation + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserHasInvestigation userHasInvestigation) throws NonexistentEntityException, Exception {
        userHasInvestigation.getUserHasInvestigationPK().setUserId(userHasInvestigation.getVmUser().getId());
        userHasInvestigation.getUserHasInvestigationPK().setInvestigationId(userHasInvestigation.getInvestigation().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserHasInvestigation persistentUserHasInvestigation = em.find(UserHasInvestigation.class, userHasInvestigation.getUserHasInvestigationPK());
            Investigation investigationOld = persistentUserHasInvestigation.getInvestigation();
            Investigation investigationNew = userHasInvestigation.getInvestigation();
            VmUser vmUserOld = persistentUserHasInvestigation.getVmUser();
            VmUser vmUserNew = userHasInvestigation.getVmUser();
            if (investigationNew != null) {
                investigationNew = em.getReference(investigationNew.getClass(), investigationNew.getId());
                userHasInvestigation.setInvestigation(investigationNew);
            }
            if (vmUserNew != null) {
                vmUserNew = em.getReference(vmUserNew.getClass(), vmUserNew.getId());
                userHasInvestigation.setVmUser(vmUserNew);
            }
            userHasInvestigation = em.merge(userHasInvestigation);
            if (investigationOld != null && !investigationOld.equals(investigationNew)) {
                investigationOld.getUserHasInvestigationList().remove(userHasInvestigation);
                investigationOld = em.merge(investigationOld);
            }
            if (investigationNew != null && !investigationNew.equals(investigationOld)) {
                investigationNew.getUserHasInvestigationList().add(userHasInvestigation);
                investigationNew = em.merge(investigationNew);
            }
            if (vmUserOld != null && !vmUserOld.equals(vmUserNew)) {
                vmUserOld.getUserHasInvestigationList().remove(userHasInvestigation);
                vmUserOld = em.merge(vmUserOld);
            }
            if (vmUserNew != null && !vmUserNew.equals(vmUserOld)) {
                vmUserNew.getUserHasInvestigationList().add(userHasInvestigation);
                vmUserNew = em.merge(vmUserNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                UserHasInvestigationPK id = userHasInvestigation.getUserHasInvestigationPK();
                if (findUserHasInvestigation(id) == null) {
                    throw new NonexistentEntityException("The userHasInvestigation with id " + id + " no longer exists.");
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

    public void destroy(UserHasInvestigationPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserHasInvestigation userHasInvestigation;
            try {
                userHasInvestigation = em.getReference(UserHasInvestigation.class, id);
                userHasInvestigation.getUserHasInvestigationPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userHasInvestigation with id " + id + " no longer exists.", enfe);
            }
            Investigation investigation = userHasInvestigation.getInvestigation();
            if (investigation != null) {
                investigation.getUserHasInvestigationList().remove(userHasInvestigation);
                investigation = em.merge(investigation);
            }
            VmUser vmUser = userHasInvestigation.getVmUser();
            if (vmUser != null) {
                vmUser.getUserHasInvestigationList().remove(userHasInvestigation);
                vmUser = em.merge(vmUser);
            }
            em.remove(userHasInvestigation);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserHasInvestigation> findUserHasInvestigationEntities() {
        return findUserHasInvestigationEntities(true, -1, -1);
    }

    public List<UserHasInvestigation> findUserHasInvestigationEntities(int maxResults, int firstResult) {
        return findUserHasInvestigationEntities(false, maxResults, firstResult);
    }

    private List<UserHasInvestigation> findUserHasInvestigationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserHasInvestigation.class));
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

    public UserHasInvestigation findUserHasInvestigation(UserHasInvestigationPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserHasInvestigation.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getUserHasInvestigationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserHasInvestigation> rt = cq.from(UserHasInvestigation.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
