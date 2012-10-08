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
@Table(name = "risk_control_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RiskControlType.findAll", query = "SELECT r FROM RiskControlType r"),
    @NamedQuery(name = "RiskControlType.findById", query = "SELECT r FROM RiskControlType r WHERE r.id = :id"),
    @NamedQuery(name = "RiskControlType.findByName", query = "SELECT r FROM RiskControlType r WHERE r.name = :name"),
    @NamedQuery(name = "RiskControlType.findByDescription", query = "SELECT r FROM RiskControlType r WHERE r.description = :description")})
public class RiskControlType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "RiskControlTypeGen")
    @TableGenerator(name = "RiskControlTypeGen", table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "risk_control_type",
    allocationSize = 1,
    initialValue = 1000)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "riskControlType")
    private List<RiskControl> riskControlList;

    public RiskControlType() {
    }

    public RiskControlType(String name, String description) {
        this.name = name;
        this.description = description;
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
    public List<RiskControl> getRiskControlList() {
        return riskControlList;
    }

    public void setRiskControlList(List<RiskControl> riskControlList) {
        this.riskControlList = riskControlList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RiskControlType)) {
            return false;
        }
        RiskControlType other = (RiskControlType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.fmea.RiskControlType[ id=" + id + " ]";
    }
}
