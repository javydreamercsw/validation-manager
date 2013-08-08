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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class StepHasExceptionPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_id")
    private int stepId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_test_case_id")
    private int stepTestCaseId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_test_case_test_id")
    private int stepTestCaseTestId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "exception_id")
    private int exceptionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "exception_reporter_id")
    private int exceptionReporterId;

    public StepHasExceptionPK() {
    }

    public StepHasExceptionPK(int stepId, int stepTestCaseId, int stepTestCaseTestId, int exceptionId, int exceptionReporterId) {
        this.stepId = stepId;
        this.stepTestCaseId = stepTestCaseId;
        this.stepTestCaseTestId = stepTestCaseTestId;
        this.exceptionId = exceptionId;
        this.exceptionReporterId = exceptionReporterId;
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

    public int getStepTestCaseTestId() {
        return stepTestCaseTestId;
    }

    public void setStepTestCaseTestId(int stepTestCaseTestId) {
        this.stepTestCaseTestId = stepTestCaseTestId;
    }

    public int getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(int exceptionId) {
        this.exceptionId = exceptionId;
    }

    public int getExceptionReporterId() {
        return exceptionReporterId;
    }

    public void setExceptionReporterId(int exceptionReporterId) {
        this.exceptionReporterId = exceptionReporterId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) stepId;
        hash += (int) stepTestCaseId;
        hash += (int) stepTestCaseTestId;
        hash += (int) exceptionId;
        hash += (int) exceptionReporterId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StepHasExceptionPK)) {
            return false;
        }
        StepHasExceptionPK other = (StepHasExceptionPK) object;
        if (this.stepId != other.stepId) {
            return false;
        }
        if (this.stepTestCaseId != other.stepTestCaseId) {
            return false;
        }
        if (this.stepTestCaseTestId != other.stepTestCaseTestId) {
            return false;
        }
        if (this.exceptionId != other.exceptionId) {
            return false;
        }
        if (this.exceptionReporterId != other.exceptionReporterId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.StepHasExceptionPK[ stepId=" + stepId + ", stepTestCaseId=" + stepTestCaseId + ", stepTestCaseTestId=" + stepTestCaseTestId + ", exceptionId=" + exceptionId + ", exceptionReporterId=" + exceptionReporterId + " ]";
    }
    
}
