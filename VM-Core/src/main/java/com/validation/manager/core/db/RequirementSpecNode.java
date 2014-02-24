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
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "requirement_spec_node")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RequirementSpecNode.findAll",
            query = "SELECT r FROM RequirementSpecNode r"),
    @NamedQuery(name = "RequirementSpecNode.findById",
            query = "SELECT r FROM RequirementSpecNode r WHERE r.requirementSpecNodePK.id = :id"),
    @NamedQuery(name = "RequirementSpecNode.findByRequirementSpecId",
            query = "SELECT r FROM RequirementSpecNode r WHERE r.requirementSpecNodePK.requirementSpecId = :requirementSpecId"),
    @NamedQuery(name = "RequirementSpecNode.findByRequirementSpecProjectId",
            query = "SELECT r FROM RequirementSpecNode r WHERE r.requirementSpecNodePK.requirementSpecProjectId = :requirementSpecProjectId"),
    @NamedQuery(name = "RequirementSpecNode.findByRequirementSpecSpecLevelId",
            query = "SELECT r FROM RequirementSpecNode r WHERE r.requirementSpecNodePK.requirementSpecSpecLevelId = :requirementSpecSpecLevelId"),
    @NamedQuery(name = "RequirementSpecNode.findByName",
            query = "SELECT r FROM RequirementSpecNode r WHERE r.name = :name"),
    @NamedQuery(name = "RequirementSpecNode.findByParentRequirementSpecNodeRequirementSpecId",
            query = "SELECT r FROM RequirementSpecNode r WHERE r.parentRequirementSpecNodeRequirementSpecId = :parentRequirementSpecNodeRequirementSpecId")})
public class RequirementSpecNode implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "requirementSpecNode")
    private List<Requirement> requirementList;

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RequirementSpecNodePK requirementSpecNodePK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Lob
    @Size(max = 65535)
    @Column(name = "scope")
    private String scope;
    @Column(name = "parent_requirement_spec_node_requirement_spec_id")
    private Integer parentRequirementSpecNodeRequirementSpecId;
    @OneToMany(mappedBy = "requirementSpecNode")
    private List<RequirementSpecNode> requirementSpecNodeList;
    @JoinColumns({
        @JoinColumn(name = "requirement_spec_node_id", referencedColumnName = "id"),
        @JoinColumn(name = "requirement_spec_node_requirement_spec_id", referencedColumnName = "requirement_spec_id"),
        @JoinColumn(name = "requirement_spec_node_requirement_spec_project_id", referencedColumnName = "requirement_spec_project_id"),
        @JoinColumn(name = "requirement_spec_node_requirement_spec_spec_level_id", referencedColumnName = "requirement_spec_spec_level_id")})
    @ManyToOne
    private RequirementSpecNode requirementSpecNode;
    @JoinColumns({
        @JoinColumn(name = "requirement_spec_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "requirement_spec_project_id", referencedColumnName = "project_id", insertable = false, updatable = false),
        @JoinColumn(name = "requirement_spec_spec_level_id", referencedColumnName = "spec_level_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private RequirementSpec requirementSpec;

    public RequirementSpecNode() {
    }

    public RequirementSpecNode(RequirementSpecNodePK requirementSpecNodePK) {
        this.requirementSpecNodePK = requirementSpecNodePK;
    }

    public RequirementSpecNode(RequirementSpecNodePK requirementSpecNodePK, String name) {
        this.requirementSpecNodePK = requirementSpecNodePK;
        this.name = name;
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

    public Integer getParentRequirementSpecNodeRequirementSpecId() {
        return parentRequirementSpecNodeRequirementSpecId;
    }

    public void setParentRequirementSpecNodeRequirementSpecId(Integer parentRequirementSpecNodeRequirementSpecId) {
        this.parentRequirementSpecNodeRequirementSpecId = parentRequirementSpecNodeRequirementSpecId;
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

    public RequirementSpec getRequirementSpec() {
        return requirementSpec;
    }

    public void setRequirementSpec(RequirementSpec requirementSpec) {
        this.requirementSpec = requirementSpec;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (requirementSpecNodePK != null ? requirementSpecNodePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
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
    @JsonIgnore
    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

}
