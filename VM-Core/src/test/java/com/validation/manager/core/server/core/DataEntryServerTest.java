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

import com.validation.manager.core.db.DataEntry;
import com.validation.manager.core.db.DataEntryProperty;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.RequirementSpec;
import com.validation.manager.core.db.RequirementSpecNode;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.test.AbstractVMTestCase;
import com.validation.manager.test.TestHelper;
import static com.validation.manager.test.TestHelper.createRequirement;
import static com.validation.manager.test.TestHelper.createRequirementSpec;
import static com.validation.manager.test.TestHelper.createRequirementSpecNode;
import static com.validation.manager.test.TestHelper.createTestCase;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DataEntryServerTest extends AbstractVMTestCase {

    private static final Logger LOG
            = getLogger(DataEntryServerTest.class.getName());
    private TestCaseServer tcs;

    @Override
    protected void postSetUp() {
        try {
            //Create step
            Project project = TestHelper.createProject("Project", "Notes");
            //Create requirements
            LOG.info("Create Requirement Spec");
            RequirementSpec rss = null;
            try {
                rss = createRequirementSpec("Test", "Test",
                        project, 1);
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            LOG.info("Create Requirement Spec Node");
            RequirementSpecNode rsns = null;
            try {
                rsns = createRequirementSpecNode(
                        rss, "Test", "Test", "Test");
            }
            catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
            Requirement r = createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            //Create Test Case
            TestCase tc = createTestCase("Dummy", "Summary");
            tcs = new TestCaseServer(tc);
            //Add steps
            List<Requirement> reqs = new ArrayList<>();
            reqs.add(r);
            for (int i = 1; i < 6; i++) {
                LOG.info(MessageFormat.format("Adding step: {0}", i));
                Step step = tcs.addStep(i, "Step " + i, "Note " + i,
                        "Criteria " + i, reqs);
                assertEquals(1, new StepServer(step).getRequirementList().size());
                tcs.update();
                assertEquals(i, tcs.getStepList().size());
                assertEquals(i, new RequirementServer(r).getStepList().size());
            }
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of getStringField method, of class DataEntryServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetStringField() throws Exception {
        System.out.println("getStringField");
        String name = "test";
        DataEntry de = DataEntryServer.getStringField(name);
        assertNull(de.getDataEntryPK());
        assertEquals(name, de.getEntryName());
        assertEquals("type.string.name", de.getDataEntryType().getTypeName());
        assertEquals(DataEntryServer.getDefaultProperties(de
                .getDataEntryType()).size(), de.getDataEntryPropertyList()
                        .size());
        DataEntryServer des = new DataEntryServer(de);
        des.setStep(tcs.getStepList().get(0));
        des.write2DB();
        assertNotNull(des.getEntity().getDataEntryPK());
        assertEquals(name, des.getEntity().getEntryName());
        assertEquals("type.string.name", des.getEntity().getDataEntryType().getTypeName());
        assertEquals(DataEntryServer.getDefaultProperties(de
                .getDataEntryType()).size(), des.getEntity()
                        .getDataEntryPropertyList().size());
    }

    /**
     * Test of getBooleanField method, of class DataEntryServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetBooleanField() throws Exception {
        System.out.println("getBooleanField");
        String name = "test";
        DataEntry de = DataEntryServer.getBooleanField(name);
        assertNull(de.getDataEntryPK());
        assertEquals(name, de.getEntryName());
        assertEquals("type.boolean.name", de.getDataEntryType().getTypeName());
        assertEquals(DataEntryServer.getDefaultProperties(de
                .getDataEntryType()).size(), de.getDataEntryPropertyList()
                        .size());
        DataEntryServer des = new DataEntryServer(de);
        des.setStep(tcs.getStepList().get(0));
        des.write2DB();
        assertNotNull(des.getEntity().getDataEntryPK());
        assertEquals(name, des.getEntity().getEntryName());
        assertEquals("type.boolean.name", des.getEntity().getDataEntryType()
                .getTypeName());
        assertEquals(DataEntryServer.getDefaultProperties(de
                .getDataEntryType()).size(), des.getEntity()
                        .getDataEntryPropertyList().size());
    }

    /**
     * Test of getAttachmentField method, of class DataEntryServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetAttachmentField() throws Exception {
        System.out.println("getAttachmentField");
        String name = "test";
        DataEntry de = DataEntryServer.getAttachmentField(name);
        assertNull(de.getDataEntryPK());
        assertEquals(name, de.getEntryName());
        assertEquals("type.attachment.name", de.getDataEntryType()
                .getTypeName());
        assertEquals(DataEntryServer.getDefaultProperties(de
                .getDataEntryType()).size(),
                de.getDataEntryPropertyList().size());
        DataEntryServer des = new DataEntryServer(de);
        des.setStep(tcs.getStepList().get(0));
        des.write2DB();
        assertNotNull(des.getEntity().getDataEntryPK());
        assertEquals(name, des.getEntity().getEntryName());
        assertEquals("type.attachment.name", des.getEntity().getDataEntryType()
                .getTypeName());
        assertEquals(DataEntryServer.getDefaultProperties(de
                .getDataEntryType()).size(), des.getEntity()
                        .getDataEntryPropertyList().size());
    }

    /**
     * Test of getNumericField method, of class DataEntryServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetNumericField() throws Exception {
        System.out.println("getNumericField");
        String name = "numeric";
        Float min = null;
        Float max = null;
        DataEntry de = DataEntryServer.getNumericField(name, min, max);
        assertNull(de.getDataEntryPK());
        assertEquals(name, de.getEntryName());
        assertEquals("type.numeric.name", de.getDataEntryType().getTypeName());
        assertEquals(DataEntryServer.getDefaultProperties(de
                .getDataEntryType()).size(), de.getDataEntryPropertyList().size());
        for (DataEntryProperty dep : de.getDataEntryPropertyList()) {
            if (dep.getPropertyName().equals("property.min")) {
                assertEquals("null", dep.getPropertyValue());
            }
            if (dep.getPropertyName().equals("property.max")) {
                assertEquals("null", dep.getPropertyValue());
            }
        }
        min = 1.0f;
        de = DataEntryServer.getNumericField(name, min, max);
        assertNull(de.getDataEntryPK());
        assertEquals(name, de.getEntryName());
        assertEquals("type.numeric.name", de.getDataEntryType().getTypeName());
        assertEquals(DataEntryServer.getDefaultProperties(de
                .getDataEntryType()).size(), de.getDataEntryPropertyList().size());
        for (DataEntryProperty dep : de.getDataEntryPropertyList()) {
            if (dep.getPropertyName().equals("property.min")) {
                assertEquals(min, Float.valueOf(dep.getPropertyValue()));
            }
            if (dep.getPropertyName().equals("property.max")) {
                assertEquals("null", dep.getPropertyValue());
            }
        }
        max = 10.0f;
        de = DataEntryServer.getNumericField(name, min, max);
        assertNull(de.getDataEntryPK());
        assertEquals(name, de.getEntryName());
        assertEquals("type.numeric.name", de.getDataEntryType().getTypeName());
        assertEquals(DataEntryServer.getDefaultProperties(de
                .getDataEntryType()).size(), de.getDataEntryPropertyList().size());
        for (DataEntryProperty dep : de.getDataEntryPropertyList()) {
            if (dep.getPropertyName().equals("property.min")) {
                assertEquals(min, Float.valueOf(dep.getPropertyValue()));
            }
            if (dep.getPropertyName().equals("property.max")) {
                assertEquals(max, Float.valueOf(dep.getPropertyValue()));
            }
        }
        de.setStep(tcs.getStepList().get(0));
        DataEntryServer des = new DataEntryServer(de);
        des.write2DB();
        assertNotNull(des.getEntity().getDataEntryPK());
        assertEquals(name, des.getEntity().getEntryName());
        assertEquals("type.numeric.name", des.getEntity()
                .getDataEntryType().getTypeName());
        assertEquals(DataEntryServer.getDefaultProperties(de
                .getDataEntryType()).size(), des.getEntity()
                        .getDataEntryPropertyList().size());
        for (DataEntryProperty dep : des.getDataEntryPropertyList()) {
            if (dep.getPropertyName().equals("property.min")) {
                assertEquals(min, Float.valueOf(dep.getPropertyValue()));
            }
            if (dep.getPropertyName().equals("property.max")) {
                assertEquals(max, Float.valueOf(dep.getPropertyValue()));
            }
        }
    }
}
