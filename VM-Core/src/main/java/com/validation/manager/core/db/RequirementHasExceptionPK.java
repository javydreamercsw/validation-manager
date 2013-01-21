/*
 * To change this template, choose Tools | Templates
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
public class RequirementHasExceptionPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_id")
    private int requirementId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "exception_id")
    private int exceptionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "exception_reporter_id")
    private int exceptionReporterId;

    public RequirementHasExceptionPK() {
    }

    public RequirementHasExceptionPK(int requirementId, int exceptionId, int exceptionReporterId) {
        this.requirementId = requirementId;
        this.exceptionId = exceptionId;
        this.exceptionReporterId = exceptionReporterId;
    }

    public int getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(int requirementId) {
        this.requirementId = requirementId;
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
        hash += (int) requirementId;
        hash += (int) exceptionId;
        hash += (int) exceptionReporterId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequirementHasExceptionPK)) {
            return false;
        }
        RequirementHasExceptionPK other = (RequirementHasExceptionPK) object;
        if (this.requirementId != other.requirementId) {
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
        return "com.validation.manager.core.db.RequirementHasExceptionPK[ requirementId=" + requirementId + ", exceptionId=" + exceptionId + ", exceptionReporterId=" + exceptionReporterId + " ]";
    }
    
}
