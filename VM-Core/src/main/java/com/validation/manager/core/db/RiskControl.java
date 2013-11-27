/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "risk_control")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RiskControl.findAll", query = "SELECT r FROM RiskControl r"),
    @NamedQuery(name = "RiskControl.findById", query = "SELECT r FROM RiskControl r WHERE r.riskControlPK.id = :id"),
    @NamedQuery(name = "RiskControl.findByRiskControlTypeId", query = "SELECT r FROM RiskControl r WHERE r.riskControlPK.riskControlTypeId = :riskControlTypeId")})
public class RiskControl implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RiskControlPK riskControlPK;
    @JoinTable(name = "risk_item_has_risk_control", joinColumns = {
        @JoinColumn(name = "risk_control_id", referencedColumnName = "id"),
        @JoinColumn(name = "risk_control_risk_control_type_id", referencedColumnName = "risk_control_type_id")}, inverseJoinColumns = {
        @JoinColumn(name = "risk_item_id", referencedColumnName = "id"),
        @JoinColumn(name = "risk_item_FMEA_id", referencedColumnName = "FMEA_id")})
    @ManyToMany
    private List<RiskItem> riskItemList;
    @JoinTable(name = "risk_control_has_test_case", joinColumns = {
        @JoinColumn(name = "risk_control_id", referencedColumnName = "id"),
        @JoinColumn(name = "risk_control_risk_control_type_id", referencedColumnName = "risk_control_type_id")}, inverseJoinColumns = {
        @JoinColumn(name = "test_case_id", referencedColumnName = "id"),
        @JoinColumn(name = "test_case_test_id", referencedColumnName = "test_id")})
    @ManyToMany
    private List<TestCase> testCaseList;
    @ManyToMany(mappedBy = "riskControlList")
    private List<Requirement> requirementList;
    @JoinTable(name = "risk_control_has_residual_risk_item", joinColumns = {
        @JoinColumn(name = "risk_control_id", referencedColumnName = "id"),
        @JoinColumn(name = "risk_control_risk_control_type_id", referencedColumnName = "risk_control_type_id")}, inverseJoinColumns = {
        @JoinColumn(name = "risk_item_id", referencedColumnName = "id"),
        @JoinColumn(name = "risk_item_FMEA_id", referencedColumnName = "FMEA_id")})
    @ManyToMany
    private List<RiskItem> riskItemList1;
    @JoinColumn(name = "risk_control_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private RiskControlType riskControlType;

    public RiskControl() {
    }

    public RiskControl(RiskControlPK riskControlPK) {
        this.riskControlPK = riskControlPK;
    }

    public RiskControl(int riskControlTypeId) {
        this.riskControlPK = new RiskControlPK(riskControlTypeId);
    }

    public RiskControlPK getRiskControlPK() {
        return riskControlPK;
    }

    public void setRiskControlPK(RiskControlPK riskControlPK) {
        this.riskControlPK = riskControlPK;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskItem> getRiskItemList() {
        return riskItemList;
    }

    public void setRiskItemList(List<RiskItem> riskItemList) {
        this.riskItemList = riskItemList;
    }

    @XmlTransient
    @JsonIgnore
    public List<TestCase> getTestCaseList() {
        return testCaseList;
    }

    public void setTestCaseList(List<TestCase> testCaseList) {
        this.testCaseList = testCaseList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskItem> getRiskItemList1() {
        return riskItemList1;
    }

    public void setRiskItemList1(List<RiskItem> riskItemList1) {
        this.riskItemList1 = riskItemList1;
    }

    public RiskControlType getRiskControlType() {
        return riskControlType;
    }

    public void setRiskControlType(RiskControlType riskControlType) {
        this.riskControlType = riskControlType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (riskControlPK != null ? riskControlPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiskControl)) {
            return false;
        }
        RiskControl other = (RiskControl) object;
        return (this.riskControlPK != null || other.riskControlPK == null) && (this.riskControlPK == null || this.riskControlPK.equals(other.riskControlPK));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskControl[ riskControlPK=" + riskControlPK + " ]";
    }

}