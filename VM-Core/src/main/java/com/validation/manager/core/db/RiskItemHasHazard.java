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
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "risk_item_has_hazard")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RiskItemHasHazard.findAll",
            query = "SELECT r FROM RiskItemHasHazard r")
    , @NamedQuery(name = "RiskItemHasHazard.findByRiskItemId",
            query = "SELECT r FROM RiskItemHasHazard r WHERE r.riskItemHasHazardPK.riskItemId = :riskItemId")
    , @NamedQuery(name = "RiskItemHasHazard.findByRiskitemFMEAid",
            query = "SELECT r FROM RiskItemHasHazard r WHERE r.riskItemHasHazardPK.riskitemFMEAid = :riskitemFMEAid")
    , @NamedQuery(name = "RiskItemHasHazard.findByRiskitemFMEAprojectid",
            query = "SELECT r FROM RiskItemHasHazard r WHERE r.riskItemHasHazardPK.riskitemFMEAprojectid = :riskitemFMEAprojectid")
    , @NamedQuery(name = "RiskItemHasHazard.findByHazardId",
            query = "SELECT r FROM RiskItemHasHazard r WHERE r.riskItemHasHazardPK.hazardId = :hazardId")})
public class RiskItemHasHazard implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RiskItemHasHazardPK riskItemHasHazardPK;
    @JoinColumn(name = "hazard_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Hazard hazard;
    @JoinColumns({
        @JoinColumn(name = "risk_item_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "risk_item_FMEA_id",
                referencedColumnName = "FMEA_id", insertable = false,
                updatable = false)
        , @JoinColumn(name = "risk_item_FMEA_project_id",
                referencedColumnName = "FMEA_project_id", insertable = false,
                updatable = false)})
    @ManyToOne(optional = false)
    private RiskItem riskItem;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "riskItemHasHazard")
    private List<HazardHasFailureMode> hazardHasFailureModeList;

    public RiskItemHasHazard() {
    }

    public RiskItemHasHazard(RiskItemHasHazardPK riskItemHasHazardPK) {
        this.riskItemHasHazardPK = riskItemHasHazardPK;
    }

    public RiskItemHasHazard(int riskItemId, int riskitemFMEAid,
            int riskitemFMEAprojectid, int hazardId) {
        this.riskItemHasHazardPK = new RiskItemHasHazardPK(riskItemId,
                riskitemFMEAid, riskitemFMEAprojectid, hazardId);
    }

    public RiskItemHasHazardPK getRiskItemHasHazardPK() {
        return riskItemHasHazardPK;
    }

    public void setRiskItemHasHazardPK(RiskItemHasHazardPK riskItemHasHazardPK) {
        this.riskItemHasHazardPK = riskItemHasHazardPK;
    }

    public Hazard getHazard() {
        return hazard;
    }

    public void setHazard(Hazard hazard) {
        this.hazard = hazard;
    }

    public RiskItem getRiskItem() {
        return riskItem;
    }

    public void setRiskItem(RiskItem riskItem) {
        this.riskItem = riskItem;
    }

    @XmlTransient
    @JsonIgnore
    public List<HazardHasFailureMode> getHazardHasFailureModeList() {
        return hazardHasFailureModeList;
    }

    public void setHazardHasFailureModeList(List<HazardHasFailureMode> hazardHasFailureModeList) {
        this.hazardHasFailureModeList = hazardHasFailureModeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (riskItemHasHazardPK != null ? riskItemHasHazardPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiskItemHasHazard)) {
            return false;
        }
        RiskItemHasHazard other = (RiskItemHasHazard) object;
        return !((this.riskItemHasHazardPK == null
                && other.riskItemHasHazardPK != null)
                || (this.riskItemHasHazardPK != null
                && !this.riskItemHasHazardPK.equals(other.riskItemHasHazardPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskItemHasHazard[ riskItemHasHazardPK="
                + riskItemHasHazardPK + " ]";
    }
}
