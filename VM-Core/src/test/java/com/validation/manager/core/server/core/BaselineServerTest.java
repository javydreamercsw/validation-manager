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
import com.validation.manager.core.db.History;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class BaselineServerTest extends AbstractVMTestCase {

    private static final Logger LOG
            = Logger.getLogger(BaselineServerTest.class.getSimpleName());

    /**
     * Test of write2DB method, of class BaselineServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWrite2DB() throws Exception {
        System.out.println("write2DB");
        String name = "Demo baseline";
        BaselineServer instance = new BaselineServer(new Date(), name);
        instance.write2DB();
        assertTrue(instance.getId() > 0);
        assertEquals(name, instance.getEntity().getBaselineName());
    }

    /**
     * Test of createBaseline method, of class BaselineServer.
     */
    @Test
    public void testCreateBaseline() {
        try {
            System.out.println("createBaseline");
            List<Requirement> REQS = new ArrayList<>();
            RequirementSpec rss = null;
            Project p = TestHelper.createProject("New Project", "Notes");
            ProjectServer project = new ProjectServer(p);
            project.setNotes("Notes 2");
            project.write2DB();
            assertTrue(new ProjectJpaController(
                    DataBaseManager.getEntityManagerFactory())
                    .findProject(project.getId()).getNotes().equals(project.getNotes()));
            //Create requirements
            System.out.println("Create Requirement Spec");
            try {
                rss = TestHelper.createRequirementSpec("Test", "Test",
                        project, 1);
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            System.out.println("Create Requirement Spec Node");
            try {
                RequirementSpecNode rsns = TestHelper.createRequirementSpecNode(
                        rss, "Test", "Test", "Test");
                REQS.add(TestHelper.createRequirement("SRS-SW-0001",
                        "Sample requirement", rsns.getRequirementSpecNodePK(),
                        "Notes", 1, 1));
                REQS.add(TestHelper.createRequirement("SRS-SW-0002",
                        "Sample requirement", rsns.getRequirementSpecNodePK(),
                        "Notes", 1, 1));
                REQS.add(TestHelper.createRequirement("SRS-SW-0003",
                        "Sample requirement", rsns.getRequirementSpecNodePK(),
                        "Notes", 1, 1));
                REQS.add(TestHelper.createRequirement("SRS-SW-0004",
                        "Sample requirement", rsns.getRequirementSpecNodePK(),
                        "Notes", 1, 1));
                REQS.add(TestHelper.createRequirement("PS-SW-0001",
                        "Sample User requirement", rsns.getRequirementSpecNodePK(),
                        "Notes", 1, 1));
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            String name = "Demo";
            String desc = "Description";
            BaselineServer r = BaselineServer.createBaseline(name, desc,
                    new RequirementSpecServer(rss).getEntity());
            assertEquals(name, r.getEntity().getBaselineName());
            assertEquals(desc, r.getEntity().getDescription());
            assertNotNull(r.getEntity().getCreationDate());
            assertEquals(REQS.size(), r.getEntity().getHistoryList().size());
            LOG.log(Level.INFO, "Baseline: {0}", r.getBaselineName());
            r.getEntity().getHistoryList().forEach(current -> {
                LOG.info(current.toString());
                assertEquals(1, (int) current.getMajorVersion());
                assertEquals(0, (int) current.getMidVersion());
                assertEquals(0, (int) current.getMinorVersion());
            });
            RequirementServer rs = new RequirementServer(REQS.get(0));
            rs.setDescription("Temp");
            rs.write2DB();
            BaselineServer r2 = BaselineServer.createBaseline(name + 2, desc + 2,
                    new RequirementSpecServer(rss).getEntity());
            int count = 0;
            LOG.log(Level.INFO, "Baseline: {0}", r2.getBaselineName());
            for (History current : r2.getEntity().getHistoryList()) {
                LOG.info(current.toString());
                assertEquals(count == 0 ? 2 : 1, (int) current.getMajorVersion());
                assertEquals(0, (int) current.getMidVersion());
                assertEquals(0, (int) current.getMinorVersion());
                count++;
            }
        }
        catch (NonexistentEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
