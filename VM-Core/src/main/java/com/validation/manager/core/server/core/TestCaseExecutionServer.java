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
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
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
                ExecutionStep executionStep = new ExecutionStep(getId(),
                        s.getStepPK().getId(), s.getStepPK().getTestCaseId());
                executionStep.setStep(s);
                executionStep.setTestCaseExecution(getEntity());
                try {
                    econtroller.create(executionStep);
                    getExecutionStepList().add(executionStep);
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
        } else {
            tce = controller.findTestCaseExecution(getId());
            update(tce, this);
            controller.edit(tce);
        }
        update(this, tce);
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
                            TestCaseExecution tce = es.getTestCaseExecution();
                            results.add(tce);
                        });
                    });
                });
            });
        });
        return results;
    }
}
