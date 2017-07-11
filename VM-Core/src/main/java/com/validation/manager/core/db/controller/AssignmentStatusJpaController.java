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

import com.validation.manager.core.db.AssignmentStatus;
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
public class AssignmentStatusJpaController implements Serializable {

    public AssignmentStatusJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(AssignmentStatus assignmentStatus) {
        if (assignmentStatus.getUserAssigmentList() == null) {
            assignmentStatus.setUserAssigmentList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<UserAssigment> attachedUserAssigmentList = new ArrayList<>();
            for (UserAssigment userAssigmentListUserAssigmentToAttach : assignmentStatus.getUserAssigmentList()) {
                userAssigmentListUserAssigmentToAttach = em.getReference(userAssigmentListUserAssigmentToAttach.getClass(), userAssigmentListUserAssigmentToAttach.getUserAssigmentPK());
                attachedUserAssigmentList.add(userAssigmentListUserAssigmentToAttach);
            }
            assignmentStatus.setUserAssigmentList(attachedUserAssigmentList);
            em.persist(assignmentStatus);
            for (UserAssigment userAssigmentListUserAssigment : assignmentStatus.getUserAssigmentList()) {
                AssignmentStatus oldAssignmentStatusOfUserAssigmentListUserAssigment = userAssigmentListUserAssigment.getAssignmentStatus();
                userAssigmentListUserAssigment.setAssignmentStatus(assignmentStatus);
                userAssigmentListUserAssigment = em.merge(userAssigmentListUserAssigment);
                if (oldAssignmentStatusOfUserAssigmentListUserAssigment != null) {
                    oldAssignmentStatusOfUserAssigmentListUserAssigment.getUserAssigmentList().remove(userAssigmentListUserAssigment);
                    oldAssignmentStatusOfUserAssigmentListUserAssigment = em.merge(oldAssignmentStatusOfUserAssigmentListUserAssigment);
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

    public void edit(AssignmentStatus assignmentStatus) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            AssignmentStatus persistentAssignmentStatus = em.find(AssignmentStatus.class, assignmentStatus.getId());
            List<UserAssigment> userAssigmentListOld = persistentAssignmentStatus.getUserAssigmentList();
            List<UserAssigment> userAssigmentListNew = assignmentStatus.getUserAssigmentList();
            List<String> illegalOrphanMessages = null;
            for (UserAssigment userAssigmentListOldUserAssigment : userAssigmentListOld) {
                if (!userAssigmentListNew.contains(userAssigmentListOldUserAssigment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain UserAssigment " + userAssigmentListOldUserAssigment + " since its assignmentStatus field is not nullable.");
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
            assignmentStatus.setUserAssigmentList(userAssigmentListNew);
            assignmentStatus = em.merge(assignmentStatus);
            for (UserAssigment userAssigmentListNewUserAssigment : userAssigmentListNew) {
                if (!userAssigmentListOld.contains(userAssigmentListNewUserAssigment)) {
                    AssignmentStatus oldAssignmentStatusOfUserAssigmentListNewUserAssigment = userAssigmentListNewUserAssigment.getAssignmentStatus();
                    userAssigmentListNewUserAssigment.setAssignmentStatus(assignmentStatus);
                    userAssigmentListNewUserAssigment = em.merge(userAssigmentListNewUserAssigment);
                    if (oldAssignmentStatusOfUserAssigmentListNewUserAssigment != null && !oldAssignmentStatusOfUserAssigmentListNewUserAssigment.equals(assignmentStatus)) {
                        oldAssignmentStatusOfUserAssigmentListNewUserAssigment.getUserAssigmentList().remove(userAssigmentListNewUserAssigment);
                        oldAssignmentStatusOfUserAssigmentListNewUserAssigment = em.merge(oldAssignmentStatusOfUserAssigmentListNewUserAssigment);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = assignmentStatus.getId();
                if (findAssignmentStatus(id) == null) {
                    throw new NonexistentEntityException("The assignmentStatus with id " + id + " no longer exists.");
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
            AssignmentStatus assignmentStatus;
            try {
                assignmentStatus = em.getReference(AssignmentStatus.class, id);
                assignmentStatus.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The assignmentStatus with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<UserAssigment> userAssigmentListOrphanCheck = assignmentStatus.getUserAssigmentList();
            for (UserAssigment userAssigmentListOrphanCheckUserAssigment : userAssigmentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This AssignmentStatus (" + assignmentStatus + ") cannot be destroyed since the UserAssigment " + userAssigmentListOrphanCheckUserAssigment + " in its userAssigmentList field has a non-nullable assignmentStatus field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(assignmentStatus);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<AssignmentStatus> findAssignmentStatusEntities() {
        return findAssignmentStatusEntities(true, -1, -1);
    }

    public List<AssignmentStatus> findAssignmentStatusEntities(int maxResults, int firstResult) {
        return findAssignmentStatusEntities(false, maxResults, firstResult);
    }

    private List<AssignmentStatus> findAssignmentStatusEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(AssignmentStatus.class));
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

    public AssignmentStatus findAssignmentStatus(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(AssignmentStatus.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getAssignmentStatusCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<AssignmentStatus> rt = cq.from(AssignmentStatus.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
