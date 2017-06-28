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
import com.validation.manager.core.db.UserHasInvestigation;
import java.util.ArrayList;
import java.util.List;
import com.validation.manager.core.db.ExceptionHasInvestigation;
import com.validation.manager.core.db.Investigation;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class InvestigationJpaController implements Serializable {

    public InvestigationJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Investigation investigation) {
        if (investigation.getUserHasInvestigationList() == null) {
            investigation.setUserHasInvestigationList(new ArrayList<>());
        }
        if (investigation.getExceptionHasInvestigationList() == null) {
            investigation.setExceptionHasInvestigationList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<UserHasInvestigation> attachedUserHasInvestigationList = new ArrayList<>();
            for (UserHasInvestigation userHasInvestigationListUserHasInvestigationToAttach : investigation.getUserHasInvestigationList()) {
                userHasInvestigationListUserHasInvestigationToAttach = em.getReference(userHasInvestigationListUserHasInvestigationToAttach.getClass(), userHasInvestigationListUserHasInvestigationToAttach.getUserHasInvestigationPK());
                attachedUserHasInvestigationList.add(userHasInvestigationListUserHasInvestigationToAttach);
            }
            investigation.setUserHasInvestigationList(attachedUserHasInvestigationList);
            List<ExceptionHasInvestigation> attachedExceptionHasInvestigationList = new ArrayList<>();
            for (ExceptionHasInvestigation exceptionHasInvestigationListExceptionHasInvestigationToAttach : investigation.getExceptionHasInvestigationList()) {
                exceptionHasInvestigationListExceptionHasInvestigationToAttach = em.getReference(exceptionHasInvestigationListExceptionHasInvestigationToAttach.getClass(), exceptionHasInvestigationListExceptionHasInvestigationToAttach.getExceptionHasInvestigationPK());
                attachedExceptionHasInvestigationList.add(exceptionHasInvestigationListExceptionHasInvestigationToAttach);
            }
            investigation.setExceptionHasInvestigationList(attachedExceptionHasInvestigationList);
            em.persist(investigation);
            for (UserHasInvestigation userHasInvestigationListUserHasInvestigation : investigation.getUserHasInvestigationList()) {
                Investigation oldInvestigationOfUserHasInvestigationListUserHasInvestigation = userHasInvestigationListUserHasInvestigation.getInvestigation();
                userHasInvestigationListUserHasInvestigation.setInvestigation(investigation);
                userHasInvestigationListUserHasInvestigation = em.merge(userHasInvestigationListUserHasInvestigation);
                if (oldInvestigationOfUserHasInvestigationListUserHasInvestigation != null) {
                    oldInvestigationOfUserHasInvestigationListUserHasInvestigation.getUserHasInvestigationList().remove(userHasInvestigationListUserHasInvestigation);
                    oldInvestigationOfUserHasInvestigationListUserHasInvestigation = em.merge(oldInvestigationOfUserHasInvestigationListUserHasInvestigation);
                }
            }
            for (ExceptionHasInvestigation exceptionHasInvestigationListExceptionHasInvestigation : investigation.getExceptionHasInvestigationList()) {
                Investigation oldInvestigationOfExceptionHasInvestigationListExceptionHasInvestigation = exceptionHasInvestigationListExceptionHasInvestigation.getInvestigation();
                exceptionHasInvestigationListExceptionHasInvestigation.setInvestigation(investigation);
                exceptionHasInvestigationListExceptionHasInvestigation = em.merge(exceptionHasInvestigationListExceptionHasInvestigation);
                if (oldInvestigationOfExceptionHasInvestigationListExceptionHasInvestigation != null) {
                    oldInvestigationOfExceptionHasInvestigationListExceptionHasInvestigation.getExceptionHasInvestigationList().remove(exceptionHasInvestigationListExceptionHasInvestigation);
                    oldInvestigationOfExceptionHasInvestigationListExceptionHasInvestigation = em.merge(oldInvestigationOfExceptionHasInvestigationListExceptionHasInvestigation);
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

    public void edit(Investigation investigation) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Investigation persistentInvestigation = em.find(Investigation.class, investigation.getId());
            List<UserHasInvestigation> userHasInvestigationListOld = persistentInvestigation.getUserHasInvestigationList();
            List<UserHasInvestigation> userHasInvestigationListNew = investigation.getUserHasInvestigationList();
            List<ExceptionHasInvestigation> exceptionHasInvestigationListOld = persistentInvestigation.getExceptionHasInvestigationList();
            List<ExceptionHasInvestigation> exceptionHasInvestigationListNew = investigation.getExceptionHasInvestigationList();
            List<String> illegalOrphanMessages = null;
            for (UserHasInvestigation userHasInvestigationListOldUserHasInvestigation : userHasInvestigationListOld) {
                if (!userHasInvestigationListNew.contains(userHasInvestigationListOldUserHasInvestigation)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain UserHasInvestigation " + userHasInvestigationListOldUserHasInvestigation + " since its investigation field is not nullable.");
                }
            }
            for (ExceptionHasInvestigation exceptionHasInvestigationListOldExceptionHasInvestigation : exceptionHasInvestigationListOld) {
                if (!exceptionHasInvestigationListNew.contains(exceptionHasInvestigationListOldExceptionHasInvestigation)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain ExceptionHasInvestigation " + exceptionHasInvestigationListOldExceptionHasInvestigation + " since its investigation field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<UserHasInvestigation> attachedUserHasInvestigationListNew = new ArrayList<>();
            for (UserHasInvestigation userHasInvestigationListNewUserHasInvestigationToAttach : userHasInvestigationListNew) {
                userHasInvestigationListNewUserHasInvestigationToAttach = em.getReference(userHasInvestigationListNewUserHasInvestigationToAttach.getClass(), userHasInvestigationListNewUserHasInvestigationToAttach.getUserHasInvestigationPK());
                attachedUserHasInvestigationListNew.add(userHasInvestigationListNewUserHasInvestigationToAttach);
            }
            userHasInvestigationListNew = attachedUserHasInvestigationListNew;
            investigation.setUserHasInvestigationList(userHasInvestigationListNew);
            List<ExceptionHasInvestigation> attachedExceptionHasInvestigationListNew = new ArrayList<>();
            for (ExceptionHasInvestigation exceptionHasInvestigationListNewExceptionHasInvestigationToAttach : exceptionHasInvestigationListNew) {
                exceptionHasInvestigationListNewExceptionHasInvestigationToAttach = em.getReference(exceptionHasInvestigationListNewExceptionHasInvestigationToAttach.getClass(), exceptionHasInvestigationListNewExceptionHasInvestigationToAttach.getExceptionHasInvestigationPK());
                attachedExceptionHasInvestigationListNew.add(exceptionHasInvestigationListNewExceptionHasInvestigationToAttach);
            }
            exceptionHasInvestigationListNew = attachedExceptionHasInvestigationListNew;
            investigation.setExceptionHasInvestigationList(exceptionHasInvestigationListNew);
            investigation = em.merge(investigation);
            for (UserHasInvestigation userHasInvestigationListNewUserHasInvestigation : userHasInvestigationListNew) {
                if (!userHasInvestigationListOld.contains(userHasInvestigationListNewUserHasInvestigation)) {
                    Investigation oldInvestigationOfUserHasInvestigationListNewUserHasInvestigation = userHasInvestigationListNewUserHasInvestigation.getInvestigation();
                    userHasInvestigationListNewUserHasInvestigation.setInvestigation(investigation);
                    userHasInvestigationListNewUserHasInvestigation = em.merge(userHasInvestigationListNewUserHasInvestigation);
                    if (oldInvestigationOfUserHasInvestigationListNewUserHasInvestigation != null && !oldInvestigationOfUserHasInvestigationListNewUserHasInvestigation.equals(investigation)) {
                        oldInvestigationOfUserHasInvestigationListNewUserHasInvestigation.getUserHasInvestigationList().remove(userHasInvestigationListNewUserHasInvestigation);
                        oldInvestigationOfUserHasInvestigationListNewUserHasInvestigation = em.merge(oldInvestigationOfUserHasInvestigationListNewUserHasInvestigation);
                    }
                }
            }
            for (ExceptionHasInvestigation exceptionHasInvestigationListNewExceptionHasInvestigation : exceptionHasInvestigationListNew) {
                if (!exceptionHasInvestigationListOld.contains(exceptionHasInvestigationListNewExceptionHasInvestigation)) {
                    Investigation oldInvestigationOfExceptionHasInvestigationListNewExceptionHasInvestigation = exceptionHasInvestigationListNewExceptionHasInvestigation.getInvestigation();
                    exceptionHasInvestigationListNewExceptionHasInvestigation.setInvestigation(investigation);
                    exceptionHasInvestigationListNewExceptionHasInvestigation = em.merge(exceptionHasInvestigationListNewExceptionHasInvestigation);
                    if (oldInvestigationOfExceptionHasInvestigationListNewExceptionHasInvestigation != null && !oldInvestigationOfExceptionHasInvestigationListNewExceptionHasInvestigation.equals(investigation)) {
                        oldInvestigationOfExceptionHasInvestigationListNewExceptionHasInvestigation.getExceptionHasInvestigationList().remove(exceptionHasInvestigationListNewExceptionHasInvestigation);
                        oldInvestigationOfExceptionHasInvestigationListNewExceptionHasInvestigation = em.merge(oldInvestigationOfExceptionHasInvestigationListNewExceptionHasInvestigation);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = investigation.getId();
                if (findInvestigation(id) == null) {
                    throw new NonexistentEntityException("The investigation with id " + id + " no longer exists.");
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
            Investigation investigation;
            try {
                investigation = em.getReference(Investigation.class, id);
                investigation.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The investigation with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<UserHasInvestigation> userHasInvestigationListOrphanCheck = investigation.getUserHasInvestigationList();
            for (UserHasInvestigation userHasInvestigationListOrphanCheckUserHasInvestigation : userHasInvestigationListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Investigation (" + investigation + ") cannot be destroyed since the UserHasInvestigation " + userHasInvestigationListOrphanCheckUserHasInvestigation + " in its userHasInvestigationList field has a non-nullable investigation field.");
            }
            List<ExceptionHasInvestigation> exceptionHasInvestigationListOrphanCheck = investigation.getExceptionHasInvestigationList();
            for (ExceptionHasInvestigation exceptionHasInvestigationListOrphanCheckExceptionHasInvestigation : exceptionHasInvestigationListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Investigation (" + investigation + ") cannot be destroyed since the ExceptionHasInvestigation " + exceptionHasInvestigationListOrphanCheckExceptionHasInvestigation + " in its exceptionHasInvestigationList field has a non-nullable investigation field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(investigation);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Investigation> findInvestigationEntities() {
        return findInvestigationEntities(true, -1, -1);
    }

    public List<Investigation> findInvestigationEntities(int maxResults, int firstResult) {
        return findInvestigationEntities(false, maxResults, firstResult);
    }

    private List<Investigation> findInvestigationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Investigation.class));
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

    public Investigation findInvestigation(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Investigation.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getInvestigationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Investigation> rt = cq.from(Investigation.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
