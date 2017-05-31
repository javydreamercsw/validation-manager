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
import javax.persistence.Basic;
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "execution_step_has_attachment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ExecutionStepHasAttachment.findAll",
            query = "SELECT e FROM ExecutionStepHasAttachment e")
    , @NamedQuery(name = "ExecutionStepHasAttachment.findByExecutionStepTestCaseExecutionId",
            query = "SELECT e FROM ExecutionStepHasAttachment e WHERE "
            + "e.executionStepHasAttachmentPK.executionStepTestCaseExecutionId ="
            + " :executionStepTestCaseExecutionId")
    , @NamedQuery(name = "ExecutionStepHasAttachment.findByExecutionStepStepId",
            query = "SELECT e FROM ExecutionStepHasAttachment e WHERE "
            + "e.executionStepHasAttachmentPK.executionStepStepId = :executionStepStepId")
    , @NamedQuery(name = "ExecutionStepHasAttachment.findByExecutionStepStepTestCaseId",
            query = "SELECT e FROM ExecutionStepHasAttachment e WHERE "
            + "e.executionStepHasAttachmentPK.executionStepStepTestCaseId = "
            + ":executionStepStepTestCaseId")
    , @NamedQuery(name = "ExecutionStepHasAttachment.findByAttachmentId",
            query = "SELECT e FROM ExecutionStepHasAttachment e WHERE "
            + "e.executionStepHasAttachmentPK.attachmentId = :attachmentId")
    , @NamedQuery(name = "ExecutionStepHasAttachment.findByAttachmentAttachmentTypeId",
            query = "SELECT e FROM ExecutionStepHasAttachment e WHERE "
            + "e.executionStepHasAttachmentPK.attachmentAttachmentTypeId ="
            + " :attachmentAttachmentTypeId")
    , @NamedQuery(name = "ExecutionStepHasAttachment.findByCreationTime",
            query = "SELECT e FROM ExecutionStepHasAttachment e WHERE "
            + "e.creationTime = :creationTime")})
public class ExecutionStepHasAttachment implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ExecutionStepHasAttachmentPK executionStepHasAttachmentPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    @JoinColumns({
        @JoinColumn(name = "attachment_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "attachment_attachment_type_id",
                referencedColumnName = "attachment_type_id", insertable = false,
                updatable = false)})
    @ManyToOne(optional = false)
    private Attachment attachment;
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

    public ExecutionStepHasAttachment() {
    }

    public ExecutionStepHasAttachment(ExecutionStepHasAttachmentPK executionStepHasAttachmentPK) {
        this.executionStepHasAttachmentPK = executionStepHasAttachmentPK;
    }

    public ExecutionStepHasAttachment(ExecutionStepHasAttachmentPK executionStepHasAttachmentPK,
            Date creationTime) {
        this.executionStepHasAttachmentPK = executionStepHasAttachmentPK;
        this.creationTime = creationTime;
    }

    public ExecutionStepHasAttachment(int executionStepTestCaseExecutionId,
            int executionStepStepId, int executionStepStepTestCaseId,
            int attachmentId, int attachmentAttachmentTypeId) {
        this.executionStepHasAttachmentPK
                = new ExecutionStepHasAttachmentPK(executionStepTestCaseExecutionId,
                        executionStepStepId, executionStepStepTestCaseId,
                        attachmentId, attachmentAttachmentTypeId);
    }

    public ExecutionStepHasAttachmentPK getExecutionStepHasAttachmentPK() {
        return executionStepHasAttachmentPK;
    }

    public void setExecutionStepHasAttachmentPK(ExecutionStepHasAttachmentPK executionStepHasAttachmentPK) {
        this.executionStepHasAttachmentPK = executionStepHasAttachmentPK;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
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
        hash += (executionStepHasAttachmentPK != null ? executionStepHasAttachmentPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof ExecutionStepHasAttachment)) {
            return false;
        }
        ExecutionStepHasAttachment other = (ExecutionStepHasAttachment) object;
        return !((this.executionStepHasAttachmentPK == null
                && other.executionStepHasAttachmentPK != null)
                || (this.executionStepHasAttachmentPK != null
                && !this.executionStepHasAttachmentPK
                        .equals(other.executionStepHasAttachmentPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.ExecutionStepHasAttachment[ "
                + "executionStepHasAttachmentPK="
                + executionStepHasAttachmentPK + " ]";
    }
}
