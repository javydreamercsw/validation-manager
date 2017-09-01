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
@Table(name = "hazard_has_failure_mode")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "HazardHasFailureMode.findAll",
            query = "SELECT h FROM HazardHasFailureMode h")
    , @NamedQuery(name = "HazardHasFailureMode.findByRiskItemId",
            query = "SELECT h FROM HazardHasFailureMode h WHERE h.hazardHasFailureModePK.riskItemId = :riskItemId")
    , @NamedQuery(name = "HazardHasFailureMode.findByFMEAid",
            query = "SELECT h FROM HazardHasFailureMode h WHERE h.hazardHasFailureModePK.fMEAid = :fMEAid")
    , @NamedQuery(name = "HazardHasFailureMode.findByFMEAprojectid",
            query = "SELECT h FROM HazardHasFailureMode h WHERE h.hazardHasFailureModePK.fMEAprojectid = :fMEAprojectid")
    , @NamedQuery(name = "HazardHasFailureMode.findByHazardId",
            query = "SELECT h FROM HazardHasFailureMode h WHERE h.hazardHasFailureModePK.hazardId = :hazardId")
    , @NamedQuery(name = "HazardHasFailureMode.findByFailureModeId",
            query = "SELECT h FROM HazardHasFailureMode h WHERE h.hazardHasFailureModePK.failureModeId = :failureModeId")})
public class HazardHasFailureMode implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected HazardHasFailureModePK hazardHasFailureModePK;
    @JoinColumn(name = "failure_mode_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private FailureMode failureMode;
    @JoinColumns({
        @JoinColumn(name = "risk_item_id",
                referencedColumnName = "risk_item_id", insertable = false,
                updatable = false)
        , @JoinColumn(name = "FMEA_id",
                referencedColumnName = "risk_item_FMEA_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "FMEA_project_id",
                referencedColumnName = "risk_item_FMEA_project_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "hazard_id",
                referencedColumnName = "hazard_id", insertable = false,
                updatable = false)})
    @ManyToOne(optional = false)
    private RiskItemHasHazard riskItemHasHazard;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hazardHasFailureMode")
    private List<FailureModeHasCause> failureModeHasCauseList;

    public HazardHasFailureMode() {
    }

    public HazardHasFailureMode(HazardHasFailureModePK hazardHasFailureModePK) {
        this.hazardHasFailureModePK = hazardHasFailureModePK;
    }

    public HazardHasFailureMode(int riskItemId, int fMEAid, int fMEAprojectid,
            int hazardId, int failureModeId) {
        this.hazardHasFailureModePK = new HazardHasFailureModePK(riskItemId,
                fMEAid, fMEAprojectid, hazardId, failureModeId);
    }

    public HazardHasFailureModePK getHazardHasFailureModePK() {
        return hazardHasFailureModePK;
    }

    public void setHazardHasFailureModePK(HazardHasFailureModePK hazardHasFailureModePK) {
        this.hazardHasFailureModePK = hazardHasFailureModePK;
    }

    @XmlTransient
    @JsonIgnore
    public List<FailureModeHasCause> getFailureModeHasCauseList() {
        return failureModeHasCauseList;
    }

    public void setFailureModeHasCauseList(List<FailureModeHasCause> failureModeHasCauseList) {
        this.failureModeHasCauseList = failureModeHasCauseList;
    }

    public FailureMode getFailureMode() {
        return failureMode;
    }

    public void setFailureMode(FailureMode failureMode) {
        this.failureMode = failureMode;
    }

    public RiskItemHasHazard getRiskItemHasHazard() {
        return riskItemHasHazard;
    }

    public void setRiskItemHasHazard(RiskItemHasHazard riskItemHasHazard) {
        this.riskItemHasHazard = riskItemHasHazard;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (hazardHasFailureModePK != null ? hazardHasFailureModePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HazardHasFailureMode)) {
            return false;
        }
        HazardHasFailureMode other = (HazardHasFailureMode) object;
        return !((this.hazardHasFailureModePK == null
                && other.hazardHasFailureModePK != null)
                || (this.hazardHasFailureModePK != null
                && !this.hazardHasFailureModePK.equals(other.hazardHasFailureModePK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.HazardHasFailureMode[ "
                + "hazardHasFailureModePK=" + hazardHasFailureModePK + " ]";
    }
}
