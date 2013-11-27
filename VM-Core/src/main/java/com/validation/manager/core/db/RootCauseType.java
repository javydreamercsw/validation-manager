/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

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
@Table(name = "root_cause_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RootCauseType.findAll", query = "SELECT r FROM RootCauseType r"),
    @NamedQuery(name = "RootCauseType.findById", query = "SELECT r FROM RootCauseType r WHERE r.id = :id"),
    @NamedQuery(name = "RootCauseType.findByDescription", query = "SELECT r FROM RootCauseType r WHERE r.description = :description"),
    @NamedQuery(name = "RootCauseType.findByName", query = "SELECT r FROM RootCauseType r WHERE r.name = :name")})
public class RootCauseType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "RootCauseTypeGen")
    @TableGenerator(name = "RootCauseTypeGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "root_cause_type",
            allocationSize = 1,
            initialValue = 1000)
    @Column(name = "id")
    private Integer id;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rootCauseType")
    private List<RootCause> rootCauseList;

    public RootCauseType() {
    }

    public RootCauseType(Integer id) {
        this.id = id;
    }

    public RootCauseType(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    @JsonIgnore
    public List<RootCause> getRootCauseList() {
        return rootCauseList;
    }

    public void setRootCauseList(List<RootCause> rootCauseList) {
        this.rootCauseList = rootCauseList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RootCauseType)) {
            return false;
        }
        RootCauseType other = (RootCauseType) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RootCauseType[ id=" + id + " ]";
    }

}