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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "execution_step_answer")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExecutionStepAnswer.findAll",
            query = "SELECT e FROM ExecutionStepAnswer e")
    , @NamedQuery(name = "ExecutionStepAnswer.findById",
            query = "SELECT e FROM ExecutionStepAnswer e WHERE e.executionStepAnswerPK.id = :id")
    , @NamedQuery(name = "ExecutionStepAnswer.findByExecutionStepTestCaseExecutionId",
            query = "SELECT e FROM ExecutionStepAnswer e WHERE e.executionStepAnswerPK.executionStepTestCaseExecutionId = :executionStepTestCaseExecutionId")
    , @NamedQuery(name = "ExecutionStepAnswer.findByExecutionStepStepId",
            query = "SELECT e FROM ExecutionStepAnswer e WHERE e.executionStepAnswerPK.executionStepStepId = :executionStepStepId")
    , @NamedQuery(name = "ExecutionStepAnswer.findByExecutionStepStepTestCaseId",
            query = "SELECT e FROM ExecutionStepAnswer e WHERE e.executionStepAnswerPK.executionStepStepTestCaseId = :executionStepStepTestCaseId")
    , @NamedQuery(name = "ExecutionStepAnswer.findByFieldName",
            query = "SELECT e FROM ExecutionStepAnswer e WHERE e.fieldName = :fieldName")})
public class ExecutionStepAnswer implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ExecutionStepAnswerPK executionStepAnswerPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "field_name")
    private String fieldName;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "field_answer")
    private String fieldAnswer;
    @JoinColumns({
        @JoinColumn(name = "execution_step_test_case_execution_id",
                referencedColumnName = "test_case_execution_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "execution_step_step_id",
                referencedColumnName = "step_id", insertable = false,
                updatable = false)
        , @JoinColumn(name = "execution_step_step_test_case_id",
                referencedColumnName = "step_test_case_id",
                insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private ExecutionStep executionStep;

    public ExecutionStepAnswer() {
    }

    public ExecutionStepAnswer(ExecutionStepAnswerPK executionStepAnswerPK) {
        this.executionStepAnswerPK = executionStepAnswerPK;
    }

    public ExecutionStepAnswer(ExecutionStepAnswerPK executionStepAnswerPK,
            String fieldName, String fieldAnswer) {
        this.executionStepAnswerPK = executionStepAnswerPK;
        this.fieldName = fieldName;
        this.fieldAnswer = fieldAnswer;
    }

    public ExecutionStepAnswer(int executionStepTestCaseExecutionId,
            int executionStepStepId, int executionStepStepTestCaseId) {
        this.executionStepAnswerPK = new ExecutionStepAnswerPK(
                executionStepTestCaseExecutionId, executionStepStepId,
                executionStepStepTestCaseId);
    }

    public ExecutionStepAnswerPK getExecutionStepAnswerPK() {
        return executionStepAnswerPK;
    }

    public void setExecutionStepAnswerPK(ExecutionStepAnswerPK executionStepAnswerPK) {
        this.executionStepAnswerPK = executionStepAnswerPK;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldAnswer() {
        return fieldAnswer;
    }

    public void setFieldAnswer(String fieldAnswer) {
        this.fieldAnswer = fieldAnswer;
    }

    public ExecutionStep getExecutionStep() {
        return executionStep;
    }

    public void setExecutionStep(ExecutionStep executionStep) {
        this.executionStep = executionStep;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (executionStepAnswerPK != null ? executionStepAnswerPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExecutionStepAnswer)) {
            return false;
        }
        ExecutionStepAnswer other = (ExecutionStepAnswer) object;
        return !((this.executionStepAnswerPK == null
                && other.executionStepAnswerPK != null)
                || (this.executionStepAnswerPK != null
                && !this.executionStepAnswerPK.equals(other.executionStepAnswerPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExecutionStepAnswer[ "
                + "executionStepAnswerPK=" + executionStepAnswerPK + " ]";
    }
}
