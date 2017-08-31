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
public class RiskItemHasHazardPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "risk_item_id")
    private int riskItemId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "risk_item_FMEA_id")
    private int riskitemFMEAid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "risk_item_FMEA_project_id")
    private int riskitemFMEAprojectid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "hazard_id")
    private int hazardId;

    public RiskItemHasHazardPK() {
    }

    public RiskItemHasHazardPK(int riskItemId, int riskitemFMEAid,
            int riskitemFMEAprojectid, int hazardId) {
        this.riskItemId = riskItemId;
        this.riskitemFMEAid = riskitemFMEAid;
        this.riskitemFMEAprojectid = riskitemFMEAprojectid;
        this.hazardId = hazardId;
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

    public int getRiskitemFMEAprojectid() {
        return riskitemFMEAprojectid;
    }

    public void setRiskitemFMEAprojectid(int riskitemFMEAprojectid) {
        this.riskitemFMEAprojectid = riskitemFMEAprojectid;
    }

    public int getHazardId() {
        return hazardId;
    }

    public void setHazardId(int hazardId) {
        this.hazardId = hazardId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) riskItemId;
        hash += (int) riskitemFMEAid;
        hash += (int) riskitemFMEAprojectid;
        hash += (int) hazardId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiskItemHasHazardPK)) {
            return false;
        }
        RiskItemHasHazardPK other = (RiskItemHasHazardPK) object;
        if (this.riskItemId != other.riskItemId) {
            return false;
        }
        if (this.riskitemFMEAid != other.riskitemFMEAid) {
            return false;
        }
        if (this.riskitemFMEAprojectid != other.riskitemFMEAprojectid) {
            return false;
        }
        return this.hazardId == other.hazardId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskItemHasHazardPK[ riskItemId="
                + riskItemId + ", riskitemFMEAid=" + riskitemFMEAid
                + ", riskitemFMEAprojectid=" + riskitemFMEAprojectid
                + ", hazardId=" + hazardId + " ]";
    }
}
