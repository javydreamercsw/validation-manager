/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db.fmea;

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
public class RiskItemPK implements Serializable {
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "RiskItemGen")
    @TableGenerator(name = "RiskItemGen", table = "vm_id",
    pkColumnName = "table_name",
    valueColumnName = "last_id",
    pkColumnValue = "risk_item",
    allocationSize = 1,
    initialValue = 1000)
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FMEA_id")
    private int fMEAid;

    public RiskItemPK() {
    }

    public RiskItemPK(int fMEAid) {
        this.fMEAid = fMEAid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFMEAid() {
        return fMEAid;
    }

    public void setFMEAid(int fMEAid) {
        this.fMEAid = fMEAid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) fMEAid;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiskItemPK)) {
            return false;
        }
        RiskItemPK other = (RiskItemPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.fMEAid != other.fMEAid) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.fmea.RiskItemPK[ id=" + id + ", fMEAid=" + fMEAid + " ]";
    }
}
