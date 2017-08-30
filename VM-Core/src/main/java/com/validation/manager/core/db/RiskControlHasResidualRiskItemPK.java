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
public class RiskControlHasResidualRiskItemPK implements Serializable {

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
    @Column(name = "risk_item_id")
    private int riskItemId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "risk_item_FMEA_id")
    private int riskitemFMEAid;

    public RiskControlHasResidualRiskItemPK() {
    }

    public RiskControlHasResidualRiskItemPK(int riskControlId,
            int riskControlRiskControlTypeId, int riskItemId,
            int riskitemFMEAid) {
        this.riskControlId = riskControlId;
        this.riskControlRiskControlTypeId = riskControlRiskControlTypeId;
        this.riskItemId = riskItemId;
        this.riskitemFMEAid = riskitemFMEAid;
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

    public int getRiskItemId() {
        return riskItemId;
    }

    public void setRiskItemId(int riskItemId) {
        this.riskItemId = riskItemId;
    }

    public int getRiskitemFMEAid() {
        return riskitemFMEAid;
    }

    public void setRiskitemFMEAid(int riskitemFMEAid) {
        this.riskitemFMEAid = riskitemFMEAid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) riskControlId;
        hash += (int) riskControlRiskControlTypeId;
        hash += (int) riskItemId;
        hash += (int) riskitemFMEAid;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RiskControlHasResidualRiskItemPK)) {
            return false;
        }
        RiskControlHasResidualRiskItemPK other = (RiskControlHasResidualRiskItemPK) object;
        if (this.riskControlId != other.riskControlId) {
            return false;
        }
        if (this.riskControlRiskControlTypeId != other.riskControlRiskControlTypeId) {
            return false;
        }
        if (this.riskItemId != other.riskItemId) {
            return false;
        }
        return this.riskitemFMEAid == other.riskitemFMEAid;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskControlHasResidualRiskItemPK[ "
                + "riskControlId=" + riskControlId
                + ", riskControlRiskControlTypeId=" + riskControlRiskControlTypeId
                + ", riskItemId=" + riskItemId + ", riskitemFMEAid="
                + riskitemFMEAid + " ]";
    }
}
