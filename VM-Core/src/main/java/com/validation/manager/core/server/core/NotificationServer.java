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
package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Notification;
import com.validation.manager.core.db.NotificationPK;
import com.validation.manager.core.db.controller.NotificationJpaController;
import java.util.Date;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class NotificationServer extends Notification
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
    public int write2DB() throws VMException {
        try {
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
        }
        catch (Exception ex) {
            throw new VMException(ex);
        }
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
        target.setArchieved(source.getArchieved());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
