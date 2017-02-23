package com.validation.manager.core.db;

import com.validation.manager.core.server.core.Versionable;
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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "requirement")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Requirement.findAll",
            query = "SELECT r FROM Requirement r")
    ,
    @NamedQuery(name = "Requirement.findByUniqueId",
            query = "SELECT r FROM Requirement r WHERE r.uniqueId = :uniqueId")
    ,
    @NamedQuery(name = "Requirement.findById",
            query = "SELECT r FROM Requirement r WHERE r.id = :id")})
public class Requirement extends Versionable implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ReqGen")
    @TableGenerator(name = "ReqGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "requirement",
            allocationSize = 1,
            initialValue = 1000)
    @NotNull
    @Column(name = "id")
    private int id;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "notes")
    private String notes;
    @Size(max = 255)
    @Column(name = "unique_id")
    private String uniqueId;
    @JoinTable(name = "requirement_has_requirement", joinColumns = {
        @JoinColumn(name = "requirement_id", referencedColumnName = "id")
        ,@JoinColumn(name = "requirement_major_version",
                referencedColumnName = "major_version", insertable = false,
                updatable = false)
        ,@JoinColumn(name = "requirement_mid_version",
                referencedColumnName = "mid_version", insertable = false,
                updatable = false)
        ,@JoinColumn(name = "requirement_minor_version",
                referencedColumnName = "minor_version", insertable = false,
                updatable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "parent_requirement_id",
                referencedColumnName = "id")
        ,@JoinColumn(name = "parent_major_version",
                referencedColumnName = "major_version", insertable = false,
                updatable = false)
        ,@JoinColumn(name = "parent_mid_version",
                referencedColumnName = "mid_version", insertable = false,
                updatable = false)
        ,@JoinColumn(name = "parent_minor_version",
                referencedColumnName = "minor_version", insertable = false,
                updatable = false)})
    @ManyToMany
    private List<Requirement> requirementList;
    @ManyToMany(mappedBy = "requirementList")
    private List<Requirement> requirementList1;
    @JoinTable(name = "step_has_requirement", joinColumns = {
        @JoinColumn(name = "requirement_id", referencedColumnName = "id")
        ,@JoinColumn(name = "requirement_major_version",
                referencedColumnName = "major_version", insertable = false,
                updatable = false)
        ,@JoinColumn(name = "requirement_mid_version",
                referencedColumnName = "mid_version", insertable = false,
                updatable = false)
        ,@JoinColumn(name = "requirement_minor_version",
                referencedColumnName = "minor_version", insertable = false,
                updatable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "step_test_case_test_id",
                referencedColumnName = "test_case_test_id")
        ,@JoinColumn(name = "step_id", referencedColumnName = "id")
        ,@JoinColumn(name = "step_test_case_id",
                referencedColumnName = "test_case_id")})
    @ManyToMany
    private List<Step> stepList;
    @JoinTable(name = "risk_control_has_requirement", joinColumns = {
        @JoinColumn(name = "requirement_id", referencedColumnName = "id")
        ,@JoinColumn(name = "requirement_major_version",
                referencedColumnName = "major_version", insertable = false,
                updatable = false)
        ,@JoinColumn(name = "requirement_mid_version",
                referencedColumnName = "mid_version", insertable = false,
                updatable = false)
        ,@JoinColumn(name = "requirement_minor_version",
                referencedColumnName = "minor_version", insertable = false,
                updatable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "risk_control_id", referencedColumnName = "id")
        ,@JoinColumn(name = "risk_control_risk_control_type_id",
                referencedColumnName = "risk_control_type_id")})
    @ManyToMany
    private List<RiskControl> riskControlList;
    @JoinColumn(name = "requirement_type_id", referencedColumnName = "id")
    @ManyToOne
    private RequirementType requirementTypeId;
    @JoinColumn(name = "requirement_status_id", referencedColumnName = "id")
    @ManyToOne
    private RequirementStatus requirementStatusId;
    @JoinColumns({
        @JoinColumn(name = "requirement_spec_node_id",
                referencedColumnName = "id")
        ,@JoinColumn(name = "requirement_spec_node_requirement_spec_project_id",
                referencedColumnName = "requirement_spec_project_id")
        ,@JoinColumn(name = "requirement_spec_node_requirement_spec_spec_level_id",
                referencedColumnName = "requirement_spec_spec_level_id")
        ,@JoinColumn(name = "requirement_spec_node_requirement_spec_id",
                referencedColumnName = "requirement_spec_id")})
    @ManyToOne
    private RequirementSpecNode requirementSpecNode;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "requirement")
    private List<RequirementHasException> requirementHasExceptionList;
    @JoinTable(name = "requirement_has_exception", joinColumns = {
        @JoinColumn(name = "requirement_id", referencedColumnName = "id")
        , @JoinColumn(name = "requirement_major_version",
                referencedColumnName = "major_version")
        , @JoinColumn(name = "requirement_mid_version",
                referencedColumnName = "mid_version")
        , @JoinColumn(name = "requirement_minor_version",
                referencedColumnName = "minor_version")}, inverseJoinColumns = {
        @JoinColumn(name = "vm_exception_id", referencedColumnName = "id")
        , @JoinColumn(name = "vm_exception_reporter_id",
                referencedColumnName = "reporter_id")})
    @ManyToMany
    private List<VmException> vmExceptionList;

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

    public Requirement(String uniqueId, String description, String notes,
            int major_version, int mid_version, int minor_version) {
        this.uniqueId = uniqueId;
        this.description = description;
        this.notes = notes;
        setMajorVersion(major_version);
        setMidVersion(mid_version);
        setMinorVersion(minor_version);
        setRequirementHasExceptionList(new ArrayList<>());
        setRequirementList(new ArrayList<>());
        setRequirementList1(new ArrayList<>());
        setRiskControlList(new ArrayList<>());
        setStepList(new ArrayList<>());
    }

    public Requirement(int major_version, int mid_version, int minor_version) {
        setMajorVersion(major_version);
        setMidVersion(mid_version);
        setMinorVersion(minor_version);
        setRequirementHasExceptionList(new ArrayList<>());
        setRequirementList(new ArrayList<>());
        setRequirementList1(new ArrayList<>());
        setRiskControlList(new ArrayList<>());
        setStepList(new ArrayList<>());
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

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @XmlTransient
    @JsonIgnore
    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public final void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Requirement> getRequirementList1() {
        return requirementList1;
    }

    public final void setRequirementList1(List<Requirement> requirementList1) {
        this.requirementList1 = requirementList1;
    }

    @XmlTransient
    @JsonIgnore
    public List<Step> getStepList() {
        return stepList;
    }

    public final void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskControl> getRiskControlList() {
        return riskControlList;
    }

    public final void setRiskControlList(List<RiskControl> riskControlList) {
        this.riskControlList = riskControlList;
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

    @XmlTransient
    @JsonIgnore
    public List<RequirementHasException> getRequirementHasExceptionList() {
        return requirementHasExceptionList;
    }

    public final void setRequirementHasExceptionList(List<RequirementHasException> requirementHasExceptionList) {
        this.requirementHasExceptionList = requirementHasExceptionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += getId();
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Requirement)) {
            return false;
        }
        Requirement other = (Requirement) object;
        return this.getId() == other.getId();
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Requirement[ id=" + getId()
                + ", uniqueId=" + getUniqueId() + " ]" + super.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlTransient
    @JsonIgnore
    public List<VmException> getVmExceptionList() {
        return vmExceptionList;
    }

    public void setVmExceptionList(List<VmException> vmExceptionList) {
        this.vmExceptionList = vmExceptionList;
    }
}
