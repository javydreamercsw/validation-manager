package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.Notification;
import com.validation.manager.core.db.NotificationPK;
import com.validation.manager.core.db.controller.NotificationJpaController;
import java.util.Date;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class NotificationServer extends Notification
        implements EntityServer<Notification> {

    public NotificationServer() {
        super();
    }

    public NotificationServer(Notification n) {
        setNotificationPK(n.getNotificationPK());
        update();
    }

    public NotificationServer(NotificationPK pk) {
        setNotificationPK(pk);
        update();
    }

    @Override
    public int write2DB() throws Exception {
        if (getNotificationPK() == null) {
            Notification n = new Notification();
            if (getCreationDate() == null) {
                setCreationDate(new Date());
            }
            update(n, this);
            new NotificationJpaController(DataBaseManager
                    .getEntityManagerFactory()).create(n);
            setNotificationPK(n.getNotificationPK());
        } else {
            Notification n = getEntity();
            update(n, this);
            new NotificationJpaController(DataBaseManager
                    .getEntityManagerFactory()).edit(n);
        }
        update();
        return getNotificationPK().getId();
    }

    @Override
    public Notification getEntity() {
        return new NotificationJpaController(DataBaseManager
                .getEntityManagerFactory())
                .findNotification(getNotificationPK());
    }

    @Override
    public void update(Notification target, Notification source) {
        target.setAcknowledgeDate(source.getAcknowledgeDate());
        target.setAuthor(source.getAuthor());
        target.setContent(source.getContent());
        target.setCreationDate(source.getCreationDate());
        target.setNotificationPK(source.getNotificationPK());
        target.setNotificationType(source.getNotificationType());
        target.setTargetUser(source.getTargetUser());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
