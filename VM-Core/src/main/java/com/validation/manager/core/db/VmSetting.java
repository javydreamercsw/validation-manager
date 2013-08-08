/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "vm_setting")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VmSetting.findAll", query = "SELECT v FROM VmSetting v"),
    @NamedQuery(name = "VmSetting.findById", query = "SELECT v FROM VmSetting v WHERE v.id = :id"),
    @NamedQuery(name = "VmSetting.findBySetting", query = "SELECT v FROM VmSetting v WHERE v.setting = :setting"),
    @NamedQuery(name = "VmSetting.findByBoolVal", query = "SELECT v FROM VmSetting v WHERE v.boolVal = :boolVal"),
    @NamedQuery(name = "VmSetting.findByIntVal", query = "SELECT v FROM VmSetting v WHERE v.intVal = :intVal")})
public class VmSetting implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VM_SettingGEN")
    @TableGenerator(name = "VM_SettingGEN",
            table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "vm_setting",
            initialValue = 1000,
            allocationSize = 1)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "setting")
    private String setting;
    @Column(name = "bool_val")
    private Boolean boolVal;
    @Column(name = "int_val")
    private Integer intVal;
    @Lob
    @Size(max = 16777215)
    @Column(name = "long_val")
    private String longVal;
    @Lob
    @Size(max = 65535)
    @Column(name = "string_val")
    private String stringVal;

    public VmSetting() {
    }

    public VmSetting(String setting) {
        this.setting = setting;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public Boolean getBoolVal() {
        return boolVal;
    }

    public void setBoolVal(Boolean boolVal) {
        this.boolVal = boolVal;
    }

    public Integer getIntVal() {
        return intVal;
    }

    public void setIntVal(Integer intVal) {
        this.intVal = intVal;
    }

    public String getLongVal() {
        return longVal;
    }

    public void setLongVal(String longVal) {
        this.longVal = longVal;
    }

    public String getStringVal() {
        return stringVal;
    }

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
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
        if (!(object instanceof VmSetting)) {
            return false;
        }
        VmSetting other = (VmSetting) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.VmSetting[ id=" + id + " ]";
    }

}
