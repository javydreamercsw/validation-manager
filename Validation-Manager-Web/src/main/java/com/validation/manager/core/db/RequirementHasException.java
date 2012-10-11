/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "requirement_has_exception")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RequirementHasException.findAll", query = "SELECT r FROM RequirementHasException r"),
    @NamedQuery(name = "RequirementHasException.findByRequirementId", query = "SELECT r FROM RequirementHasException r WHERE r.requirementHasExceptionPK.requirementId = :requirementId"),
    @NamedQuery(name = "RequirementHasException.findByExceptionId", query = "SELECT r FROM RequirementHasException r WHERE r.requirementHasExceptionPK.exceptionId = :exceptionId"),
    @NamedQuery(name = "RequirementHasException.findByExceptionReporterId", query = "SELECT r FROM RequirementHasException r WHERE r.requirementHasExceptionPK.exceptionReporterId = :exceptionReporterId"),
    @NamedQuery(name = "RequirementHasException.findByRequirementHasExceptioncol", query = "SELECT r FROM RequirementHasException r WHERE r.requirementHasExceptioncol = :requirementHasExceptioncol")})
public class RequirementHasException implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RequirementHasExceptionPK requirementHasExceptionPK;
    @Size(max = 45)
    @Column(name = "requirement_has_exceptioncol")
    private String requirementHasExceptioncol;
    @JoinColumns({
        @JoinColumn(name = "exception_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "exception_reporter_id", referencedColumnName = "reporter_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private VmException vmException;
    @PrimaryKeyJoinColumns({
        @PrimaryKeyJoinColumn(name = "requirement_id", referencedColumnName = "id"),
        @PrimaryKeyJoinColumn(name = "requirement_version", referencedColumnName = "version")})
    @ManyToOne(optional = false)
    private Requirement requirement;

    public RequirementHasException() {
    }

    public RequirementHasException(RequirementHasExceptionPK requirementHasExceptionPK) {
        this.requirementHasExceptionPK = requirementHasExceptionPK;
    }

    public RequirementHasException(int requirementId, int exceptionId, int exceptionReporterId) {
        this.requirementHasExceptionPK = new RequirementHasExceptionPK(requirementId, exceptionId, exceptionReporterId);
    }

    public RequirementHasExceptionPK getRequirementHasExceptionPK() {
        return requirementHasExceptionPK;
    }

    public void setRequirementHasExceptionPK(RequirementHasExceptionPK requirementHasExceptionPK) {
        this.requirementHasExceptionPK = requirementHasExceptionPK;
    }

    public String getRequirementHasExceptioncol() {
        return requirementHasExceptioncol;
    }

    public void setRequirementHasExceptioncol(String requirementHasExceptioncol) {
        this.requirementHasExceptioncol = requirementHasExceptioncol;
    }

    public VmException getVmException() {
        return vmException;
    }

    public void setVmException(VmException vmException) {
        this.vmException = vmException;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (requirementHasExceptionPK != null ? requirementHasExceptionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequirementHasException)) {
            return false;
        }
        RequirementHasException other = (RequirementHasException) object;
        if ((this.requirementHasExceptionPK == null && other.requirementHasExceptionPK != null) || (this.requirementHasExceptionPK != null && !this.requirementHasExceptionPK.equals(other.requirementHasExceptionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementHasException[ requirementHasExceptionPK=" + requirementHasExceptionPK + " ]";
    }
}
