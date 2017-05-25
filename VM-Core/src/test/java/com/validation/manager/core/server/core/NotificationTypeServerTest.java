package com.validation.manager.core.server.core;

import com.validation.manager.test.AbstractVMTestCase;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class NotificationTypeServerTest extends AbstractVMTestCase {

    /**
     * Test of write2DB method, of class NotificationTypeServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWrite2DB() throws Exception {
        System.out.println("write2DB");
        String type = "type";
        NotificationTypeServer instance = new NotificationTypeServer();
        assertNull(NotificationTypeServer.getType(type));
        instance.setTypeName(type);
        instance.write2DB();
        assertNotNull(NotificationTypeServer.getType(type));
    }
}
