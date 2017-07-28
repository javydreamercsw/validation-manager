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
package com.validation.manager.core.tool;

import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.server.core.TestCaseServer;
import com.validation.manager.core.server.core.TestCaseTypeServer;
import com.validation.manager.test.AbstractVMTestCase;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import static junit.framework.TestCase.*;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ToolTest extends AbstractVMTestCase {

    /**
     * Test of createImageIcon method, of class Tool.
     */
    @Test
    public void testCreateImageIcon_String_String() {
        System.out.println("createImageIcon");
        String path = "/com/validation/manager/resources/icons/VMSmall.png";
        String description = "Test";
        ImageIcon r = Tool.createImageIcon(path, description);
        assertNotNull(r);
    }

    /**
     * Test of createImageIcon method, of class Tool.
     */
    @Test
    public void testCreateImageIcon_3args() {
        System.out.println("createImageIcon");
        String path = "/com/validation/manager/resources/icons/VMSmall.png";
        String description = "Test";
        ImageIcon r = Tool.createImageIcon(path, description, Tool.class);
        assertNotNull(r);
    }

    /**
     * Test of removeDuplicates method, of class Tool.
     */
    @Test
    public void testRemoveDuplicates() {
        System.out.println("removeDuplicates");
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(2);
        list.add(2);
        list.add(2);
        list.add(3);
        Tool.removeDuplicates(list);
        assertEquals(3, list.size());
    }

    /**
     * Test of extractTCE method, of class Tool.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExtractTCE() throws Exception {
        System.out.println("extractTCE");
        TestCaseExecutionServer tce = new TestCaseExecutionServer("Test Name",
                "Test Scope");
        TestCaseServer tc = new TestCaseServer("Test Case", new Date(),
                new TestCaseTypeServer(5).getEntity());
        tc.write2DB();
        tce.addTestCase(tc);
        tce.write2DB();
        String key = Tool.buildId(tce,
                Tool.buildId(tc, null, false)).toString();
        TCEExtraction r = Tool.extractTCE(key);
        assertEquals(tce.getId(), r.getTestCaseExecution().getId());
        try {
            Tool.extractTCE(key + 1);
            fail();
        }
        catch (Exception ex) {
            //Expected
        }
    }

    /**
     * Test of createZipFile method, of class Tool.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testCreateZipFile() throws IOException {
        System.out.println("createZipFile");
        File f = new File("target/Test.txt");
        f.deleteOnExit();
        List<String> lines = Arrays.asList("The first line", "The second line");
        Path file = Paths.get(f.getAbsolutePath());
        Files.write(file, lines, Charset.forName("UTF-8"));
        File zip = Tool.createZipFile(Arrays.asList(file.toFile()), "Test");
        zip.deleteOnExit();
        assertNotNull(zip);
        assertTrue(zip.getName().endsWith(".zip"));
    }
}
