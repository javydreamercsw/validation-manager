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
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "risk_control_has_requirement")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RiskControlHasRequirement.findAll",
            query = "SELECT r FROM RiskControlHasRequirement r")
    , @NamedQuery(name = "RiskControlHasRequirement.findByRiskControlId",
            query = "SELECT r FROM RiskControlHasRequirement r WHERE "
            + "r.riskControlHasRequirementPK.riskControlId = :riskControlId")
    , @NamedQuery(name = "RiskControlHasRequirement.findByRiskControlRiskControlTypeId",
            query = "SELECT r FROM RiskControlHasRequirement r WHERE "
            + "r.riskControlHasRequirementPK.riskControlRiskControlTypeId = :riskControlRiskControlTypeId")
    , @NamedQuery(name = "RiskControlHasRequirement.findByRequirementId",
            query = "SELECT r FROM RiskControlHasRequirement r WHERE "
            + "r.riskControlHasRequirementPK.requirementId = :requirementId")
    , @NamedQuery(name = "RiskControlHasRequirement.findByRequirementMajorVersion",
            query = "SELECT r FROM RiskControlHasRequirement r WHERE "
            + "r.riskControlHasRequirementPK.requirementMajorVersion = :requirementMajorVersion")
    , @NamedQuery(name = "RiskControlHasRequirement.findByRequirementMidVersion",
            query = "SELECT r FROM RiskControlHasRequirement r WHERE "
            + "r.riskControlHasRequirementPK.requirementMidVersion = :requirementMidVersion")
    , @NamedQuery(name = "RiskControlHasRequirement.findByRequirementMinorVersion",
            query = "SELECT r FROM RiskControlHasRequirement r WHERE "
            + "r.riskControlHasRequirementPK.requirementMinorVersion = :requirementMinorVersion")})
public class RiskControlHasRequirement implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RiskControlHasRequirementPK riskControlHasRequirementPK;
    @JoinColumn(name = "requirement_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Requirement requirement;
    @JoinColumns({
        @JoinColumn(name = "risk_control_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "risk_control_risk_control_type_id",
                referencedColumnName = "risk_control_type_id",
                insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private RiskControl riskControl;

    public RiskControlHasRequirement() {
    }

    public RiskControlHasRequirement(RiskControlHasRequirementPK riskControlHasRequirementPK) {
        this.riskControlHasRequirementPK = riskControlHasRequirementPK;
    }

    public RiskControlHasRequirement(int riskControlId,
            int riskControlRiskControlTypeId, int requirementId,
            int requirementMajorVersion, int requirementMidVersion,
            int requirementMinorVersion) {
        this.riskControlHasRequirementPK
                = new RiskControlHasRequirementPK(riskControlId,
                        riskControlRiskControlTypeId, requirementId,
                        requirementMajorVersion, requirementMidVersion,
                        requirementMinorVersion);
    }

    public RiskControlHasRequirementPK getRiskControlHasRequirementPK() {
        return riskControlHasRequirementPK;
    }

    public void setRiskControlHasRequirementPK(RiskControlHasRequirementPK riskControlHasRequirementPK) {
        this.riskControlHasRequirementPK = riskControlHasRequirementPK;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    public RiskControl getRiskControl() {
        return riskControl;
    }

    public void setRiskControl(RiskControl riskControl) {
        this.riskControl = riskControl;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (riskControlHasRequirementPK != null
                ? riskControlHasRequirementPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof RiskControlHasRequirement)) {
            return false;
        }
        RiskControlHasRequirement other = (RiskControlHasRequirement) object;
        return !((this.riskControlHasRequirementPK == null
                && other.riskControlHasRequirementPK != null)
                || (this.riskControlHasRequirementPK != null
                && !this.riskControlHasRequirementPK.equals(other.riskControlHasRequirementPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskControlHasRequirement[ "
                + "riskControlHasRequirementPK=" + riskControlHasRequirementPK + " ]";
    }
}
