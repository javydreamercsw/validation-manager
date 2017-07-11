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
import com.validation.manager.core.db.RootCause;
import com.validation.manager.core.db.RootCauseType;
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
public class RootCauseTypeJpaController implements Serializable {

    public RootCauseTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RootCauseType rootCauseType) {
        if (rootCauseType.getRootCauseList() == null) {
            rootCauseType.setRootCauseList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RootCause> attachedRootCauseList = new ArrayList<>();
            for (RootCause rootCauseListRootCauseToAttach : rootCauseType.getRootCauseList()) {
                rootCauseListRootCauseToAttach = em.getReference(rootCauseListRootCauseToAttach.getClass(), rootCauseListRootCauseToAttach.getRootCausePK());
                attachedRootCauseList.add(rootCauseListRootCauseToAttach);
            }
            rootCauseType.setRootCauseList(attachedRootCauseList);
            em.persist(rootCauseType);
            for (RootCause rootCauseListRootCause : rootCauseType.getRootCauseList()) {
                RootCauseType oldRootCauseTypeOfRootCauseListRootCause = rootCauseListRootCause.getRootCauseType();
                rootCauseListRootCause.setRootCauseType(rootCauseType);
                rootCauseListRootCause = em.merge(rootCauseListRootCause);
                if (oldRootCauseTypeOfRootCauseListRootCause != null) {
                    oldRootCauseTypeOfRootCauseListRootCause.getRootCauseList().remove(rootCauseListRootCause);
                    oldRootCauseTypeOfRootCauseListRootCause = em.merge(oldRootCauseTypeOfRootCauseListRootCause);
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

    public void edit(RootCauseType rootCauseType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RootCauseType persistentRootCauseType = em.find(RootCauseType.class, rootCauseType.getId());
            List<RootCause> rootCauseListOld = persistentRootCauseType.getRootCauseList();
            List<RootCause> rootCauseListNew = rootCauseType.getRootCauseList();
            List<String> illegalOrphanMessages = null;
            for (RootCause rootCauseListOldRootCause : rootCauseListOld) {
                if (!rootCauseListNew.contains(rootCauseListOldRootCause)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain RootCause " + rootCauseListOldRootCause + " since its rootCauseType field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<RootCause> attachedRootCauseListNew = new ArrayList<>();
            for (RootCause rootCauseListNewRootCauseToAttach : rootCauseListNew) {
                rootCauseListNewRootCauseToAttach = em.getReference(rootCauseListNewRootCauseToAttach.getClass(), rootCauseListNewRootCauseToAttach.getRootCausePK());
                attachedRootCauseListNew.add(rootCauseListNewRootCauseToAttach);
            }
            rootCauseListNew = attachedRootCauseListNew;
            rootCauseType.setRootCauseList(rootCauseListNew);
            rootCauseType = em.merge(rootCauseType);
            for (RootCause rootCauseListNewRootCause : rootCauseListNew) {
                if (!rootCauseListOld.contains(rootCauseListNewRootCause)) {
                    RootCauseType oldRootCauseTypeOfRootCauseListNewRootCause = rootCauseListNewRootCause.getRootCauseType();
                    rootCauseListNewRootCause.setRootCauseType(rootCauseType);
                    rootCauseListNewRootCause = em.merge(rootCauseListNewRootCause);
                    if (oldRootCauseTypeOfRootCauseListNewRootCause != null && !oldRootCauseTypeOfRootCauseListNewRootCause.equals(rootCauseType)) {
                        oldRootCauseTypeOfRootCauseListNewRootCause.getRootCauseList().remove(rootCauseListNewRootCause);
                        oldRootCauseTypeOfRootCauseListNewRootCause = em.merge(oldRootCauseTypeOfRootCauseListNewRootCause);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = rootCauseType.getId();
                if (findRootCauseType(id) == null) {
                    throw new NonexistentEntityException("The rootCauseType with id " + id + " no longer exists.");
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
            RootCauseType rootCauseType;
            try {
                rootCauseType = em.getReference(RootCauseType.class, id);
                rootCauseType.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rootCauseType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RootCause> rootCauseListOrphanCheck = rootCauseType.getRootCauseList();
            for (RootCause rootCauseListOrphanCheckRootCause : rootCauseListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This RootCauseType (" + rootCauseType + ") cannot be destroyed since the RootCause " + rootCauseListOrphanCheckRootCause + " in its rootCauseList field has a non-nullable rootCauseType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(rootCauseType);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RootCauseType> findRootCauseTypeEntities() {
        return findRootCauseTypeEntities(true, -1, -1);
    }

    public List<RootCauseType> findRootCauseTypeEntities(int maxResults, int firstResult) {
        return findRootCauseTypeEntities(false, maxResults, firstResult);
    }

    private List<RootCauseType> findRootCauseTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RootCauseType.class));
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

    public RootCauseType findRootCauseType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RootCauseType.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getRootCauseTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RootCauseType> rt = cq.from(RootCauseType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
