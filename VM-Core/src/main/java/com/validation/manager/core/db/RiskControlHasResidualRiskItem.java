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
@Table(name = "risk_control_has_residual_risk_item")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RiskControlHasResidualRiskItem.findAll",
            query = "SELECT r FROM RiskControlHasResidualRiskItem r")
    , @NamedQuery(name = "RiskControlHasResidualRiskItem.findByRiskControlId",
            query = "SELECT r FROM RiskControlHasResidualRiskItem r WHERE r.riskControlHasResidualRiskItemPK.riskControlId = :riskControlId")
    , @NamedQuery(name = "RiskControlHasResidualRiskItem.findByRiskControlRiskControlTypeId",
            query = "SELECT r FROM RiskControlHasResidualRiskItem r WHERE r.riskControlHasResidualRiskItemPK.riskControlRiskControlTypeId = :riskControlRiskControlTypeId")
    , @NamedQuery(name = "RiskControlHasResidualRiskItem.findByRiskItemId",
            query = "SELECT r FROM RiskControlHasResidualRiskItem r WHERE r.riskControlHasResidualRiskItemPK.riskItemId = :riskItemId")
    , @NamedQuery(name = "RiskControlHasResidualRiskItem.findByRiskitemFMEAid",
            query = "SELECT r FROM RiskControlHasResidualRiskItem r WHERE r.riskControlHasResidualRiskItemPK.riskitemFMEAid = :riskitemFMEAid")})
public class RiskControlHasResidualRiskItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RiskControlHasResidualRiskItemPK riskControlHasResidualRiskItemPK;
    @JoinColumns({
        @JoinColumn(name = "risk_control_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "risk_control_risk_control_type_id",
                referencedColumnName = "risk_control_type_id",
                insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private RiskControl riskControl;
    @JoinColumns({
        @JoinColumn(name = "risk_item_id",
                referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "risk_item_FMEA_id",
                referencedColumnName = "FMEA_id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "risk_item_FMEA_project_id",
                referencedColumnName = "FMEA_project_id",
                insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private RiskItem riskItem;

    public RiskControlHasResidualRiskItem() {
    }

    public RiskControlHasResidualRiskItem(RiskControlHasResidualRiskItemPK riskControlHasResidualRiskItemPK) {
        this.riskControlHasResidualRiskItemPK = riskControlHasResidualRiskItemPK;
    }

    public RiskControlHasResidualRiskItem(int riskControlId,
            int riskControlRiskControlTypeId, int riskItemId, int riskitemFMEAid) {
        this.riskControlHasResidualRiskItemPK
                = new RiskControlHasResidualRiskItemPK(riskControlId,
                        riskControlRiskControlTypeId, riskItemId, riskitemFMEAid);
    }

    public RiskControlHasResidualRiskItemPK getRiskControlHasResidualRiskItemPK() {
        return riskControlHasResidualRiskItemPK;
    }

    public void setRiskControlHasResidualRiskItemPK(RiskControlHasResidualRiskItemPK riskControlHasResidualRiskItemPK) {
        this.riskControlHasResidualRiskItemPK = riskControlHasResidualRiskItemPK;
    }

    public RiskControl getRiskControl() {
        return riskControl;
    }

    public void setRiskControl(RiskControl riskControl) {
        this.riskControl = riskControl;
    }

    public RiskItem getRiskItem() {
        return riskItem;
    }

    public void setRiskItem(RiskItem riskItem) {
        this.riskItem = riskItem;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (riskControlHasResidualRiskItemPK != null
                ? riskControlHasResidualRiskItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiskControlHasResidualRiskItem)) {
            return false;
        }
        RiskControlHasResidualRiskItem other = (RiskControlHasResidualRiskItem) object;
        return !((this.riskControlHasResidualRiskItemPK == null
                && other.riskControlHasResidualRiskItemPK != null)
                || (this.riskControlHasResidualRiskItemPK != null
                && !this.riskControlHasResidualRiskItemPK
                        .equals(other.riskControlHasResidualRiskItemPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskControlHasResidualRiskItem[ "
                + "riskControlHasResidualRiskItemPK="
                + riskControlHasResidualRiskItemPK + " ]";
    }
}
