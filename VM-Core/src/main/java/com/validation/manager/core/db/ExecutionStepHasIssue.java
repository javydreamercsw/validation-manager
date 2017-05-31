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
import java.util.List;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "execution_step_has_issue")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExecutionStepHasIssue.findAll",
            query = "SELECT e FROM ExecutionStepHasIssue e")
    , @NamedQuery(name = "ExecutionStepHasIssue.findByExecutionStepTestCaseExecutionId",
            query = "SELECT e FROM ExecutionStepHasIssue e WHERE "
            + "e.executionStepHasIssuePK.executionStepTestCaseExecutionId = "
            + ":executionStepTestCaseExecutionId")
    , @NamedQuery(name = "ExecutionStepHasIssue.findByExecutionStepStepId",
            query = "SELECT e FROM ExecutionStepHasIssue e WHERE "
            + "e.executionStepHasIssuePK.executionStepStepId = :executionStepStepId")
    , @NamedQuery(name = "ExecutionStepHasIssue.findByExecutionStepStepTestCaseId",
            query = "SELECT e FROM ExecutionStepHasIssue e WHERE "
            + "e.executionStepHasIssuePK.executionStepStepTestCaseId = "
            + ":executionStepStepTestCaseId")
    , @NamedQuery(name = "ExecutionStepHasIssue.findByIssueId",
            query = "SELECT e FROM ExecutionStepHasIssue e WHERE "
            + "e.executionStepHasIssuePK.issueId = :issueId")
    , @NamedQuery(name = "ExecutionStepHasIssue.findByIssueIssueTypeId",
            query = "SELECT e FROM ExecutionStepHasIssue e WHERE "
            + "e.executionStepHasIssuePK.issueIssueTypeId = "
            + ":issueIssueTypeId")})
public class ExecutionStepHasIssue implements Serializable {

    private static final long serialVersionUID = 1L;
    @JoinTable(name = "vm_user_has_execution_step_has_issue", joinColumns = {
        @JoinColumn(name = "execution_step_has_issue_execution_step_test_case_execution_id",
                referencedColumnName = "execution_step_test_case_execution_id")
        , @JoinColumn(name = "execution_step_has_issue_execution_step_step_id",
                referencedColumnName = "execution_step_step_id")
        , @JoinColumn(name = "execution_step_has_issue_execution_step_step_test_case_id",
                referencedColumnName = "execution_step_step_test_case_id")
        , @JoinColumn(name = "execution_step_has_issue_issue_id",
                referencedColumnName = "issue_id")
        , @JoinColumn(name = "execution_step_has_issue_issue_issue_type_id",
                referencedColumnName = "issue_issue_type_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "vm_user_id", referencedColumnName = "id")})
    @ManyToMany
    private List<VmUser> vmUserList;

    @EmbeddedId
    protected ExecutionStepHasIssuePK executionStepHasIssuePK;
    @JoinColumns({
        @JoinColumn(name = "execution_step_test_case_execution_id",
                referencedColumnName = "test_case_execution_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "execution_step_step_id",
                referencedColumnName = "step_id", insertable = false,
                updatable = false)
        , @JoinColumn(name = "execution_step_step_test_case_id",
                referencedColumnName = "step_test_case_id", insertable = false,
                updatable = false)})
    @ManyToOne(optional = false)
    private ExecutionStep executionStep;
    @JoinColumns({
        @JoinColumn(name = "issue_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "issue_issue_type_id",
                referencedColumnName = "issue_type_id", insertable = false,
                updatable = false)})
    @ManyToOne(optional = false)
    private Issue issue;

    public ExecutionStepHasIssue() {
    }

    public ExecutionStepHasIssue(ExecutionStepHasIssuePK executionStepHasIssuePK) {
        this.executionStepHasIssuePK = executionStepHasIssuePK;
    }

    public ExecutionStepHasIssue(int executionStepTestCaseExecutionId,
            int executionStepStepId, int executionStepStepTestCaseId,
            int issueId, int issueIssueTypeId) {
        this.executionStepHasIssuePK
                = new ExecutionStepHasIssuePK(executionStepTestCaseExecutionId,
                        executionStepStepId, executionStepStepTestCaseId,
                        issueId, issueIssueTypeId);
    }

    public ExecutionStepHasIssuePK getExecutionStepHasIssuePK() {
        return executionStepHasIssuePK;
    }

    public void setExecutionStepHasIssuePK(ExecutionStepHasIssuePK executionStepHasIssuePK) {
        this.executionStepHasIssuePK = executionStepHasIssuePK;
    }

    @XmlTransient
    @JsonIgnore
    public List<VmUser> getVmUserList() {
        return vmUserList;
    }

    public void setVmUserList(List<VmUser> vmUserList) {
        this.vmUserList = vmUserList;
    }

    public ExecutionStep getExecutionStep() {
        return executionStep;
    }

    public void setExecutionStep(ExecutionStep executionStep) {
        this.executionStep = executionStep;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (executionStepHasIssuePK != null ? executionStepHasIssuePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ExecutionStepHasIssue)) {
            return false;
        }
        ExecutionStepHasIssue other = (ExecutionStepHasIssue) object;
        return !((this.executionStepHasIssuePK == null
                && other.executionStepHasIssuePK != null)
                || (this.executionStepHasIssuePK != null
                && !this.executionStepHasIssuePK.equals(other.executionStepHasIssuePK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExecutionStepHasIssue[ executionStepHasIssuePK="
                + executionStepHasIssuePK + " ]";
    }
}
