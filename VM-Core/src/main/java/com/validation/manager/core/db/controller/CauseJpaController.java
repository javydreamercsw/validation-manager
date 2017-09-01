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

import com.validation.manager.core.db.Cause;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.validation.manager.core.db.FailureModeHasCause;
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
public class CauseJpaController implements Serializable {

    public CauseJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cause cause) {
        if (cause.getFailureModeHasCauseList() == null) {
            cause.setFailureModeHasCauseList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<FailureModeHasCause> attachedFailureModeHasCauseList = new ArrayList<>();
            for (FailureModeHasCause failureModeHasCauseListFailureModeHasCauseToAttach : cause.getFailureModeHasCauseList()) {
                failureModeHasCauseListFailureModeHasCauseToAttach = em.getReference(failureModeHasCauseListFailureModeHasCauseToAttach.getClass(), failureModeHasCauseListFailureModeHasCauseToAttach.getFailureModeHasCausePK());
                attachedFailureModeHasCauseList.add(failureModeHasCauseListFailureModeHasCauseToAttach);
            }
            cause.setFailureModeHasCauseList(attachedFailureModeHasCauseList);
            em.persist(cause);
            for (FailureModeHasCause failureModeHasCauseListFailureModeHasCause : cause.getFailureModeHasCauseList()) {
                Cause oldCauseOfFailureModeHasCauseListFailureModeHasCause = failureModeHasCauseListFailureModeHasCause.getCause();
                failureModeHasCauseListFailureModeHasCause.setCause(cause);
                failureModeHasCauseListFailureModeHasCause = em.merge(failureModeHasCauseListFailureModeHasCause);
                if (oldCauseOfFailureModeHasCauseListFailureModeHasCause != null) {
                    oldCauseOfFailureModeHasCauseListFailureModeHasCause.getFailureModeHasCauseList().remove(failureModeHasCauseListFailureModeHasCause);
                    oldCauseOfFailureModeHasCauseListFailureModeHasCause = em.merge(oldCauseOfFailureModeHasCauseListFailureModeHasCause);
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

    public void edit(Cause cause) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cause persistentCause = em.find(Cause.class, cause.getId());
            List<FailureModeHasCause> failureModeHasCauseListOld = persistentCause.getFailureModeHasCauseList();
            List<FailureModeHasCause> failureModeHasCauseListNew = cause.getFailureModeHasCauseList();
            List<String> illegalOrphanMessages = null;
            for (FailureModeHasCause failureModeHasCauseListOldFailureModeHasCause : failureModeHasCauseListOld) {
                if (!failureModeHasCauseListNew.contains(failureModeHasCauseListOldFailureModeHasCause)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain FailureModeHasCause " + failureModeHasCauseListOldFailureModeHasCause + " since its cause field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<FailureModeHasCause> attachedFailureModeHasCauseListNew = new ArrayList<>();
            for (FailureModeHasCause failureModeHasCauseListNewFailureModeHasCauseToAttach : failureModeHasCauseListNew) {
                failureModeHasCauseListNewFailureModeHasCauseToAttach = em.getReference(failureModeHasCauseListNewFailureModeHasCauseToAttach.getClass(), failureModeHasCauseListNewFailureModeHasCauseToAttach.getFailureModeHasCausePK());
                attachedFailureModeHasCauseListNew.add(failureModeHasCauseListNewFailureModeHasCauseToAttach);
            }
            failureModeHasCauseListNew = attachedFailureModeHasCauseListNew;
            cause.setFailureModeHasCauseList(failureModeHasCauseListNew);
            cause = em.merge(cause);
            for (FailureModeHasCause failureModeHasCauseListNewFailureModeHasCause : failureModeHasCauseListNew) {
                if (!failureModeHasCauseListOld.contains(failureModeHasCauseListNewFailureModeHasCause)) {
                    Cause oldCauseOfFailureModeHasCauseListNewFailureModeHasCause = failureModeHasCauseListNewFailureModeHasCause.getCause();
                    failureModeHasCauseListNewFailureModeHasCause.setCause(cause);
                    failureModeHasCauseListNewFailureModeHasCause = em.merge(failureModeHasCauseListNewFailureModeHasCause);
                    if (oldCauseOfFailureModeHasCauseListNewFailureModeHasCause != null && !oldCauseOfFailureModeHasCauseListNewFailureModeHasCause.equals(cause)) {
                        oldCauseOfFailureModeHasCauseListNewFailureModeHasCause.getFailureModeHasCauseList().remove(failureModeHasCauseListNewFailureModeHasCause);
                        oldCauseOfFailureModeHasCauseListNewFailureModeHasCause = em.merge(oldCauseOfFailureModeHasCauseListNewFailureModeHasCause);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cause.getId();
                if (findCause(id) == null) {
                    throw new NonexistentEntityException("The cause with id " + id + " no longer exists.");
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
            Cause cause;
            try {
                cause = em.getReference(Cause.class, id);
                cause.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cause with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<FailureModeHasCause> failureModeHasCauseListOrphanCheck = cause.getFailureModeHasCauseList();
            for (FailureModeHasCause failureModeHasCauseListOrphanCheckFailureModeHasCause : failureModeHasCauseListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This Cause (" + cause + ") cannot be destroyed since the FailureModeHasCause " + failureModeHasCauseListOrphanCheckFailureModeHasCause + " in its failureModeHasCauseList field has a non-nullable cause field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(cause);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cause> findCauseEntities() {
        return findCauseEntities(true, -1, -1);
    }

    public List<Cause> findCauseEntities(int maxResults, int firstResult) {
        return findCauseEntities(false, maxResults, firstResult);
    }

    private List<Cause> findCauseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cause.class));
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

    public Cause findCause(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cause.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getCauseCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cause> rt = cq.from(Cause.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
