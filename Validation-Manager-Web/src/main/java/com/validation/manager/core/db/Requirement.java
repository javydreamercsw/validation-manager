/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import com.validation.manager.core.db.fmea.RiskControl;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "requirement", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"project_id", "unique_id"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Requirement.findAll", query = "SELECT r FROM Requirement r"),
    @NamedQuery(name = "Requirement.findById", query = "SELECT r FROM Requirement r WHERE r.requirementPK.id = :id"),
    @NamedQuery(name = "Requirement.findByVersion", query = "SELECT r FROM Requirement r WHERE r.requirementPK.version = :version"),
    @NamedQuery(name = "Requirement.findByUniqueId", query = "SELECT r FROM Requirement r WHERE r.uniqueId = :uniqueId")})
public class Requirement implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RequirementPK requirementPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "unique_id", nullable = false, length = 45)
    private String uniqueId;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description", nullable = false, length = 65535)
    private String description;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes", length = 65535)
    private String notes;
    @JoinTable(name = "requirement_has_exception", joinColumns = {
        @JoinColumn(name = "requirement_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "requirement_version", referencedColumnName = "version")}, inverseJoinColumns = {
        @JoinColumn(name = "exception_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "exception_reporter_id", referencedColumnName = "reporter_id", nullable = false)})
    @ManyToMany
    private List<VmException> vmExceptionList;
    @JoinTable(name = "requirement_has_requirement", joinColumns = {
        @JoinColumn(name = "requirement_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "requirement_version", referencedColumnName = "version", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "parent_requirement_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "parent_requirement_version", referencedColumnName = "version", nullable = false)})
    @ManyToMany
    private List<Requirement> requirementList;
    @ManyToMany(mappedBy = "requirementList")
    private List<Requirement> requirementList1;
    @ManyToMany(mappedBy = "requirementList")
    private List<Step> stepList;
    @ManyToMany(mappedBy = "requirementList")
    private List<RiskControl> riskControlList;
    @JoinColumns({
        @JoinColumn(name = "requirement_spec_node_id", referencedColumnName = "id"),
        @JoinColumn(name = "requirement_spec_node_requirement_spec_id", referencedColumnName = "requirement_spec_id")})
    @ManyToOne
    private RequirementSpecNode requirementSpecNode;
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Project project;
    @JoinColumn(name = "requirement_status_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private RequirementStatus requirementStatus;
    @JoinColumn(name = "requirement_type_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private RequirementType requirementType;

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
    public List<VmException> getVmExceptionList() {
        return vmExceptionList;
    }

    public void setVmExceptionList(List<VmException> vmExceptionList) {
        this.vmExceptionList = vmExceptionList;
    }

    @XmlTransient
    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

    @XmlTransient
    public List<Requirement> getRequirementList1() {
        return requirementList1;
    }

    public void setRequirementList1(List<Requirement> requirementList1) {
        this.requirementList1 = requirementList1;
    }

    @XmlTransient
    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    @XmlTransient
    public List<RiskControl> getRiskControlList() {
        return riskControlList;
    }

    public void setRiskControlList(List<RiskControl> riskControlList) {
        this.riskControlList = riskControlList;
    }

    public RequirementSpecNode getRequirementSpecNode() {
        return requirementSpecNode;
    }

    public void setRequirementSpecNode(RequirementSpecNode requirementSpecNode) {
        this.requirementSpecNode = requirementSpecNode;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public RequirementStatus getRequirementStatus() {
        return requirementStatus;
    }

    public void setRequirementStatus(RequirementStatus requirementStatus) {
        this.requirementStatus = requirementStatus;
    }

    public RequirementType getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(RequirementType requirementType) {
        this.requirementType = requirementType;
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
    
}
