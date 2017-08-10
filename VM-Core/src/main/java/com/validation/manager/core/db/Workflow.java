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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
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
@Table(name = "workflow")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Workflow.findAll",
            query = "SELECT w FROM Workflow w")
    , @NamedQuery(name = "Workflow.findById",
            query = "SELECT w FROM Workflow w WHERE w.id = :id")
    , @NamedQuery(name = "Workflow.findByWorkflowName",
            query = "SELECT w FROM Workflow w WHERE w.workflowName = :workflowName")})
public class Workflow implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "WFGEN")
    @TableGenerator(name = "WFGEN",
            table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "workflow",
            initialValue = 1_000,
            allocationSize = 1)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "workflow_name")
    private String workflowName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflow")
    private List<WorkflowInstance> workflowInstanceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflow")
    private List<WorkflowStep> workflowStepList;

    public Workflow() {
    }

    public Workflow(String workflowName) {
        this.workflowName = workflowName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
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
    public List<WorkflowStep> getWorkflowStepList() {
        return workflowStepList;
    }

    public void setWorkflowStepList(List<WorkflowStep> workflowStepList) {
        this.workflowStepList = workflowStepList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Workflow)) {
            return false;
        }
        Workflow other = (Workflow) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Workflow[ id=" + id + " ]";
    }

}
