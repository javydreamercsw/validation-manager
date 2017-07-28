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

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.Project;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.ExecutionStepJpaController;
import com.validation.manager.core.db.controller.TestCaseExecutionJpaController;
import com.validation.manager.core.db.controller.exceptions.IllegalOrphanException;
import com.validation.manager.core.db.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class TestCaseExecutionServer extends TestCaseExecution
        implements EntityServer<TestCaseExecution> {

    public TestCaseExecutionServer(String name, String scope) {
        super(name, scope);
    }

    public TestCaseExecutionServer(int id) {
        super.setId(id);
        update();
    }

    public TestCaseExecutionServer(TestCaseExecution tce) {
        super.setId(tce.getId());
        update();
    }

    public TestCaseExecutionServer() {
    }

    /**
     * Add a Test Case to this execution.
     *
     * @param tc Test Case to add
     */
    public void addTestCase(TestCase tc) {
        try {
            ExecutionStepJpaController econtroller
                    = new ExecutionStepJpaController(DataBaseManager
                            .getEntityManagerFactory());
            tc.getStepList().forEach((s) -> {
                ExecutionStep es = new ExecutionStep(getId(),
                        s.getStepPK().getId(), s.getStepPK().getTestCaseId());
                es.setStep(s);
                es.setStepHistory(s.getHistoryList()
                        .get(s.getHistoryList().size() - 1));
                es.setTestCaseExecution(getEntity());
                try {
                    econtroller.create(es);
                    getExecutionStepList().add(es);
                }
                catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            });
            write2DB();
        }
        catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public int write2DB() throws Exception {
        TestCaseExecutionJpaController controller
                = new TestCaseExecutionJpaController(DataBaseManager
                        .getEntityManagerFactory());
        TestCaseExecution tce;
        if (getId() == null) {
            //New one
            tce = new TestCaseExecution();
            update(tce, this);
            controller.create(tce);
            setId(tce.getId());
        } else {
            tce = controller.findTestCaseExecution(getId());
            update(tce, this);
            controller.edit(tce);
        }
        update();
        return getId();
    }

    @Override
    public TestCaseExecution getEntity() {
        return new TestCaseExecutionJpaController(DataBaseManager
                .getEntityManagerFactory()).findTestCaseExecution(getId());
    }

    @Override
    public void update(TestCaseExecution target, TestCaseExecution source) {
        target.setConclusion(source.getConclusion());
        target.setId(source.getId());
        target.setScope(source.getScope());
        target.setName(source.getName());
        if (target.getExecutionStepList() == null) {
            target.setExecutionStepList(new ArrayList<>());
        } else {
            target.getExecutionStepList().clear();
        }
        if (source.getExecutionStepList() != null) {
            source.getExecutionStepList().forEach((es) -> {
                target.getExecutionStepList().add(es);
            });
        }
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    /**
     * Add a complete Test Project to the execution.
     *
     * @param tp Test Project to add
     */
    public void addTestProject(TestProject tp) {
        tp.getTestPlanList().forEach((plan) -> {
            addTestPlan(plan);
        });
    }

    /**
     * Add a complete Test Plan to the execution.
     *
     * @param plan Test Plan to add
     */
    private void addTestPlan(TestPlan plan) {
        plan.getTestCaseList().forEach((tc) -> {
            addTestCase(tc);
        });
    }

    /**
     * List of Executions for the provided project.
     *
     * @param p project to look into.
     * @return List of Executions for the provided project.
     */
    public static List<TestCaseExecution> getExecutions(Project p) {
        List<TestCaseExecution> results = new ArrayList<>();
        p.getTestProjectList().forEach(tp -> {
            tp.getTestPlanList().forEach(plan -> {
                plan.getTestCaseList().forEach(tc -> {
                    tc.getStepList().forEach(s -> {
                        s.getExecutionStepList().forEach(es -> {
                            results.add(es.getTestCaseExecution());
                        });
                    });
                });
            });
        });
        return results;
    }

    public void removeTestCase(TestCase tc) throws Exception {
        List<ExecutionStep> toDelete = new ArrayList<>();
        getExecutionStepList().forEach(es -> {
            if (Objects.equals(es.getStep().getTestCase().getTestCasePK(),
                     tc.getTestCasePK())) {
                //Same test case
                toDelete.add(es);
            }
        });
        getExecutionStepList().removeAll(toDelete);
        ExecutionStepJpaController c
                = new ExecutionStepJpaController(DataBaseManager
                        .getEntityManagerFactory());
        toDelete.forEach(es -> {
            try {
                ExecutionStepServer ess = new ExecutionStepServer(es);
                ess.getExecutionStepHasAttachmentList().forEach(att -> {
                    try {
                        ess.removeAttachment(att.getAttachment());
                    }
                    catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                });
                ess.getExecutionStepHasIssueList().forEach(issue -> {
                    try {
                        ess.removeIssue(issue.getIssue());
                    }
                    catch (NonexistentEntityException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                });
                ess.getEntity();
                c.destroy(ess.getExecutionStepPK());
            }
            catch (IllegalOrphanException | NonexistentEntityException ex) {
                Exceptions.printStackTrace(ex);
            }
        });
        write2DB();
    }
}
