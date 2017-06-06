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

import com.validation.manager.core.history.Auditable;
import com.validation.manager.core.history.Versionable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "requirement")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Requirement.findAll",
            query = "SELECT r FROM Requirement r")
    , @NamedQuery(name = "Requirement.findById",
            query = "SELECT r FROM Requirement r WHERE r.id = :id")
    , @NamedQuery(name = "Requirement.findByUniqueId",
            query = "SELECT r FROM Requirement r WHERE r.uniqueId = :uniqueId")})
public class Requirement extends Versionable implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "ReqGen")
    @TableGenerator(name = "ReqGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "requirement",
            allocationSize = 1,
            initialValue = 1_000)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "unique_id")
    @Auditable
    private String uniqueId;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(max = 2_147_483_647)
    @Column(name = "description")
    @Auditable
    private String description;
    @Lob
    @Size(max = 2_147_483_647)
    @Column(name = "notes")
    @Auditable
    private String notes;
    @OneToMany(mappedBy = "parentRequirementId")
    private List<Requirement> requirementList;
    @JoinTable(name = "step_has_requirement", joinColumns = {
        @JoinColumn(name = "requirement_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                @JoinColumn(name = "step_id", referencedColumnName = "id")
                , @JoinColumn(name = "step_test_case_id",
                        referencedColumnName = "test_case_id")})
    @ManyToMany
    private List<Step> stepList;
    @JoinColumn(name = "parent_requirement_id", referencedColumnName = "id")
    @ManyToOne
    private Requirement parentRequirementId;
    @JoinColumns({
        @JoinColumn(name = "requirement_spec_node_id",
                referencedColumnName = "id")
        , @JoinColumn(name = "requirement_spec_node_requirement_spec_id",
                referencedColumnName = "requirement_spec_id")
        , @JoinColumn(name = "requirement_spec_node_requirement_spec_project_id",
                referencedColumnName = "requirement_spec_project_id")
        , @JoinColumn(name = "requirement_spec_node_requirement_spec_spec_level_id",
                referencedColumnName = "requirement_spec_spec_level_id")})
    @ManyToOne(optional = false)
    private RequirementSpecNode requirementSpecNode;
    @JoinColumn(name = "requirement_status_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private RequirementStatus requirementStatusId;
    @JoinColumn(name = "requirement_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private RequirementType requirementTypeId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "requirement")
    private List<RiskControlHasRequirement> riskControlHasRequirementList;
    @OneToMany(mappedBy = "requirementId")
    private List<History> historyList;

    public Requirement() {
        super();
    }

    public Requirement(String uniqueId, String description) {
        this.uniqueId = uniqueId;
        this.description = description;
    }

    public Requirement(String uniqueId, String description, String notes) {
        this.uniqueId = uniqueId;
        this.description = description;
        this.notes = notes;
        setRiskControlHasRequirementList(new ArrayList<>());
        setRequirementList(new ArrayList<>());
        setStepList(new ArrayList<>());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @XmlTransient
    @JsonIgnore
    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

    public Requirement getParentRequirementId() {
        return parentRequirementId;
    }

    public void setParentRequirementId(Requirement parentRequirementId) {
        this.parentRequirementId = parentRequirementId;
    }

    @XmlTransient
    @JsonIgnore
    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    public RequirementSpecNode getRequirementSpecNode() {
        return requirementSpecNode;
    }

    public void setRequirementSpecNode(RequirementSpecNode requirementSpecNode) {
        this.requirementSpecNode = requirementSpecNode;
    }

    public RequirementStatus getRequirementStatusId() {
        return requirementStatusId;
    }

    public void setRequirementStatusId(RequirementStatus requirementStatusId) {
        this.requirementStatusId = requirementStatusId;
    }

    public RequirementType getRequirementTypeId() {
        return requirementTypeId;
    }

    public void setRequirementTypeId(RequirementType requirementTypeId) {
        this.requirementTypeId = requirementTypeId;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskControlHasRequirement> getRiskControlHasRequirementList() {
        return riskControlHasRequirementList;
    }

    public void setRiskControlHasRequirementList(List<RiskControlHasRequirement> riskControlHasRequirementList) {
        this.riskControlHasRequirementList = riskControlHasRequirementList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Requirement)) {
            return false;
        }
        Requirement other = (Requirement) object;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Requirement[ id=" + getId()
                + ", uniqueId=" + getUniqueId()
                + ", description=" + getDescription()
                + " " + super.toString() + " ]";
    }

    @XmlTransient
    @JsonIgnore
    @Override
    public List<History> getHistoryList() {
        return historyList;
    }

    @Override
    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }
}
