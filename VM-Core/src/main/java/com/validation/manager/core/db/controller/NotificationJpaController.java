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

import com.validation.manager.core.db.Notification;
import com.validation.manager.core.db.NotificationPK;
import com.validation.manager.core.db.NotificationType;
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
public class NotificationJpaController implements Serializable {

    public NotificationJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Notification notification) throws PreexistingEntityException, Exception {
        if (notification.getNotificationPK() == null) {
            notification.setNotificationPK(new NotificationPK());
        }
        notification.getNotificationPK().setNotificationTypeId(notification.getNotificationType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            NotificationType notificationType = notification.getNotificationType();
            if (notificationType != null) {
                notificationType = em.getReference(notificationType.getClass(), notificationType.getId());
                notification.setNotificationType(notificationType);
            }
            VmUser targetUser = notification.getTargetUser();
            if (targetUser != null) {
                targetUser = em.getReference(targetUser.getClass(), targetUser.getId());
                notification.setTargetUser(targetUser);
            }
            VmUser author = notification.getAuthor();
            if (author != null) {
                author = em.getReference(author.getClass(), author.getId());
                notification.setAuthor(author);
            }
            em.persist(notification);
            if (notificationType != null) {
                notificationType.getNotificationList().add(notification);
                notificationType = em.merge(notificationType);
            }
            if (targetUser != null) {
                targetUser.getNotificationList().add(notification);
                targetUser = em.merge(targetUser);
            }
            if (author != null) {
                author.getNotificationList1().add(notification);
                author = em.merge(author);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            if (findNotification(notification.getNotificationPK()) != null) {
                throw new PreexistingEntityException("Notification " + notification + " already exists.", ex);
            }
            throw ex;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Notification notification) throws NonexistentEntityException, Exception {
        notification.getNotificationPK().setNotificationTypeId(notification.getNotificationType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Notification persistentNotification = em.find(Notification.class, notification.getNotificationPK());
            NotificationType notificationTypeOld = persistentNotification.getNotificationType();
            NotificationType notificationTypeNew = notification.getNotificationType();
            VmUser targetUserOld = persistentNotification.getTargetUser();
            VmUser targetUserNew = notification.getTargetUser();
            VmUser authorOld = persistentNotification.getAuthor();
            VmUser authorNew = notification.getAuthor();
            if (notificationTypeNew != null) {
                notificationTypeNew = em.getReference(notificationTypeNew.getClass(), notificationTypeNew.getId());
                notification.setNotificationType(notificationTypeNew);
            }
            if (targetUserNew != null) {
                targetUserNew = em.getReference(targetUserNew.getClass(), targetUserNew.getId());
                notification.setTargetUser(targetUserNew);
            }
            if (authorNew != null) {
                authorNew = em.getReference(authorNew.getClass(), authorNew.getId());
                notification.setAuthor(authorNew);
            }
            notification = em.merge(notification);
            if (notificationTypeOld != null && !notificationTypeOld.equals(notificationTypeNew)) {
                notificationTypeOld.getNotificationList().remove(notification);
                notificationTypeOld = em.merge(notificationTypeOld);
            }
            if (notificationTypeNew != null && !notificationTypeNew.equals(notificationTypeOld)) {
                notificationTypeNew.getNotificationList().add(notification);
                notificationTypeNew = em.merge(notificationTypeNew);
            }
            if (targetUserOld != null && !targetUserOld.equals(targetUserNew)) {
                targetUserOld.getNotificationList().remove(notification);
                targetUserOld = em.merge(targetUserOld);
            }
            if (targetUserNew != null && !targetUserNew.equals(targetUserOld)) {
                targetUserNew.getNotificationList().add(notification);
                targetUserNew = em.merge(targetUserNew);
            }
            if (authorOld != null && !authorOld.equals(authorNew)) {
                authorOld.getNotificationList1().remove(notification);
                authorOld = em.merge(authorOld);
            }
            if (authorNew != null && !authorNew.equals(authorOld)) {
                authorNew.getNotificationList1().add(notification);
                authorNew = em.merge(authorNew);
            }
            em.getTransaction().commit();
        }
        catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                NotificationPK id = notification.getNotificationPK();
                if (findNotification(id) == null) {
                    throw new NonexistentEntityException("The notification with id " + id + " no longer exists.");
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

    public void destroy(NotificationPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Notification notification;
            try {
                notification = em.getReference(Notification.class, id);
                notification.getNotificationPK();
            }
            catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The notification with id " + id + " no longer exists.", enfe);
            }
            NotificationType notificationType = notification.getNotificationType();
            if (notificationType != null) {
                notificationType.getNotificationList().remove(notification);
                notificationType = em.merge(notificationType);
            }
            VmUser targetUser = notification.getTargetUser();
            if (targetUser != null) {
                targetUser.getNotificationList().remove(notification);
                targetUser = em.merge(targetUser);
            }
            VmUser author = notification.getAuthor();
            if (author != null) {
                author.getNotificationList().remove(notification);
                author = em.merge(author);
            }
            em.remove(notification);
            em.getTransaction().commit();
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Notification> findNotificationEntities() {
        return findNotificationEntities(true, -1, -1);
    }

    public List<Notification> findNotificationEntities(int maxResults, int firstResult) {
        return findNotificationEntities(false, maxResults, firstResult);
    }

    private List<Notification> findNotificationEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Notification.class));
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

    public Notification findNotification(NotificationPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Notification.class, id);
        }
        finally {
            em.close();
        }
    }

    public int getNotificationCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Notification> rt = cq.from(Notification.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        }
        finally {
            em.close();
        }
    }

}
