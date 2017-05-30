package com.validation.manager.core.server.core;

import com.validation.manager.test.AbstractVMTestCase;
import java.util.Date;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class NotificationServerTest extends AbstractVMTestCase {

    /**
     * Test of write2DB method, of class NotificationServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWrite2DB() throws Exception {
        System.out.println("write2DB");
        VMUserServer author = new VMUserServer(1);
        VMUserServer target = new VMUserServer(2);
        NotificationServer instance = new NotificationServer();
        instance.setAuthor(author.getEntity());
        instance.setContent("Hello there!");
        instance.setCreationDate(new Date());
        instance.setNotificationType(NotificationTypeServer
                .getType("notification.test.pending"));
        instance.setTargetUser(target.getEntity());
        assertEquals(0, author.getNotificationList().size());
        assertEquals(0, target.getNotificationList().size());
        assertEquals(0, author.getNotificationList1().size());
        assertEquals(0, target.getNotificationList1().size());
        instance.write2DB();
        author.update();
        target.update();
        assertEquals(0, author.getNotificationList().size());
        assertEquals(1, target.getNotificationList().size());
        assertEquals(1, author.getNotificationList1().size());
        assertEquals(0, target.getNotificationList1().size());
        assertEquals(0, author.getPendingNotifications().size());
        assertEquals(1, target.getPendingNotifications().size());
        NotificationServer ns = new NotificationServer(target
                .getNotificationList().get(0));
        ns.setAcknowledgeDate(new Date());
        ns.write2DB();
        assertEquals(0, target.getPendingNotifications().size());
    }
}
