/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "execution_step")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExecutionStep.findAll",
            query = "SELECT e FROM ExecutionStep e")
    , @NamedQuery(name = "ExecutionStep.findByTestCaseExecutionId",
            query = "SELECT e FROM ExecutionStep e WHERE e.executionStepPK.testCaseExecutionId = :testCaseExecutionId")
    , @NamedQuery(name = "ExecutionStep.findByStepId",
            query = "SELECT e FROM ExecutionStep e WHERE e.executionStepPK.stepId = :stepId")
    , @NamedQuery(name = "ExecutionStep.findByStepTestCaseId",
            query = "SELECT e FROM ExecutionStep e WHERE e.executionStepPK.stepTestCaseId = :stepTestCaseId")
    , @NamedQuery(name = "ExecutionStep.findByExecutionTime",
            query = "SELECT e FROM ExecutionStep e WHERE e.executionTime = :executionTime")
    , @NamedQuery(name = "ExecutionStep.findByExecutionStart",
            query = "SELECT e FROM ExecutionStep e WHERE e.executionStart = :executionStart")
    , @NamedQuery(name = "ExecutionStep.findByExecutionEnd",
            query = "SELECT e FROM ExecutionStep e WHERE e.executionEnd = :executionEnd")
    , @NamedQuery(name = "ExecutionStep.findByAssignedTime",
            query = "SELECT e FROM ExecutionStep e WHERE e.assignedTime = :assignedTime")
    , @NamedQuery(name = "ExecutionStep.findByLocked",
            query = "SELECT e FROM ExecutionStep e WHERE e.locked = :locked")
    , @NamedQuery(name = "ExecutionStep.findByReviewed",
            query = "SELECT e FROM ExecutionStep e WHERE e.reviewed = :reviewed")})
public class ExecutionStep implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ExecutionStepPK executionStepPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "execution_time")
    private Double executionTime;
    @Lob
    @Size(max = 2_147_483_647)
    @Column(name = "comment")
    private String comment;
    @Column(name = "execution_start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date executionStart;
    @Column(name = "execution_end")
    @Temporal(TemporalType.TIMESTAMP)
    private Date executionEnd;
    @Column(name = "review_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reviewDate;
    @Column(name = "assigned_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignedTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "locked")
    private boolean locked;
    @Basic(optional = false)
    @NotNull
    @Column(name = "reviewed")
    private boolean reviewed;
    @JoinColumn(name = "result_id", referencedColumnName = "id")
    @ManyToOne
    private ExecutionResult resultId;
    @JoinColumn(name = "review_result_id", referencedColumnName = "id")
    @ManyToOne
    private ReviewResult reviewResultId;
    @JoinColumn(name = "assignee", referencedColumnName = "id")
    @ManyToOne
    private VmUser assignee;
    @JoinColumn(name = "assigner", referencedColumnName = "id")
    @ManyToOne
    private VmUser assigner;
    @JoinColumn(name = "reviewer", referencedColumnName = "id")
    @ManyToOne
    private VmUser reviewer;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "executionStep")
    private List<ExecutionStepHasIssue> executionStepHasIssueList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "executionStep")
    private List<ExecutionStepHasVmUser> executionStepHasVmUserList;
    @JoinTable(name = "execution_step_has_history", joinColumns = {
        @JoinColumn(name = "execution_step_test_case_execution_id",
                referencedColumnName = "test_case_execution_id")
        , @JoinColumn(name = "execution_step_step_id",
                referencedColumnName = "step_id")
        , @JoinColumn(name = "execution_step_step_test_case_id",
                referencedColumnName = "step_test_case_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "history_id", referencedColumnName = "id")})
    @ManyToMany
    private List<History> historyList;
    @JoinColumn(name = "step_history", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private History stepHistory;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "executionStep")
    private List<ExecutionStepAnswer> executionStepAnswerList;

    public ExecutionStep() {
    }

    public ExecutionStep(ExecutionStepPK executionStepPK) {
        this.executionStepPK = executionStepPK;
    }

    public ExecutionStep(ExecutionStepPK executionStepPK, boolean locked,
            boolean reviewed) {
        this.executionStepPK = executionStepPK;
        this.locked = locked;
        this.reviewed = reviewed;
    }

    public ExecutionStep(int testCaseExecutionId, int stepId,
            int stepTestCaseId) {
        this.executionStepPK = new ExecutionStepPK(testCaseExecutionId,
                stepId, stepTestCaseId);
    }

    public ExecutionStepPK getExecutionStepPK() {
        return executionStepPK;
    }

    public void setExecutionStepPK(ExecutionStepPK executionStepPK) {
        this.executionStepPK = executionStepPK;
    }

    public Double getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Double executionTime) {
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

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean getReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    public ExecutionResult getResultId() {
        return resultId;
    }

    public void setResultId(ExecutionResult resultId) {
        this.resultId = resultId;
    }

    public ReviewResult getReviewResultId() {
        return reviewResultId;
    }

    public void setReviewResultId(ReviewResult reviewResultId) {
        this.reviewResultId = reviewResultId;
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

    public void setExecutionStepHasAttachmentList(List<ExecutionStepHasAttachment> esha) {
        this.executionStepHasAttachmentList = esha;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (executionStepPK != null ? executionStepPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof ExecutionStep)) {
            return false;
        }
        ExecutionStep other = (ExecutionStep) object;
        return !((this.executionStepPK == null && other.executionStepPK != null)
                || (this.executionStepPK != null
                && !this.executionStepPK.equals(other.executionStepPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExecutionStep[ executionStepPK="
                + executionStepPK + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStepHasIssue> getExecutionStepHasIssueList() {
        return executionStepHasIssueList;
    }

    public void setExecutionStepHasIssueList(List<ExecutionStepHasIssue> eshi) {
        this.executionStepHasIssueList = eshi;
    }

    /**
     * @return the reviewDate
     */
    public Date getReviewDate() {
        return reviewDate;
    }

    /**
     * @param reviewDate the reviewDate to set
     */
    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    /**
     * @return the reviewer
     */
    public VmUser getReviewer() {
        return reviewer;
    }

    /**
     * @param reviewer the reviewer to set
     */
    public void setReviewer(VmUser reviewer) {
        this.reviewer = reviewer;
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStepHasVmUser> getExecutionStepHasVmUserList() {
        return executionStepHasVmUserList;
    }

    public void setExecutionStepHasVmUserList(List<ExecutionStepHasVmUser> eshu) {
        this.executionStepHasVmUserList = eshu;
    }

    @XmlTransient
    @JsonIgnore
    public List<History> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }

    public History getStepHistory() {
        return stepHistory;
    }

    public void setStepHistory(History stepHistory) {
        this.stepHistory = stepHistory;
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStepAnswer> getExecutionStepAnswerList() {
        return executionStepAnswerList;
    }

    public void setExecutionStepAnswerList(List<ExecutionStepAnswer> esa) {
        this.executionStepAnswerList = esa;
    }
}
