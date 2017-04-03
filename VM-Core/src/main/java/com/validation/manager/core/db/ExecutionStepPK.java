/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class ExecutionStepPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "test_case_execution_id")
    private int testCaseExecutionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_id")
    private int stepId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_test_case_id")
    private int stepTestCaseId;

    public ExecutionStepPK() {
    }

    public ExecutionStepPK(int testCaseExecutionId, int stepId, int stepTestCaseId) {
        this.testCaseExecutionId = testCaseExecutionId;
        this.stepId = stepId;
        this.stepTestCaseId = stepTestCaseId;
    }

    public int getTestCaseExecutionId() {
        return testCaseExecutionId;
    }

    public void setTestCaseExecutionId(int testCaseExecutionId) {
        this.testCaseExecutionId = testCaseExecutionId;
    }

    public int getStepId() {
        return stepId;
    }

    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public int getStepTestCaseId() {
        return stepTestCaseId;
    }

    public void setStepTestCaseId(int stepTestCaseId) {
        this.stepTestCaseId = stepTestCaseId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.testCaseExecutionId;
        hash = 31 * hash + this.stepId;
        hash = 31 * hash + this.stepTestCaseId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExecutionStepPK other = (ExecutionStepPK) obj;
        if (this.testCaseExecutionId != other.testCaseExecutionId) {
            return false;
        }
        if (this.stepId != other.stepId) {
            return false;
        }
        return this.stepTestCaseId == other.stepTestCaseId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExecutionStepPK[ testCaseExecutionId="
                + testCaseExecutionId + ", stepId=" + stepId + ", stepTestCaseId="
                + stepTestCaseId + " ]";
    }

}
