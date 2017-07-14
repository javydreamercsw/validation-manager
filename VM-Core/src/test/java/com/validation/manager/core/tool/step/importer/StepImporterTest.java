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
package com.validation.manager.core.tool.step.importer;

import static com.validation.manager.core.DataBaseManager.getEntityManagerFactory;
import static com.validation.manager.core.DataBaseManager.namedQuery;
import com.validation.manager.core.VMException;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.TestCaseJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.tool.requirement.importer.*;
import static com.validation.manager.core.tool.requirement.importer.RequirementImporter.exportTemplate;
import com.validation.manager.test.AbstractVMTestCase;
import static com.validation.manager.test.TestHelper.addTestCaseToPlan;
import static com.validation.manager.test.TestHelper.addTestProjectToProject;
import static com.validation.manager.test.TestHelper.createProject;
import static com.validation.manager.test.TestHelper.createRequirement;
import static com.validation.manager.test.TestHelper.createRequirementSpec;
import static com.validation.manager.test.TestHelper.createRequirementSpecNode;
import static com.validation.manager.test.TestHelper.createTestCase;
import static com.validation.manager.test.TestHelper.createTestPlan;
import static com.validation.manager.test.TestHelper.createTestProject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.System.getProperty;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import static junit.framework.TestCase.assertEquals;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class StepImporterTest extends AbstractVMTestCase {

    private static final Logger LOG
            = getLogger(RequirementImporterTest.class.getName());

    public StepImporterTest() {
    }

    private int testImportFile(String fileName) {
        return testImportFile(fileName, false);
    }

    private int testImportFile(String fileName, boolean check) {
        try {
            String name = StepImporterTest.class.getCanonicalName();
            Project project = createProject("Test Project", "Notes");
            name = name.substring(0, name.lastIndexOf("."));
            name = name.replace(".", getProperty("file.separator"));
            File file = new File(getProperty("user.dir")
                    + getProperty("file.separator") + "src"
                    + getProperty("file.separator") + "test"
                    + getProperty("file.separator") + "java"
                    + getProperty("file.separator")
                    + name
                    + getProperty("file.separator") + fileName);
            System.out.println(file.getAbsolutePath());
            System.out.println("Create Test Project");
            TestProject tp = null;
            try {
                tp = createTestProject("Test Project");
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            System.out.println("Add Test Project to Project");
            try {
                addTestProjectToProject(tp, project);
            }
            catch (IllegalOrphanException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            catch (NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            System.out.println("Create Test Plan");
            TestPlan plan = null;
            try {
                plan = createTestPlan(tp, "Test Plan", true, true);
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            System.out.println("Create Test");
            TestCase test = null;
            try {
                test = createTestCase("Test", "");
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            System.out.println("Add Test to Plan");
            try {
                addTestCaseToPlan(plan, test);
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            System.out.println("Create Test Case");
            com.validation.manager.core.db.TestCase tc = null;
            try {
                tc = createTestCase("Dummy", "Test Summary");
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            System.out.println("Create Requirement to link");
            Requirement r = null;
            try {
                RequirementSpec rs
                        = createRequirementSpec("Test Spec", "Description",
                                project, 1);
                RequirementSpecNode rsn
                        = createRequirementSpecNode(rs, "Root", "Description",
                                "Scope");
                r = createRequirement("SRS-SW-0001",
                        "Sample requirement", rsn.getRequirementSpecNodePK(),
                        "Notes", 1, 1);
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            //Finally, do the test
            try {
                if (tc == null) {
                    fail("Test Case shouldn't be null!");
                } else {
                    StepImporter instance = new StepImporter(file,
                            new TestCaseJpaController(
                                    getEntityManagerFactory())
                                    .findTestCase(tc.getTestCasePK()));
                    instance.importFile(true);
                    instance.processImport();
                }
            }
            catch (VMException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            RequirementServer rs = new RequirementServer(r);
            int count = 1;
            for (Step s : rs.getStepList()) {
                if (count % 2 == 1) {
                    assertEquals(1, s.getRequirementList().size());
                }
                count++;
            }
            rs.update();
            if (check) {
                assertEquals(10/*Update if the excel sheets change*/,
                        rs.getStepList().size());
            }
            return rs.getStepList().size();
        }
        catch (VMException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        return 0;
    }

    /**
     * Test of importFile method, of class RequirementImporter.
     */
    @Test
    public void testImportFilesXLS() {
        System.out.println("importFile (xls)");
        int initial = namedQuery("Requirement.findAll").size();
        assertTrue(testImportFile("Steps.xls") > initial);
        assertTrue(initial < namedQuery("Requirement.findAll").size());
    }

    @Test
    public void testImportInvalidFile() {
        System.out.println("importFile (invalid)");
        int initial = namedQuery("Requirement.findAll").size();
        assertEquals(0, testImportFile("Fail_Columns.xls"));
        assertEquals(initial + 1,
                namedQuery("Requirement.findAll").size());//One created by test
    }

    /**
     * Test of importFile method, of class RequirementImporter.
     */
    @Test
    public void testImportFileXLSX() {
        System.out.println("importFile (xlsx)");
        assertTrue(testImportFile("Steps.xlsx") > 0);
        assertFalse(namedQuery("Step.findAll").isEmpty());
    }

    @Test
    public void testGenerateTemplate() {
        System.out.println("Generate template");
        try {
            File template = exportTemplate();
            assertTrue(template.exists());
            template.deleteOnExit();
        }
        catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        catch (IOException | InvalidFormatException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
