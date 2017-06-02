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

import com.validation.manager.core.tool.MD5;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.Date;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class VMUserServerTest extends AbstractVMTestCase {

    /**
     * Test of write2DB method, of class VMUserServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWrite2DB() throws Exception {
        System.out.println("write2DB");
        String user = "test";
        String pw = "test";
        String name = "Test";
        String lastname = "User";
        String email = "test.user@test.com";
        VMUserServer instance = new VMUserServer(user, pw, name, lastname, email);
        instance.write2DB();
        assertTrue(instance.getId() > 0);
        assertEquals(instance.getId(), instance.getEntity().getId());
        assertEquals(user, instance.getEntity().getUsername());
        assertEquals(MD5.encrypt(pw), instance.getEntity().getPassword());
        assertEquals(name, instance.getEntity().getFirstName());
        assertEquals(lastname, instance.getEntity().getLastName());
        assertEquals(email, instance.getEntity().getEmail());
        assertEquals(1, instance.getEntity().getHistoryList().size());
    }

    /**
     * Test of getVMUsers method, of class VMUserServer.
     */
    @Test
    public void testGetVMUsers() {
        System.out.println("getVMUsers");
        assertTrue(!VMUserServer.getVMUsers().isEmpty());
    }

    /**
     * Test of isPasswordUsable method, of class VMUserServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testIsPasswordUsable_String_boolean() throws Exception {
        System.out.println("isPasswordUsable");
        String newPass = "test2";
        String user = "test";
        String pw = "test";
        String name = "Test";
        String lastname = "User";
        String email = "test.user@test.com";
        VMUserServer instance = new VMUserServer(user, pw, name,
                lastname, email);
        instance.write2DB();
        assertEquals(false, instance.isPasswordUsable(pw));
        assertEquals(true, instance.isPasswordUsable(newPass));
    }

    /**
     * Test of validCredentials method, of class VMUserServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testValidCredentials() throws Exception {
        System.out.println("validCredentials");
        String user = "test";
        String pw = "test";
        String name = "Test";
        String lastname = "User";
        String email = "test.user@test.com";
        VMUserServer instance = new VMUserServer(user, pw, name,
                lastname, email);
        instance.write2DB();
        assertEquals(true, VMUserServer.validCredentials(user, pw, true));
        assertEquals(false, VMUserServer.validCredentials(user, pw + 1, true));
        assertEquals(false, VMUserServer.validCredentials(user + 1, pw + 1,
                true));
        assertEquals(false, VMUserServer.validCredentials(user + 1, pw, true));
        assertEquals(true, VMUserServer.validCredentials(user, MD5.encrypt(pw),
                false));
        assertEquals(false, VMUserServer.validCredentials(user,
                MD5.encrypt(pw) + 1, false));
    }

    /**
     * Test of getPendingNotifications method, of class VMUserServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetPendingNotifications() throws Exception {
        System.out.println("getPendingNotifications");
        String user = "test";
        String pw = "test";
        String name = "Test";
        String lastname = "User";
        String email = "test.user@test.com";
        VMUserServer instance = new VMUserServer(user, pw, name,
                lastname, email);
        instance.write2DB();
        assertEquals(0, instance.getPendingNotifications().size());
        NotificationServer ns = new NotificationServer();
        ns.setAuthor(new VMUserServer(1).getEntity());
        ns.setContent("Hello there!");
        ns.setCreationDate(new Date());
        ns.setNotificationType(NotificationTypeServer
                .getType("notification.test.pending"));
        ns.setTargetUser(instance.getEntity());
        ns.write2DB();
        assertEquals(1, instance.getPendingNotifications().size());
        ns.setAcknowledgeDate(new Date());
        ns.write2DB();
        assertEquals(0, instance.getPendingNotifications().size());
    }
}