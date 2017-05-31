/* 
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
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
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_major_version")
    private int requirementMajorVersion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_mid_version")
    private int requirementMidVersion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requirement_minor_version")
    private int requirementMinorVersion;

    public RiskControlHasRequirementPK() {
    }

    public RiskControlHasRequirementPK(int riskControlId,
            int riskControlRiskControlTypeId, int requirementId,
            int requirementMajorVersion, int requirementMidVersion,
            int requirementMinorVersion) {
        this.riskControlId = riskControlId;
        this.riskControlRiskControlTypeId = riskControlRiskControlTypeId;
        this.requirementId = requirementId;
        this.requirementMajorVersion = requirementMajorVersion;
        this.requirementMidVersion = requirementMidVersion;
        this.requirementMinorVersion = requirementMinorVersion;
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

    public int getRequirementMajorVersion() {
        return requirementMajorVersion;
    }

    public void setRequirementMajorVersion(int requirementMajorVersion) {
        this.requirementMajorVersion = requirementMajorVersion;
    }

    public int getRequirementMidVersion() {
        return requirementMidVersion;
    }

    public void setRequirementMidVersion(int requirementMidVersion) {
        this.requirementMidVersion = requirementMidVersion;
    }

    public int getRequirementMinorVersion() {
        return requirementMinorVersion;
    }

    public void setRequirementMinorVersion(int requirementMinorVersion) {
        this.requirementMinorVersion = requirementMinorVersion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) riskControlId;
        hash += (int) riskControlRiskControlTypeId;
        hash += (int) requirementId;
        hash += (int) requirementMajorVersion;
        hash += (int) requirementMidVersion;
        hash += (int) requirementMinorVersion;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
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
        if (this.requirementMajorVersion != other.requirementMajorVersion) {
            return false;
        }
        if (this.requirementMidVersion != other.requirementMidVersion) {
            return false;
        }
        return this.requirementMinorVersion == other.requirementMinorVersion;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskControlHasRequirementPK[ "
                + "riskControlId=" + riskControlId
                + ", riskControlRiskControlTypeId="
                + riskControlRiskControlTypeId + ", requirementId="
                + requirementId + ", requirementMajorVersion="
                + requirementMajorVersion + ", requirementMidVersion="
                + requirementMidVersion + ", requirementMinorVersion="
                + requirementMinorVersion + " ]";
    }
}
