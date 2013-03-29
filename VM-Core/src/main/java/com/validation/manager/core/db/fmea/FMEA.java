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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "fmea")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FMEA.findAll", query = "SELECT f FROM FMEA f"),
    @NamedQuery(name = "FMEA.findById", query = "SELECT f FROM FMEA f WHERE f.id = :id"),
    @NamedQuery(name = "FMEA.findByName", query = "SELECT f FROM FMEA f WHERE f.name = :name"),
    @NamedQuery(name = "FMEA.findByDescription", query = "SELECT f FROM FMEA f WHERE f.description = :description")})
public class FMEA implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "FMEAGen")
    @TableGenerator(name = "FMEAGen", table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "fmea",
    allocationSize = 1,
    initialValue=1000)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @JoinTable(name = "fmea_has_risk_category", joinColumns = {
        @JoinColumn(name = "FMEA_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "risk_category_id", referencedColumnName = "id")})
    @ManyToMany
    private List<RiskCategory> riskCategoryList;
    @OneToMany(mappedBy = "parent")
    private List<FMEA> fMEAList;
    @JoinColumn(name = "parent", referencedColumnName = "id")
    @ManyToOne
    private FMEA parent;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fmea")
    private List<RiskItem> riskItemList;

    public FMEA() {
    }

    public FMEA(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<RiskCategory> getRiskCategoryList() {
        return riskCategoryList;
    }

    public void setRiskCategoryList(List<RiskCategory> riskCategoryList) {
        this.riskCategoryList = riskCategoryList;
    }

    @XmlTransient
    @JsonIgnore
    public List<FMEA> getFMEAList() {
        return fMEAList;
    }

    public void setFMEAList(List<FMEA> fMEAList) {
        this.fMEAList = fMEAList;
    }

    public FMEA getParent() {
        return parent;
    }

    public void setParent(FMEA parent) {
        this.parent = parent;
    }

    @XmlTransient
    public List<RiskItem> getRiskItemList() {
        return riskItemList;
    }

    public void setRiskItemList(List<RiskItem> riskItemList) {
        this.riskItemList = riskItemList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof FMEA)) {
            return false;
        }
        FMEA other = (FMEA) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.fmea.FMEA[ id=" + id + " ]";
    }
    
}
