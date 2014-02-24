package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "risk_control_has_requirement")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RiskControlHasRequirement.findAll",
            query = "SELECT r FROM RiskControlHasRequirement r"),
    @NamedQuery(name = "RiskControlHasRequirement.findByRiskControlId",
            query = "SELECT r FROM RiskControlHasRequirement r WHERE r.riskControlHasRequirementPK.riskControlId = :riskControlId"),
    @NamedQuery(name = "RiskControlHasRequirement.findByRiskControlRiskControlTypeId",
            query = "SELECT r FROM RiskControlHasRequirement r WHERE r.riskControlHasRequirementPK.riskControlRiskControlTypeId = :riskControlRiskControlTypeId"),
    @NamedQuery(name = "RiskControlHasRequirement.findByRequirementId",
            query = "SELECT r FROM RiskControlHasRequirement r WHERE r.riskControlHasRequirementPK.requirementId = :requirementId"),
    @NamedQuery(name = "RiskControlHasRequirement.findByRequirementMajorVersion",
            query = "SELECT r FROM RiskControlHasRequirement r WHERE r.riskControlHasRequirementPK.requirementMajorVersion = :requirementMajorVersion"),
    @NamedQuery(name = "RiskControlHasRequirement.findByRequirementMidVersion",
            query = "SELECT r FROM RiskControlHasRequirement r WHERE r.riskControlHasRequirementPK.requirementMidVersion = :requirementMidVersion"),
    @NamedQuery(name = "RiskControlHasRequirement.findByRequirementMinorVersion",
            query = "SELECT r FROM RiskControlHasRequirement r WHERE r.riskControlHasRequirementPK.requirementMinorVersion = :requirementMinorVersion")})
public class RiskControlHasRequirement implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RiskControlHasRequirementPK riskControlHasRequirementPK;

    public RiskControlHasRequirement() {
    }

    public RiskControlHasRequirement(RiskControlHasRequirementPK riskControlHasRequirementPK) {
        this.riskControlHasRequirementPK = riskControlHasRequirementPK;
    }

    public RiskControlHasRequirement(int riskControlId, int riskControlRiskControlTypeId, int requirementId, int requirementMajorVersion, int requirementMidVersion, int requirementMinorVersion) {
        this.riskControlHasRequirementPK = new RiskControlHasRequirementPK(riskControlId, riskControlRiskControlTypeId, requirementId, requirementMajorVersion, requirementMidVersion, requirementMinorVersion);
    }

    public RiskControlHasRequirementPK getRiskControlHasRequirementPK() {
        return riskControlHasRequirementPK;
    }

    public void setRiskControlHasRequirementPK(RiskControlHasRequirementPK riskControlHasRequirementPK) {
        this.riskControlHasRequirementPK = riskControlHasRequirementPK;
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
