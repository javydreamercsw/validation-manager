/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import com.validation.manager.core.db.fmea.RiskControl;
import java.io.Serializable;
import javax.persistence.Basic;
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "risk_control_has_requirement")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RiskControlHasRequirement.findAll", query = "SELECT r FROM RiskControlHasRequirement r"),
    @NamedQuery(name = "RiskControlHasRequirement.findByRiskControlId", query = "SELECT r FROM RiskControlHasRequirement r WHERE r.riskControlHasRequirementPK.riskControlId = :riskControlId"),
    @NamedQuery(name = "RiskControlHasRequirement.findByRiskControlRiskControlTypeId", query = "SELECT r FROM RiskControlHasRequirement r WHERE r.riskControlHasRequirementPK.riskControlRiskControlTypeId = :riskControlRiskControlTypeId"),
    @NamedQuery(name = "RiskControlHasRequirement.findByRequirementId", query = "SELECT r FROM RiskControlHasRequirement r WHERE r.riskControlHasRequirementPK.requirementId = :requirementId"),
    @NamedQuery(name = "RiskControlHasRequirement.findByRequirementVersion", query = "SELECT r FROM RiskControlHasRequirement r WHERE r.requirementVersion = :requirementVersion")})
public class RiskControlHasRequirement implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RiskControlHasRequirementPK riskControlHasRequirementPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_version")
    private int requirementVersion;
    @JoinColumns({
        @JoinColumn(name = "risk_control_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "risk_control_risk_control_type_id", referencedColumnName = "risk_control_type_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private RiskControl riskControl;
    @PrimaryKeyJoinColumns({
        @PrimaryKeyJoinColumn(name = "requirement_id", referencedColumnName = "id"),
        @PrimaryKeyJoinColumn(name = "requirement_version", referencedColumnName = "version")})
    @ManyToOne(optional = false)
    private Requirement requirement;

    public RiskControlHasRequirement() {
    }

    public RiskControlHasRequirement(RiskControlHasRequirementPK riskControlHasRequirementPK) {
        this.riskControlHasRequirementPK = riskControlHasRequirementPK;
    }

    public RiskControlHasRequirement(RiskControlHasRequirementPK riskControlHasRequirementPK, int requirementVersion) {
        this.riskControlHasRequirementPK = riskControlHasRequirementPK;
        this.requirementVersion = requirementVersion;
    }

    public RiskControlHasRequirement(int riskControlId, int riskControlRiskControlTypeId, int requirementId) {
        this.riskControlHasRequirementPK = new RiskControlHasRequirementPK(riskControlId, riskControlRiskControlTypeId, requirementId);
    }

    public RiskControlHasRequirementPK getRiskControlHasRequirementPK() {
        return riskControlHasRequirementPK;
    }

    public void setRiskControlHasRequirementPK(RiskControlHasRequirementPK riskControlHasRequirementPK) {
        this.riskControlHasRequirementPK = riskControlHasRequirementPK;
    }

    public int getRequirementVersion() {
        return requirementVersion;
    }

    public void setRequirementVersion(int requirementVersion) {
        this.requirementVersion = requirementVersion;
    }

    public RiskControl getRiskControl() {
        return riskControl;
    }

    public void setRiskControl(RiskControl riskControl) {
        this.riskControl = riskControl;
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
        hash += (riskControlHasRequirementPK != null ? riskControlHasRequirementPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiskControlHasRequirement)) {
            return false;
        }
        RiskControlHasRequirement other = (RiskControlHasRequirement) object;
        if ((this.riskControlHasRequirementPK == null && other.riskControlHasRequirementPK != null) || (this.riskControlHasRequirementPK != null && !this.riskControlHasRequirementPK.equals(other.riskControlHasRequirementPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskControlHasRequirement[ riskControlHasRequirementPK=" + riskControlHasRequirementPK + " ]";
    }
    
}
