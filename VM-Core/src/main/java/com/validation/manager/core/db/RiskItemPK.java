/*
 * Copyright 2017 Javier A. Ortiz Bultron javier.ortiz.78@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.TableGenerator;
import jakarta.validation.constraints.NotNull;

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
            initialValue = 1_000)
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FMEA_id")
    private int fMEAid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FMEA_project_id")
    private int fMEAprojectid;

    public RiskItemPK() {
    }

    public RiskItemPK(int fMEAid, int fMEAprojectid) {
        this.fMEAid = fMEAid;
        this.fMEAprojectid = fMEAprojectid;
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

    public int getFMEAprojectid() {
        return fMEAprojectid;
    }

    public void setFMEAprojectid(int fMEAprojectid) {
        this.fMEAprojectid = fMEAprojectid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) fMEAid;
        hash += (int) fMEAprojectid;
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
        return this.fMEAprojectid == other.fMEAprojectid;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskItemPK[ id=" + id
                + ", fMEAid=" + fMEAid + ", fMEAprojectid=" + fMEAprojectid + " ]";
    }
}
