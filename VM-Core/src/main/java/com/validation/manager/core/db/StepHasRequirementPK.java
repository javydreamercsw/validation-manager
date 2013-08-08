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
public class StepHasRequirementPK implements Serializable {
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
    @Column(name = "requirement_id")
    private int requirementId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_version")
    private int requirementVersion;

    public StepHasRequirementPK() {
    }

    public StepHasRequirementPK(int stepId, int stepTestCaseId, int stepTestCaseTestId, int requirementId, int requirementVersion) {
        this.stepId = stepId;
        this.stepTestCaseId = stepTestCaseId;
        this.stepTestCaseTestId = stepTestCaseTestId;
        this.requirementId = requirementId;
        this.requirementVersion = requirementVersion;
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

    public int getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(int requirementId) {
        this.requirementId = requirementId;
    }

    public int getRequirementVersion() {
        return requirementVersion;
    }

    public void setRequirementVersion(int requirementVersion) {
        this.requirementVersion = requirementVersion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) stepId;
        hash += (int) stepTestCaseId;
        hash += (int) stepTestCaseTestId;
        hash += (int) requirementId;
        hash += (int) requirementVersion;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StepHasRequirementPK)) {
            return false;
        }
        StepHasRequirementPK other = (StepHasRequirementPK) object;
        if (this.stepId != other.stepId) {
            return false;
        }
        if (this.stepTestCaseId != other.stepTestCaseId) {
            return false;
        }
        if (this.stepTestCaseTestId != other.stepTestCaseTestId) {
            return false;
        }
        if (this.requirementId != other.requirementId) {
            return false;
        }
        if (this.requirementVersion != other.requirementVersion) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.StepHasRequirementPK[ stepId=" + stepId + ", stepTestCaseId=" + stepTestCaseId + ", stepTestCaseTestId=" + stepTestCaseTestId + ", requirementId=" + requirementId + ", requirementVersion=" + requirementVersion + " ]";
    }
    
}
