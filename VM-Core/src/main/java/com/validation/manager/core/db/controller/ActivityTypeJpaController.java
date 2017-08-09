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
import com.validation.manager.core.db.Activity;
import com.validation.manager.core.db.ActivityType;
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
public class ActivityTypeJpaController implements Serializable {

    public ActivityTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ActivityType activityType) {
        if (activityType.getActivityList() == null) {
            activityType.setActivityList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Activity> attachedActivityList = new ArrayList<>();
            for (Activity activityListActivityToAttach : activityType.getActivityList()) {
                activityListActivityToAttach = em.getReference(activityListActivityToAttach.getClass(), activityListActivityToAttach.getActivityPK());
                attachedActivityList.add(activityListActivityToAttach);
            }
            activityType.setActivityList(attachedActivityList);
            em.persist(activityType);
            for (Activity activityListActivity : activityType.getActivityList()) {
                ActivityType oldActivityTypeOfActivityListActivity = activityListActivity.getActivityType();
                activityListActivity.setActivityType(activityType);
                activityListActivity = em.merge(activityListActivity);
                if (oldActivityTypeOfActivityListActivity != null) {
                    oldActivityTypeOfActivityListActivity.getActivityList().remove(activityListActivity);
                    oldActivityTypeOfActivityListActivity = em.merge(oldActivityTypeOfActivityListActivity);
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

    public void edit(ActivityType activityType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ActivityType persistentActivityType = em.find(ActivityType.class, activityType.getId());
            List<Activity> activityListOld = persistentActivityType.getActivityList();
            List<Activity> activityListNew = activityType.getActivityList();
            List<String> illegalOrphanMessages = null;
            for (Activity activityListOldActivity : activityListOld) {
                if (!activityListNew.contains(activityListOldActivity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Activity " + activityListOldActivity + " since its activityType field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Activity> attachedActivityListNew = new ArrayList<>();
            for (Activity activityListNewActivityToAttach : activityListNew) {
                activityListNewActivityToAttach = em.getReference(activityListNewActivityToAttach.getClass(), activityListNewActivityToAttach.getActivityPK());
                attachedActivityListNew.add(activityListNewActivityToAttach);
            }
            activityListNew = attachedActivityListNew;
            activityType.setActivityList(activityListNew);
            activityType = em.merge(activityType);
            for (Activity activityListNewActivity : activityListNew) {
                if (!activityListOld.contains(activityListNewActivity)) {
                    ActivityType oldActivityTypeOfActivityListNewActivity = activityListNewActivity.getActivityType();
                    activityListNewActivity.setActivityType(activityType);
                    activityListNewActivity = em.merge(activityListNewActivity);
                    if (oldActivityTypeOfActivityListNewActivity != null && !oldActivityTypeOfActivityListNewActivity.equals(activityType)) {
                        oldActivityTypeOfActivityListNewActivity.getActivityList().remove(activityListNewActivity);
                        oldActivityTypeOfActivityListNewActivity = em.merge(oldActivityTypeOfActivityListNewActivity);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = activityType.getId();
                if (findActivityType(id) == null) {
                    throw new NonexistentEntityException("The activityType with id " + id + " no longer exists.");
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
            ActivityType activityType;
            try {
                activityType = em.getReference(ActivityType.class, id);
                activityType.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The activityType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Activity> activityListOrphanCheck = activityType.getActivityList();
            for (Activity activityListOrphanCheckActivity : activityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This ActivityType (" + activityType + ") cannot be destroyed since the Activity " + activityListOrphanCheckActivity + " in its activityList field has a non-nullable activityType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(activityType);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ActivityType> findActivityTypeEntities() {
        return findActivityTypeEntities(true, -1, -1);
    }

    public List<ActivityType> findActivityTypeEntities(int maxResults, int firstResult) {
        return findActivityTypeEntities(false, maxResults, firstResult);
    }

    private List<ActivityType> findActivityTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ActivityType.class));
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

    public ActivityType findActivityType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ActivityType.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getActivityTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ActivityType> rt = cq.from(ActivityType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
