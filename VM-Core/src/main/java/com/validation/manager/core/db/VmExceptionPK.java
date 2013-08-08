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
public class VmExceptionPK implements Serializable {

    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "VM_ExceptionGEN")
    @TableGenerator(name = "VM_ExceptionGEN",
            table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "vm_exception",
            initialValue = 1000,
            allocationSize = 1)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "reporter_id")
    private int reporterId;

    public VmExceptionPK() {
    }

    public VmExceptionPK(int reporterId) {
        this.reporterId = reporterId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReporterId() {
        return reporterId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) reporterId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VmExceptionPK)) {
            return false;
        }
        VmExceptionPK other = (VmExceptionPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.reporterId != other.reporterId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.VmExceptionPK[ id=" + id + ", reporterId=" + reporterId + " ]";
    }

}
