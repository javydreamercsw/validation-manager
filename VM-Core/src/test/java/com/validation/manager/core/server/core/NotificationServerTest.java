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
        NotificationServer instance2 = new NotificationServer();
        instance2.setAuthor(author.getEntity());
        instance2.setContent("Hello there!");
        instance2.setCreationDate(new Date());
        instance2.setNotificationType(NotificationTypeServer
                .getType("notification.test.pending"));
        instance2.setTargetUser(target.getEntity());
        instance2.write2DB();
    }
}
