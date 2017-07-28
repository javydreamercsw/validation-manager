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
package com.validation.manager.core.db;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import com.validation.manager.core.db.controller.RoleJpaController;
import com.validation.manager.core.db.controller.exceptions.PreexistingEntityException;
import com.validation.manager.core.server.core.TestProjectServer;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TestProjectTest extends AbstractVMTestCase {

    private static final Logger LOG
            = Logger.getLogger(TestProjectTest.class.getSimpleName());

    @Test
    @Ignore
    @SuppressWarnings("unchecked")
    public void testCreateTestProject() {
        createTestUsers();
        try {
            LOG.log(Level.INFO, "Create Test Project");
            parameters.clear();
            parameters.put("name", "Test Project");
            TestProjectServer tp = new TestProjectServer("Test Project", true);
            tp.write2DB();
            assertTrue(tp.getId() >= 0);
            assignRolesForTestProject(tp);
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    private void assignRolesForTestProject(TestProjectServer tp) {
        try {
            LOG.log(Level.INFO, "Assigning roles");
            //Designer role
            tp.addUserTestProjectRole(tp.getEntity(), designer,
                    new RoleJpaController(getEntityManagerFactory())
                            .findRole(4));
            //Tester role
            tp.addUserTestProjectRole(tp.getEntity(), tester,
                    new RoleJpaController(getEntityManagerFactory())
                            .findRole(7));
            //Leader role
            tp.addUserTestProjectRole(tp.getEntity(), leader,
                    new RoleJpaController(getEntityManagerFactory())
                            .findRole(9));
            assertEquals(3, tp.getUserTestProjectRoleList().size());
        }
        catch (PreexistingEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
