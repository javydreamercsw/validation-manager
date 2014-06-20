package com.validation.manager.core.db;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Entity
@Table(name = "requirement_spec")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RequirementSpec.findAll", 
            query = "SELECT r FROM RequirementSpec r"),
    @NamedQuery(name = "RequirementSpec.findByModificationDate", 
            query = "SELECT r FROM RequirementSpec r WHERE r.modificationDate = :modificationDate"),
    @NamedQuery(name = "RequirementSpec.findByName", 
            query = "SELECT r FROM RequirementSpec r WHERE r.name = :name"),
    @NamedQuery(name = "RequirementSpec.findByVersion", 
            query = "SELECT r FROM RequirementSpec r WHERE r.version = :version"),
    @NamedQuery(name = "RequirementSpec.findById", 
            query = "SELECT r FROM RequirementSpec r WHERE r.requirementSpecPK.id = :id"),
    @NamedQuery(name = "RequirementSpec.findByProjectId", 
            query = "SELECT r FROM RequirementSpec r WHERE r.requirementSpecPK.projectId = :projectId"),
    @NamedQuery(name = "RequirementSpec.findBySpecLevelId",
            query = "SELECT r FROM RequirementSpec r WHERE r.requirementSpecPK.specLevelId = :specLevelId")})
public class RequirementSpec implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RequirementSpecPK requirementSpecPK;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;
    @Column(name = "modificationDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationDate;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Column(name = "version")
    private Integer version;
    @JoinColumn(name = "spec_level_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private SpecLevel specLevel;
    @JoinColumn(name = "project_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Project project;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "requirementSpec")
    private List<RequirementSpecNode> requirementSpecNodeList;

    public RequirementSpec() {
    }

    public RequirementSpec(RequirementSpecPK requirementSpecPK) {
        this.requirementSpecPK = requirementSpecPK;
    }

    public RequirementSpec(RequirementSpecPK requirementSpecPK, String name,
            int version, Date modificationDate) {
        this.requirementSpecPK = requirementSpecPK;
        this.name = name;
        this.version = version;
        this.modificationDate = modificationDate;
    }

    public RequirementSpec(int projectId, int specLevelId) {
        this.requirementSpecPK = new RequirementSpecPK(projectId, specLevelId);
    }

    public RequirementSpecPK getRequirementSpecPK() {
        return requirementSpecPK;
    }

    public void setRequirementSpecPK(RequirementSpecPK requirementSpecPK) {
        this.requirementSpecPK = requirementSpecPK;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public SpecLevel getSpecLevel() {
        return specLevel;
    }

    public void setSpecLevel(SpecLevel specLevel) {
        this.specLevel = specLevel;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @XmlTransient
    @JsonIgnore
    public List<RequirementSpecNode> getRequirementSpecNodeList() {
        return requirementSpecNodeList;
    }

    public void setRequirementSpecNodeList(List<RequirementSpecNode> requirementSpecNodeList) {
        this.requirementSpecNodeList = requirementSpecNodeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (requirementSpecPK != null ? requirementSpecPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RequirementSpec)) {
            return false;
        }
        RequirementSpec other = (RequirementSpec) object;
        return (this.requirementSpecPK != null || other.requirementSpecPK == null) && (this.requirementSpecPK == null || this.requirementSpecPK.equals(other.requirementSpecPK));
    }

    @Override
    public String toString() {
        return "com.validation.manager.core.db.RequirementSpec[ requirementSpecPK=" + requirementSpecPK + " ]";
    }

}
