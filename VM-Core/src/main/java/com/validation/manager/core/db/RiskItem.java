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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@Entity
@Table(name = "risk_item")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RiskItem.findAll",
            query = "SELECT r FROM RiskItem r")
    , @NamedQuery(name = "RiskItem.findById",
            query = "SELECT r FROM RiskItem r WHERE r.riskItemPK.id = :id")
    , @NamedQuery(name = "RiskItem.findByFMEAid",
            query = "SELECT r FROM RiskItem r WHERE r.riskItemPK.fMEAid = :fMEAid")
    , @NamedQuery(name = "RiskItem.findByFMEAprojectid",
            query = "SELECT r FROM RiskItem r WHERE r.riskItemPK.fMEAprojectid = :fMEAprojectid")
    , @NamedQuery(name = "RiskItem.findByDescription",
            query = "SELECT r FROM RiskItem r WHERE r.description = :description")})
public class RiskItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RiskItemPK riskItemPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "riskItem")
    private List<RiskItemHasHazard> riskItemHasHazardList;
    @JoinColumns({
        @JoinColumn(name = "FMEA_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "FMEA_project_id",
                referencedColumnName = "project_id", insertable = false,
                updatable = false)})
    @ManyToOne(optional = false)
    private Fmea fmea;
    @ManyToMany(mappedBy = "riskItemList")
    private List<RiskControl> riskControlList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "riskItem")
    private List<RiskControlHasResidualRiskItem> riskControlHasResidualRiskItemList;

    public RiskItem() {
    }

    public RiskItem(RiskItemPK riskItemPK) {
        this.riskItemPK = riskItemPK;
    }

    public RiskItem(RiskItemPK riskItemPK, String description) {
        this.riskItemPK = riskItemPK;
        this.description = description;
    }

    public RiskItem(int fMEAid, int fMEAprojectid) {
        this.riskItemPK = new RiskItemPK(fMEAid, fMEAprojectid);
    }

    public RiskItem(FmeaPK pk) {
        this.riskItemPK = new RiskItemPK(pk.getId(), pk.getProjectId());
    }

    public RiskItemPK getRiskItemPK() {
        return riskItemPK;
    }

    public void setRiskItemPK(RiskItemPK riskItemPK) {
        this.riskItemPK = riskItemPK;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskItemHasHazard> getRiskItemHasHazardList() {
        return riskItemHasHazardList;
    }

    public void setRiskItemHasHazardList(List<RiskItemHasHazard> riskItemHasHazardList) {
        this.riskItemHasHazardList = riskItemHasHazardList;
    }

    public Fmea getFmea() {
        return fmea;
    }

    public void setFmea(Fmea fmea) {
        this.fmea = fmea;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (riskItemPK != null ? riskItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RiskItem)) {
            return false;
        }
        RiskItem other = (RiskItem) object;
        return !((this.riskItemPK == null && other.riskItemPK != null)
                || (this.riskItemPK != null
                && !this.riskItemPK.equals(other.riskItemPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RiskItem[ riskItemPK="
                + riskItemPK + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskControl> getRiskControlList() {
        return riskControlList;
    }

    public void setRiskControlList(List<RiskControl> riskControlList) {
        this.riskControlList = riskControlList;
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskControlHasResidualRiskItem> getRiskControlHasResidualRiskItemList() {
        return riskControlHasResidualRiskItemList;
    }

    public void setRiskControlHasResidualRiskItemList(List<RiskControlHasResidualRiskItem> riskControlHasResidualRiskItemList) {
        this.riskControlHasResidualRiskItemList = riskControlHasResidualRiskItemList;
    }
}
