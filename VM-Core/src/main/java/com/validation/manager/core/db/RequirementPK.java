/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.TableGenerator;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Embeddable
public class RequirementPK implements Serializable {

    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "ReqGen")
    @TableGenerator(name = "ReqGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "requirement",
            allocationSize = 1,
            initialValue = 1000)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "major_version")
    private int major_version;
    @Basic(optional = false)
    @NotNull
    @Column(name = "mid_version")
    private int mid_version;
    @Basic(optional = false)
    @NotNull
    @Column(name = "minor_version")
    private int minor_version;

    public RequirementPK() {
    }

    public RequirementPK(int major_version, int mid_version, int minor_version) {
        this.major_version = major_version;
        this.mid_version = mid_version;
        this.minor_version = minor_version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMinorVersion() {
        return minor_version;
    }

    public void setMinorVersion(int version) {
        this.minor_version = version;
    }

    public int getMajorVersion() {
        return major_version;
    }

    public void setMajorVersion(int version) {
        this.major_version = version;
    }

    public int getMidVersion() {
        return mid_version;
    }

    public void setMidVersion(int version) {
        this.mid_version = version;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) minor_version;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequirementPK)) {
            return false;
        }
        RequirementPK other = (RequirementPK) object;
        if (this.id != other.id) {
            return false;
        }
        return this.minor_version == other.minor_version;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementPK[ id=" + id
                + ", version=" + major_version + "." + mid_version
                + "." + minor_version + " ]";
    }

}
