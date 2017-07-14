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
public class TestCaseServerTest extends AbstractVMTestCase {

    private static final Logger LOG
            = getLogger(TestCaseServerTest.class.getName());

    public TestCaseServerTest() {
    }

    /**
     * Test of addStep method, of class TestCaseServer.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddStep() throws Exception {
        System.out.println("addStep");
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
        try {
            RequirementSpecNode rsns = createRequirementSpecNode(
                    rss, "Test", "Test", "Test");
            Requirement r = createRequirement("SRS-SW-0001",
                    "Sample requirement", rsns.getRequirementSpecNodePK(),
                    "Notes", 1, 1);
            //Create Test Case
            TestCase tc = createTestCase("Dummy", "Summary");
            TestCaseServer tcs = new TestCaseServer(tc);
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
}
