package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class BaselineServerTest extends AbstractVMTestCase {

    private static final Logger LOG
            = Logger.getLogger(BaselineServerTest.class.getSimpleName());
    private final List<Requirement> REQS = new ArrayList<>();

    @Before
    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
    }

    @After
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        REQS.clear();
    }

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
        System.out.println("createBaseline");
        String name = "Demo";
        String desc = "Description";
        BaselineServer r = BaselineServer.createBaseline(name, desc, REQS);
        assertEquals(name, r.getEntity().getBaselineName());
        assertEquals(desc, r.getEntity().getDescription());
        assertNotNull(r.getEntity().getCreationDate());
        assertEquals(REQS.size(), r.getEntity().getHistoryList().size());
        r.getEntity().getHistoryList().forEach(current -> {
            assertEquals(1, (int) current.getMajorVersion());
            assertEquals(0, (int) current.getMidVersion());
            assertEquals(0, (int) current.getMinorVersion());
        });
    }
}
