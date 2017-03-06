/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "execution_step")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExecutionStep.findAll", query = "SELECT e FROM ExecutionStep e")
    , @NamedQuery(name = "ExecutionStep.findByTestCaseExecutionId", query = "SELECT e FROM ExecutionStep e WHERE e.executionStepPK.testCaseExecutionId = :testCaseExecutionId")
    , @NamedQuery(name = "ExecutionStep.findByStepId", query = "SELECT e FROM ExecutionStep e WHERE e.executionStepPK.stepId = :stepId")
    , @NamedQuery(name = "ExecutionStep.findByStepTestCaseId", query = "SELECT e FROM ExecutionStep e WHERE e.executionStepPK.stepTestCaseId = :stepTestCaseId")
    , @NamedQuery(name = "ExecutionStep.findByExecutionTime", query = "SELECT e FROM ExecutionStep e WHERE e.executionTime = :executionTime")
    , @NamedQuery(name = "ExecutionStep.findByComment", query = "SELECT e FROM ExecutionStep e WHERE e.comment = :comment")})
public class ExecutionStep implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ExecutionStepPK executionStepPK;
    @Column(name = "execution_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date executionTime;
    @Size(max = 45)
    @Column(name = "comment")
    private String comment;
    @JoinColumn(name = "result_id", referencedColumnName = "id")
    @ManyToOne
    private ExecutionResult resultId;
    @JoinColumn(name = "vm_user_id", referencedColumnName = "id")
    @ManyToOne
    private VmUser vmUserId;
    @JoinColumns({
        @JoinColumn(name = "step_id", referencedColumnName = "id", insertable = false, updatable = false)
        , @JoinColumn(name = "step_test_case_id", referencedColumnName = "test_case_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Step step;
    @JoinColumn(name = "test_case_execution_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TestCaseExecution testCaseExecution;

    public ExecutionStep() {
    }

    public ExecutionStep(ExecutionStepPK executionStepPK) {
        this.executionStepPK = executionStepPK;
    }

    public ExecutionStep(int testCaseExecutionId, int stepId, int stepTestCaseId) {
        this.executionStepPK = new ExecutionStepPK(testCaseExecutionId, stepId, stepTestCaseId);
    }

    public ExecutionStepPK getExecutionStepPK() {
        return executionStepPK;
    }

    public void setExecutionStepPK(ExecutionStepPK executionStepPK) {
        this.executionStepPK = executionStepPK;
    }

    public Date getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Date executionTime) {
        this.executionTime = executionTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ExecutionResult getResultId() {
        return resultId;
    }

    public void setResultId(ExecutionResult resultId) {
        this.resultId = resultId;
    }

    public VmUser getVmUserId() {
        return vmUserId;
    }

    public void setVmUserId(VmUser vmUserId) {
        this.vmUserId = vmUserId;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public TestCaseExecution getTestCaseExecution() {
        return testCaseExecution;
    }

    public void setTestCaseExecution(TestCaseExecution testCaseExecution) {
        this.testCaseExecution = testCaseExecution;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (executionStepPK != null ? executionStepPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExecutionStep)) {
            return false;
        }
        ExecutionStep other = (ExecutionStep) object;
        if ((this.executionStepPK == null && other.executionStepPK != null) || (this.executionStepPK != null && !this.executionStepPK.equals(other.executionStepPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExecutionStep[ executionStepPK=" + executionStepPK + " ]";
    }

}
