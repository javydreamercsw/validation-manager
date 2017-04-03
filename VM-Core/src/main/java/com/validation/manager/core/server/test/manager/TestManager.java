/*
 * This class manages the testing management operations.
 */
package com.validation.manager.core.server.test.manager;

import com.validation.manager.core.DataBaseManager;
import com.validation.manager.core.db.ExecutionStep;
import com.validation.manager.core.db.Step;
import com.validation.manager.core.db.TestCase;
import com.validation.manager.core.db.TestCaseExecution;
import com.validation.manager.core.db.TestPlan;
import com.validation.manager.core.db.VmUser;
import com.validation.manager.core.db.controller.ExecutionStepJpaController;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.db.controller.TestCaseExecutionJpaController;
import com.validation.manager.core.server.core.VMUserServer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestManager {

    private static final Logger LOG
            = Logger.getLogger(TestManager.class.getSimpleName());

    /**
     * Add a complete Test Plan
     *
     * @param plans Test Plans to add
     * @return created execution.
     */
    public TestCaseExecution createTestExecutionFromPlan(List<TestPlan> plans) {
        ArrayList<Step> steps = new ArrayList<>();
        plans.forEach((tp) -> {
            tp.getTestCaseList().forEach((tc) -> {
                tc.getStepList().forEach((s) -> {
                    steps.add(s);
                });
            });
        });
        return createTestExecutionFromStep(steps);
    }

    /**
     * Add a complete Test Case
     *
     * @param testCases Test Case to add
     * @return created execution.
     */
    public TestCaseExecution createTestExecutionFromTestCase(List<TestCase> testCases) {
        ArrayList<Step> steps = new ArrayList<>();
        testCases.forEach((tc) -> {
            tc.getStepList().forEach((s) -> {
                steps.add(s);
            });
        });
        return createTestExecutionFromStep(steps);
    }

    /**
     * Add a complete Test Case to the execution.
     *
     * @param steps Steps to add
     * @return created execution.
     */
    public TestCaseExecution createTestExecutionFromStep(List<Step> steps) {
        TestCaseExecution result = new TestCaseExecution();
        TestCaseExecutionJpaController controller
                = new TestCaseExecutionJpaController(DataBaseManager
                        .getEntityManagerFactory());
        ExecutionStepJpaController c2
                = new ExecutionStepJpaController(DataBaseManager
                        .getEntityManagerFactory());
        StepJpaController c3 = new StepJpaController(DataBaseManager
                .getEntityManagerFactory());
        try {
            controller.create(result);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        steps.forEach((s) -> {
            try {
                ExecutionStep es = new ExecutionStep();
                es.setStep(s);
                es.setTestCaseExecution(result);
                c2.create(es);
                s.getExecutionStepList().add(es);
                c3.edit(s);
                result.getExecutionStepList().add(es);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        });
        return result;
    }

    /**
     * Assign user to the specified steps.
     *
     * @param assignee User to be assigned to.
     * @param assigner User assigning the step.
     * @param steps Steps to assign.
     * @throws java.lang.Exception
     */
    public void assignUser(VmUser assignee, VmUser assigner,
            List<ExecutionStep> steps) throws Exception {
        ExecutionStepJpaController controller
                = new ExecutionStepJpaController(DataBaseManager
                        .getEntityManagerFactory());
        VMUserServer assignees = new VMUserServer(assignee);
        for (ExecutionStep es : steps) {
            if (alreadyAssigned(assignees, es)) {
                LOG.log(Level.INFO,
                        "Skipping already existing step: {0}", es.toString());
            } else {
                es.setVmUserId(assignee);
                es.setAssignedTime(new Date());
                controller.edit(es);
                //Add assigner
                VMUserServer assigners = new VMUserServer(assigner);
                assigners.getExecutionStepCollection().add(es);
                assigners.write2DB();
            }
        }
    }

    public boolean alreadyAssigned(VmUser user, ExecutionStep es) {
        return user.getExecutionSteps().stream().anyMatch((e)
                -> (e.getExecutionStepPK().equals(es.getExecutionStepPK())));
    }

    /**
     * Update the DAO
     *
     * @param r
     */
    public void update(TestCaseExecution r) {
        TestCaseExecutionJpaController controller
                = new TestCaseExecutionJpaController(DataBaseManager
                        .getEntityManagerFactory());
        update(r, controller.findTestCaseExecution(r.getId()));
    }

    private void update(TestCaseExecution target,
            TestCaseExecution source) {
        target.setExecutionStepList(source.getExecutionStepList());
    }
}
