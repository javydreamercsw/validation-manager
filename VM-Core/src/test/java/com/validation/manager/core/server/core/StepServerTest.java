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
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class StepServerTest extends AbstractVMTestCase {

    /**
     * Test of getEntity method, of class StepServer.
     */
    @Test
    public void testStepCreation() {
        try {
            System.out.println("Database interaction");
            System.out.println("Create a project");
            Project root = TestHelper.createProject("Test", "Notes");
            RequirementSpec mainSpec
                    = TestHelper.createRequirementSpec("Spec", "Desc", root, 1);
            System.out.println("Create Spec for sub project.");
            RequirementSpecNode node = TestHelper.createRequirementSpecNode(mainSpec,
                    "Requirement Doc", "Desc", "Scope");
            Requirement req = TestHelper.createRequirement("REQ-001", "Desc",
                    node.getRequirementSpecNodePK(), "Notes", 1, 1);
            Requirement req2 = TestHelper.createRequirement("REQ-002", "Desc",
                    node.getRequirementSpecNodePK(), "Notes", 1, 1);
            //Create Test Case
            TestCase tc = TestHelper.createTestCase("Dummy", "Summary");
            tc = TestHelper.addStep(tc, 1, "Step " + 1, "Note " + 1, "Result " + 1);
            Step step = tc.getStepList().get(0);
            TestHelper.addRequirementToStep(step, req);
            new TestCaseServer(tc).write2DB();
            StepServer ss = new StepServer(step);
            RequirementServer rs = new RequirementServer(req);
            RequirementServer rs2 = new RequirementServer(req2);
            assertEquals(1, ss.getRequirementList().size());
            assertEquals(1, rs.getStepList().size());
            //Try adding the same requirement
            ss.addRequirement(req);
            assertEquals(1, ss.getRequirementList().size());
            assertEquals(1, rs.getStepList().size());
            ss.addRequirement(req2);
            assertEquals(2, ss.getRequirementList().size());
            assertEquals(1, rs.getStepList().size());
            rs2.update();
            assertEquals(1, rs2.getStepList().size());
            ss.removeRequirement(req);
            rs.update();
            assertEquals(1, ss.getRequirementList().size());
            assertEquals(0, rs.getStepList().size());
            assertEquals(1, ss.getHistoryList().size());
            ss.getHistoryList().forEach(h -> {
                h.getHistoryFieldList().forEach(hf -> {

                    switch (hf.getFieldName()) {
                        case "text":
                            assertEquals(new String(step.getText(),
                                    StandardCharsets.UTF_8),
                                    hf.getFieldValue());
                            break;
                        case "expectedResult":
                            assertEquals(new String(step.getExpectedResult(),
                                    StandardCharsets.UTF_8),
                                    hf.getFieldValue());
                            break;
                        case "notes":
                            assertEquals(step.getNotes(),
                                    hf.getFieldValue());
                            break;
                        default:
                            System.err.println("Unexpected field: "
                                    + hf.getFieldName());
                            fail();
                    }
                });
            });
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            fail();
        }
    }
}
