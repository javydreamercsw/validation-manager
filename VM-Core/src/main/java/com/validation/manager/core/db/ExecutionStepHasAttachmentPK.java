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
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Embeddable
public class ExecutionStepHasAttachmentPK implements Serializable {

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
    @Basic(optional = false)
    @NotNull
    @Column(name = "attachment_id")
    private int attachmentId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "attachment_attachment_type_id")
    private int attachmentAttachmentTypeId;

    public ExecutionStepHasAttachmentPK() {
    }

    public ExecutionStepHasAttachmentPK(int executionStepTestCaseExecutionId,
            int executionStepStepId, int executionStepStepTestCaseId,
            int attachmentId, int attachmentAttachmentTypeId) {
        this.executionStepTestCaseExecutionId = executionStepTestCaseExecutionId;
        this.executionStepStepId = executionStepStepId;
        this.executionStepStepTestCaseId = executionStepStepTestCaseId;
        this.attachmentId = attachmentId;
        this.attachmentAttachmentTypeId = attachmentAttachmentTypeId;
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

    public int getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public int getAttachmentAttachmentTypeId() {
        return attachmentAttachmentTypeId;
    }

    public void setAttachmentAttachmentTypeId(int attachmentAttachmentTypeId) {
        this.attachmentAttachmentTypeId = attachmentAttachmentTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) executionStepTestCaseExecutionId;
        hash += (int) executionStepStepId;
        hash += (int) executionStepStepTestCaseId;
        hash += (int) attachmentId;
        hash += (int) attachmentAttachmentTypeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ExecutionStepHasAttachmentPK)) {
            return false;
        }
        ExecutionStepHasAttachmentPK other = (ExecutionStepHasAttachmentPK) object;
        if (this.executionStepTestCaseExecutionId != other.executionStepTestCaseExecutionId) {
            return false;
        }
        if (this.executionStepStepId != other.executionStepStepId) {
            return false;
        }
        if (this.executionStepStepTestCaseId != other.executionStepStepTestCaseId) {
            return false;
        }
        if (this.attachmentId != other.attachmentId) {
            return false;
        }
        return this.attachmentAttachmentTypeId == other.attachmentAttachmentTypeId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExecutionStepHasAttachmentPK[ "
                + "executionStepTestCaseExecutionId="
                + executionStepTestCaseExecutionId + ", executionStepStepId="
                + executionStepStepId + ", executionStepStepTestCaseId="
                + executionStepStepTestCaseId + ", attachmentId="
                + attachmentId + ", attachmentAttachmentTypeId="
                + attachmentAttachmentTypeId + " ]";
    }
}
