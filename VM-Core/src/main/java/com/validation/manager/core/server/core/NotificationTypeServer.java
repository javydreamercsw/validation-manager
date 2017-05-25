package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.NotificationType;
import com.validation.manager.core.db.controller.NotificationTypeJpaController;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class NotificationTypeServer extends NotificationType
        implements EntityServer<NotificationType> {

    public static NotificationType getType(String type) {
        parameters.clear();
        parameters.put("typeName", type);
        List<Object> result = DataBaseManager
                .namedQuery("NotificationType.findByTypeName",
                        parameters);
        if (result.isEmpty()) {
            return null;
        }
        return (NotificationType) result.get(0);
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() == null) {
            NotificationType nt = new NotificationType();
            update(nt, this);
            new NotificationTypeJpaController(DataBaseManager
                    .getEntityManagerFactory()).create(nt);
            setId(nt.getId());
        } else {
            NotificationType nt = getEntity();
            update(nt, this);
            new NotificationTypeJpaController(DataBaseManager
                    .getEntityManagerFactory()).edit(nt);
        }
        update();
        return getId();
    }

    @Override
    public NotificationType getEntity() {
        return new NotificationTypeJpaController(DataBaseManager
                .getEntityManagerFactory()).findNotificationType(getId());
    }

    @Override
    public void update(NotificationType target, NotificationType source) {
        target.setId(source.getId());
        target.setNotificationList(source.getNotificationList());
        target.setTypeName(source.getTypeName());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
