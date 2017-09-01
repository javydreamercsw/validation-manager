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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "failure_mode_has_cause_has_risk_category")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FailureModeHasCauseHasRiskCategory.findAll",
            query = "SELECT f FROM FailureModeHasCauseHasRiskCategory f")
    , @NamedQuery(name = "FailureModeHasCauseHasRiskCategory.findByRiskItemId",
            query = "SELECT f FROM FailureModeHasCauseHasRiskCategory f WHERE f.failureModeHasCauseHasRiskCategoryPK.riskItemId = :riskItemId")
    , @NamedQuery(name = "FailureModeHasCauseHasRiskCategory.findByFMEAid",
            query = "SELECT f FROM FailureModeHasCauseHasRiskCategory f WHERE f.failureModeHasCauseHasRiskCategoryPK.fMEAid = :fMEAid")
    , @NamedQuery(name = "FailureModeHasCauseHasRiskCategory.findByProjectId",
            query = "SELECT f FROM FailureModeHasCauseHasRiskCategory f WHERE f.failureModeHasCauseHasRiskCategoryPK.projectId = :projectId")
    , @NamedQuery(name = "FailureModeHasCauseHasRiskCategory.findByHazardId",
            query = "SELECT f FROM FailureModeHasCauseHasRiskCategory f WHERE f.failureModeHasCauseHasRiskCategoryPK.hazardId = :hazardId")
    , @NamedQuery(name = "FailureModeHasCauseHasRiskCategory.findByFailureModeId",
            query = "SELECT f FROM FailureModeHasCauseHasRiskCategory f WHERE f.failureModeHasCauseHasRiskCategoryPK.failureModeId = :failureModeId")
    , @NamedQuery(name = "FailureModeHasCauseHasRiskCategory.findByCauseId",
            query = "SELECT f FROM FailureModeHasCauseHasRiskCategory f WHERE f.failureModeHasCauseHasRiskCategoryPK.causeId = :causeId")
    , @NamedQuery(name = "FailureModeHasCauseHasRiskCategory.findByRiskCategoryId",
            query = "SELECT f FROM FailureModeHasCauseHasRiskCategory f WHERE f.failureModeHasCauseHasRiskCategoryPK.riskCategoryId = :riskCategoryId")
    , @NamedQuery(name = "FailureModeHasCauseHasRiskCategory.findByCategoryValue",
            query = "SELECT f FROM FailureModeHasCauseHasRiskCategory f WHERE f.categoryValue = :categoryValue")})
public class FailureModeHasCauseHasRiskCategory implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected FailureModeHasCauseHasRiskCategoryPK failureModeHasCauseHasRiskCategoryPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "category_value")
    private double categoryValue;
    @JoinColumns({
        @JoinColumn(name = "risk_item_id", referencedColumnName = "risk_item_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "FMEA_id", referencedColumnName = "FMEA_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "project_id", referencedColumnName = "project_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "hazard_id", referencedColumnName = "hazard_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "failure_mode_id",
                referencedColumnName = "failure_mode_id", insertable = false,
                updatable = false)
        , @JoinColumn(name = "cause_id", referencedColumnName = "cause_id",
                insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private FailureModeHasCause failureModeHasCause;
    @JoinColumn(name = "risk_category_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private RiskCategory riskCategory;

    public FailureModeHasCauseHasRiskCategory() {
    }

    public FailureModeHasCauseHasRiskCategory(FailureModeHasCauseHasRiskCategoryPK failureModeHasCauseHasRiskCategoryPK) {
        this.failureModeHasCauseHasRiskCategoryPK = failureModeHasCauseHasRiskCategoryPK;
    }

    public FailureModeHasCauseHasRiskCategory(FailureModeHasCauseHasRiskCategoryPK failureModeHasCauseHasRiskCategoryPK, float categoryValue) {
        this.failureModeHasCauseHasRiskCategoryPK = failureModeHasCauseHasRiskCategoryPK;
        this.categoryValue = categoryValue;
    }

    public FailureModeHasCauseHasRiskCategory(int riskItemId, int fMEAid,
            int projectId, int hazardId, int failureModeId, int causeId,
            int riskCategoryId) {
        this.failureModeHasCauseHasRiskCategoryPK
                = new FailureModeHasCauseHasRiskCategoryPK(riskItemId, fMEAid,
                        projectId, hazardId, failureModeId, causeId,
                        riskCategoryId);
    }

    public FailureModeHasCauseHasRiskCategoryPK getFailureModeHasCauseHasRiskCategoryPK() {
        return failureModeHasCauseHasRiskCategoryPK;
    }

    public void setFailureModeHasCauseHasRiskCategoryPK(FailureModeHasCauseHasRiskCategoryPK failureModeHasCauseHasRiskCategoryPK) {
        this.failureModeHasCauseHasRiskCategoryPK = failureModeHasCauseHasRiskCategoryPK;
    }

    public double getCategoryValue() {
        return categoryValue;
    }

    public void setCategoryValue(double categoryValue) {
        this.categoryValue = categoryValue;
    }

    public FailureModeHasCause getFailureModeHasCause() {
        return failureModeHasCause;
    }

    public void setFailureModeHasCause(FailureModeHasCause failureModeHasCause) {
        this.failureModeHasCause = failureModeHasCause;
    }

    public RiskCategory getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(RiskCategory riskCategory) {
        this.riskCategory = riskCategory;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (failureModeHasCauseHasRiskCategoryPK != null
                ? failureModeHasCauseHasRiskCategoryPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FailureModeHasCauseHasRiskCategory)) {
            return false;
        }
        FailureModeHasCauseHasRiskCategory other = (FailureModeHasCauseHasRiskCategory) object;
        return !((this.failureModeHasCauseHasRiskCategoryPK == null
                && other.failureModeHasCauseHasRiskCategoryPK != null)
                || (this.failureModeHasCauseHasRiskCategoryPK != null
                && !this.failureModeHasCauseHasRiskCategoryPK
                        .equals(other.failureModeHasCauseHasRiskCategoryPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.FailureModeHasCauseHasRiskCategory["
                + " failureModeHasCauseHasRiskCategoryPK=" + failureModeHasCauseHasRiskCategoryPK + " ]";
    }
}
