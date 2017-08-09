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
public class WorkflowInstanceHasTransitionPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "workflow_instance_id")
    private int workflowInstanceId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "workflow_instance_workflow")
    private int workflowInstanceWorkflow;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_transitions_to_step_source_step")
    private int stepTransitionsToStepSourceStep;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_transitions_to_step_source_step_workflow")
    private int stepTransitionsToStepSourceStepWorkflow;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_transitions_to_step_target_step")
    private int stepTransitionsToStepTargetStep;
    @Basic(optional = false)
    @NotNull
    @Column(name = "step_transitions_to_step_target_step_workflow")
    private int stepTransitionsToStepTargetStepWorkflow;

    public WorkflowInstanceHasTransitionPK() {
    }

    public WorkflowInstanceHasTransitionPK(int workflowInstanceId, int workflowInstanceWorkflow, int stepTransitionsToStepSourceStep, int stepTransitionsToStepSourceStepWorkflow, int stepTransitionsToStepTargetStep, int stepTransitionsToStepTargetStepWorkflow) {
        this.workflowInstanceId = workflowInstanceId;
        this.workflowInstanceWorkflow = workflowInstanceWorkflow;
        this.stepTransitionsToStepSourceStep = stepTransitionsToStepSourceStep;
        this.stepTransitionsToStepSourceStepWorkflow = stepTransitionsToStepSourceStepWorkflow;
        this.stepTransitionsToStepTargetStep = stepTransitionsToStepTargetStep;
        this.stepTransitionsToStepTargetStepWorkflow = stepTransitionsToStepTargetStepWorkflow;
    }

    public int getWorkflowInstanceId() {
        return workflowInstanceId;
    }

    public void setWorkflowInstanceId(int workflowInstanceId) {
        this.workflowInstanceId = workflowInstanceId;
    }

    public int getWorkflowInstanceWorkflow() {
        return workflowInstanceWorkflow;
    }

    public void setWorkflowInstanceWorkflow(int workflowInstanceWorkflow) {
        this.workflowInstanceWorkflow = workflowInstanceWorkflow;
    }

    public int getStepTransitionsToStepSourceStep() {
        return stepTransitionsToStepSourceStep;
    }

    public void setStepTransitionsToStepSourceStep(int stepTransitionsToStepSourceStep) {
        this.stepTransitionsToStepSourceStep = stepTransitionsToStepSourceStep;
    }

    public int getStepTransitionsToStepSourceStepWorkflow() {
        return stepTransitionsToStepSourceStepWorkflow;
    }

    public void setStepTransitionsToStepSourceStepWorkflow(int stepTransitionsToStepSourceStepWorkflow) {
        this.stepTransitionsToStepSourceStepWorkflow = stepTransitionsToStepSourceStepWorkflow;
    }

    public int getStepTransitionsToStepTargetStep() {
        return stepTransitionsToStepTargetStep;
    }

    public void setStepTransitionsToStepTargetStep(int stepTransitionsToStepTargetStep) {
        this.stepTransitionsToStepTargetStep = stepTransitionsToStepTargetStep;
    }

    public int getStepTransitionsToStepTargetStepWorkflow() {
        return stepTransitionsToStepTargetStepWorkflow;
    }

    public void setStepTransitionsToStepTargetStepWorkflow(int stepTransitionsToStepTargetStepWorkflow) {
        this.stepTransitionsToStepTargetStepWorkflow = stepTransitionsToStepTargetStepWorkflow;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) workflowInstanceId;
        hash += (int) workflowInstanceWorkflow;
        hash += (int) stepTransitionsToStepSourceStep;
        hash += (int) stepTransitionsToStepSourceStepWorkflow;
        hash += (int) stepTransitionsToStepTargetStep;
        hash += (int) stepTransitionsToStepTargetStepWorkflow;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkflowInstanceHasTransitionPK)) {
            return false;
        }
        WorkflowInstanceHasTransitionPK other = (WorkflowInstanceHasTransitionPK) object;
        if (this.workflowInstanceId != other.workflowInstanceId) {
            return false;
        }
        if (this.workflowInstanceWorkflow != other.workflowInstanceWorkflow) {
            return false;
        }
        if (this.stepTransitionsToStepSourceStep != other.stepTransitionsToStepSourceStep) {
            return false;
        }
        if (this.stepTransitionsToStepSourceStepWorkflow != other.stepTransitionsToStepSourceStepWorkflow) {
            return false;
        }
        if (this.stepTransitionsToStepTargetStep != other.stepTransitionsToStepTargetStep) {
            return false;
        }
        if (this.stepTransitionsToStepTargetStepWorkflow != other.stepTransitionsToStepTargetStepWorkflow) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.WorkflowInstanceHasTransitionPK[ workflowInstanceId=" + workflowInstanceId + ", workflowInstanceWorkflow=" + workflowInstanceWorkflow + ", stepTransitionsToStepSourceStep=" + stepTransitionsToStepSourceStep + ", stepTransitionsToStepSourceStepWorkflow=" + stepTransitionsToStepSourceStepWorkflow + ", stepTransitionsToStepTargetStep=" + stepTransitionsToStepTargetStep + ", stepTransitionsToStepTargetStepWorkflow=" + stepTransitionsToStepTargetStepWorkflow + " ]";
    }

}
