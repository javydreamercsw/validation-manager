/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "risk_item_has_risk_category")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RiskItemHasRiskCategory.findAll", query = "SELECT r FROM RiskItemHasRiskCategory r"),
    @NamedQuery(name = "RiskItemHasRiskCategory.findByRiskItemId", query = "SELECT r FROM RiskItemHasRiskCategory r WHERE r.riskItemHasRiskCategoryPK.riskItemId = :riskItemId"),
    @NamedQuery(name = "RiskItemHasRiskCategory.findByRiskitemFMEAid", query = "SELECT r FROM RiskItemHasRiskCategory r WHERE r.riskItemHasRiskCategoryPK.riskitemFMEAid = :riskitemFMEAid"),
    @NamedQuery(name = "RiskItemHasRiskCategory.findByRiskCategoryId", query = "SELECT r FROM RiskItemHasRiskCategory r WHERE r.riskItemHasRiskCategoryPK.riskCategoryId = :riskCategoryId"),
    @NamedQuery(name = "RiskItemHasRiskCategory.findByValue", query = "SELECT r FROM RiskItemHasRiskCategory r WHERE r.value = :value")})
public class RiskItemHasRiskCategory implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RiskItemHasRiskCategoryPK riskItemHasRiskCategoryPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "value")
    private int value;
    @JoinColumn(name = "risk_category_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private RiskCategory riskCategory;
    @JoinColumns({
        @JoinColumn(name = "risk_item_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "risk_item_FMEA_id", referencedColumnName = "FMEA_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private RiskItem riskItem;

    public RiskItemHasRiskCategory() {
    }

    public RiskItemHasRiskCategory(RiskItemHasRiskCategoryPK riskItemHasRiskCategoryPK) {
        this.riskItemHasRiskCategoryPK = riskItemHasRiskCategoryPK;
    }

    public RiskItemHasRiskCategory(RiskItemHasRiskCategoryPK riskItemHasRiskCategoryPK, int value) {
        this.riskItemHasRiskCategoryPK = riskItemHasRiskCategoryPK;
        this.value = value;
    }

    public RiskItemHasRiskCategory(int riskItemId, int riskitemFMEAid, int riskCategoryId) {
        this.riskItemHasRiskCategoryPK = new RiskItemHasRiskCategoryPK(riskItemId, riskitemFMEAid, riskCategoryId);
    }

    public RiskItemHasRiskCategoryPK getRiskItemHasRiskCategoryPK() {
        return riskItemHasRiskCategoryPK;
    }

    public void setRiskItemHasRiskCategoryPK(RiskItemHasRiskCategoryPK riskItemHasRiskCategoryPK) {
        this.riskItemHasRiskCategoryPK = riskItemHasRiskCategoryPK;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public RiskCategory getRiskCategory() {
        return riskCategory;
    }

    public void setRiskCategory(RiskCategory riskCategory) {
        this.riskCategory = riskCategory;
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
        hash += (riskItemHasRiskCategoryPK != null ? riskItemHasRiskCategoryPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiskItemHasRiskCategory)) {
            return false;
        }
        RiskItemHasRiskCategory other = (RiskItemHasRiskCategory) object;
        if ((this.riskItemHasRiskCategoryPK == null && other.riskItemHasRiskCategoryPK != null) || (this.riskItemHasRiskCategoryPK != null && !this.riskItemHasRiskCategoryPK.equals(other.riskItemHasRiskCategoryPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskItemHasRiskCategory[ riskItemHasRiskCategoryPK=" + riskItemHasRiskCategoryPK + " ]";
    }
    
}
