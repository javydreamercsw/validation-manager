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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "workflow_instance")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WorkflowInstance.findAll",
            query = "SELECT w FROM WorkflowInstance w")
    , @NamedQuery(name = "WorkflowInstance.findById",
            query = "SELECT w FROM WorkflowInstance w WHERE w.workflowInstancePK.id = :id")
    , @NamedQuery(name = "WorkflowInstance.findByWorkflow",
            query = "SELECT w FROM WorkflowInstance w WHERE w.workflowInstancePK.workflow = :workflow")})
public class WorkflowInstance implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected WorkflowInstancePK workflowInstancePK;
    @JoinColumns({
        @JoinColumn(name = "current_step", referencedColumnName = "id")
        , @JoinColumn(name = "current_step_workflow", referencedColumnName = "workflow")})
    @ManyToOne(optional = false)
    private WorkflowStep workflowStep;
    @JoinColumn(name = "assigned_user", referencedColumnName = "id")
    @ManyToOne
    private VmUser assignedUser;
    @JoinColumn(name = "workflow", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Workflow workflow;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowInstance")
    private List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionList;

    public WorkflowInstance() {
    }

    public WorkflowInstance(WorkflowInstancePK workflowInstancePK) {
        this.workflowInstancePK = workflowInstancePK;
    }

    public WorkflowInstance(int workflow) {
        this.workflowInstancePK = new WorkflowInstancePK(workflow);
    }

    public WorkflowInstancePK getWorkflowInstancePK() {
        return workflowInstancePK;
    }

    public void setWorkflowInstancePK(WorkflowInstancePK workflowInstancePK) {
        this.workflowInstancePK = workflowInstancePK;
    }

    public WorkflowStep getWorkflowStep() {
        return workflowStep;
    }

    public void setWorkflowStep(WorkflowStep workflowStep) {
        this.workflowStep = workflowStep;
    }

    public VmUser getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(VmUser assignedUser) {
        this.assignedUser = assignedUser;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    @XmlTransient
    @JsonIgnore
    public List<WorkflowInstanceHasTransition> getWorkflowInstanceHasTransitionList() {
        return workflowInstanceHasTransitionList;
    }

    public void setWorkflowInstanceHasTransitionList(List<WorkflowInstanceHasTransition> workflowInstanceHasTransitionList) {
        this.workflowInstanceHasTransitionList = workflowInstanceHasTransitionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workflowInstancePK != null ? workflowInstancePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkflowInstance)) {
            return false;
        }
        WorkflowInstance other = (WorkflowInstance) object;
        if ((this.workflowInstancePK == null && other.workflowInstancePK != null) || (this.workflowInstancePK != null && !this.workflowInstancePK.equals(other.workflowInstancePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.WorkflowInstance[ workflowInstancePK=" + workflowInstancePK + " ]";
    }

}
