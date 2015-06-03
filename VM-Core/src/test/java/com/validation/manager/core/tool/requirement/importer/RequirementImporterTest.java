package com.validation.manager.core.tool.requirement.importer;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import static com.validation.manager.core.tool.requirement.importer.RequirementImporter.exportTemplate;
import com.validation.manager.test.AbstractVMTestCase;
import static com.validation.manager.test.TestHelper.createProject;
import static com.validation.manager.test.TestHelper.createRequirementSpec;
import static com.validation.manager.test.TestHelper.createRequirementSpecNode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.System.getProperty;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;
import static org.openide.util.Exceptions.printStackTrace;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementImporterTest extends AbstractVMTestCase {

    private static final Logger LOG
            = getLogger(RequirementImporterTest.class.getName());

    public RequirementImporterTest() {
    }

    /**
     * Test of importFile method, of class RequirementImporter.
     */
    @Test
    public void testImportFileXLS() {
        LOG.info("importFile (xls)");
        Project project = createProject("Test Project", "Notes");
        String name = RequirementImporterTest.class.getCanonicalName();
        name = name.substring(0, name.lastIndexOf("."));
        name = name.replace(".", getProperty("file.separator"));
        File file = new File(getProperty("user.dir")
                + getProperty("file.separator") + "src"
                + getProperty("file.separator") + "test"
                + getProperty("file.separator") + "java"
                + getProperty("file.separator")
                + name
                + getProperty("file.separator") + "Reqs.xls");
        LOG.info(file.getAbsolutePath());
        assertTrue(namedQuery("Requirement.findAll").isEmpty());
        LOG.info("Create Requirement Spec");
        RequirementSpec rss = null;
        try {
            rss = createRequirementSpec("Test", "Test",
                    project, 1);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
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
            //It has headers
            instance.importFile(true);
            assertTrue(instance.processImport());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        assertFalse(namedQuery("Requirement.findAll").isEmpty());
    }

    /**
     * Test of importFile method, of class RequirementImporter.
     */
    @Test
    public void testImportFileXLSX() {
        LOG.info("importFile (xlsx)");
        Project product = createProject("Test Project", "Notes");
        String name = RequirementImporterTest.class.getCanonicalName();
        name = name.substring(0, name.lastIndexOf("."));
        name = name.replace(".", getProperty("file.separator"));
        File file = new File(getProperty("user.dir")
                + getProperty("file.separator") + "src"
                + getProperty("file.separator") + "test"
                + getProperty("file.separator") + "java"
                + getProperty("file.separator")
                + name
                + getProperty("file.separator") + "Reqs.xlsx");
        LOG.info(file.getAbsolutePath());
        LOG.info("Create Requirement Spec");
        RequirementSpec rss = null;
        try {
            rss = createRequirementSpec("Test", "Test",
                    product, 1);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
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
            instance.importFile();
            assertTrue(instance.processImport());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        assertFalse(namedQuery("Requirement.findAll").isEmpty());
    }

    @Test
    public void testImportDuplicates() {
        LOG.info("importFile (duplicates)");
        Project product = createProject("Test Project", "Notes");
        String name = RequirementImporterTest.class.getCanonicalName();
        name = name.substring(0, name.lastIndexOf("."));
        name = name.replace(".", getProperty("file.separator"));
        File file = new File(getProperty("user.dir")
                + getProperty("file.separator") + "src"
                + getProperty("file.separator") + "test"
                + getProperty("file.separator") + "java"
                + getProperty("file.separator")
                + name
                + getProperty("file.separator") + "Failure.xls");
        LOG.info(file.getAbsolutePath());
        assertTrue(namedQuery("Requirement.findAll").isEmpty());
        LOG.info("Create Requirement Spec");
        RequirementSpec rss = null;
        try {
            rss = createRequirementSpec("Test", "Test",
                    product, 1);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        LOG.info("Create Requirement Spec Node");
        RequirementSpecNode rsns = null;
        try {
            rsns = createRequirementSpecNode(
                    rss, "Test", "Test", "Test");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        if (rsns == null) {
            fail();
        } else {
            try {
                RequirementImporter instance = new RequirementImporter(file,
                        new RequirementSpecNodeJpaController(
                                getEntityManagerFactory())
                        .findRequirementSpecNode(rsns.getRequirementSpecNodePK()));
                List<Requirement> imported = instance.importFile();
                assertTrue(instance.processImport());
                assertEquals(19, imported.size());
                imported = instance.importFile();
                assertTrue(instance.processImport());
                assertEquals(0, imported.size());
            } catch (RequirementImportException ex) {
                printStackTrace(ex);
                fail();
            } catch (VMException ex) {
                printStackTrace(ex);
                fail();
            }
        }
    }

    @Test
    public void testImportHeader() {
        LOG.info("importFile (ignoring header)");
        Project product = createProject("Test Project", "Notes");
        String name = RequirementImporterTest.class.getCanonicalName();
        name = name.substring(0, name.lastIndexOf("."));
        name = name.replace(".", getProperty("file.separator"));
        File file = new File(getProperty("user.dir")
                + getProperty("file.separator") + "src"
                + getProperty("file.separator") + "test"
                + getProperty("file.separator") + "java"
                + getProperty("file.separator")
                + name
                + getProperty("file.separator") + "Header.xlsx");
        LOG.info(file.getAbsolutePath());
        assertTrue(namedQuery("Requirement.findAll").isEmpty());
        LOG.info("Create Requirement Spec");
        RequirementSpec rss = null;
        try {
            rss = createRequirementSpec("Test", "Test",
                    product, 1);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
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
            assertTrue(instance.importFile(true).size() == 20);
            assertTrue(instance.processImport());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    @Test
    public void testGenerateTemplate() {
        LOG.info("Generate template");
        try {
            File template = exportTemplate();
            assertTrue(template.exists());
            template.deleteOnExit();
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        } catch (IOException | InvalidFormatException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
