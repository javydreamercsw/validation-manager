/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "step_has_exception")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "StepHasException.findAll", query = "SELECT s FROM StepHasException s"),
    @NamedQuery(name = "StepHasException.findByStepId", query = "SELECT s FROM StepHasException s WHERE s.stepHasExceptionPK.stepId = :stepId"),
    @NamedQuery(name = "StepHasException.findByStepTestCaseId", query = "SELECT s FROM StepHasException s WHERE s.stepHasExceptionPK.stepTestCaseId = :stepTestCaseId"),
    @NamedQuery(name = "StepHasException.findByStepTestCaseTestId", query = "SELECT s FROM StepHasException s WHERE s.stepHasExceptionPK.stepTestCaseTestId = :stepTestCaseTestId"),
    @NamedQuery(name = "StepHasException.findByExceptionId", query = "SELECT s FROM StepHasException s WHERE s.stepHasExceptionPK.exceptionId = :exceptionId"),
    @NamedQuery(name = "StepHasException.findByExceptionReporterId", query = "SELECT s FROM StepHasException s WHERE s.stepHasExceptionPK.exceptionReporterId = :exceptionReporterId")})
public class StepHasException implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected StepHasExceptionPK stepHasExceptionPK;
    @JoinColumns({
        @JoinColumn(name = "step_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "step_test_case_id", referencedColumnName = "test_case_id", insertable = false, updatable = false),
        @JoinColumn(name = "step_test_case_test_id", referencedColumnName = "test_case_test_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Step step;
    @JoinColumns({
        @JoinColumn(name = "exception_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "exception_reporter_id", referencedColumnName = "reporter_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private VmException vmException;

    public StepHasException() {
    }

    public StepHasException(StepHasExceptionPK stepHasExceptionPK) {
        this.stepHasExceptionPK = stepHasExceptionPK;
    }

    public StepHasException(int stepId, int stepTestCaseId, int stepTestCaseTestId, int exceptionId, int exceptionReporterId) {
        this.stepHasExceptionPK = new StepHasExceptionPK(stepId, stepTestCaseId, stepTestCaseTestId, exceptionId, exceptionReporterId);
    }

    public StepHasExceptionPK getStepHasExceptionPK() {
        return stepHasExceptionPK;
    }

    public void setStepHasExceptionPK(StepHasExceptionPK stepHasExceptionPK) {
        this.stepHasExceptionPK = stepHasExceptionPK;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public VmException getVmException() {
        return vmException;
    }

    public void setVmException(VmException vmException) {
        this.vmException = vmException;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (stepHasExceptionPK != null ? stepHasExceptionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StepHasException)) {
            return false;
        }
        StepHasException other = (StepHasException) object;
        if ((this.stepHasExceptionPK == null && other.stepHasExceptionPK != null) || (this.stepHasExceptionPK != null && !this.stepHasExceptionPK.equals(other.stepHasExceptionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.StepHasException[ stepHasExceptionPK=" + stepHasExceptionPK + " ]";
    }
    
}
