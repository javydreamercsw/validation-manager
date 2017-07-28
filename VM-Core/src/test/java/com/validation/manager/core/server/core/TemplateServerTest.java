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

import com.validation.manager.core.db.TemplateNode;
import com.validation.manager.core.db.TemplateNodeType;
import com.validation.manager.test.AbstractVMTestCase;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TemplateServerTest extends AbstractVMTestCase {

    /**
     * Test of write2DB method, of class TemplateServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWrite2DB() throws Exception {
        System.out.println("write2DB");
        TemplateServer instance = new TemplateServer("Test Template");
        instance.write2DB();
        assertTrue(instance.getEntity().getId() >= 1000);
        assertTrue(instance.getId() >= 1000);
        assertEquals("Test Template",
                instance.getEntity().getTemplateName());
        assertNotNull(instance.getTemplateNodeList());
        instance.setTemplateName("Test");
        instance.write2DB();
        assertEquals("Test",
                instance.getEntity().getTemplateName());
    }

    /**
     * Test of addNode method, of class TemplateServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddNode() throws Exception {
        System.out.println("addNode");
        String name = "test-node";
        TemplateNodeType type = TemplateNodeTypeServer
                .getType("general.requirement");
        TemplateServer instance = new TemplateServer("Test Template");
        instance.write2DB();
        TemplateNode node = instance.addNode(name, type);
        assertNotNull(node);
        assertNotNull(instance.getTemplateNodeList());
        assertEquals(1, instance.getTemplateNodeList().size());
        assertEquals(1, instance.getEntity().getTemplateNodeList().size());
        assertEquals(name, node.getNodeName());
        assertTrue(node.getTemplateNodePK().getId() >= 1000);
    }

    /**
     * Test of addChildNode method, of class TemplateServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddChildNode() throws Exception {
        System.out.println("addChildNode");
        String name = "test-node";
        TemplateNodeType type = TemplateNodeTypeServer
                .getType("general.requirement");
        TemplateServer instance = new TemplateServer("Test Template");
        instance.write2DB();
        TemplateNodeServer node = new TemplateNodeServer(name, type,
                instance.getEntity());
        node.write2DB();
        TemplateNodeServer node2 = new TemplateNodeServer(name + 1, type,
                instance.getEntity());
        node2.write2DB();
        instance.addChildNode(node.getEntity(), node2.getEntity(), true);
    }
}
