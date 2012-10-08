/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "requirement_spec_node")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RequirementSpecNode.findAll", query = "SELECT r FROM RequirementSpecNode r"),
    @NamedQuery(name = "RequirementSpecNode.findById", query = "SELECT r FROM RequirementSpecNode r WHERE r.requirementSpecNodePK.id = :id"),
    @NamedQuery(name = "RequirementSpecNode.findByRequirementSpecId", query = "SELECT r FROM RequirementSpecNode r WHERE r.requirementSpecNodePK.requirementSpecId = :requirementSpecId"),
    @NamedQuery(name = "RequirementSpecNode.findByName", query = "SELECT r FROM RequirementSpecNode r WHERE r.name = :name"),
    @NamedQuery(name = "RequirementSpecNode.findByDescription", query = "SELECT r FROM RequirementSpecNode r WHERE r.description = :description"),
    @NamedQuery(name = "RequirementSpecNode.findByScope", query = "SELECT r FROM RequirementSpecNode r WHERE r.scope = :scope")})
public class RequirementSpecNode implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RequirementSpecNodePK requirementSpecNodePK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Size(max = 265)
    @Column(name = "description", length = 265)
    private String description;
    @Size(max = 265)
    @Column(name = "scope", length = 265)
    private String scope;
    @OneToMany(mappedBy = "requirementSpecNode")
    private List<Requirement> requirementList;
    @JoinColumns({
        @JoinColumn(name = "requirement_spec_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "requirement_spec_project_id", referencedColumnName = "project_id", insertable = false, updatable = false),
        @JoinColumn(name = "requirement_spec_level_id", referencedColumnName = "spec_level_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private RequirementSpec requirementSpec;
    @OneToMany(mappedBy = "requirementSpecNode")
    private List<RequirementSpecNode> requirementSpecNodeList;
    @JoinColumns({
        @JoinColumn(name = "parent_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "parent_requirement_spec_id", referencedColumnName = "requirement_spec_id", insertable = false, updatable = false)})
    @ManyToOne
    private RequirementSpecNode requirementSpecNode;

    public RequirementSpecNode() {
    }

    public RequirementSpecNode(RequirementSpecNodePK requirementSpecNodePK) {
        this.requirementSpecNodePK = requirementSpecNodePK;
    }

    public RequirementSpecNode(RequirementSpecNodePK requirementSpecNodePK, String name) {
        this.requirementSpecNodePK = requirementSpecNodePK;
        this.name = name;
    }

    public RequirementSpecNode(int requirementSpecId) {
        this.requirementSpecNodePK = new RequirementSpecNodePK(requirementSpecId);
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
        if ((this.requirementSpecNodePK == null && other.requirementSpecNodePK != null) || (this.requirementSpecNodePK != null && !this.requirementSpecNodePK.equals(other.requirementSpecNodePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementSpecNode[ requirementSpecNodePK=" + requirementSpecNodePK + " ]";
    }

    @XmlTransient
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
}
