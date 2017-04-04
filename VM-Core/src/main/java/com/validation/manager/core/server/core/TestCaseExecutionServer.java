package com.validation.manager.core.server.core;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.EntityServer;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.TestProject;
import com.validation.manager.core.db.controller.ExecutionStepJpaController;
import com.validation.manager.core.db.controller.TestCaseExecutionJpaController;
import java.util.ArrayList;
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
        TestCaseExecution tces = new TestCaseExecutionJpaController(DataBaseManager
                .getEntityManagerFactory()).findTestCaseExecution(id);
        update(TestCaseExecutionServer.this, tces);
    }

    public TestCaseExecutionServer(TestCaseExecution tce) {
        TestCaseExecution tces = new TestCaseExecutionJpaController(DataBaseManager
                .getEntityManagerFactory()).findTestCaseExecution(tce.getId());
        update(TestCaseExecutionServer.this, tces);
    }

    public TestCaseExecutionServer() {
    }

    /**
     * Add a Test Case to this execution.
     *
     * @param tc Test Case to add
     */
    public void addTestCase(TestCase tc) {
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
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        });
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
        if (target.getProjects() == null) {
            target.setProjects(new ArrayList<>());
        } else {
            target.getProjects().clear();
        }
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
        if (source.getProjects() != null) {
            source.getProjects().forEach((p) -> {
                target.getProjects().add(p);
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
}
