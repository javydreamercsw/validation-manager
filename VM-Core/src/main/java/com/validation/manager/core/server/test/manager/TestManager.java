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
import com.validation.manager.core.db.controller.ExecutionStepJpaController;
import com.validation.manager.core.db.controller.StepJpaController;
import com.validation.manager.core.db.controller.TestCaseExecutionJpaController;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestManager {

    /**
     * Add a complete Test Plan
     *
     * @param plans Test Plans to add
     * @return created execution.
     */
    public TestCaseExecution createTestExecutionFromPlan(List<TestPlan> plans) {

        return null;
    }

    /**
     * Add a complete Test Case
     *
     * @param testCases Test Case to add
     * @return created execution.
     */
    public TestCaseExecution createTestExecutionFromTestCase(List<TestCase> testCases) {

        return null;
    }

    /**
     * Add a complete Test Case
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
            Exceptions.printStackTrace(ex);
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
                Exceptions.printStackTrace(ex);
            }
        });
        return result;
    }
}
