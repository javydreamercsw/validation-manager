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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "workflow_instance_has_transition")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WorkflowInstanceHasTransition.findAll",
            query = "SELECT w FROM WorkflowInstanceHasTransition w")
    , @NamedQuery(name = "WorkflowInstanceHasTransition.findByWorkflowInstanceId",
            query = "SELECT w FROM WorkflowInstanceHasTransition w WHERE w.workflowInstanceHasTransitionPK.workflowInstanceId = :workflowInstanceId")
    , @NamedQuery(name = "WorkflowInstanceHasTransition.findByWorkflowInstanceWorkflow",
            query = "SELECT w FROM WorkflowInstanceHasTransition w WHERE w.workflowInstanceHasTransitionPK.workflowInstanceWorkflow = :workflowInstanceWorkflow")
    , @NamedQuery(name = "WorkflowInstanceHasTransition.findByStepTransitionsToStepSourceStep",
            query = "SELECT w FROM WorkflowInstanceHasTransition w WHERE w.workflowInstanceHasTransitionPK.stepTransitionsToStepSourceStep = :stepTransitionsToStepSourceStep")
    , @NamedQuery(name = "WorkflowInstanceHasTransition.findByStepTransitionsToStepSourceStepWorkflow",
            query = "SELECT w FROM WorkflowInstanceHasTransition w WHERE w.workflowInstanceHasTransitionPK.stepTransitionsToStepSourceStepWorkflow = :stepTransitionsToStepSourceStepWorkflow")
    , @NamedQuery(name = "WorkflowInstanceHasTransition.findByStepTransitionsToStepTargetStep",
            query = "SELECT w FROM WorkflowInstanceHasTransition w WHERE w.workflowInstanceHasTransitionPK.stepTransitionsToStepTargetStep = :stepTransitionsToStepTargetStep")
    , @NamedQuery(name = "WorkflowInstanceHasTransition.findByStepTransitionsToStepTargetStepWorkflow",
            query = "SELECT w FROM WorkflowInstanceHasTransition w WHERE w.workflowInstanceHasTransitionPK.stepTransitionsToStepTargetStepWorkflow = :stepTransitionsToStepTargetStepWorkflow")
    , @NamedQuery(name = "WorkflowInstanceHasTransition.findByTransitionDate",
            query = "SELECT w FROM WorkflowInstanceHasTransition w WHERE w.transitionDate = :transitionDate")})
public class WorkflowInstanceHasTransition implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected WorkflowInstanceHasTransitionPK workflowInstanceHasTransitionPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "transition_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date transitionDate;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "transition_source")
    private String transitionSource;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowInstanceHasTransition")
    private List<WorkflowInstanceHasTransitionStepField> workflowInstanceHasTransitionStepFieldList;
    @JoinColumns({
        @JoinColumn(name = "step_transitions_to_step_source_step",
                referencedColumnName = "source_step", insertable = false, updatable = false)
        , @JoinColumn(name = "step_transitions_to_step_source_step_workflow",
                referencedColumnName = "source_step_workflow", insertable = false, updatable = false)
        , @JoinColumn(name = "step_transitions_to_step_target_step",
                referencedColumnName = "target_step", insertable = false, updatable = false)
        , @JoinColumn(name = "step_transitions_to_step_target_step_workflow",
                referencedColumnName = "target_step_workflow", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private StepTransitionsToStep stepTransitionsToStep;
    @JoinColumns({
        @JoinColumn(name = "workflow_instance_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "workflow_instance_workflow",
                referencedColumnName = "workflow", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private WorkflowInstance workflowInstance;
    @JoinColumn(name = "transitioner", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private VmUser transitioner;

    public WorkflowInstanceHasTransition() {
    }

    public WorkflowInstanceHasTransition(WorkflowInstanceHasTransitionPK workflowInstanceHasTransitionPK) {
        this.workflowInstanceHasTransitionPK = workflowInstanceHasTransitionPK;
    }

    public WorkflowInstanceHasTransition(WorkflowInstanceHasTransitionPK workflowInstanceHasTransitionPK, Date transitionDate, String transitionSource) {
        this.workflowInstanceHasTransitionPK = workflowInstanceHasTransitionPK;
        this.transitionDate = transitionDate;
        this.transitionSource = transitionSource;
    }

    public WorkflowInstanceHasTransition(int workflowInstanceId, int workflowInstanceWorkflow, int stepTransitionsToStepSourceStep, int stepTransitionsToStepSourceStepWorkflow, int stepTransitionsToStepTargetStep, int stepTransitionsToStepTargetStepWorkflow) {
        this.workflowInstanceHasTransitionPK = new WorkflowInstanceHasTransitionPK(workflowInstanceId, workflowInstanceWorkflow, stepTransitionsToStepSourceStep, stepTransitionsToStepSourceStepWorkflow, stepTransitionsToStepTargetStep, stepTransitionsToStepTargetStepWorkflow);
    }

    public WorkflowInstanceHasTransitionPK getWorkflowInstanceHasTransitionPK() {
        return workflowInstanceHasTransitionPK;
    }

    public void setWorkflowInstanceHasTransitionPK(WorkflowInstanceHasTransitionPK workflowInstanceHasTransitionPK) {
        this.workflowInstanceHasTransitionPK = workflowInstanceHasTransitionPK;
    }

    public Date getTransitionDate() {
        return transitionDate;
    }

    public void setTransitionDate(Date transitionDate) {
        this.transitionDate = transitionDate;
    }

    public String getTransitionSource() {
        return transitionSource;
    }

    public void setTransitionSource(String transitionSource) {
        this.transitionSource = transitionSource;
    }

    @XmlTransient
    @JsonIgnore
    public List<WorkflowInstanceHasTransitionStepField> getWorkflowInstanceHasTransitionStepFieldList() {
        return workflowInstanceHasTransitionStepFieldList;
    }

    public void setWorkflowInstanceHasTransitionStepFieldList(List<WorkflowInstanceHasTransitionStepField> workflowInstanceHasTransitionStepFieldList) {
        this.workflowInstanceHasTransitionStepFieldList = workflowInstanceHasTransitionStepFieldList;
    }

    public StepTransitionsToStep getStepTransitionsToStep() {
        return stepTransitionsToStep;
    }

    public void setStepTransitionsToStep(StepTransitionsToStep stepTransitionsToStep) {
        this.stepTransitionsToStep = stepTransitionsToStep;
    }

    public WorkflowInstance getWorkflowInstance() {
        return workflowInstance;
    }

    public void setWorkflowInstance(WorkflowInstance workflowInstance) {
        this.workflowInstance = workflowInstance;
    }

    public VmUser getTransitioner() {
        return transitioner;
    }

    public void setTransitioner(VmUser transitioner) {
        this.transitioner = transitioner;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workflowInstanceHasTransitionPK != null ? workflowInstanceHasTransitionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkflowInstanceHasTransition)) {
            return false;
        }
        WorkflowInstanceHasTransition other = (WorkflowInstanceHasTransition) object;
        if ((this.workflowInstanceHasTransitionPK == null && other.workflowInstanceHasTransitionPK != null) || (this.workflowInstanceHasTransitionPK != null && !this.workflowInstanceHasTransitionPK.equals(other.workflowInstanceHasTransitionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.WorkflowInstanceHasTransition[ workflowInstanceHasTransitionPK=" + workflowInstanceHasTransitionPK + " ]";
    }

}
