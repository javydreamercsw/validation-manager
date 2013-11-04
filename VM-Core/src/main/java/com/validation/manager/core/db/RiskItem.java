/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "risk_item")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RiskItem.findAll", query = "SELECT r FROM RiskItem r"),
    @NamedQuery(name = "RiskItem.findBySequence", query = "SELECT r FROM RiskItem r WHERE r.sequence = :sequence"),
    @NamedQuery(name = "RiskItem.findByVersion", query = "SELECT r FROM RiskItem r WHERE r.version = :version"),
    @NamedQuery(name = "RiskItem.findById", query = "SELECT r FROM RiskItem r WHERE r.riskItemPK.id = :id"),
    @NamedQuery(name = "RiskItem.findByFMEAid", query = "SELECT r FROM RiskItem r WHERE r.riskItemPK.fMEAid = :fMEAid")})
public class RiskItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RiskItemPK riskItemPK;
    @Column(name = "sequence")
    private Integer sequence;
    @Column(name = "version")
    private Integer version;
    @ManyToMany(mappedBy = "riskItemList")
    private List<FailureMode> failureModeList;
    @ManyToMany(mappedBy = "riskItemList")
    private List<RiskControl> riskControlList;
    @ManyToMany(mappedBy = "riskItemList")
    private List<Cause> causeList;
    @ManyToMany(mappedBy = "riskItemList1")
    private List<RiskControl> riskControlList1;
    @ManyToMany(mappedBy = "riskItemList")
    private List<Hazard> hazardList;
    @JoinColumn(name = "FMEA_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Fmea fmea;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "riskItem")
    private List<RiskItemHasRiskCategory> riskItemHasRiskCategoryList;

    public RiskItem() {
    }

    public RiskItem(RiskItemPK riskItemPK) {
        this.riskItemPK = riskItemPK;
    }

    public RiskItem(RiskItemPK riskItemPK, int sequence, int version) {
        this.riskItemPK = riskItemPK;
        this.sequence = sequence;
        this.version = version;
    }

    public RiskItem(int fMEAid) {
        this.riskItemPK = new RiskItemPK(fMEAid);
    }

    public RiskItemPK getRiskItemPK() {
        return riskItemPK;
    }

    public void setRiskItemPK(RiskItemPK riskItemPK) {
        this.riskItemPK = riskItemPK;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @XmlTransient
    @JsonIgnore
    public List<FailureMode> getFailureModeList() {
        return failureModeList;
    }

    public void setFailureModeList(List<FailureMode> failureModeList) {
        this.failureModeList = failureModeList;
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
    public List<Cause> getCauseList() {
        return causeList;
    }

    public void setCauseList(List<Cause> causeList) {
        this.causeList = causeList;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskControl> getRiskControlList1() {
        return riskControlList1;
    }

    public void setRiskControlList1(List<RiskControl> riskControlList1) {
        this.riskControlList1 = riskControlList1;
    }

    @XmlTransient
    @JsonIgnore
    public List<Hazard> getHazardList() {
        return hazardList;
    }

    public void setHazardList(List<Hazard> hazardList) {
        this.hazardList = hazardList;
    }

    public Fmea getFmea() {
        return fmea;
    }

    public void setFmea(Fmea fmea) {
        this.fmea = fmea;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskItemHasRiskCategory> getRiskItemHasRiskCategoryList() {
        return riskItemHasRiskCategoryList;
    }

    public void setRiskItemHasRiskCategoryList(List<RiskItemHasRiskCategory> riskItemHasRiskCategoryList) {
        this.riskItemHasRiskCategoryList = riskItemHasRiskCategoryList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (riskItemPK != null ? riskItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiskItem)) {
            return false;
        }
        RiskItem other = (RiskItem) object;
        return (this.riskItemPK != null || other.riskItemPK == null) && (this.riskItemPK == null || this.riskItemPK.equals(other.riskItemPK));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskItem[ riskItemPK=" + riskItemPK + " ]";
    }

}
