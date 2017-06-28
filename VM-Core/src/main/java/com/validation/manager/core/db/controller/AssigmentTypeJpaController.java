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

import com.validation.manager.core.db.AssigmentType;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.UserAssigment;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class AssigmentTypeJpaController implements Serializable {

    public AssigmentTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(AssigmentType assigmentType) {
        if (assigmentType.getUserAssigmentList() == null) {
            assigmentType.setUserAssigmentList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<UserAssigment> attachedUserAssigmentList = new ArrayList<>();
            for (UserAssigment userAssigmentListUserAssigmentToAttach : assigmentType.getUserAssigmentList()) {
                userAssigmentListUserAssigmentToAttach = em.getReference(userAssigmentListUserAssigmentToAttach.getClass(), userAssigmentListUserAssigmentToAttach.getUserAssigmentPK());
                attachedUserAssigmentList.add(userAssigmentListUserAssigmentToAttach);
            }
            assigmentType.setUserAssigmentList(attachedUserAssigmentList);
            em.persist(assigmentType);
            for (UserAssigment userAssigmentListUserAssigment : assigmentType.getUserAssigmentList()) {
                AssigmentType oldAssigmentTypeOfUserAssigmentListUserAssigment = userAssigmentListUserAssigment.getAssigmentType();
                userAssigmentListUserAssigment.setAssigmentType(assigmentType);
                userAssigmentListUserAssigment = em.merge(userAssigmentListUserAssigment);
                if (oldAssigmentTypeOfUserAssigmentListUserAssigment != null) {
                    oldAssigmentTypeOfUserAssigmentListUserAssigment.getUserAssigmentList().remove(userAssigmentListUserAssigment);
                    oldAssigmentTypeOfUserAssigmentListUserAssigment = em.merge(oldAssigmentTypeOfUserAssigmentListUserAssigment);
                }
            }
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(AssigmentType assigmentType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            AssigmentType persistentAssigmentType = em.find(AssigmentType.class, assigmentType.getId());
            List<UserAssigment> userAssigmentListOld = persistentAssigmentType.getUserAssigmentList();
            List<UserAssigment> userAssigmentListNew = assigmentType.getUserAssigmentList();
            List<String> illegalOrphanMessages = null;
            for (UserAssigment userAssigmentListOldUserAssigment : userAssigmentListOld) {
                if (!userAssigmentListNew.contains(userAssigmentListOldUserAssigment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain UserAssigment " + userAssigmentListOldUserAssigment + " since its assigmentType field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<UserAssigment> attachedUserAssigmentListNew = new ArrayList<>();
            for (UserAssigment userAssigmentListNewUserAssigmentToAttach : userAssigmentListNew) {
                userAssigmentListNewUserAssigmentToAttach = em.getReference(userAssigmentListNewUserAssigmentToAttach.getClass(), userAssigmentListNewUserAssigmentToAttach.getUserAssigmentPK());
                attachedUserAssigmentListNew.add(userAssigmentListNewUserAssigmentToAttach);
            }
            userAssigmentListNew = attachedUserAssigmentListNew;
            assigmentType.setUserAssigmentList(userAssigmentListNew);
            assigmentType = em.merge(assigmentType);
            for (UserAssigment userAssigmentListNewUserAssigment : userAssigmentListNew) {
                if (!userAssigmentListOld.contains(userAssigmentListNewUserAssigment)) {
                    AssigmentType oldAssigmentTypeOfUserAssigmentListNewUserAssigment = userAssigmentListNewUserAssigment.getAssigmentType();
                    userAssigmentListNewUserAssigment.setAssigmentType(assigmentType);
                    userAssigmentListNewUserAssigment = em.merge(userAssigmentListNewUserAssigment);
                    if (oldAssigmentTypeOfUserAssigmentListNewUserAssigment != null && !oldAssigmentTypeOfUserAssigmentListNewUserAssigment.equals(assigmentType)) {
                        oldAssigmentTypeOfUserAssigmentListNewUserAssigment.getUserAssigmentList().remove(userAssigmentListNewUserAssigment);
                        oldAssigmentTypeOfUserAssigmentListNewUserAssigment = em.merge(oldAssigmentTypeOfUserAssigmentListNewUserAssigment);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = assigmentType.getId();
                if (findAssigmentType(id) == null) {
                    throw new NonexistentEntityException("The assigmentType with id " + id + " no longer exists.");
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

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            AssigmentType assigmentType;
            try {
                assigmentType = em.getReference(AssigmentType.class, id);
                assigmentType.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The assigmentType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<UserAssigment> userAssigmentListOrphanCheck = assigmentType.getUserAssigmentList();
            for (UserAssigment userAssigmentListOrphanCheckUserAssigment : userAssigmentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This AssigmentType (" + assigmentType + ") cannot be destroyed since the UserAssigment " + userAssigmentListOrphanCheckUserAssigment + " in its userAssigmentList field has a non-nullable assigmentType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(assigmentType);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<AssigmentType> findAssigmentTypeEntities() {
        return findAssigmentTypeEntities(true, -1, -1);
    }

    public List<AssigmentType> findAssigmentTypeEntities(int maxResults, int firstResult) {
        return findAssigmentTypeEntities(false, maxResults, firstResult);
    }

    private List<AssigmentType> findAssigmentTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(AssigmentType.class));
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

    public AssigmentType findAssigmentType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(AssigmentType.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getAssigmentTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<AssigmentType> rt = cq.from(AssigmentType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
