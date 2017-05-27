package net.sourceforge.javydreamercsw.validation.manager.web.notification;

import com.validation.manager.core.api.notification.INotificationManager;
import com.validation.manager.core.api.notification.NotificationTypes;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.server.core.NotificationServer;
import com.validation.manager.core.server.core.NotificationTypeServer;
import java.util.Date;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = INotificationManager.class)
public class NotificationManager implements INotificationManager {

    @Override
    public void addNotification(String message, NotificationTypes type,
            VmUser target, VmUser author) throws Exception {
        NotificationServer ns = new NotificationServer();
        ns.setAuthor(author);
        ns.setTargetUser(target);
        ns.setContent(message);
        ns.setCreationDate(new Date());
        String selected;
        switch (type) {
            case TEST:
                selected = "notification.test.pending";
                break;
            case REVIEW:
                selected = "notification.review.pending";
                break;
            default:
                selected = "general.notification";
        }
        ns.setNotificationType(NotificationTypeServer.getType(selected));
        ns.write2DB();
    }
}
