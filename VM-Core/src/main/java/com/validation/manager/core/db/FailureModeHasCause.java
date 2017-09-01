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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "failure_mode_has_cause")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FailureModeHasCause.findAll",
            query = "SELECT f FROM FailureModeHasCause f")
    , @NamedQuery(name = "FailureModeHasCause.findByRiskItemId",
            query = "SELECT f FROM FailureModeHasCause f WHERE f.failureModeHasCausePK.riskItemId = :riskItemId")
    , @NamedQuery(name = "FailureModeHasCause.findByFMEAid",
            query = "SELECT f FROM FailureModeHasCause f WHERE f.failureModeHasCausePK.fMEAid = :fMEAid")
    , @NamedQuery(name = "FailureModeHasCause.findByProjectId",
            query = "SELECT f FROM FailureModeHasCause f WHERE f.failureModeHasCausePK.projectId = :projectId")
    , @NamedQuery(name = "FailureModeHasCause.findByHazardId",
            query = "SELECT f FROM FailureModeHasCause f WHERE f.failureModeHasCausePK.hazardId = :hazardId")
    , @NamedQuery(name = "FailureModeHasCause.findByFailureModeId",
            query = "SELECT f FROM FailureModeHasCause f WHERE f.failureModeHasCausePK.failureModeId = :failureModeId")
    , @NamedQuery(name = "FailureModeHasCause.findByCauseId",
            query = "SELECT f FROM FailureModeHasCause f WHERE f.failureModeHasCausePK.causeId = :causeId")})
public class FailureModeHasCause implements Serializable {

    @JoinTable(name = "risk_item_has_risk_control", joinColumns = {
        @JoinColumn(name = "risk_item_id", referencedColumnName = "risk_item_id")
        , @JoinColumn(name = "FMEA_id", referencedColumnName = "FMEA_id")
        , @JoinColumn(name = "project_id", referencedColumnName = "project_id")
        , @JoinColumn(name = "hazard_id", referencedColumnName = "hazard_id")
        , @JoinColumn(name = "failure_mode_id", referencedColumnName = "failure_mode_id")
        , @JoinColumn(name = "cause_id", referencedColumnName = "cause_id")}, inverseJoinColumns = {
        @JoinColumn(name = "risk_control_id", referencedColumnName = "id")
        , @JoinColumn(name = "risk_control_type_id", referencedColumnName = "risk_control_type_id")})
    @ManyToMany
    private List<RiskControl> riskControlList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "failureModeHasCause")
    private List<FailureModeHasCauseHasRiskCategory> failureModeHasCauseHasRiskCategoryList;

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected FailureModeHasCausePK failureModeHasCausePK;
    @JoinTable(name = "failure_mode_has_cause_has_risk_category", joinColumns = {
        @JoinColumn(name = "risk_item_id", referencedColumnName = "risk_item_id")
        , @JoinColumn(name = "FMEA_id", referencedColumnName = "FMEA_id")
        , @JoinColumn(name = "project_id", referencedColumnName = "project_id")
        , @JoinColumn(name = "hazard_id", referencedColumnName = "hazard_id")
        , @JoinColumn(name = "failure_mode_id", referencedColumnName = "failure_mode_id")
        , @JoinColumn(name = "cause_id", referencedColumnName = "cause_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "risk_category_id", referencedColumnName = "id")})
    @ManyToMany
    private List<RiskCategory> riskCategoryList;
    @JoinColumn(name = "cause_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Cause cause;
    @JoinColumns({
        @JoinColumn(name = "risk_item_id", referencedColumnName = "risk_item_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "FMEA_id", referencedColumnName = "FMEA_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "project_id", referencedColumnName = "FMEA_project_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "hazard_id", referencedColumnName = "hazard_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "failure_mode_id",
                referencedColumnName = "failure_mode_id", insertable = false,
                updatable = false)})
    @ManyToOne(optional = false)
    private HazardHasFailureMode hazardHasFailureMode;

    public FailureModeHasCause() {
    }

    public FailureModeHasCause(FailureModeHasCausePK failureModeHasCausePK) {
        this.failureModeHasCausePK = failureModeHasCausePK;
    }

    public FailureModeHasCause(int riskItemId, int fMEAid, int projectId,
            int hazardId, int failureModeId, int causeId) {
        this.failureModeHasCausePK = new FailureModeHasCausePK(riskItemId,
                fMEAid, projectId, hazardId, failureModeId, causeId);
    }

    public FailureModeHasCausePK getFailureModeHasCausePK() {
        return failureModeHasCausePK;
    }

    public void setFailureModeHasCausePK(FailureModeHasCausePK failureModeHasCausePK) {
        this.failureModeHasCausePK = failureModeHasCausePK;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskCategory> getRiskCategoryList() {
        return riskCategoryList;
    }

    public void setRiskCategoryList(List<RiskCategory> riskCategoryList) {
        this.riskCategoryList = riskCategoryList;
    }

    public Cause getCause() {
        return cause;
    }

    public void setCause(Cause cause) {
        this.cause = cause;
    }

    public HazardHasFailureMode getHazardHasFailureMode() {
        return hazardHasFailureMode;
    }

    public void setHazardHasFailureMode(HazardHasFailureMode hazardHasFailureMode) {
        this.hazardHasFailureMode = hazardHasFailureMode;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (failureModeHasCausePK != null ? failureModeHasCausePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FailureModeHasCause)) {
            return false;
        }
        FailureModeHasCause other = (FailureModeHasCause) object;
        return !((this.failureModeHasCausePK == null
                && other.failureModeHasCausePK != null)
                || (this.failureModeHasCausePK != null
                && !this.failureModeHasCausePK.equals(other.failureModeHasCausePK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.FailureModeHasCause[ "
                + "failureModeHasCausePK=" + failureModeHasCausePK + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskControl> getRiskControlList() {
        return riskControlList;
    }

    public void setRiskControlList(List<RiskControl> riskControlList) {
        this.riskControlList = riskControlList;
    }

    @XmlTransient
    @JsonIgnore
    public List<FailureModeHasCauseHasRiskCategory> getFailureModeHasCauseHasRiskCategoryList() {
        return failureModeHasCauseHasRiskCategoryList;
    }

    public void setFailureModeHasCauseHasRiskCategoryList(List<FailureModeHasCauseHasRiskCategory> failureModeHasCauseHasRiskCategoryList) {
        this.failureModeHasCauseHasRiskCategoryList = failureModeHasCauseHasRiskCategoryList;
    }
}
