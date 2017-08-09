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
public class WorkflowInstanceHasTransitionStepFieldPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "instance")
    private int instance;
    @Basic(optional = false)
    @NotNull
    @Column(name = "workflow")
    private int workflow;
    @Basic(optional = false)
    @NotNull
    @Column(name = "source_step")
    private int sourceStep;
    @Basic(optional = false)
    @NotNull
    @Column(name = "source_step_workflow")
    private int sourceStepWorkflow;
    @Basic(optional = false)
    @NotNull
    @Column(name = "target_step")
    private int targetStep;
    @Basic(optional = false)
    @NotNull
    @Column(name = "target_step_workflow")
    private int targetStepWorkflow;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_field_id")
    private int stepFieldId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "field_type")
    private int fieldType;

    public WorkflowInstanceHasTransitionStepFieldPK() {
    }

    public WorkflowInstanceHasTransitionStepFieldPK(int instance,
            int workflow, int sourceStep, int sourceStepWorkflow,
            int targetStep, int targetStepWorkflow, int stepFieldId,
            int fieldType) {
        this.instance = instance;
        this.workflow = workflow;
        this.sourceStep = sourceStep;
        this.sourceStepWorkflow = sourceStepWorkflow;
        this.targetStep = targetStep;
        this.targetStepWorkflow = targetStepWorkflow;
        this.stepFieldId = stepFieldId;
        this.fieldType = fieldType;
    }

    public int getInstance() {
        return instance;
    }

    public void setInstance(int instance) {
        this.instance = instance;
    }

    public int getWorkflow() {
        return workflow;
    }

    public void setWorkflow(int workflow) {
        this.workflow = workflow;
    }

    public int getSourceStep() {
        return sourceStep;
    }

    public void setSourceStep(int sourceStep) {
        this.sourceStep = sourceStep;
    }

    public int getSourceStepWorkflow() {
        return sourceStepWorkflow;
    }

    public void setSourceStepWorkflow(int sourceStepWorkflow) {
        this.sourceStepWorkflow = sourceStepWorkflow;
    }

    public int getTargetStep() {
        return targetStep;
    }

    public void setTargetStep(int targetStep) {
        this.targetStep = targetStep;
    }

    public int getTargetStepWorkflow() {
        return targetStepWorkflow;
    }

    public void setTargetStepWorkflow(int targetStepWorkflow) {
        this.targetStepWorkflow = targetStepWorkflow;
    }

    public int getStepFieldId() {
        return stepFieldId;
    }

    public void setStepFieldId(int stepFieldId) {
        this.stepFieldId = stepFieldId;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) instance;
        hash += (int) workflow;
        hash += (int) sourceStep;
        hash += (int) sourceStepWorkflow;
        hash += (int) targetStep;
        hash += (int) targetStepWorkflow;
        hash += (int) stepFieldId;
        hash += (int) fieldType;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkflowInstanceHasTransitionStepFieldPK)) {
            return false;
        }
        WorkflowInstanceHasTransitionStepFieldPK other
                = (WorkflowInstanceHasTransitionStepFieldPK) object;
        if (this.instance != other.instance) {
            return false;
        }
        if (this.workflow != other.workflow) {
            return false;
        }
        if (this.sourceStep != other.sourceStep) {
            return false;
        }
        if (this.sourceStepWorkflow != other.sourceStepWorkflow) {
            return false;
        }
        if (this.targetStep != other.targetStep) {
            return false;
        }
        if (this.targetStepWorkflow != other.targetStepWorkflow) {
            return false;
        }
        if (this.stepFieldId != other.stepFieldId) {
            return false;
        }
        return this.fieldType == other.fieldType;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.WorkflowInstanceHasTransitionStepFieldPK[ instance=" + instance + ", workflow=" + workflow + ", sourceStep=" + sourceStep + ", sourceStepWorkflow=" + sourceStepWorkflow + ", targetStep=" + targetStep + ", targetStepWorkflow=" + targetStepWorkflow + ", stepFieldId=" + stepFieldId + ", fieldType=" + fieldType + " ]";
    }

}
