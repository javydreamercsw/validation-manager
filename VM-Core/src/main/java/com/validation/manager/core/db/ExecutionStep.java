package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "execution_step")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExecutionStep.findAll",
            query = "SELECT e FROM ExecutionStep e")
    , @NamedQuery(name = "ExecutionStep.findByTestCaseExecutionId",
            query = "SELECT e FROM ExecutionStep e WHERE "
            + "e.executionStepPK.testCaseExecutionId "
            + "= :testCaseExecutionId")
    , @NamedQuery(name = "ExecutionStep.findByStepId",
            query = "SELECT e FROM ExecutionStep e WHERE "
            + "e.executionStepPK.stepId = :stepId")
    , @NamedQuery(name = "ExecutionStep.findByStepTestCaseId",
            query = "SELECT e FROM ExecutionStep e WHERE "
            + "e.executionStepPK.stepTestCaseId = :stepTestCaseId")
    , @NamedQuery(name = "ExecutionStep.findByExecutionTime",
            query = "SELECT e FROM ExecutionStep e WHERE "
            + "e.executionTime = :executionTime")
    , @NamedQuery(name = "ExecutionStep.findByComment",
            query = "SELECT e FROM ExecutionStep e WHERE "
            + "e.comment = :comment")})
public class ExecutionStep implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ExecutionStepPK executionStepPK;
    @Column(name = "execution_time")
    private long executionTime;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "comment")
    private String comment;
    @Column(name = "locked")
    private boolean locked = false;
    @Column(name = "execution_start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date executionStart;
    @Column(name = "execution_end")
    @Temporal(TemporalType.TIMESTAMP)
    private Date executionEnd;
    @Column(name = "assigned_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignedTime;
    @JoinTable(name = "execution_step_has_vm_exception", joinColumns = {
        @JoinColumn(name = "execution_step_test_case_execution_id",
                referencedColumnName = "test_case_execution_id")
        , @JoinColumn(name = "execution_step_step_id",
                referencedColumnName = "step_id")
        , @JoinColumn(name = "execution_step_step_test_case_id",
                referencedColumnName = "step_test_case_id")}, inverseJoinColumns = {
        @JoinColumn(name = "vm_exception_id", referencedColumnName = "id")
        , @JoinColumn(name = "vm_exception_reporter_id",
                referencedColumnName = "reporter_id")})
    @ManyToMany
    private List<VmException> vmExceptionList;
    @JoinColumn(name = "result_id", referencedColumnName = "id")
    @ManyToOne
    private ExecutionResult resultId;
    @JoinColumn(name = "assignee", referencedColumnName = "id")
    @ManyToOne
    private VmUser assignee;
    @JoinColumn(name = "assigner", referencedColumnName = "id")
    @ManyToOne
    private VmUser assigner;
    @JoinColumns({
        @JoinColumn(name = "step_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "step_test_case_id",
                referencedColumnName = "test_case_id", insertable = false,
                updatable = false)})
    @ManyToOne(optional = false)
    private Step step;
    @JoinColumn(name = "test_case_execution_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TestCaseExecution testCaseExecution;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "executionStep")
    private List<ExecutionStepHasAttachment> executionStepHasAttachmentList;

    public ExecutionStep() {
    }

    public ExecutionStep(ExecutionStepPK executionStepPK) {
        this.executionStepPK = executionStepPK;
    }

    public ExecutionStep(int testCaseExecutionId, int stepId, int stepTestCaseId) {
        this.executionStepPK = new ExecutionStepPK(testCaseExecutionId,
                stepId, stepTestCaseId);
    }

    public ExecutionStepPK getExecutionStepPK() {
        return executionStepPK;
    }

    public void setExecutionStepPK(ExecutionStepPK executionStepPK) {
        this.executionStepPK = executionStepPK;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getExecutionStart() {
        return executionStart;
    }

    public void setExecutionStart(Date executionStart) {
        this.executionStart = executionStart;
    }

    public Date getExecutionEnd() {
        return executionEnd;
    }

    public void setExecutionEnd(Date executionEnd) {
        this.executionEnd = executionEnd;
    }

    public Date getAssignedTime() {
        return assignedTime;
    }

    public void setAssignedTime(Date assignedTime) {
        this.assignedTime = assignedTime;
    }

    @XmlTransient
    @JsonIgnore
    public List<VmException> getVmExceptionList() {
        return vmExceptionList;
    }

    public void setVmExceptionList(List<VmException> vmExceptionList) {
        this.vmExceptionList = vmExceptionList;
    }

    public ExecutionResult getResultId() {
        return resultId;
    }

    public void setResultId(ExecutionResult resultId) {
        this.resultId = resultId;
    }

    public VmUser getAssignee() {
        return assignee;
    }

    public void setAssignee(VmUser assignee) {
        this.assignee = assignee;
    }

    public VmUser getAssigner() {
        return assigner;
    }

    public void setAssigner(VmUser assigner) {
        this.assigner = assigner;
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

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStepHasAttachment> getExecutionStepHasAttachmentList() {
        return executionStepHasAttachmentList;
    }

    public void setExecutionStepHasAttachmentList(List<ExecutionStepHasAttachment> executionStepHasAttachmentList) {
        this.executionStepHasAttachmentList = executionStepHasAttachmentList;
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
        return !((this.executionStepPK == null
                && other.executionStepPK != null)
                || (this.executionStepPK != null
                && !this.executionStepPK.equals(other.executionStepPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExecutionStep[ executionStepPK="
                + executionStepPK + " ]";
    }

    /**
     * @return the locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * @param locked the locked to set
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
