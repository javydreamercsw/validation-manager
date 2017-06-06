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
import javax.persistence.Lob;
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
@Table(name = "requirement_spec_node")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RequirementSpecNode.findAll",
            query = "SELECT r FROM RequirementSpecNode r")
    , @NamedQuery(name = "RequirementSpecNode.findById",
            query = "SELECT r FROM RequirementSpecNode r WHERE r.requirementSpecNodePK.id = :id")
    , @NamedQuery(name = "RequirementSpecNode.findByRequirementSpecId",
            query = "SELECT r FROM RequirementSpecNode r WHERE r.requirementSpecNodePK.requirementSpecId = :requirementSpecId")
    , @NamedQuery(name = "RequirementSpecNode.findByRequirementSpecProjectId",
            query = "SELECT r FROM RequirementSpecNode r WHERE r.requirementSpecNodePK.requirementSpecProjectId = :requirementSpecProjectId")
    , @NamedQuery(name = "RequirementSpecNode.findByRequirementSpecSpecLevelId",
            query = "SELECT r FROM RequirementSpecNode r WHERE r.requirementSpecNodePK.requirementSpecSpecLevelId = :requirementSpecSpecLevelId")
    , @NamedQuery(name = "RequirementSpecNode.findByName",
            query = "SELECT r FROM RequirementSpecNode r WHERE r.name = :name")})
public class RequirementSpecNode implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RequirementSpecNodePK requirementSpecNodePK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @Lob
    @Size(max = 2_147_483_647)
    @Column(name = "description")
    private String description;
    @Lob
    @Size(max = 2_147_483_647)
    @Column(name = "scope")
    private String scope;
    @JoinColumns({
        @JoinColumn(name = "requirement_spec_id", referencedColumnName = "id",
                insertable = false, updatable = false)
        , @JoinColumn(name = "requirement_spec_project_id",
                referencedColumnName = "project_id", insertable = false,
                updatable = false)
        , @JoinColumn(name = "requirement_spec_spec_level_id",
                referencedColumnName = "spec_level_id", insertable = false,
                updatable = false)})
    @ManyToOne(optional = false)
    private RequirementSpec requirementSpec;
    @OneToMany(mappedBy = "requirementSpecNode")
    private List<RequirementSpecNode> requirementSpecNodeList;
    @JoinColumns({
        @JoinColumn(name = "requirement_spec_node_id",
                referencedColumnName = "id")
        , @JoinColumn(name = "requirement_spec_node_requirement_spec_id",
                referencedColumnName = "requirement_spec_id")
        , @JoinColumn(name = "requirement_spec_node_requirement_spec_project_id",
                referencedColumnName = "requirement_spec_project_id")
        , @JoinColumn(name = "requirement_spec_node_requirement_spec_spec_level_id",
                referencedColumnName = "requirement_spec_spec_level_id")})
    @ManyToOne
    private RequirementSpecNode requirementSpecNode;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "requirementSpecNode")
    private List<Requirement> requirementList;

    public RequirementSpecNode() {
    }

    public RequirementSpecNode(RequirementSpecNodePK requirementSpecNodePK) {
        this.requirementSpecNodePK = requirementSpecNodePK;
    }

    public RequirementSpecNode(RequirementSpecNodePK requirementSpecNodePK,
            String name) {
        this.requirementSpecNodePK = requirementSpecNodePK;
        this.name = name;
    }

    public RequirementSpecNode(RequirementSpecPK pk) {
        this.requirementSpecNodePK = new RequirementSpecNodePK(pk.getId(),
                pk.getProjectId(), pk.getSpecLevelId());
    }

    public RequirementSpecNode(int requirementSpecId,
            int requirementSpecProjectId, int requirementSpecSpecLevelId) {
        this.requirementSpecNodePK
                = new RequirementSpecNodePK(requirementSpecId,
                        requirementSpecProjectId, requirementSpecSpecLevelId);
    }

    public RequirementSpecNodePK getRequirementSpecNodePK() {
        return requirementSpecNodePK;
    }

    public void setRequirementSpecNodePK(RequirementSpecNodePK requirementSpecNodePK) {
        this.requirementSpecNodePK = requirementSpecNodePK;
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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public RequirementSpec getRequirementSpec() {
        return requirementSpec;
    }

    public void setRequirementSpec(RequirementSpec requirementSpec) {
        this.requirementSpec = requirementSpec;
    }

    @XmlTransient
    @JsonIgnore
    public List<RequirementSpecNode> getRequirementSpecNodeList() {
        return requirementSpecNodeList;
    }

    public void setRequirementSpecNodeList(List<RequirementSpecNode> requirementSpecNodeList) {
        this.requirementSpecNodeList = requirementSpecNodeList;
    }

    public RequirementSpecNode getRequirementSpecNode() {
        return requirementSpecNode;
    }

    public void setRequirementSpecNode(RequirementSpecNode requirementSpecNode) {
        this.requirementSpecNode = requirementSpecNode;
    }

    @XmlTransient
    @JsonIgnore
    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (requirementSpecNodePK != null ? requirementSpecNodePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof RequirementSpecNode)) {
            return false;
        }
        RequirementSpecNode other = (RequirementSpecNode) object;
        return !((this.requirementSpecNodePK == null
                && other.requirementSpecNodePK != null)
                || (this.requirementSpecNodePK != null
                && !this.requirementSpecNodePK.equals(other.requirementSpecNodePK)));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementSpecNode[ requirementSpecNodePK="
                + requirementSpecNodePK + " ]";
    }
}
