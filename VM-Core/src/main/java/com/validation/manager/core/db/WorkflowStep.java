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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "workflow_step")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WorkflowStep.findAll",
            query = "SELECT w FROM WorkflowStep w")
    , @NamedQuery(name = "WorkflowStep.findById",
            query = "SELECT w FROM WorkflowStep w WHERE w.workflowStepPK.id = :id")
    , @NamedQuery(name = "WorkflowStep.findByWorkflow",
            query = "SELECT w FROM WorkflowStep w WHERE w.workflowStepPK.workflow = :workflow")
    , @NamedQuery(name = "WorkflowStep.findByStepName",
            query = "SELECT w FROM WorkflowStep w WHERE w.stepName = :stepName")})
public class WorkflowStep implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected WorkflowStepPK workflowStepPK;
    @Size(max = 255)
    @Column(name = "step_name")
    private String stepName;
    @JoinTable(name = "workflow_step_has_field", joinColumns = {
        @JoinColumn(name = "step_id", referencedColumnName = "id")
        , @JoinColumn(name = "step_workflow",
                referencedColumnName = "workflow")}, inverseJoinColumns = {
        @JoinColumn(name = "step_field", referencedColumnName = "id")
        , @JoinColumn(name = "step_field_field_type",
                referencedColumnName = "field_type")})
    @ManyToMany
    private List<WorkflowStepField> workflowStepFieldList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowStep")
    private List<WorkflowInstance> workflowInstanceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowStepSource")
    private List<StepTransitionsToStep> sourceTransitions;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowStepTarget")
    private List<StepTransitionsToStep> targetTransitions;
    @JoinColumn(name = "workflow", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Workflow workflow;

    public WorkflowStep() {
    }

    public WorkflowStep(WorkflowStepPK workflowStepPK) {
        this.workflowStepPK = workflowStepPK;
    }

    public WorkflowStep(int workflow) {
        this.workflowStepPK = new WorkflowStepPK(workflow);
    }

    public WorkflowStepPK getWorkflowStepPK() {
        return workflowStepPK;
    }

    public void setWorkflowStepPK(WorkflowStepPK workflowStepPK) {
        this.workflowStepPK = workflowStepPK;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    @XmlTransient
    @JsonIgnore
    public List<WorkflowStepField> getWorkflowStepFieldList() {
        return workflowStepFieldList;
    }

    public void setWorkflowStepFieldList(List<WorkflowStepField> workflowStepFieldList) {
        this.workflowStepFieldList = workflowStepFieldList;
    }

    @XmlTransient
    @JsonIgnore
    public List<WorkflowInstance> getWorkflowInstanceList() {
        return workflowInstanceList;
    }

    public void setWorkflowInstanceList(List<WorkflowInstance> workflowInstanceList) {
        this.workflowInstanceList = workflowInstanceList;
    }

    @XmlTransient
    @JsonIgnore
    public List<StepTransitionsToStep> getSourceTransitions() {
        return sourceTransitions;
    }

    public void setSourceTransitions(List<StepTransitionsToStep> sourceTransitions) {
        this.sourceTransitions = sourceTransitions;
    }

    @XmlTransient
    @JsonIgnore
    public List<StepTransitionsToStep> getTargetTransitions() {
        return targetTransitions;
    }

    public void setTargetTransitions(List<StepTransitionsToStep> targetTransitions) {
        this.targetTransitions = targetTransitions;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workflowStepPK != null ? workflowStepPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkflowStep)) {
            return false;
        }
        WorkflowStep other = (WorkflowStep) object;
        return !((this.workflowStepPK == null && other.workflowStepPK != null)
                || (this.workflowStepPK != null
                && !this.workflowStepPK.equals(other.workflowStepPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.WorkflowStep[ workflowStepPK="
                + workflowStepPK + " ]";
    }
}
