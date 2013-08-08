/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "requirement_has_requirement")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RequirementHasRequirement.findAll", query = "SELECT r FROM RequirementHasRequirement r"),
    @NamedQuery(name = "RequirementHasRequirement.findByRequirementId", query = "SELECT r FROM RequirementHasRequirement r WHERE r.requirementHasRequirementPK.requirementId = :requirementId"),
    @NamedQuery(name = "RequirementHasRequirement.findByRequirementVersion", query = "SELECT r FROM RequirementHasRequirement r WHERE r.requirementHasRequirementPK.requirementVersion = :requirementVersion"),
    @NamedQuery(name = "RequirementHasRequirement.findByParentRequirementId", query = "SELECT r FROM RequirementHasRequirement r WHERE r.requirementHasRequirementPK.parentRequirementId = :parentRequirementId"),
    @NamedQuery(name = "RequirementHasRequirement.findByParentRequirementVersion", query = "SELECT r FROM RequirementHasRequirement r WHERE r.requirementHasRequirementPK.parentRequirementVersion = :parentRequirementVersion")})
public class RequirementHasRequirement implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RequirementHasRequirementPK requirementHasRequirementPK;
    @JoinColumns({
        @JoinColumn(name = "parent_requirement_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "parent_requirement_version", referencedColumnName = "version", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Requirement parentRequirement;
    @JoinColumns({
        @JoinColumn(name = "requirement_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "requirement_version", referencedColumnName = "version", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Requirement childRequirement;

    public RequirementHasRequirement() {
    }

    public RequirementHasRequirement(RequirementHasRequirementPK requirementHasRequirementPK) {
        this.requirementHasRequirementPK = requirementHasRequirementPK;
    }

    public RequirementHasRequirement(int requirementId, int requirementVersion, int parentRequirementId, int parentRequirementVersion) {
        this.requirementHasRequirementPK = new RequirementHasRequirementPK(requirementId, requirementVersion, parentRequirementId, parentRequirementVersion);
    }

    public RequirementHasRequirementPK getRequirementHasRequirementPK() {
        return requirementHasRequirementPK;
    }

    public void setRequirementHasRequirementPK(RequirementHasRequirementPK requirementHasRequirementPK) {
        this.requirementHasRequirementPK = requirementHasRequirementPK;
    }

    public Requirement getParentRequirement() {
        return parentRequirement;
    }

    public void setParentRequirement(Requirement requirement) {
        this.parentRequirement = requirement;
    }

    public Requirement getChildRequirement() {
        return childRequirement;
    }

    public void setChildRequirement(Requirement requirement1) {
        this.childRequirement = requirement1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (requirementHasRequirementPK != null ? requirementHasRequirementPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequirementHasRequirement)) {
            return false;
        }
        RequirementHasRequirement other = (RequirementHasRequirement) object;
        if ((this.requirementHasRequirementPK == null && other.requirementHasRequirementPK != null) || (this.requirementHasRequirementPK != null && !this.requirementHasRequirementPK.equals(other.requirementHasRequirementPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementHasRequirement[ requirementHasRequirementPK=" + requirementHasRequirementPK + " ]";
    }
    
}
