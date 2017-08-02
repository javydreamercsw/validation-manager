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

import com.validation.manager.core.db.Activity;
import com.validation.manager.core.db.ActivityPK;
import com.validation.manager.core.db.ActivityType;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ActivityJpaController implements Serializable {

    public ActivityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Activity activity) throws PreexistingEntityException, Exception {
        if (activity.getActivityPK() == null) {
            activity.setActivityPK(new ActivityPK());
        }
        activity.getActivityPK().setActivityType(activity.getActivityType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ActivityType activityType1 = activity.getActivityType();
            if (activityType1 != null) {
                activityType1 = em.getReference(activityType1.getClass(), activityType1.getId());
                activity.setActivityType(activityType1);
            }
            VmUser sourceUser = activity.getSourceUser();
            if (sourceUser != null) {
                sourceUser = em.getReference(sourceUser.getClass(), sourceUser.getId());
                activity.setSourceUser(sourceUser);
            }
            em.persist(activity);
            if (activityType1 != null) {
                activityType1.getActivityList().add(activity);
                activityType1 = em.merge(activityType1);
            }
            if (sourceUser != null) {
                sourceUser.getActivityList().add(activity);
                sourceUser = em.merge(sourceUser);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findActivity(activity.getActivityPK()) != null) {
                throw new PreexistingEntityException("Activity "
                        + activity + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Activity activity) throws NonexistentEntityException, Exception {
        activity.getActivityPK().setActivityType(activity.getActivityType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Activity persistentActivity = em.find(Activity.class, activity.getActivityPK());
            ActivityType activityType1Old = persistentActivity.getActivityType();
            ActivityType activityType1New = activity.getActivityType();
            VmUser sourceUserOld = persistentActivity.getSourceUser();
            VmUser sourceUserNew = activity.getSourceUser();
            if (activityType1New != null) {
                activityType1New = em.getReference(activityType1New.getClass(),
                        activityType1New.getId());
                activity.setActivityType(activityType1New);
            }
            if (sourceUserNew != null) {
                sourceUserNew = em.getReference(sourceUserNew.getClass(), sourceUserNew.getId());
                activity.setSourceUser(sourceUserNew);
            }
            activity = em.merge(activity);
            if (activityType1Old != null && !activityType1Old.equals(activityType1New)) {
                activityType1Old.getActivityList().remove(activity);
                activityType1Old = em.merge(activityType1Old);
            }
            if (activityType1New != null && !activityType1New.equals(activityType1Old)) {
                activityType1New.getActivityList().add(activity);
                activityType1New = em.merge(activityType1New);
            }
            if (sourceUserOld != null && !sourceUserOld.equals(sourceUserNew)) {
                sourceUserOld.getActivityList().remove(activity);
                sourceUserOld = em.merge(sourceUserOld);
            }
            if (sourceUserNew != null && !sourceUserNew.equals(sourceUserOld)) {
                sourceUserNew.getActivityList().add(activity);
                sourceUserNew = em.merge(sourceUserNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ActivityPK id = activity.getActivityPK();
                if (findActivity(id) == null) {
                    throw new NonexistentEntityException("The activity with id " + id + " no longer exists.");
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

    public void destroy(ActivityPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Activity activity;
            try {
                activity = em.getReference(Activity.class, id);
                activity.getActivityPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The activity with id " + id + " no longer exists.", enfe);
            }
            ActivityType activityType1 = activity.getActivityType();
            if (activityType1 != null) {
                activityType1.getActivityList().remove(activity);
                activityType1 = em.merge(activityType1);
            }
            VmUser sourceUser = activity.getSourceUser();
            if (sourceUser != null) {
                sourceUser.getActivityList().remove(activity);
                sourceUser = em.merge(sourceUser);
            }
            em.remove(activity);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Activity> findActivityEntities() {
        return findActivityEntities(true, -1, -1);
    }

    public List<Activity> findActivityEntities(int maxResults, int firstResult) {
        return findActivityEntities(false, maxResults, firstResult);
    }

    private List<Activity> findActivityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Activity.class));
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

    public Activity findActivity(ActivityPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Activity.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getActivityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Activity> rt = cq.from(Activity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
