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
public class RootCausePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "RootCGen")
    @TableGenerator(name = "RootCGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "root_cause",
            allocationSize = 1,
            initialValue = 1000)
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "root_cause_type_id")
    private int rootCauseTypeId;

    public RootCausePK() {
    }

    public RootCausePK(int id, int rootCauseTypeId) {
        this.id = id;
        this.rootCauseTypeId = rootCauseTypeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRootCauseTypeId() {
        return rootCauseTypeId;
    }

    public void setRootCauseTypeId(int rootCauseTypeId) {
        this.rootCauseTypeId = rootCauseTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) rootCauseTypeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RootCausePK)) {
            return false;
        }
        RootCausePK other = (RootCausePK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.rootCauseTypeId != other.rootCauseTypeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RootCausePK[ id=" + id + ", rootCauseTypeId=" + rootCauseTypeId + " ]";
    }

}
