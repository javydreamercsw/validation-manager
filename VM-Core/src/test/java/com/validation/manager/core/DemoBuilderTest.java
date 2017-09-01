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
package com.validation.manager.core;

import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.Requirement;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.server.core.ProjectServer;
import com.validation.manager.core.server.core.RequirementServer;
import com.validation.manager.core.server.core.TestCaseExecutionServer;
import com.validation.manager.core.tool.Tool;
import com.validation.manager.test.AbstractVMTestCase;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.TestCase.*;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class DemoBuilderTest extends AbstractVMTestCase {

    private static final Logger LOG
            = Logger.getLogger(DemoBuilderTest.class.getSimpleName());

    /**
     * Test of buildDemoProject method, of class DemoBuilder.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testBuildDemoProject() throws Exception {
        System.out.println("buildDemoProject");
        try {
            DemoBuilder.buildDemoProject();
            List<Project> projects = ProjectServer.getProjects();
            assertTrue(projects.size() > 0);
            int testProjects = 0, plans = 0;
            for (Project p : projects) {
                for (TestProject tp : p.getTestProjectList()) {
                    testProjects++;
                    //Check all Test Projects have related projects..
                    assertTrue(tp.getProjectList().size() > 0);
                    for (TestPlan plan : tp.getTestPlanList()) {
                        plans++;
                        plan.getTestCaseList().stream().map((tc) -> {
                            //Check all test cases have steps.
                            assertTrue(tc.getStepList().size() > 0);
                            return tc;
                        }).forEachOrdered((tc) -> {
                            tc.getStepList().forEach(s -> {
                                //Check all steps have related requirements.
                                assertTrue(s.getRequirementList().size() > 0);
                                //Check all steps have related fields.
                                assertTrue(s.getDataEntryList().size() > 0);
                                //Check all fields have properties.
                                s.getDataEntryList().forEach(de -> {
                                    assertTrue(de.getDataEntryPropertyList().size() > 0);
                                });
                            });
                        });
                    }
                }
                List<TestCaseExecution> executions
                        = TestCaseExecutionServer.getExecutions(p);
                if (p.getParentProjectId() != null) {
                    assertTrue(executions.size() > 0);
                }
                executions.forEach(ex -> {
                    //Check all execution has steps.
                    List<ExecutionStep> steps = ex.getExecutionStepList();
                    assertTrue(steps.size() > 0);
                    steps.forEach(es -> {
                        //Check all execution steps have history.
                        assertTrue(es.getHistoryList().size() > 0);
                    });
                });
                //Check all requirements have either children or a parent
                List<Requirement> reqs = Tool.extractRequirements(p);
                assertTrue(reqs.size() > 0);
                reqs.forEach(r -> {
                    try {
                        RequirementServer rs = new RequirementServer(r);
                        assertTrue(rs.getParentRequirementId() != null
                                || rs.getRequirementList().size() > 0);
                        //Make sure all have a related step
                        if (rs.getStepList().isEmpty()) {
                            LOG.log(Level.INFO, "{0} has no steps!",
                                    rs.getUniqueId());
                        }
                        assertTrue(rs.getStepList().size() > 0);
                    }
                    catch (VMException ex) {
                        Exceptions.printStackTrace(ex);
                        fail();
                    }
                });
                assertEquals(1, p.getFmeaList().size());
                p.getFmeaList().forEach(fmea -> {
                    assertTrue(fmea.getRiskItemList().size() > 0);
                    fmea.getRiskItemList().forEach(item -> {
                        assertTrue(item.getRiskItemHasHazardList().size() > 0);
                        item.getRiskItemHasHazardList().forEach(rihh -> {
                            assertTrue(rihh.getHazardHasFailureModeList().size() > 0);
                            rihh.getHazardHasFailureModeList().forEach(hhfm -> {
                                assertTrue(hhfm.getFailureModeHasCauseList().size() > 0);
                                hhfm.getFailureModeHasCauseList().forEach(fmhc -> {
                                    assertTrue(fmhc.getFailureModeHasCauseHasRiskCategoryList().size() > 0);
                                    fmhc.getFailureModeHasCauseHasRiskCategoryList().forEach(fmhchrc -> {
                                        assertTrue(fmhchrc.getCategoryValue() > 0);
                                    });
                                });
                            });
                        });
                    });
                });
            }
            assertTrue(testProjects > 0);
            assertTrue(plans > 0);
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
            fail();
        }
    }
}
