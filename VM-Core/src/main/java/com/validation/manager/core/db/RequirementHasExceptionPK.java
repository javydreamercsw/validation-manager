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
public class RequirementHasExceptionPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "vm_exception_reporter_id")
    private int vmExceptionReporterId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_version")
    private int requirementVersion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "vm_exception_id")
    private int vmExceptionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_id")
    private int requirementId;

    public RequirementHasExceptionPK() {
    }

    public RequirementHasExceptionPK(int requirementId, int exceptionId,
            int exceptionReporterId) {
        this.requirementId = requirementId;
        this.vmExceptionId = exceptionId;
        this.vmExceptionReporterId = exceptionReporterId;
    }

    public int getVmExceptionReporterId() {
        return vmExceptionReporterId;
    }

    public void setVmExceptionReporterId(int vmExceptionReporterId) {
        this.vmExceptionReporterId = vmExceptionReporterId;
    }

    public int getRequirementVersion() {
        return requirementVersion;
    }

    public void setRequirementVersion(int requirementVersion) {
        this.requirementVersion = requirementVersion;
    }

    public int getVmExceptionId() {
        return vmExceptionId;
    }

    public void setVmExceptionId(int vmExceptionId) {
        this.vmExceptionId = vmExceptionId;
    }

    public int getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(int requirementId) {
        this.requirementId = requirementId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) vmExceptionReporterId;
        hash += (int) requirementVersion;
        hash += (int) vmExceptionId;
        hash += (int) requirementId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequirementHasExceptionPK)) {
            return false;
        }
        RequirementHasExceptionPK other = (RequirementHasExceptionPK) object;
        if (this.vmExceptionReporterId != other.vmExceptionReporterId) {
            return false;
        }
        if (this.requirementVersion != other.requirementVersion) {
            return false;
        }
        if (this.vmExceptionId != other.vmExceptionId) {
            return false;
        }
        return this.requirementId == other.requirementId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementHasExceptionPK[ vmExceptionReporterId=" + vmExceptionReporterId + ", requirementVersion=" + requirementVersion + ", vmExceptionId=" + vmExceptionId + ", requirementId=" + requirementId + " ]";
    }

}
