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
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TestPlanServerTest extends AbstractVMTestCase {

    private static final Logger LOG
            = Logger.getLogger(TestPlanServerTest.class.getName());
    private TestProjectServer tp;

    @Override
    protected void postSetUp() {
        try {
            tp = new TestProjectServer("Test Project", true);
            tp.write2DB();
        }
        catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of write2DB method, of class TestPlanServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWrite2DB() throws Exception {
        System.out.println("write2DB");
        TestPlanServer instance = new TestPlanServer(tp.getEntity(), true, true);
        assertTrue(instance.write2DB() > 0);
    }

    /**
     * Test of deleteTestPlan method, of class TestPlanServer.
     *
     * @throws com.validation.manager.core.VMException
     */
    @Test
    public void testDeleteTestPlan() throws VMException {
        System.out.println("deleteTestPlan");
        TestPlanServer instance = new TestPlanServer(tp.getEntity(), true, true);
        instance.write2DB();
        assertNotNull(instance.getEntity());
        assertTrue(TestPlanServer.deleteTestPlan(instance.getEntity()));
        assertNull(instance.getEntity());
    }

    /**
     * Test of addTestCase method, of class TestPlanServer.
     *
     * @throws com.validation.manager.core.VMException
     * @throws
     * com.validation.manager.core.db.controller.exceptions.NonexistentEntityException
     */
    @Test
    public void testAddTestCase() throws VMException,
            NonexistentEntityException, Exception {
        System.out.println("addTestCase");
        TestPlanServer instance = new TestPlanServer(tp.getEntity(), true, true);
        instance.write2DB();
        TestCaseServer tc = new TestCaseServer("Test", new Date(),
                new TestCaseTypeServer(5).getEntity());
        tc.write2DB();
        assertEquals(0, instance.getEntity().getTestCaseList().size());
        instance.addTestCase(tc.getEntity());
        assertEquals(1, instance.getEntity().getTestCaseList().size());
        //Reproduce issue where test Cases dissapear ater disconnecting to the data base.
        DataBaseManager.close();
        DataBaseManager.getEntityManager();
        assertEquals(1, instance.getEntity().getTestCaseList().size());
    }

    /**
     * Test of update method, of class TestPlanServer.
     *
     * @throws com.validation.manager.core.VMException
     */
    @Test
    public void testUpdate() throws VMException {
        System.out.println("update");
        TestPlanServer instance = new TestPlanServer(tp.getEntity(), true, true);
        instance.write2DB();
        instance.setName("Test");
        instance.write2DB();//This calls update
        assertEquals("Test", instance.getEntity().getName());
    }

    /**
     * Test of addUserTestPlanRole method, of class TestPlanServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddUserTestPlanRole() throws Exception {
        System.out.println("addUserTestPlanRole");
        TestPlanServer instance = new TestPlanServer(tp.getEntity(), true, true);
        instance.write2DB();
        VMUserServer user = new VMUserServer("test", "test", "Mr.", "Tester", "");
        user.write2DB();
        assertEquals(0, instance.getEntity().getUserTestPlanRoleList().size());
        instance.addUserTestPlanRole(user.getEntity(),
                RoleServer.getRole("tester"));
        assertEquals(1, instance.getEntity().getUserTestPlanRoleList().size());
    }
}
