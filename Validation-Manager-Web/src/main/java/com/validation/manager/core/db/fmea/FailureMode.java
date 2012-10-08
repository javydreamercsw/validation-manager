/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.fmea;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "failure_mode")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FailureMode.findAll", query = "SELECT f FROM FailureMode f"),
    @NamedQuery(name = "FailureMode.findById", query = "SELECT f FROM FailureMode f WHERE f.id = :id"),
    @NamedQuery(name = "FailureMode.findByName", query = "SELECT f FROM FailureMode f WHERE f.name = :name"),
    @NamedQuery(name = "FailureMode.findByDescription", query = "SELECT f FROM FailureMode f WHERE f.description = :description")})
public class FailureMode implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "FailureModeGen")
    @TableGenerator(name = "FailureModeGen", table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "failure_mode",
    allocationSize = 1,
    initialValue = 1)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @ManyToMany(mappedBy = "failureModeList")
    private List<RiskItem> riskItemList;

    public FailureMode() {
    }

    public FailureMode(String name, String description) {
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
        if (!(object instanceof FailureMode)) {
            return false;
        }
        FailureMode other = (FailureMode) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.fmea.FailureMode[ id=" + id + " ]";
    }
}
