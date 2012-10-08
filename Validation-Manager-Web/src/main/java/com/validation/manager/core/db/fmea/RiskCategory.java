/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.fmea;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "risk_category")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RiskCategory.findAll", query = "SELECT r FROM RiskCategory r"),
    @NamedQuery(name = "RiskCategory.findById", query = "SELECT r FROM RiskCategory r WHERE r.id = :id"),
    @NamedQuery(name = "RiskCategory.findByName", query = "SELECT r FROM RiskCategory r WHERE r.name = :name"),
    @NamedQuery(name = "RiskCategory.findByMinimum", query = "SELECT r FROM RiskCategory r WHERE r.minimum = :minimum"),
    @NamedQuery(name = "RiskCategory.findByMaximum", query = "SELECT r FROM RiskCategory r WHERE r.maximum = :maximum")})
public class RiskCategory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "RiskCategoryGen")
    @TableGenerator(name = "RiskCategoryGen", table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "risk_category",
    allocationSize = 1,
    initialValue = 1000)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "minimum")
    private int minimum;
    @Basic(optional = false)
    @Column(name = "maximum")
    private int maximum;
    @ManyToMany(mappedBy = "riskCategoryList")
    private List<FMEA> fMEAList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "riskCategory")
    private List<RiskItemHasRiskCategory> riskItemHasRiskCategoryList;

    public RiskCategory() {
    }

    public RiskCategory(String name, int minimum, int maximum) {
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    @XmlTransient
    public List<FMEA> getFMEAList() {
        return fMEAList;
    }

    public void setFMEAList(List<FMEA> fMEAList) {
        this.fMEAList = fMEAList;
    }

    @XmlTransient
    public List<RiskItemHasRiskCategory> getRiskItemHasRiskCategoryList() {
        return riskItemHasRiskCategoryList;
    }

    public void setRiskItemHasRiskCategoryList(List<RiskItemHasRiskCategory> riskItemHasRiskCategoryList) {
        this.riskItemHasRiskCategoryList = riskItemHasRiskCategoryList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RiskCategory)) {
            return false;
        }
        RiskCategory other = (RiskCategory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.fmea.RiskCategory[ id=" + id + " ]";
    }
}
