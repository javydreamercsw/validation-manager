/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
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

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "vm_exception")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VmException.findAll", query = "SELECT v FROM VmException v"),
    @NamedQuery(name = "VmException.findById", query = "SELECT v FROM VmException v WHERE v.vmExceptionPK.id = :id"),
    @NamedQuery(name = "VmException.findByReporterId", query = "SELECT v FROM VmException v WHERE v.vmExceptionPK.reporterId = :reporterId"),
    @NamedQuery(name = "VmException.findByReportDate", query = "SELECT v FROM VmException v WHERE v.reportDate = :reportDate"),
    @NamedQuery(name = "VmException.findByCloseDate", query = "SELECT v FROM VmException v WHERE v.closeDate = :closeDate")})
public class VmException implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected VmExceptionPK vmExceptionPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "report_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date reportDate;
    @Column(name = "close_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date closeDate;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description", nullable = false, length = 65535)
    private String description;
    @JoinTable(name = "exception_has_corrective_action", joinColumns = {
        @JoinColumn(name = "exception_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "exception_reporter_id", referencedColumnName = "reporter_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "corrective_action_id", referencedColumnName = "id", nullable = false)})
    @ManyToMany
    private List<CorrectiveAction> correctiveActionList;
    @ManyToMany(mappedBy = "vmExceptionList")
    private List<Requirement> requirementList;
//    @ManyToMany(mappedBy = "vmExceptionList")
//    private List<Step> stepList;
    @JoinTable(name = "exception_has_root_cause", joinColumns = {
        @JoinColumn(name = "exception_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "exception_reporter_id", referencedColumnName = "reporter_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "root_cause_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "root_cause_root_cause_type_id", referencedColumnName = "root_cause_type_id", nullable = false)})
    @ManyToMany
    private List<RootCause> rootCauseList;
    @JoinTable(name = "exception_has_investigation", joinColumns = {
        @JoinColumn(name = "exception_id", referencedColumnName = "id", nullable = false),
        @JoinColumn(name = "exception_reporter_id", referencedColumnName = "reporter_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "investigation_id", referencedColumnName = "id", nullable = false)})
    @ManyToMany
    private List<Investigation> investigationList;
    @JoinColumn(name = "reporter_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private VmUser vmUser;
    @JoinTable(name = "step_has_exception", joinColumns = {
        @JoinColumn(name = "exception_id", referencedColumnName = "id"),
        @JoinColumn(name = "exception_reporter_id", referencedColumnName = "reporter_id")}, inverseJoinColumns = {
        @JoinColumn(name = "step_id", referencedColumnName = "id"),
        @JoinColumn(name = "step_test_case_id", referencedColumnName = "test_case_id"),
        @JoinColumn(name = "step_test_case_test_id", referencedColumnName = "test_case_test_id")})
    @ManyToMany
    private List<Step> stepList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vmException")
    private List<RequirementHasException> requirementHasExceptionList;

    public VmException() {
    }

    public VmException(VmExceptionPK vmExceptionPK) {
        this.vmExceptionPK = vmExceptionPK;
    }

    public VmException(VmExceptionPK vmExceptionPK, Date reportDate, String description) {
        this.vmExceptionPK = vmExceptionPK;
        this.reportDate = reportDate;
        this.description = description;
    }

    public VmException(int reporterId) {
        this.vmExceptionPK = new VmExceptionPK(reporterId);
    }

    public VmExceptionPK getVmExceptionPK() {
        return vmExceptionPK;
    }

    public void setVmExceptionPK(VmExceptionPK vmExceptionPK) {
        this.vmExceptionPK = vmExceptionPK;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<CorrectiveAction> getCorrectiveActionList() {
        return correctiveActionList;
    }

    public void setCorrectiveActionList(List<CorrectiveAction> correctiveActionList) {
        this.correctiveActionList = correctiveActionList;
    }

    @XmlTransient
    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

    @XmlTransient
    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    @XmlTransient
    public List<RootCause> getRootCauseList() {
        return rootCauseList;
    }

    public void setRootCauseList(List<RootCause> rootCauseList) {
        this.rootCauseList = rootCauseList;
    }

    @XmlTransient
    public List<Investigation> getInvestigationList() {
        return investigationList;
    }

    public void setInvestigationList(List<Investigation> investigationList) {
        this.investigationList = investigationList;
    }

    public VmUser getVmUser() {
        return vmUser;
    }

    public void setVmUser(VmUser vmUser) {
        this.vmUser = vmUser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vmExceptionPK != null ? vmExceptionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VmException)) {
            return false;
        }
        VmException other = (VmException) object;
        if ((this.vmExceptionPK == null && other.vmExceptionPK != null) || (this.vmExceptionPK != null && !this.vmExceptionPK.equals(other.vmExceptionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.VmException[ vmExceptionPK=" + vmExceptionPK + " ]";
    }
}
