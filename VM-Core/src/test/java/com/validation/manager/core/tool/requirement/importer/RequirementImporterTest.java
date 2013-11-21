package com.validation.manager.core.tool.requirement.importer;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.controller.RequirementSpecNodeJpaController;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RequirementImporterTest extends AbstractVMTestCase {

    private static final Logger LOG
            = Logger.getLogger(RequirementImporterTest.class.getName());

    public RequirementImporterTest() {
    }

    /**
     * Test of importFile method, of class RequirementImporter.
     */
    @Test
    public void testImportFileXLS() {
        System.out.println("importFile (xls)");
        Project project = TestHelper.createProject("Test Project", "Notes");
        String name = RequirementImporterTest.class.getCanonicalName();
        name = name.substring(0, name.lastIndexOf("."));
        name = name.replace(".", System.getProperty("file.separator"));
        File file = new File(System.getProperty("user.dir")
                + System.getProperty("file.separator") + "src"
                + System.getProperty("file.separator") + "test"
                + System.getProperty("file.separator") + "java"
                + System.getProperty("file.separator")
                + name
                + System.getProperty("file.separator") + "Reqs.xls");
        System.out.println(file.getAbsolutePath());
        assertTrue(DataBaseManager.namedQuery("Requirement.findAll").isEmpty());
        System.out.println("Create Requirement Spec");
        RequirementSpec rss = null;
        try {
            rss = TestHelper.createRequirementSpec("Test", "Test",
                    project, 1);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Create Requirement Spec Node");
        RequirementSpecNode rsns;
        try {
            rsns = TestHelper.createRequirementSpecNode(
                    rss, "Test", "Test", "Test");
            RequirementImporter instance = new RequirementImporter(file,
                    new RequirementSpecNodeJpaController(
                            DataBaseManager.getEntityManagerFactory())
                    .findRequirementSpecNode(rsns.getRequirementSpecNodePK()));
            //It has headers
            instance.importFile(true);
            assertTrue(instance.processImport());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        assertFalse(DataBaseManager.namedQuery("Requirement.findAll").isEmpty());
    }

    /**
     * Test of importFile method, of class RequirementImporter.
     */
    @Test
    public void testImportFileXLSX() {
        System.out.println("importFile (xlsx)");
        Project product = TestHelper.createProject("Test Project", "Notes");
        String name = RequirementImporterTest.class.getCanonicalName();
        name = name.substring(0, name.lastIndexOf("."));
        name = name.replace(".", System.getProperty("file.separator"));
        File file = new File(System.getProperty("user.dir")
                + System.getProperty("file.separator") + "src"
                + System.getProperty("file.separator") + "test"
                + System.getProperty("file.separator") + "java"
                + System.getProperty("file.separator")
                + name
                + System.getProperty("file.separator") + "Reqs.xlsx");
        System.out.println(file.getAbsolutePath());
        System.out.println("Create Requirement Spec");
        RequirementSpec rss = null;
        try {
            rss = TestHelper.createRequirementSpec("Test", "Test",
                    product, 1);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Create Requirement Spec Node");
        RequirementSpecNode rsns;
        try {
            rsns = TestHelper.createRequirementSpecNode(
                    rss, "Test", "Test", "Test");
            RequirementImporter instance = new RequirementImporter(file,
                    new RequirementSpecNodeJpaController(
                            DataBaseManager.getEntityManagerFactory())
                    .findRequirementSpecNode(rsns.getRequirementSpecNodePK()));
            instance.importFile();
            assertTrue(instance.processImport());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        assertFalse(DataBaseManager.namedQuery("Requirement.findAll").isEmpty());
    }

    @Test
    public void testImportDuplicates() {
        System.out.println("importFile (duplicates)");
        Project product = TestHelper.createProject("Test Project", "Notes");
        String name = RequirementImporterTest.class.getCanonicalName();
        name = name.substring(0, name.lastIndexOf("."));
        name = name.replace(".", System.getProperty("file.separator"));
        File file = new File(System.getProperty("user.dir")
                + System.getProperty("file.separator") + "src"
                + System.getProperty("file.separator") + "test"
                + System.getProperty("file.separator") + "java"
                + System.getProperty("file.separator")
                + name
                + System.getProperty("file.separator") + "Failure.xls");
        System.out.println(file.getAbsolutePath());
        assertTrue(DataBaseManager.namedQuery("Requirement.findAll").isEmpty());
        System.out.println("Create Requirement Spec");
        RequirementSpec rss = null;
        try {
            rss = TestHelper.createRequirementSpec("Test", "Test",
                    product, 1);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Create Requirement Spec Node");
        RequirementSpecNode rsns = null;
        try {
            rsns = TestHelper.createRequirementSpecNode(
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
                                DataBaseManager.getEntityManagerFactory())
                        .findRequirementSpecNode(rsns.getRequirementSpecNodePK()));
                instance.importFile();
                assertFalse(instance.processImport());
            } catch (RequirementImportException ex) {
                Exceptions.printStackTrace(ex);
                fail();
            } catch (VMException ex) {
                Exceptions.printStackTrace(ex);
                fail();
            }
        }
    }

    @Test
    public void testImportHeader() {
        System.out.println("importFile (ignoring header)");
        Project product = TestHelper.createProject("Test Project", "Notes");
        String name = RequirementImporterTest.class.getCanonicalName();
        name = name.substring(0, name.lastIndexOf("."));
        name = name.replace(".", System.getProperty("file.separator"));
        File file = new File(System.getProperty("user.dir")
                + System.getProperty("file.separator") + "src"
                + System.getProperty("file.separator") + "test"
                + System.getProperty("file.separator") + "java"
                + System.getProperty("file.separator")
                + name
                + System.getProperty("file.separator") + "Header.xlsx");
        System.out.println(file.getAbsolutePath());
        assertTrue(DataBaseManager.namedQuery("Requirement.findAll").isEmpty());
        System.out.println("Create Requirement Spec");
        RequirementSpec rss = null;
        try {
            rss = TestHelper.createRequirementSpec("Test", "Test",
                    product, 1);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        System.out.println("Create Requirement Spec Node");
        RequirementSpecNode rsns;
        try {
            rsns = TestHelper.createRequirementSpecNode(
                    rss, "Test", "Test", "Test");
            RequirementImporter instance = new RequirementImporter(file,
                    new RequirementSpecNodeJpaController(
                            DataBaseManager.getEntityManagerFactory())
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
        System.out.println("Generate template");
        try {
            File template = RequirementImporter.exportTemplate();
            assertTrue(template.exists());
            template.deleteOnExit();
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        } catch (InvalidFormatException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
