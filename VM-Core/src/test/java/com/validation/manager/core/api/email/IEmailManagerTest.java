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
package com.validation.manager.core.api.email;

import com.validation.manager.test.AbstractVMTestCase;
import de.saly.javamail.mock2.MockMailbox;
import java.util.Arrays;
import javax.mail.Message;
import org.junit.Test;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class IEmailManagerTest extends AbstractVMTestCase {

    /**
     * Test of sendEmail method, of class IEmailManager.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSendEmail() throws Exception {
        System.out.println("sendEmail");
        String to = "target@test.com";
        String cc = "cc@test.com";
        String from = "from@test.com";
        String subject = "Sample Email";
        String bodyText = "Hello World!";
        //Setup mock email server
        MockMailbox toMailbox = MockMailbox.get(to);
        MockMailbox ccMailbox = MockMailbox.get(cc);
        MockMailbox fromMailbox = MockMailbox.get(from);
        assertEquals(0, toMailbox.getInbox().getMessageCount());
        assertEquals(0, ccMailbox.getInbox().getMessageCount());
        assertEquals(0, fromMailbox.getInbox().getMessageCount());
        IEmailManager instance = Lookup.getDefault().lookup(IEmailManager.class);
        instance.sendEmail(to, cc, from, subject, bodyText);
        assertEquals(1, toMailbox.getInbox().getMessageCount());
        for (Message m : toMailbox.getInbox().getMessages()) {
            System.out.println(m.getMessageNumber());
            System.out.println("From: " + Arrays.toString(m.getFrom()));
            assertEquals(1, m.getFrom().length);
            assertEquals(from, m.getFrom()[0].toString());
            System.out.println("Sent: " + m.getSentDate());
            System.out.println(m.getSubject());
            assertEquals(subject, m.getSubject());
            System.out.println(m.getContent());
            assertEquals(bodyText, m.getContent());
        }
        assertEquals(1, ccMailbox.getInbox().getMessageCount());
        for (Message m : ccMailbox.getInbox().getMessages()) {
            System.out.println(m.getMessageNumber());
            System.out.println("From: " + Arrays.toString(m.getFrom()));
            assertEquals(1, m.getFrom().length);
            assertEquals(from, m.getFrom()[0].toString());
            System.out.println("Sent: " + m.getSentDate());
            System.out.println(m.getSubject());
            assertEquals(subject, m.getSubject());
            System.out.println(m.getContent());
            assertEquals(bodyText, m.getContent());
        }
        assertEquals(0, fromMailbox.getInbox().getMessageCount());
    }
}
