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
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Table(name = "step_transitions_to_step")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "StepTransitionsToStep.findAll",
            query = "SELECT s FROM StepTransitionsToStep s")
    , @NamedQuery(name = "StepTransitionsToStep.findBySourceStep",
            query = "SELECT s FROM StepTransitionsToStep s WHERE s.stepTransitionsToStepPK.sourceStep = :sourceStep")
    , @NamedQuery(name = "StepTransitionsToStep.findBySourceStepWorkflow",
            query = "SELECT s FROM StepTransitionsToStep s WHERE s.stepTransitionsToStepPK.sourceStepWorkflow = :sourceStepWorkflow")
    , @NamedQuery(name = "StepTransitionsToStep.findByTargetStep",
            query = "SELECT s FROM StepTransitionsToStep s WHERE s.stepTransitionsToStepPK.targetStep = :targetStep")
    , @NamedQuery(name = "StepTransitionsToStep.findByTargetStepWorkflow",
            query = "SELECT s FROM StepTransitionsToStep s WHERE s.stepTransitionsToStepPK.targetStepWorkflow = :targetStepWorkflow")})
public class StepTransitionsToStep implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected StepTransitionsToStepPK stepTransitionsToStepPK;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "stepTransitionsToStep")
    private List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionList;
    @JoinColumns({
        @JoinColumn(name = "source_step", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "source_step_workflow",
                referencedColumnName = "workflow", insertable = false,
                updatable = false)})
    @ManyToOne(optional = false)
    private WorkflowStep workflowStepSource;
    @JoinColumns({
        @JoinColumn(name = "target_step", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "target_step_workflow",
                referencedColumnName = "workflow", insertable = false,
                updatable = false)})
    @ManyToOne(optional = false)
    private WorkflowStep workflowStepTarget;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "transition_name")
    private String transitionName;

    public StepTransitionsToStep() {
    }

    public StepTransitionsToStep(StepTransitionsToStepPK stepTransitionsToStepPK) {
        this.stepTransitionsToStepPK = stepTransitionsToStepPK;
    }

    public StepTransitionsToStep(int sourceStep, int sourceStepWorkflow,
            int targetStep, int targetStepWorkflow) {
        this.stepTransitionsToStepPK = new StepTransitionsToStepPK(sourceStep,
                sourceStepWorkflow, targetStep, targetStepWorkflow);
    }

    public StepTransitionsToStepPK getStepTransitionsToStepPK() {
        return stepTransitionsToStepPK;
    }

    public void setStepTransitionsToStepPK(StepTransitionsToStepPK stepTransitionsToStepPK) {
        this.stepTransitionsToStepPK = stepTransitionsToStepPK;
    }

    @XmlTransient
    @JsonIgnore
    public List<WorkflowInstanceHasTransition> getWorkflowInstanceHasTransitionList() {
        return workflowInstanceHasTransitionList;
    }

    public void setWorkflowInstanceHasTransitionList(List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionList) {
        this.workflowInstanceHasTransitionList = workflowInstanceHasTransitionList;
    }

    public WorkflowStep getWorkflowStepSource() {
        return workflowStepSource;
    }

    public void setWorkflowStepSource(WorkflowStep workflowStepSource) {
        this.workflowStepSource = workflowStepSource;
    }

    public WorkflowStep getWorkflowStepTarget() {
        return workflowStepTarget;
    }

    public void setWorkflowStepTarget(WorkflowStep workflowStepTarget) {
        this.workflowStepTarget = workflowStepTarget;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (stepTransitionsToStepPK != null ? stepTransitionsToStepPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StepTransitionsToStep)) {
            return false;
        }
        StepTransitionsToStep other = (StepTransitionsToStep) object;
        return !((this.stepTransitionsToStepPK == null
                && other.stepTransitionsToStepPK != null)
                || (this.stepTransitionsToStepPK != null
                && !this.stepTransitionsToStepPK.equals(other.stepTransitionsToStepPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.StepTransitionsToStep[ stepTransitionsToStepPK="
                + stepTransitionsToStepPK + " ]";
    }

    public String getTransitionName() {
        return transitionName;
    }

    public void setTransitionName(String transitionName) {
        this.transitionName = transitionName;
    }
}
