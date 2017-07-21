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
package com.validation.manager.core;

import com.validation.manager.core.db.controller.VmSettingJpaController;
import com.validation.manager.core.server.core.VMSettingServer;
import com.validation.manager.test.AbstractVMTestCase;
import java.sql.SQLException;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DataBaseManagerTest extends AbstractVMTestCase {

    /**
     * Test of clean method, of class DataBaseManager.
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testClean() throws SQLException, Exception {
        System.out.println("clean");
        VmSettingJpaController c
                = new VmSettingJpaController(DataBaseManager
                        .getEntityManagerFactory());
        int initial = c.findVmSettingEntities().size();
        new VMSettingServer("test", false, 0, 0, "").write2DB();
        assertEquals(initial + 1, c.findVmSettingEntities().size());
        DataBaseManager.demo = true;
        DataBaseManager.clean();
        c = new VmSettingJpaController(DataBaseManager
                .getEntityManagerFactory());
        assertEquals(initial, c.findVmSettingEntities().size());
    }
}
