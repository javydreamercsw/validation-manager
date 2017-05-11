package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "history")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "History.findAll",
            query = "SELECT h FROM History h")
    , @NamedQuery(name = "History.findById",
            query = "SELECT h FROM History h WHERE h.id = :id")
    , @NamedQuery(name = "History.findByVersionMajor",
            query = "SELECT h FROM History h WHERE h.versionMajor = :versionMajor")
    , @NamedQuery(name = "History.findByVersionMid",
            query = "SELECT h FROM History h WHERE h.versionMid = :versionMid")
    , @NamedQuery(name = "History.findByVersionMinor",
            query = "SELECT h FROM History h WHERE h.versionMinor = :versionMinor")
    , @NamedQuery(name = "History.findByModificationTime",
            query = "SELECT h FROM History h WHERE h.modificationTime = :modificationTime")})
public class History implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE,
            generator = "History_IDGEN")
    @TableGenerator(name = "History_IDGEN", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "history",
            initialValue = 1,
            allocationSize = 1)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version_major")
    private int versionMajor = 0;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version_mid")
    private int versionMid = 0;
    @Basic(optional = false)
    @NotNull
    @Column(name = "version_minor")
    private int versionMinor = 1;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "reason")
    private String reason;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modification_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationTime;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "history")
    private List<HistoryField> historyFieldList;
    @JoinColumn(name = "modifier_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private VmUser modifierId;
    @JoinTable(name = "requirement_has_history", joinColumns = {
        @JoinColumn(name = "history_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                @JoinColumn(name = "requirement_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Requirement> requirementList;
    @JoinTable(name = "vm_setting_has_history", joinColumns = {
        @JoinColumn(name = "history_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "vm_setting_id", referencedColumnName = "id")})
    @ManyToMany
    private List<VmSetting> vmSettingList;
    @JoinTable(name = "project_has_history", joinColumns = {
        @JoinColumn(name = "history_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "project_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Project> projectList;
    @ManyToMany(mappedBy = "historyList")
    private List<ExecutionStep> executionStepList;
    @ManyToMany(mappedBy = "historyList")
    private List<Baseline> baselineList;
    @JoinTable(name = "step_has_history", joinColumns = {
        @JoinColumn(name = "history_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "step_id", referencedColumnName = "id")
        , @JoinColumn(name = "step_test_case_id", referencedColumnName = "test_case_id")})
    @ManyToMany
    private List<Step> stepList;

    public History() {
    }

    public History(Integer id) {
        this.id = id;
    }

    public History(Integer id, int versionMajor, int versionMid,
            int versionMinor, String reason, Date modificationTime) {
        this.id = id;
        this.versionMajor = versionMajor;
        this.versionMid = versionMid;
        this.versionMinor = versionMinor;
        this.reason = reason;
        this.modificationTime = modificationTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getMajorVersion() {
        return versionMajor;
    }

    public void setMajorVersion(int versionMajor) {
        this.versionMajor = versionMajor;
    }

    public int getMidVersion() {
        return versionMid;
    }

    public void setMidVersion(int versionMid) {
        this.versionMid = versionMid;
    }

    public int getMinorVersion() {
        return versionMinor;
    }

    public void setMinorVersion(int versionMinor) {
        this.versionMinor = versionMinor;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    @XmlTransient
    @JsonIgnore
    public List<HistoryField> getHistoryFieldList() {
        return historyFieldList;
    }

    public void setHistoryFieldList(List<HistoryField> historyFieldList) {
        this.historyFieldList = historyFieldList;
    }

    public VmUser getModifierId() {
        return modifierId;
    }

    public void setModifierId(VmUser modifierId) {
        this.modifierId = modifierId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof History)) {
            return false;
        }
        History other = (History) object;
        return !((this.id == null && other.id != null) || (this.id != null
                && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Fields: ");
        getHistoryFieldList().forEach(hf -> {
            if (!sb.toString().isEmpty()) {
                sb.append(", ");
            }
            sb.append(hf.getFieldName())
                    .append("= ")
                    .append(hf.getFieldValue());
        });
        return "History{" + "id=" + id + ", versionMajor=" + versionMajor
                + ", versionMid=" + versionMid + ", versionMinor="
                + versionMinor + ", reason=" + reason + ", modificationTime="
                + modificationTime + ", modifierId=" + modifierId
                + "," + sb.toString() + '}';
    }

    @XmlTransient
    @JsonIgnore
    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

    /**
     * @return the vmSettingList
     */
    public List<VmSetting> getVmSettingList() {
        return vmSettingList;
    }

    /**
     * @param vmSettingList the vmSettingList to set
     */
    public void setVmSettingList(List<VmSetting> vmSettingList) {
        this.vmSettingList = vmSettingList;
    }

    /**
     * @return the projectList
     */
    public List<Project> getProjectList() {
        return projectList;
    }

    /**
     * @param projectList the projectList to set
     */
    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Baseline> getBaselineList() {
        return baselineList;
    }

    public void setBaselineList(List<Baseline> baselineList) {
        this.baselineList = baselineList;
    }

    @XmlTransient
    @JsonIgnore
    public List<ExecutionStep> getExecutionStepList() {
        return executionStepList;
    }

    public void setExecutionStepList(List<ExecutionStep> executionStepList) {
        this.executionStepList = executionStepList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }
}
