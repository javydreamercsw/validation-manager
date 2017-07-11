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
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Embeddable
public class ExecutionStepAnswerPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "Execution_Step_Answer_IDGEN")
    @TableGenerator(name = "Execution_Step_Answer_IDGEN", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "execution_step_answer",
            initialValue = 1,
            allocationSize = 1)
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "execution_step_test_case_execution_id")
    private int executionStepTestCaseExecutionId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "execution_step_step_id")
    private int executionStepStepId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "execution_step_step_test_case_id")
    private int executionStepStepTestCaseId;

    public ExecutionStepAnswerPK() {
    }

    public ExecutionStepAnswerPK(int executionStepTestCaseExecutionId,
            int executionStepStepId, int executionStepStepTestCaseId) {
        this.executionStepTestCaseExecutionId = executionStepTestCaseExecutionId;
        this.executionStepStepId = executionStepStepId;
        this.executionStepStepTestCaseId = executionStepStepTestCaseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExecutionStepTestCaseExecutionId() {
        return executionStepTestCaseExecutionId;
    }

    public void setExecutionStepTestCaseExecutionId(int executionStepTestCaseExecutionId) {
        this.executionStepTestCaseExecutionId = executionStepTestCaseExecutionId;
    }

    public int getExecutionStepStepId() {
        return executionStepStepId;
    }

    public void setExecutionStepStepId(int executionStepStepId) {
        this.executionStepStepId = executionStepStepId;
    }

    public int getExecutionStepStepTestCaseId() {
        return executionStepStepTestCaseId;
    }

    public void setExecutionStepStepTestCaseId(int executionStepStepTestCaseId) {
        this.executionStepStepTestCaseId = executionStepStepTestCaseId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) executionStepTestCaseExecutionId;
        hash += (int) executionStepStepId;
        hash += (int) executionStepStepTestCaseId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExecutionStepAnswerPK)) {
            return false;
        }
        ExecutionStepAnswerPK other = (ExecutionStepAnswerPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.executionStepTestCaseExecutionId != other.executionStepTestCaseExecutionId) {
            return false;
        }
        if (this.executionStepStepId != other.executionStepStepId) {
            return false;
        }
        return this.executionStepStepTestCaseId == other.executionStepStepTestCaseId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExecutionStepAnswerPK[ id="
                + id + ", executionStepTestCaseExecutionId="
                + executionStepTestCaseExecutionId + ", executionStepStepId="
                + executionStepStepId + ", executionStepStepTestCaseId="
                + executionStepStepTestCaseId + " ]";
    }
}
