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
import javax.persistence.ManyToMany;
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
@Table(name = "workflow_step_field")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WorkflowStepField.findAll",
            query = "SELECT w FROM WorkflowStepField w")
    , @NamedQuery(name = "WorkflowStepField.findById",
            query = "SELECT w FROM WorkflowStepField w WHERE w.workflowStepFieldPK.id = :id")
    , @NamedQuery(name = "WorkflowStepField.findByFieldType",
            query = "SELECT w FROM WorkflowStepField w WHERE w.workflowStepFieldPK.fieldType = :fieldType")
    , @NamedQuery(name = "WorkflowStepField.findByFieldName",
            query = "SELECT w FROM WorkflowStepField w WHERE w.fieldName = :fieldName")})
public class WorkflowStepField implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected WorkflowStepFieldPK workflowStepFieldPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "field_name")
    private String fieldName;
    @ManyToMany(mappedBy = "workflowStepFieldList")
    private List<WorkflowStep> workflowStepList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowStepField")
    private List<WorkflowInstanceHasTransitionStepField> workflowInstanceHasTransitionStepFieldList;
    @JoinColumn(name = "field_type", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private FieldType fieldType;

    public WorkflowStepField() {
    }

    public WorkflowStepField(WorkflowStepFieldPK workflowStepFieldPK) {
        this.workflowStepFieldPK = workflowStepFieldPK;
    }

    public WorkflowStepField(WorkflowStepFieldPK workflowStepFieldPK, String fieldName) {
        this.workflowStepFieldPK = workflowStepFieldPK;
        this.fieldName = fieldName;
    }

    public WorkflowStepField(int fieldType) {
        this.workflowStepFieldPK = new WorkflowStepFieldPK(fieldType);
    }

    public WorkflowStepFieldPK getWorkflowStepFieldPK() {
        return workflowStepFieldPK;
    }

    public void setWorkflowStepFieldPK(WorkflowStepFieldPK workflowStepFieldPK) {
        this.workflowStepFieldPK = workflowStepFieldPK;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @XmlTransient
    @JsonIgnore
    public List<WorkflowStep> getWorkflowStepList() {
        return workflowStepList;
    }

    public void setWorkflowStepList(List<WorkflowStep> workflowStepList) {
        this.workflowStepList = workflowStepList;
    }

    @XmlTransient
    @JsonIgnore
    public List<WorkflowInstanceHasTransitionStepField> getWorkflowInstanceHasTransitionStepFieldList() {
        return workflowInstanceHasTransitionStepFieldList;
    }

    public void setWorkflowInstanceHasTransitionStepFieldList(List<WorkflowInstanceHasTransitionStepField> workflowInstanceHasTransitionStepFieldList) {
        this.workflowInstanceHasTransitionStepFieldList = workflowInstanceHasTransitionStepFieldList;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (workflowStepFieldPK != null ? workflowStepFieldPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkflowStepField)) {
            return false;
        }
        WorkflowStepField other = (WorkflowStepField) object;
        return !((this.workflowStepFieldPK == null
                && other.workflowStepFieldPK != null)
                || (this.workflowStepFieldPK != null
                && !this.workflowStepFieldPK.equals(other.workflowStepFieldPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.WorkflowStepField[ workflowStepFieldPK="
                + workflowStepFieldPK + " ]";
    }
}
