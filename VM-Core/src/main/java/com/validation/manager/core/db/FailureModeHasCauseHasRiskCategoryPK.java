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
public class FailureModeHasCauseHasRiskCategoryPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "risk_item_id")
    private int riskItemId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FMEA_id")
    private int fMEAid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "project_id")
    private int projectId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "hazard_id")
    private int hazardId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "failure_mode_id")
    private int failureModeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cause_id")
    private int causeId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "risk_category_id")
    private int riskCategoryId;

    public FailureModeHasCauseHasRiskCategoryPK() {
    }

    public FailureModeHasCauseHasRiskCategoryPK(int riskItemId, int fMEAid,
            int projectId, int hazardId, int failureModeId, int causeId,
            int riskCategoryId) {
        this.riskItemId = riskItemId;
        this.fMEAid = fMEAid;
        this.projectId = projectId;
        this.hazardId = hazardId;
        this.failureModeId = failureModeId;
        this.causeId = causeId;
        this.riskCategoryId = riskCategoryId;
    }

    public int getRiskItemId() {
        return riskItemId;
    }

    public void setRiskItemId(int riskItemId) {
        this.riskItemId = riskItemId;
    }

    public int getFMEAid() {
        return fMEAid;
    }

    public void setFMEAid(int fMEAid) {
        this.fMEAid = fMEAid;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getHazardId() {
        return hazardId;
    }

    public void setHazardId(int hazardId) {
        this.hazardId = hazardId;
    }

    public int getFailureModeId() {
        return failureModeId;
    }

    public void setFailureModeId(int failureModeId) {
        this.failureModeId = failureModeId;
    }

    public int getCauseId() {
        return causeId;
    }

    public void setCauseId(int causeId) {
        this.causeId = causeId;
    }

    public int getRiskCategoryId() {
        return riskCategoryId;
    }

    public void setRiskCategoryId(int riskCategoryId) {
        this.riskCategoryId = riskCategoryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) riskItemId;
        hash += (int) fMEAid;
        hash += (int) projectId;
        hash += (int) hazardId;
        hash += (int) failureModeId;
        hash += (int) causeId;
        hash += (int) riskCategoryId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FailureModeHasCauseHasRiskCategoryPK)) {
            return false;
        }
        FailureModeHasCauseHasRiskCategoryPK other = (FailureModeHasCauseHasRiskCategoryPK) object;
        if (this.riskItemId != other.riskItemId) {
            return false;
        }
        if (this.fMEAid != other.fMEAid) {
            return false;
        }
        if (this.projectId != other.projectId) {
            return false;
        }
        if (this.hazardId != other.hazardId) {
            return false;
        }
        if (this.failureModeId != other.failureModeId) {
            return false;
        }
        if (this.causeId != other.causeId) {
            return false;
        }
        return this.riskCategoryId == other.riskCategoryId;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.FailureModeHasCauseHasRiskCategoryPK[ "
                + "riskItemId=" + riskItemId + ", fMEAid=" + fMEAid
                + ", projectId=" + projectId + ", hazardId=" + hazardId
                + ", failureModeId=" + failureModeId + ", causeId=" + causeId
                + ", riskCategoryId=" + riskCategoryId + " ]";
    }
}
