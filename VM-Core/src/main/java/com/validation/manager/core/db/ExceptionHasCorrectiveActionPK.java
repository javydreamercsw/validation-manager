package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Embeddable
public class ExceptionHasCorrectiveActionPK implements Serializable {

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
    @Column(name = "corrective_action_id")
    private int correctiveActionId;

    public ExceptionHasCorrectiveActionPK() {
    }

    public ExceptionHasCorrectiveActionPK(int exceptionId,
            int exceptionReporterId, int correctiveActionId) {
        this.exceptionId = exceptionId;
        this.exceptionReporterId = exceptionReporterId;
        this.correctiveActionId = correctiveActionId;
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

    public int getCorrectiveActionId() {
        return correctiveActionId;
    }

    public void setCorrectiveActionId(int correctiveActionId) {
        this.correctiveActionId = correctiveActionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) exceptionId;
        hash += (int) exceptionReporterId;
        hash += (int) correctiveActionId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ExceptionHasCorrectiveActionPK)) {
            return false;
        }
        ExceptionHasCorrectiveActionPK other = (ExceptionHasCorrectiveActionPK) object;
        if (this.exceptionId != other.exceptionId) {
            return false;
        }
        if (this.exceptionReporterId != other.exceptionReporterId) {
            return false;
        }
        return this.correctiveActionId == other.correctiveActionId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExceptionHasCorrectiveActionPK[ "
                + "exceptionId=" + exceptionId + ", exceptionReporterId="
                + exceptionReporterId + ", correctiveActionId="
                + correctiveActionId + " ]";
    }
}
