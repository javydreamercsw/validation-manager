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
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Embeddable
public class RiskItemPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "RIGen")
    @TableGenerator(name = "RIGen", table = "vm_id",
            pkColumnName = "table_name",
            valueColumnName = "last_id",
            pkColumnValue = "risk_item",
            allocationSize = 1,
            initialValue = 1000)
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
        
        if (!(object instanceof RiskItemPK)) {
            return false;
        }
        RiskItemPK other = (RiskItemPK) object;
        if (this.id != other.id) {
            return false;
        }
        return this.fMEAid == other.fMEAid;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskItemPK[ id=" + id
                + ", fMEAid=" + fMEAid + " ]";
    }
}
