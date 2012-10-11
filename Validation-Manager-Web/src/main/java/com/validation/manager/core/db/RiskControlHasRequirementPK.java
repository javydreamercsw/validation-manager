/*
 * To change this template, choose Tools | Templates
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
public class RiskControlHasRequirementPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "risk_control_id")
    private int riskControlId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "risk_control_risk_control_type_id")
    private int riskControlRiskControlTypeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_id")
    private int requirementId;

    public RiskControlHasRequirementPK() {
    }

    public RiskControlHasRequirementPK(int riskControlId, int riskControlRiskControlTypeId, int requirementId) {
        this.riskControlId = riskControlId;
        this.riskControlRiskControlTypeId = riskControlRiskControlTypeId;
        this.requirementId = requirementId;
    }

    public int getRiskControlId() {
        return riskControlId;
    }

    public void setRiskControlId(int riskControlId) {
        this.riskControlId = riskControlId;
    }

    public int getRiskControlRiskControlTypeId() {
        return riskControlRiskControlTypeId;
    }

    public void setRiskControlRiskControlTypeId(int riskControlRiskControlTypeId) {
        this.riskControlRiskControlTypeId = riskControlRiskControlTypeId;
    }

    public int getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(int requirementId) {
        this.requirementId = requirementId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) riskControlId;
        hash += (int) riskControlRiskControlTypeId;
        hash += (int) requirementId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiskControlHasRequirementPK)) {
            return false;
        }
        RiskControlHasRequirementPK other = (RiskControlHasRequirementPK) object;
        if (this.riskControlId != other.riskControlId) {
            return false;
        }
        if (this.riskControlRiskControlTypeId != other.riskControlRiskControlTypeId) {
            return false;
        }
        if (this.requirementId != other.requirementId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskControlHasRequirementPK[ riskControlId=" + riskControlId + ", riskControlRiskControlTypeId=" + riskControlRiskControlTypeId + ", requirementId=" + requirementId + " ]";
    }
    
}
