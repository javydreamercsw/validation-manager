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
import com.validation.manager.core.db.Notification;
import com.validation.manager.core.db.NotificationType;
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
public class NotificationTypeJpaController implements Serializable {

    public NotificationTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(NotificationType notificationType) {
        if (notificationType.getNotificationList() == null) {
            notificationType.setNotificationList(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Notification> attachedNotificationList = new ArrayList<>();
            for (Notification notificationListNotificationToAttach : notificationType.getNotificationList()) {
                notificationListNotificationToAttach = em.getReference(notificationListNotificationToAttach.getClass(), notificationListNotificationToAttach.getNotificationPK());
                attachedNotificationList.add(notificationListNotificationToAttach);
            }
            notificationType.setNotificationList(attachedNotificationList);
            em.persist(notificationType);
            for (Notification notificationListNotification : notificationType.getNotificationList()) {
                NotificationType oldNotificationTypeOfNotificationListNotification = notificationListNotification.getNotificationType();
                notificationListNotification.setNotificationType(notificationType);
                notificationListNotification = em.merge(notificationListNotification);
                if (oldNotificationTypeOfNotificationListNotification != null) {
                    oldNotificationTypeOfNotificationListNotification.getNotificationList().remove(notificationListNotification);
                    oldNotificationTypeOfNotificationListNotification = em.merge(oldNotificationTypeOfNotificationListNotification);
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

    public void edit(NotificationType notificationType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            NotificationType persistentNotificationType = em.find(NotificationType.class, notificationType.getId());
            List<Notification> notificationListOld = persistentNotificationType.getNotificationList();
            List<Notification> notificationListNew = notificationType.getNotificationList();
            List<String> illegalOrphanMessages = null;
            for (Notification notificationListOldNotification : notificationListOld) {
                if (!notificationListNew.contains(notificationListOldNotification)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<>();
                    }
                    illegalOrphanMessages.add("You must retain Notification " + notificationListOldNotification + " since its notificationType field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Notification> attachedNotificationListNew = new ArrayList<>();
            for (Notification notificationListNewNotificationToAttach : notificationListNew) {
                notificationListNewNotificationToAttach = em.getReference(notificationListNewNotificationToAttach.getClass(), notificationListNewNotificationToAttach.getNotificationPK());
                attachedNotificationListNew.add(notificationListNewNotificationToAttach);
            }
            notificationListNew = attachedNotificationListNew;
            notificationType.setNotificationList(notificationListNew);
            notificationType = em.merge(notificationType);
            for (Notification notificationListNewNotification : notificationListNew) {
                if (!notificationListOld.contains(notificationListNewNotification)) {
                    NotificationType oldNotificationTypeOfNotificationListNewNotification = notificationListNewNotification.getNotificationType();
                    notificationListNewNotification.setNotificationType(notificationType);
                    notificationListNewNotification = em.merge(notificationListNewNotification);
                    if (oldNotificationTypeOfNotificationListNewNotification != null && !oldNotificationTypeOfNotificationListNewNotification.equals(notificationType)) {
                        oldNotificationTypeOfNotificationListNewNotification.getNotificationList().remove(notificationListNewNotification);
                        oldNotificationTypeOfNotificationListNewNotification = em.merge(oldNotificationTypeOfNotificationListNewNotification);
                    }
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = notificationType.getId();
                if (findNotificationType(id) == null) {
                    throw new NonexistentEntityException("The notificationType with id " + id + " no longer exists.");
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
            NotificationType notificationType;
            try {
                notificationType = em.getReference(NotificationType.class, id);
                notificationType.getId();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The notificationType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Notification> notificationListOrphanCheck = notificationType.getNotificationList();
            for (Notification notificationListOrphanCheckNotification : notificationListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This NotificationType (" + notificationType + ") cannot be destroyed since the Notification " + notificationListOrphanCheckNotification + " in its notificationList field has a non-nullable notificationType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(notificationType);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<NotificationType> findNotificationTypeEntities() {
        return findNotificationTypeEntities(true, -1, -1);
    }

    public List<NotificationType> findNotificationTypeEntities(int maxResults, int firstResult) {
        return findNotificationTypeEntities(false, maxResults, firstResult);
    }

    private List<NotificationType> findNotificationTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(NotificationType.class));
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

    public NotificationType findNotificationType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(NotificationType.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getNotificationTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<NotificationType> rt = cq.from(NotificationType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
