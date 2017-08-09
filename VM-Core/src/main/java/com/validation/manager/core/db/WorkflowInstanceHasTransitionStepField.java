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
@Table(name = "workflow_instance_has_transition_step_field")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WorkflowInstanceHasTransitionStepField.findAll",
            query = "SELECT w FROM WorkflowInstanceHasTransitionStepField w")
    , @NamedQuery(name = "WorkflowInstanceHasTransitionStepField.findByInstance",
            query = "SELECT w FROM WorkflowInstanceHasTransitionStepField w WHERE w.workflowInstanceHasTransitionStepFieldPK.instance = :instance")
    , @NamedQuery(name = "WorkflowInstanceHasTransitionStepField.findByWorkflow",
            query = "SELECT w FROM WorkflowInstanceHasTransitionStepField w WHERE w.workflowInstanceHasTransitionStepFieldPK.workflow = :workflow")
    , @NamedQuery(name = "WorkflowInstanceHasTransitionStepField.findBySourceStep",
            query = "SELECT w FROM WorkflowInstanceHasTransitionStepField w WHERE w.workflowInstanceHasTransitionStepFieldPK.sourceStep = :sourceStep")
    , @NamedQuery(name = "WorkflowInstanceHasTransitionStepField.findBySourceStepWorkflow",
            query = "SELECT w FROM WorkflowInstanceHasTransitionStepField w WHERE w.workflowInstanceHasTransitionStepFieldPK.sourceStepWorkflow = :sourceStepWorkflow")
    , @NamedQuery(name = "WorkflowInstanceHasTransitionStepField.findByTargetStep",
            query = "SELECT w FROM WorkflowInstanceHasTransitionStepField w WHERE w.workflowInstanceHasTransitionStepFieldPK.targetStep = :targetStep")
    , @NamedQuery(name = "WorkflowInstanceHasTransitionStepField.findByTargetStepWorkflow",
            query = "SELECT w FROM WorkflowInstanceHasTransitionStepField w WHERE w.workflowInstanceHasTransitionStepFieldPK.targetStepWorkflow = :targetStepWorkflow")
    , @NamedQuery(name = "WorkflowInstanceHasTransitionStepField.findByStepFieldId",
            query = "SELECT w FROM WorkflowInstanceHasTransitionStepField w WHERE w.workflowInstanceHasTransitionStepFieldPK.stepFieldId = :stepFieldId")
    , @NamedQuery(name = "WorkflowInstanceHasTransitionStepField.findByFieldType",
            query = "SELECT w FROM WorkflowInstanceHasTransitionStepField w WHERE w.workflowInstanceHasTransitionStepFieldPK.fieldType = :fieldType")})
public class WorkflowInstanceHasTransitionStepField implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected WorkflowInstanceHasTransitionStepFieldPK workflowInstanceHasTransitionStepFieldPK;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "field_value")
    private String fieldValue;
    @JoinColumns({
        @JoinColumn(name = "step_field_id", referencedColumnName = "id", insertable = false, updatable = false)
        , @JoinColumn(name = "field_type", referencedColumnName = "field_type", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private WorkflowStepField workflowStepField;
    @JoinColumns({
        @JoinColumn(name = "instance", referencedColumnName = "workflow_instance_id", insertable = false, updatable = false)
        , @JoinColumn(name = "workflow", referencedColumnName = "workflow_instance_workflow", insertable = false, updatable = false)
        , @JoinColumn(name = "source_step", referencedColumnName = "step_transitions_to_step_source_step", insertable = false, updatable = false)
        , @JoinColumn(name = "source_step_workflow", referencedColumnName = "step_transitions_to_step_source_step_workflow", insertable = false, updatable = false)
        , @JoinColumn(name = "target_step", referencedColumnName = "step_transitions_to_step_target_step", insertable = false, updatable = false)
        , @JoinColumn(name = "target_step_workflow", referencedColumnName = "step_transitions_to_step_target_step_workflow", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private WorkflowInstanceHasTransition workflowInstanceHasTransition;

    public WorkflowInstanceHasTransitionStepField() {
    }

    public WorkflowInstanceHasTransitionStepField(WorkflowInstanceHasTransitionStepFieldPK workflowInstanceHasTransitionStepFieldPK) {
        this.workflowInstanceHasTransitionStepFieldPK = workflowInstanceHasTransitionStepFieldPK;
    }

    public WorkflowInstanceHasTransitionStepField(WorkflowInstanceHasTransitionStepFieldPK workflowInstanceHasTransitionStepFieldPK, String fieldValue) {
        this.workflowInstanceHasTransitionStepFieldPK = workflowInstanceHasTransitionStepFieldPK;
        this.fieldValue = fieldValue;
    }

    public WorkflowInstanceHasTransitionStepField(int instance, int workflow, int sourceStep, int sourceStepWorkflow, int targetStep, int targetStepWorkflow, int stepFieldId, int fieldType) {
        this.workflowInstanceHasTransitionStepFieldPK = new WorkflowInstanceHasTransitionStepFieldPK(instance, workflow, sourceStep, sourceStepWorkflow, targetStep, targetStepWorkflow, stepFieldId, fieldType);
    }

    public WorkflowInstanceHasTransitionStepFieldPK getWorkflowInstanceHasTransitionStepFieldPK() {
        return workflowInstanceHasTransitionStepFieldPK;
    }

    public void setWorkflowInstanceHasTransitionStepFieldPK(WorkflowInstanceHasTransitionStepFieldPK workflowInstanceHasTransitionStepFieldPK) {
        this.workflowInstanceHasTransitionStepFieldPK = workflowInstanceHasTransitionStepFieldPK;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public WorkflowStepField getWorkflowStepField() {
        return workflowStepField;
    }

    public void setWorkflowStepField(WorkflowStepField workflowStepField) {
        this.workflowStepField = workflowStepField;
    }

    public WorkflowInstanceHasTransition getWorkflowInstanceHasTransition() {
        return workflowInstanceHasTransition;
    }

    public void setWorkflowInstanceHasTransition(WorkflowInstanceHasTransition workflowInstanceHasTransition) {
        this.workflowInstanceHasTransition = workflowInstanceHasTransition;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workflowInstanceHasTransitionStepFieldPK != null ? workflowInstanceHasTransitionStepFieldPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkflowInstanceHasTransitionStepField)) {
            return false;
        }
        WorkflowInstanceHasTransitionStepField other = (WorkflowInstanceHasTransitionStepField) object;
        return !((this.workflowInstanceHasTransitionStepFieldPK == null
                && other.workflowInstanceHasTransitionStepFieldPK != null)
                || (this.workflowInstanceHasTransitionStepFieldPK != null
                && !this.workflowInstanceHasTransitionStepFieldPK
                        .equals(other.workflowInstanceHasTransitionStepFieldPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.WorkflowInstanceHasTransitionStepField[ workflowInstanceHasTransitionStepFieldPK=" + workflowInstanceHasTransitionStepFieldPK + " ]";
    }
}
