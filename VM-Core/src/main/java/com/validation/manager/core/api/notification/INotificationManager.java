package com.validation.manager.core.api.notification;

import com.validation.manager.core.db.VmUser;

/**
 * This handles the notification from the system.
 *
 * @author Javier Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public interface INotificationManager {

    void addNotification(String message, NotificationTypes type,
            VmUser target, VmUser author) throws Exception;
}
