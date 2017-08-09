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
public class StepTransitionsToStepPK implements Serializable {

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

    public StepTransitionsToStepPK() {
    }

    public StepTransitionsToStepPK(int sourceStep, int sourceStepWorkflow, int targetStep, int targetStepWorkflow) {
        this.sourceStep = sourceStep;
        this.sourceStepWorkflow = sourceStepWorkflow;
        this.targetStep = targetStep;
        this.targetStepWorkflow = targetStepWorkflow;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) sourceStep;
        hash += (int) sourceStepWorkflow;
        hash += (int) targetStep;
        hash += (int) targetStepWorkflow;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StepTransitionsToStepPK)) {
            return false;
        }
        StepTransitionsToStepPK other = (StepTransitionsToStepPK) object;
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
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.StepTransitionsToStepPK[ sourceStep=" + sourceStep + ", sourceStepWorkflow=" + sourceStepWorkflow + ", targetStep=" + targetStep + ", targetStepWorkflow=" + targetStepWorkflow + " ]";
    }

}
