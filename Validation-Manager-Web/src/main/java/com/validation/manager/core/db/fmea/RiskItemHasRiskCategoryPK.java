/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.fmea;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class RiskItemHasRiskCategoryPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "risk_item_id")
    private int riskItemId;
    @Basic(optional = false)
    @Column(name = "risk_item_FMEA_id")
    private int riskitemFMEAid;
    @Basic(optional = false)
    @Column(name = "risk_category_id")
    private int riskCategoryId;
    @Basic(optional = false)
    @Column(name = "FMEA_id")
    private int fMEAid;

    public RiskItemHasRiskCategoryPK() {
    }

    public RiskItemHasRiskCategoryPK(int riskCategoryId, int fMEAid) {
        this.riskCategoryId = riskCategoryId;
        this.fMEAid = fMEAid;
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
        hash += (int) riskitemFMEAid;
        hash += (int) riskCategoryId;
        hash += (int) fMEAid;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RiskItemHasRiskCategoryPK)) {
            return false;
        }
        RiskItemHasRiskCategoryPK other = (RiskItemHasRiskCategoryPK) object;
        if (this.riskItemId != other.riskItemId) {
            return false;
        }
        if (this.riskitemFMEAid != other.riskitemFMEAid) {
            return false;
        }
        if (this.riskCategoryId != other.riskCategoryId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.fmea.RiskItemHasRiskCategoryPK[ riskItemId=" + riskItemId + ", riskitemFMEAid=" + riskitemFMEAid + ", riskCategoryId=" + riskCategoryId + " ]";
    }
}
