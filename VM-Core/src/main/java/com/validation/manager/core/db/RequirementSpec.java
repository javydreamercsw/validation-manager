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
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "requirement_spec")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RequirementSpec.findAll",
            query = "SELECT r FROM RequirementSpec r")
    , @NamedQuery(name = "RequirementSpec.findById",
            query = "SELECT r FROM RequirementSpec r WHERE r.requirementSpecPK.id = :id")
    , @NamedQuery(name = "RequirementSpec.findByProjectId",
            query = "SELECT r FROM RequirementSpec r WHERE r.requirementSpecPK.projectId = :projectId")
    , @NamedQuery(name = "RequirementSpec.findBySpecLevelId",
            query = "SELECT r FROM RequirementSpec r WHERE r.requirementSpecPK.specLevelId = :specLevelId")
    , @NamedQuery(name = "RequirementSpec.findByName",
            query = "SELECT r FROM RequirementSpec r WHERE r.name = :name")
    , @NamedQuery(name = "RequirementSpec.findByModificationDate",
            query = "SELECT r FROM RequirementSpec r WHERE r.modificationDate = :modificationDate")})
public class RequirementSpec implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RequirementSpecPK requirementSpecPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @Lob
    @Size(max = 2_147_483_647)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "modificationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "requirementSpec")
    private List<RequirementSpecNode> requirementSpecNodeList;
    @JoinColumn(name = "project_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Project project;
    @JoinColumn(name = "spec_level_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private SpecLevel specLevel;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "requirementSpec")
    private List<Baseline> baselineList;

    public RequirementSpec() {
        super();
    }

    public RequirementSpec(RequirementSpecPK requirementSpecPK) {
        this.requirementSpecPK = requirementSpecPK;
    }

    public RequirementSpec(RequirementSpecPK requirementSpecPK,
            String name, Date modificationDate) {
        super();
        this.requirementSpecPK = requirementSpecPK;
        this.name = name;
        this.modificationDate = modificationDate;
    }

    public RequirementSpec(int projectId, int specLevelId) {
        super();
        this.requirementSpecPK = new RequirementSpecPK(projectId, specLevelId);
    }

    public RequirementSpecPK getRequirementSpecPK() {
        return requirementSpecPK;
    }

    public void setRequirementSpecPK(RequirementSpecPK requirementSpecPK) {
        this.requirementSpecPK = requirementSpecPK;
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

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    @XmlTransient
    @JsonIgnore
    public List<RequirementSpecNode> getRequirementSpecNodeList() {
        return requirementSpecNodeList;
    }

    public void setRequirementSpecNodeList(List<RequirementSpecNode> requirementSpecNodeList) {
        this.requirementSpecNodeList = requirementSpecNodeList;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public SpecLevel getSpecLevel() {
        return specLevel;
    }

    public void setSpecLevel(SpecLevel specLevel) {
        this.specLevel = specLevel;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (requirementSpecPK != null ? requirementSpecPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof RequirementSpec)) {
            return false;
        }
        RequirementSpec other = (RequirementSpec) object;
        return !((this.requirementSpecPK == null
                && other.requirementSpecPK != null)
                || (this.requirementSpecPK != null
                && !this.requirementSpecPK.equals(other.requirementSpecPK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementSpec[ requirementSpecPK="
                + requirementSpecPK + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public List<Baseline> getBaselineList() {
        return baselineList;
    }

    public void setBaselineList(List<Baseline> baselineList) {
        this.baselineList = baselineList;
    }
}
