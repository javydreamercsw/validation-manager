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
public class ExceptionHasInvestigationPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "exception_id")
    private int exceptionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "exception_reporter_id")
    private int exceptionReporterId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "investigation_id")
    private int investigationId;

    public ExceptionHasInvestigationPK() {
    }

    public ExceptionHasInvestigationPK(int exceptionId,
            int exceptionReporterId, int investigationId) {
        this.exceptionId = exceptionId;
        this.exceptionReporterId = exceptionReporterId;
        this.investigationId = investigationId;
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

    public int getInvestigationId() {
        return investigationId;
    }

    public void setInvestigationId(int investigationId) {
        this.investigationId = investigationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) exceptionId;
        hash += (int) exceptionReporterId;
        hash += (int) investigationId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ExceptionHasInvestigationPK)) {
            return false;
        }
        ExceptionHasInvestigationPK other = (ExceptionHasInvestigationPK) object;
        if (this.exceptionId != other.exceptionId) {
            return false;
        }
        if (this.exceptionReporterId != other.exceptionReporterId) {
            return false;
        }
        return this.investigationId == other.investigationId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExceptionHasInvestigationPK[ "
                + "exceptionId=" + exceptionId + ", exceptionReporterId="
                + exceptionReporterId + ", investigationId="
                + investigationId + " ]";
    }
}
