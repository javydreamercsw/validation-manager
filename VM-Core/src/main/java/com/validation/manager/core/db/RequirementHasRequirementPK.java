/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class RequirementHasRequirementPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_id")
    private int requirementId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_version")
    private int requirementVersion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "parent_requirement_id")
    private int parentRequirementId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "parent_requirement_version")
    private int parentRequirementVersion;

    public RequirementHasRequirementPK() {
    }

    public RequirementHasRequirementPK(int requirementId, int requirementVersion, int parentRequirementId, int parentRequirementVersion) {
        this.requirementId = requirementId;
        this.requirementVersion = requirementVersion;
        this.parentRequirementId = parentRequirementId;
        this.parentRequirementVersion = parentRequirementVersion;
    }

    public int getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(int requirementId) {
        this.requirementId = requirementId;
    }

    public int getRequirementVersion() {
        return requirementVersion;
    }

    public void setRequirementVersion(int requirementVersion) {
        this.requirementVersion = requirementVersion;
    }

    public int getParentRequirementId() {
        return parentRequirementId;
    }

    public void setParentRequirementId(int parentRequirementId) {
        this.parentRequirementId = parentRequirementId;
    }

    public int getParentRequirementVersion() {
        return parentRequirementVersion;
    }

    public void setParentRequirementVersion(int parentRequirementVersion) {
        this.parentRequirementVersion = parentRequirementVersion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) requirementId;
        hash += (int) requirementVersion;
        hash += (int) parentRequirementId;
        hash += (int) parentRequirementVersion;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequirementHasRequirementPK)) {
            return false;
        }
        RequirementHasRequirementPK other = (RequirementHasRequirementPK) object;
        if (this.requirementId != other.requirementId) {
            return false;
        }
        if (this.requirementVersion != other.requirementVersion) {
            return false;
        }
        if (this.parentRequirementId != other.parentRequirementId) {
            return false;
        }
        if (this.parentRequirementVersion != other.parentRequirementVersion) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementHasRequirementPK[ requirementId=" + requirementId + ", requirementVersion=" + requirementVersion + ", parentRequirementId=" + parentRequirementId + ", parentRequirementVersion=" + parentRequirementVersion + " ]";
    }
    
}
