/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import com.validation.manager.core.db.fmea.RiskControl;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "requirement", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "requirement_spec_node_requirement_spec_project_id",
        "requirementPK.version",
        "unique_id"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Requirement.findAll", query = "SELECT r FROM Requirement r"),
    @NamedQuery(name = "Requirement.findById", query = "SELECT r FROM Requirement r WHERE r.requirementPK.id = :id"),
    @NamedQuery(name = "Requirement.findByVersion", query = "SELECT r FROM Requirement r WHERE r.requirementPK.version = :version"),
    @NamedQuery(name = "Requirement.findByUniqueId", query = "SELECT r FROM Requirement r WHERE r.uniqueId = :uniqueId")})
public class Requirement implements Serializable {

    @JoinTable(name = "risk_control_has_requirement", joinColumns = {
        @JoinColumn(name = "requirement_id", referencedColumnName = "id"),
        @JoinColumn(name = "requirement_version", referencedColumnName = "version")}, inverseJoinColumns = {
        @JoinColumn(name = "risk_control_id", referencedColumnName = "id"),
        @JoinColumn(name = "risk_control_risk_control_type_id", referencedColumnName = "risk_control_type_id")})
    @ManyToMany
    private List<RiskControl> riskControlList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "requirement")
    private List<RequirementHasException> requirementHasExceptionList;
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RequirementPK requirementPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "unique_id")
    private String uniqueId;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description")
    private String description;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @JoinTable(name = "requirement_has_requirement", joinColumns = {
        @JoinColumn(name = "requirement_id", referencedColumnName = "id"),
        @JoinColumn(name = "requirement_version", referencedColumnName = "version")}, inverseJoinColumns = {
        @JoinColumn(name = "parent_requirement_id", referencedColumnName = "id"),
        @JoinColumn(name = "parent_requirement_version", referencedColumnName = "version")})
    @ManyToMany
    private List<Requirement> requirementList;
    @ManyToMany(mappedBy = "requirementList")
    private List<Requirement> requirementList1;
    @JoinTable(name = "step_has_requirement", joinColumns = {
        @JoinColumn(name = "requirement_id", referencedColumnName = "id"),
        @JoinColumn(name = "requirement_version", referencedColumnName = "version")}, inverseJoinColumns = {
        @JoinColumn(name = "step_id", referencedColumnName = "id"),
        @JoinColumn(name = "step_test_case_id", referencedColumnName = "test_case_id"),
        @JoinColumn(name = "step_test_case_test_id", referencedColumnName = "test_case_test_id")})
    @ManyToMany
    private List<Step> stepList;
    @JoinColumn(name = "requirement_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private RequirementType requirementTypeId;
    @JoinColumn(name = "requirement_status_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private RequirementStatus requirementStatusId;
    @JoinColumns({
        @JoinColumn(name = "requirement_spec_node_id", referencedColumnName = "id"),
        @JoinColumn(name = "requirement_spec_node_requirement_spec_id", referencedColumnName = "requirement_spec_id"),
        @JoinColumn(name = "requirement_spec_node_requirement_spec_project_id", referencedColumnName = "requirement_spec_project_id"),
        @JoinColumn(name = "requirement_spec_node_requirement_spec_spec_level_id", referencedColumnName = "requirement_spec_spec_level_id")})
    @ManyToOne(optional = false)
    private RequirementSpecNode requirementSpecNode;

    public Requirement() {
    }

    public Requirement(String uniqueId, String description) {
        this.uniqueId = uniqueId;
        this.description = description;
    }

    public Requirement(String uniqueId, String description, String notes) {
        this.uniqueId = uniqueId;
        this.description = description;
        this.notes = notes;
    }

    public Requirement(int version) {
        this.requirementPK = new RequirementPK(version);
    }

    public RequirementPK getRequirementPK() {
        return requirementPK;
    }

    public void setRequirementPK(RequirementPK requirementPK) {
        this.requirementPK = requirementPK;
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

    @XmlTransient
    @JsonIgnore
    public List<Requirement> getRequirementList1() {
        return requirementList1;
    }

    public void setRequirementList1(List<Requirement> requirementList1) {
        this.requirementList1 = requirementList1;
    }

    @XmlTransient
    @JsonIgnore
    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    public RequirementType getRequirementTypeId() {
        return requirementTypeId;
    }

    public void setRequirementTypeId(RequirementType requirementTypeId) {
        this.requirementTypeId = requirementTypeId;
    }

    public RequirementStatus getRequirementStatusId() {
        return requirementStatusId;
    }

    public void setRequirementStatusId(RequirementStatus requirementStatusId) {
        this.requirementStatusId = requirementStatusId;
    }

    public RequirementSpecNode getRequirementSpecNode() {
        return requirementSpecNode;
    }

    public void setRequirementSpecNode(RequirementSpecNode requirementSpecNode) {
        this.requirementSpecNode = requirementSpecNode;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (requirementPK != null ? requirementPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Requirement)) {
            return false;
        }
        Requirement other = (Requirement) object;
        if ((this.requirementPK == null && other.requirementPK != null) || (this.requirementPK != null && !this.requirementPK.equals(other.requirementPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Requirement[ requirementPK=" + requirementPK + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<RequirementHasException> getRequirementHasExceptionList() {
        return requirementHasExceptionList;
    }

    public void setRequirementHasExceptionList(List<RequirementHasException> requirementHasExceptionList) {
        this.requirementHasExceptionList = requirementHasExceptionList;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskControl> getRiskControlList() {
        return riskControlList;
    }

    public void setRiskControlList(List<RiskControl> riskControlList) {
        this.riskControlList = riskControlList;
    }
}
