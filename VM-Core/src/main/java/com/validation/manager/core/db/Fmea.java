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
import javax.persistence.JoinTable;
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
@Table(name = "fmea")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Fmea.findAll",
            query = "SELECT f FROM Fmea f")
    , @NamedQuery(name = "Fmea.findById",
            query = "SELECT f FROM Fmea f WHERE f.fmeaPK.id = :id")
    , @NamedQuery(name = "Fmea.findByProjectId",
            query = "SELECT f FROM Fmea f WHERE f.fmeaPK.projectId = :projectId")
    , @NamedQuery(name = "Fmea.findByName",
            query = "SELECT f FROM Fmea f WHERE f.name = :name")
    , @NamedQuery(name = "Fmea.findByDescription",
            query = "SELECT f FROM Fmea f WHERE f.description = :description")})
public class Fmea implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected FmeaPK fmeaPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @Size(max = 45)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    private List<Fmea> fmeaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fmea")
    private List<RiskItem> riskItemList;
    @JoinColumns({
        @JoinColumn(name = "parent_id", referencedColumnName = "id")
        , @JoinColumn(name = "parent_project_id",
                referencedColumnName = "project_id")})
    @ManyToOne(optional = false)
    private Fmea parent;
    @JoinColumn(name = "project_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Project project;
    @JoinTable(name = "fmea_has_risk_category", joinColumns = {
        @JoinColumn(name = "FMEA_id", referencedColumnName = "id")
        , @JoinColumn(name = "FMEA_project_id",
                referencedColumnName = "project_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "risk_category_id", referencedColumnName = "id")})
    @ManyToMany
    private List<RiskCategory> riskCategoryList;

    public Fmea() {
    }

    public Fmea(String name) {
        this.name = name;
    }

    public Fmea(FmeaPK fmeaPK) {
        this.fmeaPK = fmeaPK;
    }

    public Fmea(FmeaPK fmeaPK, String name) {
        this.fmeaPK = fmeaPK;
        this.name = name;
    }

    public Fmea(int projectId) {
        this.fmeaPK = new FmeaPK(projectId);
    }

    public FmeaPK getFmeaPK() {
        return fmeaPK;
    }

    public void setFmeaPK(FmeaPK fmeaPK) {
        this.fmeaPK = fmeaPK;
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
    @JsonIgnore
    public List<RiskItem> getRiskItemList() {
        return riskItemList;
    }

    public void setRiskItemList(List<RiskItem> riskItemList) {
        this.riskItemList = riskItemList;
    }

    @XmlTransient
    @JsonIgnore
    public List<Fmea> getFmeaList() {
        return fmeaList;
    }

    public void setFmeaList(List<Fmea> fmeaList) {
        this.fmeaList = fmeaList;
    }

    public Fmea getParent() {
        return parent;
    }

    public void setParent(Fmea parent) {
        this.parent = parent;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fmeaPK != null ? fmeaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Fmea)) {
            return false;
        }
        Fmea other = (Fmea) object;
        return !((this.fmeaPK == null && other.fmeaPK != null)
                || (this.fmeaPK != null && !this.fmeaPK.equals(other.fmeaPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.Fmea[ fmeaPK=" + fmeaPK + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<RiskCategory> getRiskCategoryList() {
        return riskCategoryList;
    }

    public void setRiskCategoryList(List<RiskCategory> riskCategoryList) {
        this.riskCategoryList = riskCategoryList;
    }
}
