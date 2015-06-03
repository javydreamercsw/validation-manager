package com.validation.manager.core.server.core;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import static com.validation.manager.core.DataBaseManager.setPersistenceUnitName;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.ProjectJpaController;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.tool.requirement.importer.RequirementImporter;
import com.validation.manager.test.AbstractVMTestCase;
import java.io.File;
import static java.lang.System.getProperty;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class LoadTest extends AbstractVMTestCase {

    private static final Logger LOG
            = getLogger(LoadTest.class.getName());

    /**
     * @param args the command line arguments
     */
    @Test
    public void loadTest() {
        LOG.info("Load Test");
        setPersistenceUnitName("TestVMPU");
        Project product = createProject("Test Project", "Notes");
        String name = LoadTest.class.getCanonicalName();
        name = name.substring(0, name.lastIndexOf("."));
        name = name.replace(".", getProperty("file.separator"));
        File file = new File(getProperty("user.dir")
                + getProperty("file.separator") + "src"
                + getProperty("file.separator") + "test"
                + getProperty("file.separator") + "java"
                + getProperty("file.separator")
                + name
                + getProperty("file.separator") + "Load Test.xlsx");
        LOG.info(file.getAbsolutePath());
        LOG.info("Create Requirement Spec");
        RequirementSpec rss = null;
        try {
            rss = createRequirementSpec("Test", "Test", product, 1);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        LOG.info("Create Requirement Spec Node");
        RequirementSpecNode rsns;
        try {
            rsns = createRequirementSpecNode(
                    rss, "Test", "Test", "Test");
            RequirementImporter instance = new RequirementImporter(file,
                    new RequirementSpecNodeJpaController(
                            getEntityManagerFactory())
                    .findRequirementSpecNode(rsns.getRequirementSpecNodePK()));
            LOG.info("Loading file...");
            instance.importFile();
            LOG.info("Processing file...");
            instance.processImport();
            LOG.info("Done!");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        int imported = namedQuery("Requirement.findAll").size();
        LOG.log(Level.INFO, "Imported: {0}", imported);
        assertEquals(10000, imported);
    }

    private RequirementSpecNode createRequirementSpecNode(
            RequirementSpec rss, String name, String description, String scope)
            throws Exception {
        RequirementSpecNodeServer rsns = new RequirementSpecNodeServer(rss,
                name, description, scope);
        rsns.write2DB();
        return rsns;
    }

    private RequirementSpec createRequirementSpec(String name,
            String description, Project project, int specLevelId) throws Exception {
        RequirementSpecServer rss = new RequirementSpecServer(name, description,
                project.getId(), specLevelId);
        rss.write2DB();
        project.getRequirementSpecList().add(rss);
        new ProjectServer(project).write2DB();
        return rss;
    }

    private static Project createProject(String name, String notes) {
        ProjectServer ps = new ProjectServer(name, notes);
        try {
            ps.write2DB();
        } catch (IllegalOrphanException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (NonexistentEntityException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        Project p = new ProjectJpaController(
                getEntityManagerFactory()).findProject(ps.getId());
        return p;
    }
}
